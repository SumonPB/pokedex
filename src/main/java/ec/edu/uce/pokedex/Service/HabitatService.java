package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.repositories.HabitatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Clase HabitatService
 *
 * Servicio de Spring que gestiona las operaciones relacionadas con la entidad Habitat.
 * Proporciona métodos para buscar, guardar y filtrar Habitat en la base de datos.
 * Utiliza el repositorio HabitatRepository para interactuar con la capa de persistencia.
 */
@Service
public class HabitatService {

    private final HabitatRepository habitatRepository;

    @Autowired
    public HabitatService(HabitatRepository habitatRepository) {
        this.habitatRepository = habitatRepository;
    }

    /**
     * Guarda un Habitat en la base de datos.
     *
     * @param habitat Objeto Habitat que se desea guardar.
     */
    public void saveHabitad(Habitat habitat) {
        habitatRepository.save(habitat);
    }

    /**
     * Busca un Habitat por su ID.
     *
     * @param id ID del Habitat que se desea buscar.
     * @return el habitat si se encuentra, o vacío si no existe.
     */
    public Habitat findById(int id) {
        return habitatRepository.findById(id).get();
    }
}
