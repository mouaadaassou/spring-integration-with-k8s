package com.stackoverflow.questions.handler;


import com.stackoverflow.questions.dto.PolledFile;
import java.io.File;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class FileReaderHandler {

  public PolledFile read(File pFile) {
    log.info("====== polling file {} ======", pFile.getName());
    return PolledFile.builder()
        .polledFile(pFile.getAbsoluteFile())
        .polledAt(LocalDateTime.now())
        .build();
  }
}
