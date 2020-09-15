package com.stackoverflow.questions.config;


import static java.util.Arrays.asList;

import com.stackoverflow.questions.dto.WriteResult;
import com.stackoverflow.questions.handler.FileReaderHandler;
import com.stackoverflow.questions.handler.StudentErrorHandler;
import com.stackoverflow.questions.handler.StudentWriterHandler;
import com.stackoverflow.questions.service.DirectoryManagerService;
import com.stackoverflow.questions.transformer.FileToStudentTransformer;
import java.io.File;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.file.filters.RegexPatternFileListFilter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
public class MainIntegrationFlow {

  @Value("${regex.filename.pattern}")
  private String regexFileNamePattern;

  @Value("${root.file.dir}")
  private String rootFileDir;

  @Value("${default.polling.rate}")
  private Long defaultPollingRate;

  private final DirectoryManagerService directoryManagerService;
  private final StudentErrorHandler studentErrorHandler;
  private final FileReaderHandler fileReaderHandler;
  private final StudentWriterHandler studentWriterHandler;
  private final FileToStudentTransformer fileToStudentTransformer;

  @Bean("mainStudentIntegrationFlow")
  public IntegrationFlow mainStudentIntegrationFlow(
      @Qualifier("mainFileReadingSourceMessage") MessageSource<File> mainFileReadingSourceMessage,
      @Qualifier("fileReaderChannel") MessageChannel fileReaderChannel) {
    return IntegrationFlows.from(mainFileReadingSourceMessage)
        .channel(fileReaderChannel)
        .handle(fileReaderHandler)
        .transform(fileToStudentTransformer)
        .handle(studentWriterHandler)
        .<WriteResult, Boolean>route(WriteResult::isWriten,
            mapping -> mapping
                .subFlowMapping(true, moveToProcessedDirFlow())
                .subFlowMapping(false, moveToErrorDirFlow()))
        .get();
  }


  public IntegrationFlow moveToProcessedDirFlow() {
    return flow -> flow.handle(message ->
        directoryManagerService
            .moveToProcessedDir(((WriteResult) message.getPayload()).getFilename()));
  }

  public IntegrationFlow moveToErrorDirFlow() {
    return flow -> flow.channel("studentErrorChannel")
        .handle(message ->
            directoryManagerService
                .moveToErrorDir(((WriteResult) message.getPayload()).getFilename()));
  }

  @Bean(name = "errorHandlerMainFlow")
  public IntegrationFlow errorHandlerMainFlow() {
    return IntegrationFlows.from("errorChannel")
        .handle(studentErrorHandler)
        .get();
  }

  @Bean(name = PollerMetadata.DEFAULT_POLLER)
  public PollerMetadata mainPollerMetadata() {
    return Pollers.fixedRate(defaultPollingRate, TimeUnit.SECONDS)
        .maxMessagesPerPoll(0)
        .get();
  }

  @Bean
  public MessageChannel fileReaderChannel() {
    return MessageChannels.queue("fileReaderChannel").get();
  }

  @Bean("mainDirectoryScanner")
  public DirectoryScanner mainDirectoryScanner() {
    DirectoryScanner recursiveDirectoryScanner = new RecursiveDirectoryScanner();

    CompositeFileListFilter<File> compositeFileListFilter = new CompositeFileListFilter<>(
        asList(new AcceptOnceFileListFilter<>(),
            new RegexPatternFileListFilter(regexFileNamePattern)));

    recursiveDirectoryScanner.setFilter(compositeFileListFilter);
    return recursiveDirectoryScanner;
  }

  @Bean("mainFileReadingSourceMessage")
  public MessageSource<File> mainFileReadingSourceMessage(
      @Qualifier("mainDirectoryScanner") DirectoryScanner mainDirectoryScanner) {
    FileReadingMessageSource fileReadingMessageSource = new FileReadingMessageSource();
    fileReadingMessageSource.setDirectory(new File(rootFileDir));
    fileReadingMessageSource.setScanner(mainDirectoryScanner);

    return fileReadingMessageSource;
  }
}
