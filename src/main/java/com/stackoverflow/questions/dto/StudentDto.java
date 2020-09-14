package com.stackoverflow.questions.dto;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
@AllArgsConstructor
public class StudentDto {

  private File studentFile;
  private Student student;
}
