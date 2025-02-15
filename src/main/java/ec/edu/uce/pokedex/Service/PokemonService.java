package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PokemonService {

    @Autowired
    private PokemonRepository pokemonRepository;

    public List<Integer> findAllPokemonIds() {
        return pokemonRepository.findAllPokemonIds();
    }

    public void savePokemon(Pokemon pokemon) {
        pokemonRepository.save(pokemon);
    }

    public Optional<Pokemon> findById(int id) {
        return Optional.ofNullable(pokemonRepository.findById(id));
    }


    public Optional<Pokemon> findByIdAndLoadHabitatAndRegions(int id){
        return pokemonRepository.findByIdAndLoadHabitatAndRegions(id);
    }

    public List<Pokemon> findPokemonsByFilters(String type,String region,String ability,String habitat) {
        return pokemonRepository.findPokemonsByFilters(type, region, ability, habitat);
    }
    public long nPokemones(){
        return pokemonRepository.count();
    }

}