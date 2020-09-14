package com.stackoverflow.questions.transformer;


import com.stackoverflow.questions.dto.PolledFile;
import com.stackoverflow.questions.dto.Student;
import com.stackoverflow.questions.dto.StudentDto;
import com.stackoverflow.questions.xml.JaxbConverterService;
import javax.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileToStudentTransformer {

  private final JaxbConverterService jaxbConverterService;

  public StudentDto transform(PolledFile polledFile) throws JAXBException {
    log.info("====== Unmarshalling file {} polled at {} ======",
        polledFile.getPolledFile().getName(), polledFile.getPolledFile());

    return StudentDto.builder()
        .student(jaxbConverterService.convertToStudent(polledFile.getPolledFile()))
        .studentFile(polledFile.getPolledFile())
        .build();
  }
}
