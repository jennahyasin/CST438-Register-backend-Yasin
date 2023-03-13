package com.cst438.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.cst438.domain.StudentRepository;
import com.cst438.domain.Student;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	@Autowired
	StudentRepository studentRepository;
	
	/*
	 * adding a new student to the system
	 */
	//TODO: make sure only admins can do this 
	@PostMapping("/student/add/{email}/{name}")
	@Transactional
	public Student addNewStudent(@PathVariable String email,  @PathVariable String name)
	{
		Student newStudent = studentRepository.findByEmail(email);
		if(newStudent != null) {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student with this email address already exists in the system: " + email);
		}
		else {
			newStudent = new Student();
			newStudent.setName(name);
			newStudent.setEmail(email);
			
			studentRepository.save(newStudent);
			
			return newStudent;
		}
	}
	
	/*
	 * Adding hold on student registration
	 * Status Codes: 0 - No Hold
	 * 				 1 - On Hold
	 */
	@PostMapping("/student/addhold/{email}")
	@Transactional
	public Student addHoldtoStudent(@PathVariable String email)
	{
		Student student = studentRepository.findByEmail(email);		
		if(student == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student does not exist/not registered: " + email);
		}
		else {
			student.setStatusCode(1);
			student.setStatus("ON HOLD");
			studentRepository.save(student);
			return student;
		}
	}
	
	/*
	 * Deleting (updating) hold on student registration
	 * Status Codes: 0 - No Hold
	 * 				 1 - On Hold
	 */
	@PutMapping("/student/deletehold/{email}")
	@Transactional
	public Student deleteHold(@PathVariable String email) {
		
		Student student = studentRepository.findByEmail(email);
		if(student == null) {
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST,"Student is not registered: " + email);
		}
		String currentStatus = student.getStatus();
		
		if(currentStatus == null)
		{
			throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not on hold: " + email);
		}
		else {
			student.setStatusCode(0);
			student.setStatus(null);
			studentRepository.save(student);
			return student;
		}
	}

}


