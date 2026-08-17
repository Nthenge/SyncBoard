package com.eclectics.collaboration.Tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollaborationToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollaborationToolApplication.class, args);
	}

}
