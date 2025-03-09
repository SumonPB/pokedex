package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Abilities;
import ec.edu.uce.pokedex.repositories.AbilitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Clase AbilitiesService
 *
 * Servicio de Spring que gestiona las operaciones relacionadas con la entidad Abilities.
 * Proporciona métodos para buscar, guardar y filtrar Abilities en la base de datos.
 * Utiliza el repositorio AbilitiesRepository para interactuar con la capa de persistencia.
 */
@Service
public class AbilitiesService {

    private final AbilitiesRepository abilitiesRepository;

    @Autowired
    public AbilitiesService(AbilitiesRepository abilitiesRepository) {
        this.abilitiesRepository = abilitiesRepository;
    }

    /**
     * Guarda un Abilities en la base de datos.
     *
     * @param abilities Objeto Types que se desea guardar.
     */
    public void saveAbilities (Abilities abilities) {
        abilitiesRepository.save(abilities);
    }
    /**
     * Busca un Abilities por su ID.
     *
     * @param id ID del Abilities que se desea buscar.
     * @return el abilities si se encuentra, o vacío si no existe.
     */
    public Abilities findById(int id) {
        return abilitiesRepository.findById(id).get();
    }
}
