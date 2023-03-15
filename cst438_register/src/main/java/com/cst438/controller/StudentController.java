package com.cst438.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.Student;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	@Autowired
	StudentRepository studentRepository;
	
	/*
	 * adding a new student to the system
	 */
	@PostMapping("/student/add/{email}/{name}")
	@Transactional
	public StudentDTO addNewStudent(@PathVariable String email,  @PathVariable String name)
	{
		//Check if student exists in the system already
		Student newStudent = studentRepository.findByEmail(email);
		
		//If the student email already exists, throw error message.
		if(newStudent != null) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with this email address already exists in the system: " + email);
		}
		//If new student email doesn't already exist, create new student 
		else {
			newStudent = new Student();
			newStudent.setName(name);
			newStudent.setEmail(email);
			newStudent = studentRepository.save(newStudent);
			
			//Data transfer oject to display to server
			StudentDTO student = createStudentDTO(newStudent);
			
			return student;
		}
	}
	
	/*
	 * Adding hold on student registration
	 * Status Codes: 0 - No Hold
	 * 		 1 - On Hold
	 */
	@PutMapping("/student/addhold/{email}")
	@Transactional
	public StudentDTO addHoldtoStudent(@PathVariable String email)
	{
		//Find a student to add a hold to by their email 
		Student student = studentRepository.findByEmail(email);		
		
		//If email doesn't exist in the system, throw an error
		if(student == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student does not exist/not registered: " + email);
		}
		
		//If email exists in the system, update the status and status code to be ON HOLd and 1
		else {
			student.setStatusCode(1);
			student.setStatus("ON HOLD");
			studentRepository.save(student);

			//data transfer object to display to server
			StudentDTO studentDTO = createStudentDTO(student);
			return studentDTO;
		}
	}
	
	/*
	 * Deleting (updating) hold on student registration
	 * Status Codes: 0 - No Hold
	 * 		 1 - On Hold
	 */
	@PutMapping("/student/deletehold/{email}")
	@Transactional
	public StudentDTO deleteHold(@PathVariable String email) {
		
		//Finds a student to delete a hold by their email
		Student student = studentRepository.findByEmail(email);
		
		//If the email is not found, throw an error
		if(student == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST,"Student is not registered: " + email);
		}
		
		//If email is found, update the status and status code to be NULL and 0. 
		else {
			student.setStatusCode(0);
			student.setStatus(null);
			studentRepository.save(student);

			//Create student data object to display to server
			StudentDTO studentDTO = createStudentDTO(student);
			return studentDTO;
		}
	}
	
	/*
	 * Helper Function
	 * For creating student data transfer object
	 */
	private StudentDTO createStudentDTO(Student student)
	{
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.student_id = student.getStudent_id();
		studentDTO.name = student.getName();
		studentDTO.email =student.getEmail();
		studentDTO.statusCode = student.getStatusCode();
		studentDTO.status = student.getStatus();

		return studentDTO;
	}
}

