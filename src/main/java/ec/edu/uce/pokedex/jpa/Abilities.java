package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

import java.util.List;

/**
 * Clase Abilities
 *
 * Representa la entidad Abilities en la base de datos..
 *  * Esta clase mapea la tabla "Abilities" y define las relacion con Pokemon
 */
@Entity
@Table (name = "Abilities")
public class Abilities {

    @Id @Column (name = "id")
    private int id; // Identificador único de la habilidad
    @Column (name = "name_abilities")
    private String name; // Nombre de la habilidad
    @ManyToMany(mappedBy = "abilities")
    // Relación Many-to-Many con la entidad Pokemon
    private List<Pokemon> pokemones;

    public List<Pokemon> getPokemones() {
        return pokemones;
    }

    public void setPokemones(List<Pokemon> pokemones) {
        this.pokemones = pokemones;
    }
    /**
     * Constructor por defecto.
     */
    public Abilities() { }

    public Abilities(int id, String name) {
        this.id = id;
        this.name = name;
    }

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
}
