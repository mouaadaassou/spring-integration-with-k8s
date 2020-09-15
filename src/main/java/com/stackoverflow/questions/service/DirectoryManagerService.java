package com.stackoverflow.questions.service;

import static org.apache.commons.io.FileUtils.moveDirectory;
import static org.apache.commons.io.FileUtils.moveFile;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DirectoryManagerService {

  @Value("${error.dir.path}")
  private String errorDir;

  @Value("${processed.dir.path}")
  private String processedDir;

  public void moveToErrorDir(File fileToMove)  {
    try {
      moveFile(fileToMove, new File(errorDir.concat(fileToMove.getName())));
    } catch (IOException e) {
      log.error("====== unable to move file {} to Error Dir {} ======", fileToMove.getAbsoluteFile(), errorDir);
    }
  }

  public void moveToProcessedDir(File fileToMove) {
    try {
      moveFile(fileToMove, new File(processedDir.concat(fileToMove.getName())));
    } catch (IOException e) {
      log.error("====== unable to move file {} to Error Dir ======", fileToMove.getName());
    }
  }
}
