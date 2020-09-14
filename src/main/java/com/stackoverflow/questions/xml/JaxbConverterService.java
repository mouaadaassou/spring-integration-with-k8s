package com.stackoverflow.questions.xml;


import static java.util.Optional.ofNullable;

import com.stackoverflow.questions.dto.Student;
import com.stackoverflow.questions.exception.StudentIsNotEligibaleException;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;

@Service
public class JaxbConverterService {

  public Student convertToStudent(File file) throws JAXBException {
    return unmarshallAndValidate(file);
  }

  public Student unmarshallAndValidate(File file) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(Student.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    Student student = (Student) jaxbUnmarshaller.unmarshal(file);

    return ofNullable(student).filter(Student::isEligabaleStudent).orElseThrow(() -> new StudentIsNotEligibaleException("Student with id " + student.getStudentId() + " is not eligable"));
  }
}
