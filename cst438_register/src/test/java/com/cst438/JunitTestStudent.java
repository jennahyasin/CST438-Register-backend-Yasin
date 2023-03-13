package com.cst438;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
	public static final String TEST_STUDENT_EMAIL = "123";
	public static final String TEST_STUDENT_NAME  = "test";
	
	@MockBean
	StudentRepository studentRepository;
	

	@Autowired
	private MockMvc mvc;
	
	@Test
	void addNewStudent() throws Exception {
		MockHttpServletResponse response;
		
		Student student = new Student();
		student.setName(TEST_STUDENT_NAME);
		student.setEmail(TEST_STUDENT_NAME);

		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		given(studentRepository.save(any(Student.class))).willReturn(student);
		
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.post("/student/add/{email}/{name}", TEST_STUDENT_EMAIL, TEST_STUDENT_NAME))
				.andReturn().getResponse();
				
		StudentDTO studentDTO = fromJsonString(response.getContentAsString(), StudentDTO.class);

		System.out.println(studentDTO);
		assertEquals(200, response.getStatus());
		assertEquals(TEST_STUDENT_EMAIL, studentDTO.email);
		
		verify(studentRepository).save(any(Student.class));

		//verify(studentRepository, times(1)).findByEmail(TEST_STUDENT_EMAIL);
		}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
