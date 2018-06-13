package com.tesco.SpringBatchQuartzExample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class })
public class SpringBatchQuartzExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchQuartzExampleApplication.class, args);
	}
}
