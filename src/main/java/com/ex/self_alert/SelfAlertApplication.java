package com.ex.self_alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class SelfAlertApplication {

	public static void main(String[] args) {
		SpringApplication.run(SelfAlertApplication.class, args);
	}

}
