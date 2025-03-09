package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Region;
import ec.edu.uce.pokedex.repositories.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Clase RegionService
 *
 * Servicio de Spring que gestiona las operaciones relacionadas con la entidad Region.
 * Proporciona métodos para buscar, guardar y filtrar Region en la base de datos.
 * Utiliza el repositorio RegionRepository para interactuar con la capa de persistencia.
 */
@Service
public class RegionService {

    private final RegionRepository regionRepository;

    @Autowired
    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    /**
     * Guarda un Region en la base de datos.
     *
     * @param region Objeto Region que se desea guardar.
     */
    public void saveRegion(Region region) {
        regionRepository.save(region);
    }
    /**
     * Busca una Region por su ID.
     *
     * @param id ID del Region que se desea buscar.
     * @return la Region si se encuentra, o vacío si no existe.
     */
    public Region findById(int id) {
        return regionRepository.findById(id).get();
    }
}
