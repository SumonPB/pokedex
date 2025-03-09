package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

import java.util.List;

/**
 * Clase Types
 *
 * Representa la entidad Types en la base de datos..
 *  * Esta clase mapea la tabla "Types" y define las relacion con Pokemon
 */
@Entity
@Table(name = "Types")
public class Types {

    @Id @Column(name = "id")
    private int id;// Identificador único del Tipo.
    @Column (name = "name_types")
    private String name; // Nombre del tipo
    // Relacion Many-To-Many con la entidad Pokemon
    @ManyToMany(mappedBy = "types")
    private List<Pokemon> pokemons;

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    /**
     * Constructor por defecto.
     */
    public Types() { }

    public Types(int id, String name) {
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
