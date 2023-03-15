package com.cst438.domain;

//Data transfer object class for Student.java 
public class StudentDTO {
	//Public variables from Student.java class
	public int student_id;
	public String name;
	public String email;
	public int statusCode;
	public String status;
	
	@Override
	public String toString() {
		return "StudentDTO [student_id= " + student_id +", name= " + name 
				+ ", email= " + email + ", statusCode = " + statusCode 
				+ ", status= " + status + "]";
	}
}
