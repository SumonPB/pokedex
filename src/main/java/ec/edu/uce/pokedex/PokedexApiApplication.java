package ec.edu.uce.pokedex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "ec.edu.uce.pokedex.DataCharge", "ec.edu.uce.pokedex.repositories", "ec.edu.uce.pokedex.Service", "ec.edu.uce.pokedex.DTO", "ec.edu.uce.pokedex.DAO", "ec.edu.uce.pokedex.Controller"})
public class PokedexApiApplication {


	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);
	}


}
