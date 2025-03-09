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

/**
 * Clase PokemonController
 *
 * Controlador de Spring que maneja las solicitudes relacionadas con Pokémon.
 * Proporciona endpoints para filtrar Pokémon, cargar datos y obtener detalles de un Pokémon específico.
 */
@Controller
@RequestMapping("/pokedex")
public class PokemonController {

    @Autowired
    private PokemonDTO pokemonDTO; // DTO para interactuar con los datos de Pokémon.

    @Autowired
    private CargarDatos cargarDatos; // Servicio para cargar datos de Pokémon y sus entidades relacionadas.

    /**
     * Endpoint para filtrar Pokémon según los criterios proporcionados.
     *
     * @param type Tipo de Pokémon (opcional).
     * @param region Región del Pokémon (opcional).
     * @param ability Habilidad del Pokémon (opcional).
     * @param habitat Hábitat del Pokémon (opcional).
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra la lista de Pokémon filtrados.
     */
    @GetMapping("/pokemon/filter")
    public String filterPokemons(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "ability", required = false) String ability,
            @RequestParam(name = "habitat", required = false) String habitat,
            Model model) {

        // Filtrar Pokémon según los criterios proporcionados.
        List<PokemonDTO> filteredPokemons = pokemonDTO.findPokemonsByFilters(type, region, ability, habitat);
        model.addAttribute("pokemons", filteredPokemons);

        return "pokemonList"; // Vista que muestra la lista de Pokémon filtrados.
    }

    /**
     * Endpoint para cargar datos de Pokémon y sus entidades relacionadas.
     *
     * @return Cadena vacía (no redirige a ninguna vista).
     */
    @GetMapping("/cargarDatos")
    public String cargarDatos() {
        cargarDatos.cargar(); // Inicia la carga de datos.
        return ""; // No redirige a ninguna vista.
    }

    /**
     * Endpoint para obtener detalles de un Pokémon específico.
     *
     * @param id ID del Pokémon (opcional).
     * @param name Nombre del Pokémon (opcional).
     * @param model Modelo para pasar datos a la vista.
     * @return Nombre de la vista que muestra los detalles del Pokémon.
     */
    @GetMapping("/pokemon")
    public String getPokemon(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam(name = "name", required = false) String name,
            Model model) {

        PokemonDTO pokemon = null;

        if (name != null && !name.isEmpty()) {
            // Buscar Pokémon por nombre.
            pokemon = pokemonDTO.buscarByName(name);
        } else if (id != null) {
            // Buscar Pokémon por ID.
            pokemon = pokemonDTO.pokemon(id);
        } else {
            // Si no hay parámetros, mostrar por defecto el Pokémon con ID 1.
            pokemon = pokemonDTO.pokemon(1);
        }

        // Manejar el caso en el que no se encuentre el Pokémon.
        if (pokemon == null) {
            model.addAttribute("error", "No se encontró el Pokémon solicitado.");
            return "error"; // Vista de error.
        }

        // Calcular prevId y nextId para la navegación entre Pokémon.
        int currentId = pokemon.getId(); // Obtener el ID del Pokémon actual.
        int prevId = Math.max(1, currentId - 1); // ID del Pokémon anterior.
        int nextId = currentId + 1; // ID del siguiente Pokémon.

        // Verificar si prevId y nextId existen en la base de datos.
        if (pokemonDTO.pokemon(prevId) == null) {
            prevId = currentId; // Si no existe, mantener el ID actual.
        }
        if (pokemonDTO.pokemon(nextId) == null) {
            nextId = currentId; // Si no existe, mantener el ID actual.
        }

        // Agregar atributos al modelo.
        model.addAttribute("pokemon", pokemon);
        model.addAttribute("prevId", prevId);
        model.addAttribute("nextId", nextId);

        return "index"; // Vista que muestra los detalles del Pokémon.
    }
}