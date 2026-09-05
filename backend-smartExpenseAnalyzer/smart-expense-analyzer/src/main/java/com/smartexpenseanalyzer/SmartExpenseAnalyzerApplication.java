package com.smartexpenseanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartExpenseAnalyzerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartExpenseAnalyzerApplication.class, args);
		System.err.print(" smart expense analyzer started !!");
	}

}
