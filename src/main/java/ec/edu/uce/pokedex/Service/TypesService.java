package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Types;
import ec.edu.uce.pokedex.repositories.TypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Clase TypesService
 *
 * Servicio de Spring que gestiona las operaciones relacionadas con la entidad Types.
 * Proporciona métodos para buscar, guardar y filtrar Types en la base de datos.
 * Utiliza el repositorio TypesRepository para interactuar con la capa de persistencia.
 */
@Service
public class TypesService {

    @Autowired
    private  TypesRepository typesRepository;

    public TypesService(TypesRepository typesRepository) {
        this.typesRepository = typesRepository;
    }

    /**
     * Guarda un Tipo en la base de datos.
     *
     * @param types Objeto Types que se desea guardar.
     */
    public void saveTypes(Types types) {
        typesRepository.save(types);
    }

    /**
     * Busca un Types por su ID.
     *
     * @param id ID del Type que se desea buscar.
     * @return el type si se encuentra, o vacío si no existe.
     */
    public Types findById(int id) {
        return typesRepository.findById(id).get();
    }
}
