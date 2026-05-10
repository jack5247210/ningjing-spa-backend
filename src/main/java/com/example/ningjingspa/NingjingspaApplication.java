package com.example.ningjingspa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NingjingspaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NingjingspaApplication.class, args);
	}

}
