package com.stackoverflow.questions.handler;

import com.stackoverflow.questions.dto.Student;
import com.stackoverflow.questions.dto.StudentDto;
import com.stackoverflow.questions.dto.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudentWriterHandler {

  public WriteResult logStudent(StudentDto studentDto) {
    log.info("====== registering student {} to the Database ======", studentDto.getStudent());
    return WriteResult.builder()
        .filename(studentDto.getStudentFile())
        .isWriten(true)
        .build();
  }
}
