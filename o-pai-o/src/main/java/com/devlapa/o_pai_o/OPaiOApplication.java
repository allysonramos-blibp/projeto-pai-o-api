package com.devlapa.o_pai_o;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OPaiOApplication {

	public static void main(String[] args) {
		SpringApplication.run(OPaiOApplication.class, args);
	}

}
