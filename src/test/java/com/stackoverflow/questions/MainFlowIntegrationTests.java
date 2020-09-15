package com.stackoverflow.questions;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import com.stackoverflow.questions.service.DirectoryManagerService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class MainFlowIntegrationTests {


  private static final String MOCK_FILE_DIR = "intFiles/";
  private static final String VALID_XML_MOCK_FILE = "valid01-student-01.xml";
  private static final String INVALID_XML_MOCK_FILE = "invalid02-student-02.xml";

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

  @AfterEach
  public void tearDown() throws IOException {
    deleteRequiredDirectories();
  }

  @Test
  public void readingFileAndMoveItToProcessedDir() throws IOException, InterruptedException {
    // When: the fileReaderChannel receives a valid XML file
    fileReaderChannel
        .send(MessageBuilder.withPayload(new File(queueDir, VALID_XML_MOCK_FILE)).build());

    // Then: the valid XML file should be sent to the processedDir
    await().until(() -> processed.list().length == 1);
  }

  @Test
  public void readingInvalidFileAndMoveItToErrorDir() throws IOException, InterruptedException {
    // When: the fileReaderChannel receives a invalid XML file
    fileReaderChannel
        .send(MessageBuilder.withPayload(new File(queueDir, INVALID_XML_MOCK_FILE)).build());

    // Then: the invalid XML file should be sent to the errorDir
    await().until(() -> error.list().length == 1);
  }

  private void injectProperties() {
    ReflectionTestUtils
        .setField(directoryManagerService, "errorDir", error.getAbsolutePath().concat("/"));
    ReflectionTestUtils
        .setField(directoryManagerService, "processedDir", processed.getAbsolutePath().concat("/"));
  }

  private void moveFilesToQueueDir() throws IOException {
    File intFiles = new ClassPathResource(MOCK_FILE_DIR).getFile();

    for (String filename : intFiles.list()) {
      FileUtils.copyFile(new File(intFiles, filename), new File(queueDir, filename));
    }
  }

  private void createRequiredDirectories() throws IOException {
    queueDir = Files.createTempDirectory("queueDir").toFile();
    processed = Files.createTempDirectory("processedDir").toFile();
    error = Files.createTempDirectory("errorDir").toFile();
  }

  private void deleteRequiredDirectories() throws IOException {
    forceDelete(queueDir);
    forceDelete(processed);
    forceDelete(error);
  }

}
