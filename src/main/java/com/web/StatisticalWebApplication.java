package com.web;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatisticalWebApplication {

	public static void main(String[] args) throws Exception {

		Output output = new Output();
		output.startClassifier();
		//SpringApplication.run(StatisticalWebApplication.class, args);
	}

}
