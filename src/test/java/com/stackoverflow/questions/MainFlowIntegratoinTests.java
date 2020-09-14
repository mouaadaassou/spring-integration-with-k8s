package com.stackoverflow.questions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.support.MessageBuilder;

@SpringBootTest
@SpringIntegrationTest(noAutoStartup = "fileReadingEndpoint")
public class MainFlowIntegratoinTests {

  @Autowired
  private MockIntegrationContext mockIntegrationContext;

  @Autowired
  private SourcePollingChannelAdapter fileReadingEndpoint;

  @Rule
  private TemporaryFolder temporaryFolder;

  @Test
  public void readingInvalidFileAndMoveItToErrorDir() throws IOException {
    File file = new ClassPathResource("valid01-student-01.xml").getFile();
    MessageSource<File> mockInvalidStudentFile = () -> MessageBuilder.withPayload(file).build();

    mockIntegrationContext.substituteMessageSourceFor("fileReadingEndpoint", mockInvalidStudentFile);

    // start the file adapter manually
    fileReadingEndpoint.start();
  }

}
