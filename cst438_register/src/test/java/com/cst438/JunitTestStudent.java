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
	
	@Test
	public void addNewStudent() throws Exception {
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setStudent_id(TEST_STUDENT_ID);
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/add/{email}/{name}", TEST_STUDENT_EMAIL, TEST_STUDENT_NAME))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);
		
		assertEquals(TEST_STUDENT_ID, studentDTO.student_id);
		assertEquals(TEST_STUDENT_NAME, studentDTO.name);
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);


		//assertEquals(student.getEmail(), studentDTO.email);
		//assertEquals(true, found, "Student already exists yet");
		verify(studentRepository).save(any(Student.class));
				// verify that repository find method was called.
	    verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);

		System.out.println("Student added: " + studentDTO);

	}
	
	@Test
	public void addHoldtoStudent()  throws Exception {
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatus(TEST_STUDENT_STATUS1);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE1);
		student.setStudent_id(TEST_STUDENT_ID);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/addhold/{email}", TEST_STUDENT_EMAIL))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);
		
		assertEquals(TEST_STUDENT_ID, studentDTO.student_id);
		assertEquals(TEST_STUDENT_NAME, studentDTO.name);
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);
		assertEquals(TEST_STUDENT_STATUS1, studentDTO.status);
		assertEquals(TEST_STUDENT_STATUS_CODE1, studentDTO.statusCode);

		verify(studentRepository).save(any(Student.class));
	    verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);

		System.out.println("Added hold to found user: " + studentDTO);
	}
	
	@Test
	public void deleteHold() throws Exception{
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatus(TEST_STUDENT_STATUS0);
		student.setStatusCode(TEST_STUDENT_STATUS_CODE0);
		student.setStudent_id(TEST_STUDENT_ID);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/deletehold/{email}", TEST_STUDENT_EMAIL))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertEquals(TEST_STUDENT_ID, studentDTO.student_id);
		assertEquals(TEST_STUDENT_NAME, studentDTO.name);
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);
		assertEquals(TEST_STUDENT_STATUS0, studentDTO.status);
		assertEquals(TEST_STUDENT_STATUS_CODE0, studentDTO.statusCode);
		
		verify(studentRepository).save(any(Student.class));
	    verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);

		System.out.println("Removed hold from found user: " + studentDTO);
	}
		

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
  


}
