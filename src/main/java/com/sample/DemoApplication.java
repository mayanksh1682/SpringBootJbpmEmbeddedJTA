package com.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
public class DemoApplication {

	@RequestMapping("/")
	String home() {
		return "<html><body><h1>Hello World From Spring Boot</h1></body></html>";		
	}
	
	@RequestMapping("/new")
	String newForm() {
		return "<h2> Create a new thing </h2>";		
	}
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
