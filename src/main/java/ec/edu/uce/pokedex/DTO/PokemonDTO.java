package ec.edu.uce.pokedex.DTO;
import ec.edu.uce.pokedex.DAO.PokemonDAO;

import ec.edu.uce.pokedex.jpa.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PokemonDTO {

    @Autowired
    private PokemonDAO pokemonDAO;


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
    private List<PokemonDTO> pokemons;

    public List<PokemonDTO> getPokemons() {
        return pokemons;
    }
    public void setPokemons(List<PokemonDTO> pokemons) {
        this.pokemons = pokemons;
    }
    public PokemonDTO() {
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

    public PokemonDTO pokemon(int id){
        PokemonDTO pokemonDTO = pokemonDAO.buscarPokemon(id);
        return pokemonDTO;
    }
    public PokemonDTO buscarByName(String name) {
        PokemonDTO pokemonDTO = pokemonDAO.buscarByNombre(name);
        return pokemonDTO;
    }
    public List<PokemonDTO> findPokemonsByFilters(String type, String region, String ability, String habitat){

        List<PokemonDTO> pokemonDTO = pokemonDAO.findPokemonsByFilters(type, region, ability, habitat);
        return pokemonDTO;
    }

}
