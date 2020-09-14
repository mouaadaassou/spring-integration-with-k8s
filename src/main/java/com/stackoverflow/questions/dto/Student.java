package com.stackoverflow.questions.dto;


import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;

@Getter
@XmlRootElement(name = "student")
public class Student {

  private String studentId;
  private String firstName;
  private String lastName;
  private String age;

  @XmlElement
  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  @XmlElement
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @XmlElement
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @XmlAttribute
  public void setAge(String age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return String
        .format("Student: id: %s, firstName: %s, lastName: %s, age: %s", studentId, firstName, lastName,
            age);
  }

  public boolean isEligabaleStudent() {
    return Integer.parseInt(age) > 12;
  }
}
