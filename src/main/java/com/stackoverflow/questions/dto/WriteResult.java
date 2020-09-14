package com.stackoverflow.questions.dto;


import java.io.File;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class WriteResult {

  private File filename;
  private boolean isWriten;
}
