package co.edu.unbosque.detectia;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DetectiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DetectiaApplication.class, args);
	}

	 @Bean
	    public ModelMapper getModelMapper() {
	        return new ModelMapper();
	    }
}
