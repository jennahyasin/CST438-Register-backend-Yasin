package com.cst438;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;



import com.cst438.controller.StudentController;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
class JunitTestStudent {

	//data that will be used in tests
	static final String URL = "http://localhost:8080";
	public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test";
	public static final String TEST_STUDENT_STATUS1 = "ON HOLD";
	public static final int TEST_STUDENT_STATUS_CODE1 = 1;
	public static final String TEST_STUDENT_STATUS0 = null;
	public static final int TEST_STUDENT_STATUS_CODE0 = 0;
	public static final int TEST_STUDENT_ID = 1;
	
	
	@MockBean
	StudentRepository studentRepository;
	

	@Autowired
	private MockMvc mvc;
	
	//Test for adding a new student function 
	@Test
	public void addNewStudent() throws Exception {
		MockHttpServletResponse response;
		
		//create student object
		Student student = new Student();
		
		//set all data to the test data
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStudent_id(TEST_STUDENT_ID);
		
		//expecting that the findByEmail will return null for student email to ensure no duplicates
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		//expecting to return the created student to make sure save worked
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		//mock for adding a student
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/add/{email}/{name}", TEST_STUDENT_EMAIL, TEST_STUDENT_NAME))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		//data transfer object to read the Json response
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);
		
		//comparing that the returned data is the entered (test) data
		assertEquals(TEST_STUDENT_ID, studentDTO.student_id);
		assertEquals(TEST_STUDENT_NAME, studentDTO.name);
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);

		//verify that the save method was called to save the new student
		verify(studentRepository).save(any(Student.class));
		// verify that repository find method was called.
	   	verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);

		System.out.println("Student added: " + studentDTO);

	}
	
	//Test for adding a hold to a student method
	@Test
	public void addHoldtoStudent()  throws Exception {
		MockHttpServletResponse response;
		
		//Creates a new student object
		Student student = new Student();
		//sets all of the student's data to the test data
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatus(TEST_STUDENT_STATUS1);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE1);
		student.setStudent_id(TEST_STUDENT_ID);
		
		//assumes that will return student to ensure a student was found with given inpuut
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		//assumes that the hold will be saved in the repository
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		
		//mock for testing the function
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/addhold/{email}", TEST_STUDENT_EMAIL))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		//creats a data transfer object to get json response from server
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);
		
		//compares that the data entered acutally appears in the json response
		assertEquals(TEST_STUDENT_ID, studentDTO.student_id);
		assertEquals(TEST_STUDENT_NAME, studentDTO.name);
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);
		assertEquals(TEST_STUDENT_STATUS1, studentDTO.status);
		assertEquals(TEST_STUDENT_STATUS_CODE1, studentDTO.statusCode);

		//verifies that the save function is called and changes are updated
		verify(studentRepository).save(any(Student.class));
		//checks that the student was found
	    	verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);

		System.out.println("Added hold to found user: " + studentDTO);
	}
	
	//Test for deleting a hold from a student
	@Test
	public void deleteHold() throws Exception{
		MockHttpServletResponse response;
		
		//Creates a new student object 
		Student student = new Student();
		//sets the new student to all of the test data
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatus(TEST_STUDENT_STATUS0);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE0);
		student.setStudent_id(TEST_STUDENT_ID);
		
		//Assuming that the user exits, findByEmail will return the student of the inputted test email
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		//Assuming that the changes were saved
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		//mock request for testing the funcion
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/deletehold/{email}", TEST_STUDENT_EMAIL))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		//creates a data transfer object of the mock response
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);
		
		//compares the test data to the actual response data to ensure it is actually inputted
		assertEquals(TEST_STUDENT_ID, studentDTO.student_id);
		assertEquals(TEST_STUDENT_NAME, studentDTO.name);
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);
		assertEquals(TEST_STUDENT_STATUS0, studentDTO.status);
		assertEquals(TEST_STUDENT_STATUS_CODE0, studentDTO.statusCode);
		
		//verifies that the updated student is saved to the repository
		verify(studentRepository).save(any(Student.class));
		//verifies that the student with the main changes was found
	    	verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);

		System.out.println("Removed hold from found user: " + studentDTO);
	}
		
	//helper function to grapb the json response data
	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
  


}
