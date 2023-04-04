package com.cst438;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;


@SpringBootTest
public class EndToEndStudentTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/jennahyasin/Downloads/chromedriver_mac64/chromedriver";
	
	public static final String URL = "http://localhost:3000";
	
//	public static final String TEST_STUDENT_EMAIL = "e2etest@csumb.edu";
	public static final String TEST_STUDENT_EMAIL = "e2etest@csumb.edu";

	
	public static final String TEST_STUDENT_NAME = "e2e Test";
	
	public static final int SLEEP_DURATION = 1000;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Test
	public void addStudentTest() throws Exception {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");	


        WebDriver driver = new ChromeDriver(ops);
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		try {
			
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			WebElement we = driver.findElement(By.xpath("(//input[@type='radio'])[last()]"));
			we.click();
 
			driver.findElement(By.xpath("//a[last()]")).click();
			Thread.sleep(SLEEP_DURATION);
			

			
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_STUDENT_EMAIL);
			Thread.sleep(SLEEP_DURATION);
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_STUDENT_NAME);
			Thread.sleep(SLEEP_DURATION);
			driver.findElement(By.xpath("//button[@id='submitButton']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			Student addedStudent = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
			assertNotNull(addedStudent, "Test student not added to database.");
		} catch (Exception ex) {
			throw ex;
		} 
		finally {
			driver.close();
			driver.quit();
		}
	}
	
	
}
