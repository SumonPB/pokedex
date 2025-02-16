package ec.edu.uce.pokedex.Controller;

import ec.edu.uce.pokedex.DTO.PokemonDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pokedex")
public class PokemonController {
    @Autowired
    PokemonDTO pokemonDTO;

    @GetMapping("/pokemon")
    public String getPokemon(Model model) {

        model.addAttribute("pokemon", pokemonDTO.pokemon(2));


        return "index"; // Carga el archivo index.html
    }


}
