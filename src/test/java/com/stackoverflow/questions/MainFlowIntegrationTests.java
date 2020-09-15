package com.stackoverflow.questions;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.stackoverflow.questions.service.DirectoryManagerService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@SpringIntegrationTest(noAutoStartup = "fileReadingEndpoint")
public class MainFlowIntegrationTests {

  @Autowired
  private MessageChannel fileReaderChannel;

  @Autowired
  private DirectoryManagerService directoryManagerService;

  private File queueDir;
  private File processed;
  private File error;

  @BeforeEach
  public void setup() throws IOException {
    createRequiredDirectories();
    moveFilesToQueueDir();
    injectProperties();
  }

  @Test
  public void readingValidFileAndMoveItToProcessedDir() throws IOException, InterruptedException {
    // When: the fileReaderChannel receives a valid XML file

    fileReaderChannel
        .send(MessageBuilder.withPayload(new File(queueDir, "valid01-student-01.xml")).build());

    // Then: the valid XML file should be sent to the processedDir

    assertThat(processed.list().length).isEqualTo(1);
  }

  private void injectProperties() {
    ReflectionTestUtils.setField(directoryManagerService, "errorDir", error.getAbsolutePath());
    ReflectionTestUtils
        .setField(directoryManagerService, "processedDir", processed.getAbsolutePath());
  }

  private void moveFilesToQueueDir() throws IOException {
    File intfiles = new ClassPathResource("intFiles/").getFile();

    for (String filename : intfiles.list()) {
      FileUtils.copyFile(new File(intfiles, filename), new File(queueDir, filename));
    }
  }

  private void createRequiredDirectories() throws IOException {
    queueDir = Files.createTempDirectory("queueDir").toFile();
    processed = Files.createTempDirectory("processedDir").toFile();
    error = Files.createTempDirectory("errorDir").toFile();
  }

}
