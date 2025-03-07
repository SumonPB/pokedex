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

        PokemonDTO pokemon = null;

        if (name != null && !name.isEmpty()) {
            // Buscar Pokémon por nombre
            pokemon = pokemonDTO.buscarByName(name);
        } else if (id != null) {
            // Buscar Pokémon por ID
            pokemon = pokemonDTO.pokemon(id);
        } else {
            // Si no hay parámetros, mostrar por defecto el Pokémon con ID 1
            pokemon = pokemonDTO.pokemon(1);
        }

        // Manejar el caso en el que no se encuentre el Pokémon
        if (pokemon == null) {
            model.addAttribute("error", "No se encontró el Pokémon solicitado.");
            return "error"; // Vista de error
        }

        // Calcular prevId y nextId
        int currentId = pokemon.getId(); // Asume que PokemonDTO tiene un método getId()
        int prevId = Math.max(1, currentId - 1);
        int nextId = currentId + 1;

        // Verificar si prevId y nextId existen en la base de datos
        if (pokemonDTO.pokemon(prevId) == null) {
            prevId = currentId; // Si no existe, mantener el ID actual
        }
        if (pokemonDTO.pokemon(nextId) == null) {
            nextId = currentId; // Si no existe, mantener el ID actual
        }

        // Agregar atributos al modelo
        model.addAttribute("pokemon", pokemon);
        model.addAttribute("prevId", prevId);
        model.addAttribute("nextId", nextId);

        return "index";
    }
}
