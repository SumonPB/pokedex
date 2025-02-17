package ec.edu.uce.pokedex.DAO;

import ec.edu.uce.pokedex.DTO.PokemonDTO;
import ec.edu.uce.pokedex.Service.PokemonService;
import ec.edu.uce.pokedex.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PokemonDAO {
    @Autowired
    private PokemonService pokemonService;



    private int id;
    private String name;
    private int height;
    private int weight;
    private double stats_hp;
    private double stats_attack;
    private double stats_defense;
    private double stats_special_attack;
    private double stats_special_defense;
    private double stats_speed;
    private double stats_accuracy;
    private double stats_evasion;
    private List<Integer> envoles;  // Aquí no es necesario el @ManyToMany, ya que es una lista simple de enteros.
    private List<String> types;  // Relación Many-to-Many con la entidad Types
    private String habitat;  // Relación Many-to-One con Habitat
    private List<String> regions; // Relación Many-to-Many con Region
    private List<PokemonDAO> pokemons;

    public PokemonService getPokemonService() {
        return pokemonService;
    }

    public void setPokemonService(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    public List<PokemonDAO> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<PokemonDAO> pokemons) {
        this.pokemons = pokemons;
    }

    public PokemonDAO() {
    }

    public List<String> getRegions() {
        return regions;
    }
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }
    public String getHabitat() {
        return habitat;
    }
    public void setHabitat(String habitat) {
        this.habitat = habitat;
    }
    public List<String> getTypes() {
        return types;
    }
    public void setTypes(List<String> types) {
        this.types = types;
    }
    public List<Integer> getEnvoles() {
        return envoles;
    }
    public void setEnvoles(List<Integer> envoles) {
        this.envoles = envoles;
    }
    public double getStats_evasion() {
        return stats_evasion;
    }
    public void setStats_evasion(double stats_evasion) {
        this.stats_evasion = stats_evasion;
    }
    public double getStats_accuracy() {
        return stats_accuracy;
    }
    public void setStats_accuracy(double stats_accuracy) {
        this.stats_accuracy = stats_accuracy;
    }
    public double getStats_speed() {
        return stats_speed;
    }
    public void setStats_speed(double stats_speed) {
        this.stats_speed = stats_speed;
    }
    public double getStats_special_defense() {
        return stats_special_defense;
    }
    public void setStats_special_defense(double stats_special_defense) {
        this.stats_special_defense = stats_special_defense;
    }
    public double getStats_special_attack() {
        return stats_special_attack;
    }
    public void setStats_special_attack(double stats_special_attack) {
        this.stats_special_attack = stats_special_attack;
    }
    public double getStats_defense() {
        return stats_defense;
    }
    public void setStats_defense(double stats_defense) {
        this.stats_defense = stats_defense;
    }
    public double getStats_attack() {
        return stats_attack;
    }
    public void setStats_attack(double stats_attack) {
        this.stats_attack = stats_attack;
    }
    public double getStats_hp() {
        return stats_hp;
    }
    public void setStats_hp(double stats_hp) {
        this.stats_hp = stats_hp;
    }
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }



    public PokemonDAO toDAO(Optional<Pokemon> optionalPokemon) {
        if (optionalPokemon.isEmpty()) {
            return null;
        }

        Pokemon pokemon = optionalPokemon.get();
        PokemonDAO pokemonDAO = new PokemonDAO();

        pokemonDAO.setId(pokemon.getId());
        pokemonDAO.setName(pokemon.getName());
        pokemonDAO.setHeight(pokemon.getHeight());
        pokemonDAO.setWeight(pokemon.getWeight());

        // Stats
        pokemonDAO.setStats_evasion(pokemon.getStats_evasion());
        pokemonDAO.setStats_accuracy(pokemon.getStats_accuracy());
        pokemonDAO.setStats_speed(pokemon.getStats_speed());
        pokemonDAO.setStats_special_defense(pokemon.getStats_special_defense());
        pokemonDAO.setStats_special_attack(pokemon.getStats_special_attack());
        pokemonDAO.setStats_hp(pokemon.getStats_hp());
        pokemonDAO.setStats_attack(pokemon.getStats_attack());
        pokemonDAO.setStats_defense(pokemon.getStats_defense());

// Evoluciones
        List<PokemonDAO> envolesPokemon = new ArrayList<PokemonDAO>();

// Verifica si getEnvoles() es null antes de llamar a size()
        if (pokemon.getEnvoles() != null && !pokemon.getEnvoles().isEmpty()) {
            for (int i = 0; i < pokemon.getEnvoles().size(); i++) {
                Optional<Pokemon> pokemonEnvo = pokemonService.findById(pokemon.getEnvoles().get(i));
                if (pokemonEnvo.isPresent()) {
                    PokemonDAO envoleDAO = new PokemonDAO();
                    envoleDAO.setId(pokemonEnvo.get().getId());
                    envoleDAO.setName(pokemonEnvo.get().getName());
                    envolesPokemon.add(envoleDAO);
                }
            }
        }
        pokemonDAO.setEnvoles(pokemon.getEnvoles());
        pokemonDAO.setPokemons(envolesPokemon);
        //Tipos name
        List<String> tiposName = new ArrayList<>();
        for (Types types : pokemon.getTypes())
        {
            tiposName.add(types.getName());
        }
        pokemonDAO.setTypes(tiposName);

        //Segunda parte de carga
        Optional<Pokemon> pokemon2 = pokemonService.findByIdAndLoadHabitatAndRegions(pokemon.getId());
        if (pokemon2.isEmpty()) {
            return null;
        }
        Pokemon pokemon2Carg = pokemon2.get();
        List<String> regionNames = new ArrayList<>();
        if (pokemon2.get().getRegions().isEmpty()){
            regionNames.add("Ninguna");
        }else {
            for (Region region : pokemon2.get().getRegions()) {
                regionNames.add(region.getName());
            }
        }
        pokemonDAO.setRegions(regionNames);
        if (pokemon2.get().getHabitat() == null) {
            pokemonDAO.setHabitat("Ninguno");
        }else {
            pokemonDAO.setHabitat(pokemon2Carg.getHabitat().getName());
        }

        return pokemonDAO;
    }


    public PokemonDTO toDTO(PokemonDAO pokemonDAO){

        PokemonDTO pokemonDTO = new PokemonDTO();
        pokemonDTO.setId(pokemonDAO.getId());
        pokemonDTO.setName(pokemonDAO.getName());
        pokemonDTO.setHeight(pokemonDAO.getHeight());
        pokemonDTO.setWeight(pokemonDAO.getWeight());
        //stats
        pokemonDTO.setStats_evasion(pokemonDAO.getStats_evasion());
        pokemonDTO.setStats_accuracy(pokemonDAO.getStats_accuracy());
        pokemonDTO.setStats_speed(pokemonDAO.getStats_speed());
        pokemonDTO.setStats_special_defense(pokemonDAO.getStats_special_defense());
        pokemonDTO.setStats_special_attack(pokemonDAO.getStats_special_attack());
        pokemonDTO.setStats_hp(pokemonDAO.getStats_hp());
        pokemonDTO.setStats_attack(pokemonDAO.getStats_attack());
        pokemonDTO.setStats_defense(pokemonDAO.getStats_defense());
        //stats
        //evolucion
        List<PokemonDTO> envolesPokemon = new ArrayList<PokemonDTO>();
        for (int i = 0; i < pokemonDAO.getPokemons().size(); i++) {
            PokemonDTO envoleDTO = new PokemonDTO();
            envoleDTO.setId(pokemonDAO.getPokemons().get(i).getId());
            envoleDTO.setName(pokemonDAO.getPokemons().get(i).getName());
            envolesPokemon.add(envoleDTO);
        }
        pokemonDTO.setPokemons(envolesPokemon);
        pokemonDTO.setEnvoles(pokemonDAO.getEnvoles());
        //fin evolucion

        //envoles
        //types
        pokemonDTO.setTypes(pokemonDAO.getTypes());
        //types
        //regions
        pokemonDTO.setRegions(pokemonDAO.getRegions());
        //regions
        //habitad
        pokemonDTO.setHabitat(pokemonDAO.getHabitat());
        //habitad

        return pokemonDTO;
    }
