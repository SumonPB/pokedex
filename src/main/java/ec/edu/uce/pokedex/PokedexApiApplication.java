package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DTO.PokemonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "ec.edu.uce.pokedex.DataCharge", "ec.edu.uce.pokedex.repositories", "ec.edu.uce.pokedex.Service", "ec.edu.uce.pokedex.DTO", "ec.edu.uce.pokedex.DAO"})
public class PokedexApiApplication implements CommandLineRunner {

	//@Autowired
	//private MainWindow mainWindow;
	@Autowired
	PokemonDTO pokemonDTO;


	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (int i=1;i<10;i++)
		{
			pokemonDTO.pokemon(i);
		}

	}
}
