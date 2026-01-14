package com.lilly.url_shortener;

import com.mongodb.client.MongoClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@SpringBootApplication
@EnableMongoAuditing
public class UrlShortenerApplication {


	public static void main(String[] args) {

		SpringApplication.run(UrlShortenerApplication.class, args);
	}


	// Inside your @SpringBootApplication class
	@Bean
	@Primary
	public SimpleMongoClientDatabaseFactory myCustomMongoFactory() {
		try{
			// HARDCODED CONNECTION STRING
			System.out.println("--- ATTEMPTING TO CONNECT TO MONGO ---");
			// 1. Create the client manually
			var client = MongoClients.create("mongodb+srv://lydatily_db_user:cGbUJ4IfLfKGZy0f@urls.cdprrnf.mongodb.net/urls?retryWrites=true&w=majority");

			// 2. Pass client + database name explicitly
			var factory = new SimpleMongoClientDatabaseFactory(client, "urls");
			System.out.println("--- CONNECTION FACTORY CREATED SUCCESSFULLY ---");
			return factory;

		}catch (Exception e) {
			System.err.println("!!! CRITICAL ERROR CREATING MONGO BEAN !!!");
			e.printStackTrace();
			throw e;
		}

	}


}