//evoluciones
    //busqueda por ID
    public PokemonDTO buscarPokemon(int idPokemon){
        Optional<Pokemon> pokemon = pokemonService.findById(idPokemon);
       PokemonDTO pokemonDTO = toDTO(toDAO(pokemon));
        return pokemonDTO;
    }
    //Busqueda por Nombre
    public PokemonDTO buscarByNombre(String nombre){
        Optional<Pokemon> pokemon = pokemonService.findByName(nombre);
        PokemonDTO pokemonDTO = toDTO(toDAO(pokemon));
        return pokemonDTO;
    }

    public List<PokemonDTO> findPokemonsByFilters(String type, String region, String ability, String habitat) {

        if (type=="") {
            type=null;
        }
        if (region=="") {
            region=null;
        }
        if (ability=="") {
            ability=null;
        }
        if (habitat=="") {
            habitat=null;
        }

        // Obtener la lista de Pokémon sin necesidad de Optional
        List<Pokemon> pokemons = pokemonService.findPokemonsByFilters(type, region,null,habitat);
        // Verificar si la lista está vacía
        if (pokemons.isEmpty()) {
            return new ArrayList<>(); // Retorna una lista vacía si no hay Pokémon
        }

        // Convertir la lista de Pokémon a DTOs
        List<PokemonDTO> pokemonDTOs = new ArrayList<>();
        for (Pokemon pokemon : pokemons) {

            PokemonDTO pokemonDTO = toDTO(toDAO(Optional.ofNullable(pokemon)));
            pokemonDTOs.add(pokemonDTO);
        }

        return pokemonDTOs;
    }


}
