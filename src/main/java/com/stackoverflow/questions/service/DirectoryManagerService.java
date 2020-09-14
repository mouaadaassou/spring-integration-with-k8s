package com.stackoverflow.questions.service;

import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.io.FileUtils.moveFile;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DirectoryManagerService {

  private static final String ERROR_DIR_PATH = "/tmp/error/";
  private static final String PROCESSED_DIR_PATH = "/tmp/processed/";

  public void moveToErrorDir(File fileToMove)  {
    try {
      moveFile(fileToMove, new File(ERROR_DIR_PATH.concat(fileToMove.getName())));
    } catch (IOException e) {
      log.error("====== unable to move file {} to Error Dir {} ======", fileToMove.getAbsoluteFile(), ERROR_DIR_PATH);
    }
  }

  public void moveToProcessedDir(File fileToMove) {
    try {
      moveFile(fileToMove, new File(PROCESSED_DIR_PATH.concat(fileToMove.getName())));
    } catch (IOException e) {
      log.error("====== unable to move file {} to Error Dir ======", fileToMove.getName());
    }
  }
}
