package com.greencredits.actionintakeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ActionIntakeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActionIntakeServiceApplication.class, args);
	}

}
