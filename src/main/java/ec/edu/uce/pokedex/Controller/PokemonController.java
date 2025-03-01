package ec.edu.uce.pokedex.Controller;

import ec.edu.uce.pokedex.DTO.PokemonDTO;
import ec.edu.uce.pokedex.Service.CargarDatos;
import ec.edu.uce.pokedex.jpa.Pokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/pokedex")
public class PokemonController {


    @Autowired
    PokemonDTO pokemonDTO;
    @Autowired
    CargarDatos cargarDatos;

    @GetMapping("/pokemon/filter")
    public String filterPokemons(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "ability", required = false) String ability,
            @RequestParam(name = "habitat", required = false) String habitat,
            Model model) {

        List<PokemonDTO> filteredPokemons = pokemonDTO.findPokemonsByFilters(type, region, ability, habitat);
        model.addAttribute("pokemons", filteredPokemons);

        return "pokemonList";
    }

    @GetMapping("/cargarDatos")
    public String cargarDatos() {
        cargarDatos.cargar(); // Solo carga si no hay datos
        return "";
    }


    @GetMapping("/pokemon")
    public String getPokemon(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "name", required = false) String name,
            Model model) {

        if (name != null && !name.isEmpty()) {
            // Buscar Pokémon por nombre (suponiendo que tienes un método para ello)
            model.addAttribute("pokemon", pokemonDTO.buscarByName(name));
        } else if (id != null) {
            model.addAttribute("pokemon", pokemonDTO.pokemon(id));
        } else {
            // Si no hay parámetros, mostrar por defecto el Pokémon con ID 1
            model.addAttribute("pokemon", pokemonDTO.pokemon(1));
        }

        model.addAttribute("prevId", id != null ? Math.max(1, id - 1) : 1);
        model.addAttribute("nextId", id != null ? id + 1 : 2);
        return "index";
    }
}
