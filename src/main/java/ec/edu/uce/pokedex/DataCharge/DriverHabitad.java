package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import ec.edu.uce.pokedex.Service.HabitatService;
import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.repositories.HabitatRepository;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Clase DriverHabitad
 *
 * Servicio de Spring que se encarga de obtener y procesar datos de los habitats de Pokémon desde la API pública de PokeAPI.
 * Utiliza concurrencia para mejorar el rendimiento al realizar múltiples solicitudes HTTP en paralelo.
 * Los datos obtenidos se almacenan en una base de datos mediante el servicio HabitadService.
 */
@Service
public class DriverHabitad {
    private final ExecutorService executorService;
    private CargaDatosListener cargaDatosMoveListener; // Observer

    @Autowired
    private HabitatService habitatService;

    /**
     * Constructor de la clase.
     * Inicializa el ExecutorService con un pool de 10 hilos.
     */
    public DriverHabitad() {
        this.executorService = Executors.newFixedThreadPool(10);
    }

    /**
     * Establece un observador para notificar cuando la carga de datos ha finalizado.
     *
     * @param listener Implementación de CargaDatosListener que recibirá la notificación.
     */
    public void setCargaDatosListener(CargaDatosListener listener) {
        this.cargaDatosMoveListener = listener;
    }

    /**
     * Método principal que inicia la carga de datos de los habitats.
     * Realiza una solicitud a la API para obtener la lista de los habitats y luego procesa cada región de manera concurrente.
     */
    public void ejecutar() {
        // Consultar los hábitats de Pokémon desde la API
        JSONObject habitatData = obtenerHabitats();

        if (habitatData != null) {
            // Extraer la lista de los habitats del JSON obtenido
            JSONArray habitats = habitatData.getJSONArray("results");
            List<JSONObject> habitadList = Stream.iterate(0,i->i+1)
                    .limit(habitats.length())
                    .map(habitats::getJSONObject)
                    .collect(Collectors.toList());

            // Procesar cada habitat en paralelo.
            habitadList.stream().parallel().forEach(habita -> executorService.execute(()->{
                // Crear un nuevo objeto Habitat y asignar datos básicos.
                Habitat newHabitat = new Habitat(habitadList.indexOf(habita)+1,habita.optString("name"));
                // Guardar el habitat en la base de datos.
                habitatService.saveHabitad(newHabitat);
            }));

            // Cerrar el pool de hilos y esperar a que finalicen.
            executorService.shutdown();
            try {
                if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    // Notificar al observador que la carga de datos ha finalizado.
                    if (cargaDatosMoveListener != null) {
                        cargaDatosMoveListener.onCargaCompleta();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("No se pudo obtener información de los hábitats.");
        }
    }
    /**
     * Obtiene los datos de los habitats desde la API de PokeAPI.
     *
     * @return JSONObject con los datos de las habitats, o null si ocurre un error.
     */
    public JSONObject obtenerHabitats() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/pokemon-habitat");
    }

    /**
     * Realiza una solicitud HTTP GET a la URL proporcionada y devuelve los datos en formato JSONObject.
     *
     * @param url URL de la API a la que se realizará la solicitud.
     * @return JSONObject con los datos obtenidos, o null si ocurre un error.
     */
    public JSONObject obtenerDatosDeUrl(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity);
                return new JSONObject(jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
