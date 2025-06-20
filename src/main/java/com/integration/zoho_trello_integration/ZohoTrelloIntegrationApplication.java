package com.integration.zoho_trello_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class ZohoTrelloIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZohoTrelloIntegrationApplication.class, args);
	}

}
