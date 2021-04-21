package com.example.snmpreport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@RestController
public class DemoApplication {
	private static final Logger log = LoggerFactory.getLogger(DemoApplication.class);


	private static final ScanSnmp scanSnmp = new ScanSnmp();

	public static void main(String[] args) {
		System.out.println(Arrays.toString(args));
		SpringApplication.run(DemoApplication.class, args);
	}
}