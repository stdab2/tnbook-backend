package com.tnbook.tnbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class TnbookApplication {

	public static void main(String[] args) {
		SpringApplication.run(TnbookApplication.class, args);
	}

}
