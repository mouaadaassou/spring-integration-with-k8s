package com.stackoverflow.questions.dto;


import java.io.File;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PolledFile {

  private File polledFile;
  private LocalDateTime polledAt;
}
