package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.jpa.Abilities;
import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.repositories.HabitatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitatService {

    private final HabitatRepository habitatRepository;

    @Autowired
    public HabitatService(HabitatRepository habitatRepository) {
        this.habitatRepository = habitatRepository;
    }

    public void saveHabitad(Habitat habitat) {
        habitatRepository.save(habitat);
    }
    public Habitat findById(int id) {
        return habitatRepository.findById(id).get();
    }
}
