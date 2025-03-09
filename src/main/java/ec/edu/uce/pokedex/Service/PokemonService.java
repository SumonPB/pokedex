package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Clase PokemonService
 *
 * Servicio de Spring que gestiona las operaciones relacionadas con la entidad Pokémon.
 * Proporciona métodos para buscar, guardar y filtrar Pokémon en la base de datos.
 * Utiliza el repositorio PokemonRepository para interactuar con la capa de persistencia.
 */
@Service
public class PokemonService {

    @Autowired
    private PokemonRepository pokemonRepository; // Repositorio para acceder a los datos de Pokémon.

    /**
     * Obtiene una lista de todos los IDs de Pokémon almacenados en la base de datos.
     *
     * @return Lista de enteros que representan los IDs de los Pokémon.
     */
    public List<Integer> findAllPokemonIds() {
        return pokemonRepository.findAllPokemonIds();
    }

    /**
     * Guarda un Pokémon en la base de datos.
     *
     * @param pokemon Objeto Pokémon que se desea guardar.
     */
    public void savePokemon(Pokemon pokemon) {
        pokemonRepository.save(pokemon);
    }

    /**
     * Busca un Pokémon por su ID.
     *
     * @param id ID del Pokémon que se desea buscar.
     * @return Un Optional que contiene el Pokémon si se encuentra, o vacío si no existe.
     */
    public Optional<Pokemon> findById(int id) {
        return Optional.ofNullable(pokemonRepository.findById(id));
    }

    /**
     * Busca un Pokémon por su nombre.
     *
     * @param name Nombre del Pokémon que se desea buscar.
     * @return Un Optional que contiene el Pokémon si se encuentra, o vacío si no existe.
     */
    public Optional<Pokemon> findByName(String name) {
        return pokemonRepository.findByName(name);
    }

    /**
     * Busca un Pokémon por su ID y carga su hábitat y regiones relacionadas.
     *
     * @param id ID del Pokémon que se desea buscar.
     * @return Un Optional que contiene el Pokémon con su hábitat y regiones si se encuentra, o vacío si no existe.
     */
    public Optional<Pokemon> findByIdAndLoadHabitatAndRegions(int id) {
        return pokemonRepository.findByIdAndLoadHabitatAndRegions(id);
    }

    /**
     * Filtra Pokémon según los criterios proporcionados.
     *
     * @param type Tipo de Pokémon (opcional).
     * @param region Región del Pokémon (opcional).
     * @param ability Habilidad del Pokémon (opcional).
     * @param habitat Hábitat del Pokémon (opcional).
     * @return Lista de Pokémon que coinciden con los filtros aplicados.
     */
    public List<Pokemon> findPokemonsByFilters(String type, String region, String ability, String habitat) {
        return pokemonRepository.findPokemonsByFilters(type, region, ability, habitat);
    }

    /**
     * Obtiene el número total de Pokémon almacenados en la base de datos.
     *
     * @return Número total de Pokémon.
     */
    public long nPokemones() {
        return pokemonRepository.count();
    }
}