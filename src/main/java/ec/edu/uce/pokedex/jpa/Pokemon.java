package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;
import java.util.List;

/**
 * Clase Pokemon
 *
 * Representa la entidad Pokémon en la base de datos.
 * Esta clase mapea la tabla "Pokemon" y define las relaciones con otras entidades como Types, Habitat, Region y Abilities.
 */
@Entity
@Table(name = "Pokemon")
public class Pokemon {

    @Id
    @Column(name = "id")
    private int id; // Identificador único del Pokémon.

    @Column(name = "pokemon_name")
    private String name; // Nombre del Pokémon.

    @Column(name = "pokemon_heigth")
    private int height; // Altura del Pokémon.

    @Column(name = "pokemon_weight")
    private int weight; // Peso del Pokémon.

    @Column(name = "pokemon_stat_hp")
    private double stats_hp; // Puntos de salud (HP) del Pokémon.

    @Column(name = "pokemon_stat_attack")
    private double stats_attack; // Puntos de ataque del Pokémon.

    @Column(name = "pokemon_stat_defense")
    private double stats_defense; // Puntos de defensa del Pokémon.

    @Column(name = "pokemon_stat_special_attack")
    private double stats_special_attack; // Puntos de ataque especial del Pokémon.

    @Column(name = "pokemon_special_defense")
    private double stats_special_defense; // Puntos de defensa especial del Pokémon.

    @Column(name = "pokemon_stat_speed")
    private double stats_speed; // Puntos de velocidad del Pokémon.

    @Column(name = "pokemon_stat_accuracy")
    private double stats_accuracy; // Precisión del Pokémon.

    @Column(name = "pokemon_stat_evasion")
    private double stats_evasion; // Evasión del Pokémon.

    @ElementCollection
    @Column(name = "pokemon_envoles")
    private List<Integer> envoles; // Lista de IDs de las evoluciones del Pokémon.

    // Relación Many-to-Many con la entidad Types.
    @ManyToMany
    @JoinTable(
            name = "pokemon_types",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private List<Types> types; // Lista de tipos asociados al Pokémon.

    // Relación Many-to-One con la entidad Habitat.
    @ManyToOne
    @JoinColumn(name = "habitat_id")
    private Habitat habitat; // Hábitat asociado al Pokémon.

    // Relación Many-to-Many con la entidad Region.
    @ManyToMany
    @JoinTable(
            name = "pokemon_region",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private List<Region> regions; // Lista de regiones asociadas al Pokémon.

    // Relación Many-to-Many con la entidad Abilities.
    @ManyToMany
    @JoinTable(
            name = "pokemon_abilities",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "ability_id")
    )
    private List<Abilities> abilities; // Lista de habilidades asociadas al Pokémon.

    /**
     * Constructor por defecto.
     */
    public Pokemon() {
    }

    // Métodos getters y setters para cada atributo.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getStats_hp() {
        return stats_hp;
    }

    public void setStats_hp(double stats_hp) {
        this.stats_hp = stats_hp;
    }

    public double getStats_attack() {
        return stats_attack;
    }

    public void setStats_attack(double stats_attack) {
        this.stats_attack = stats_attack;
    }

    public double getStats_defense() {
        return stats_defense;
    }

    public void setStats_defense(double stats_defense) {
        this.stats_defense = stats_defense;
    }

    public double getStats_special_attack() {
        return stats_special_attack;
    }

    public void setStats_special_attack(double stats_special_attack) {
        this.stats_special_attack = stats_special_attack;
    }

    public double getStats_special_defense() {
        return stats_special_defense;
    }

    public void setStats_special_defense(double stats_special_defense) {
        this.stats_special_defense = stats_special_defense;
    }

    public double getStats_speed() {
        return stats_speed;
    }

    public void setStats_speed(double stats_speed) {
        this.stats_speed = stats_speed;
    }

    public double getStats_accuracy() {
        return stats_accuracy;
    }

    public void setStats_accuracy(double stats_accuracy) {
        this.stats_accuracy = stats_accuracy;
    }

    public double getStats_evasion() {
        return stats_evasion;
    }

    public void setStats_evasion(double stats_evasion) {
        this.stats_evasion = stats_evasion;
    }

    public List<Integer> getEnvoles() {
        return envoles;
    }

    public void setEnvoles(List<Integer> envoles) {
        this.envoles = envoles;
    }

    public List<Types> getTypes() {
        return types;
    }

    public void setTypes(List<Types> types) {
        this.types = types;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public List<Abilities> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Abilities> abilities) {
        this.abilities = abilities;
    }
}