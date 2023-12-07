package com.dogukandinler.EmployeeCheckerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeCheckerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeCheckerServiceApplication.class, args);
		MathQuizApp.launch(MathQuizApp.class, args);
	}

}
