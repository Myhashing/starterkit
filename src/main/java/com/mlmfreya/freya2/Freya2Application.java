package com.mlmfreya.freya2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class freya2Application {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(freya2Application.class, args);
	}

}
