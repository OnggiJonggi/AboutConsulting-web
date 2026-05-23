package com.axaboutconsulting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AboutConsultingWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(AboutConsultingWebApplication.class, args);
	}

}
