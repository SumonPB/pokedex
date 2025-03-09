package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

import java.util.List;
/**
 * Clase Region
 *
 * Representa la entidad Region en la base de datos..
 *  * Esta clase mapea la tabla "Region" y define las relacion con Pokemon
 */
@Entity
@Table (name = "Region")
public class Region {

    @Id @Column (name = "id")
    private int id; // Identificador único de la region
    @Column (name = "name_region")
    private String name; // Nombre de la region

    //Relacion Many-To-Many con la entidad Pokemon
    @ManyToMany(mappedBy = "regions")
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
    public Region() { }

    public Region(int id, String name) {
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
