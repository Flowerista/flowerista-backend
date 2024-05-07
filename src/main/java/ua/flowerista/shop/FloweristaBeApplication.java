package ua.flowerista.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FloweristaBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FloweristaBeApplication.class, args);
	}

}
