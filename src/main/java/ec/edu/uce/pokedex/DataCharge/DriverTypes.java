package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import ec.edu.uce.pokedex.Service.TypesService;
import ec.edu.uce.pokedex.jpa.Types;
import ec.edu.uce.pokedex.repositories.TypesRepository;
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
 * Clase DriverTypes
 *
 * Servicio de Spring que se encarga de obtener y procesar datos de regiones de Pokémon desde la API pública de PokeAPI.
 * Utiliza concurrencia para mejorar el rendimiento al realizar múltiples solicitudes HTTP en paralelo.
 * Los datos obtenidos se almacenan en una base de datos mediante el servicio TypesService.
 */
@Service
public class DriverTypes {
    private final ExecutorService executorService;
    private CargaDatosListener cargaDatosMoveListener; // Observer

    @Autowired
    private TypesService typesService;


    /**
     * Constructor de la clase.
     * Inicializa el ExecutorService con un pool de 10 hilos.
     */
    public DriverTypes() {
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
     * Método principal que inicia la carga de datos de tipos.
     * Realiza una solicitud a la API para obtener la lista de tipos y luego procesa cada tipo de manera concurrente.
     */
    public void ejecutar() {
        // Consulta los tipos de Pokémon desde la API
        JSONObject typesData = obtenerTipos();

        if (typesData != null) {
            // Extraer la lista de tipos del JSON obtenido.
            JSONArray types = typesData.getJSONArray("results");
            List<JSONObject> typeList = Stream.iterate(0, i -> i + 1)
                    .limit(types.length())
                    .map(types::getJSONObject)
                    .collect(Collectors.toList());

            // Procesar en cada tipo en paralelo
            typeList.stream().parallel().forEach(tipo->executorService.execute(()->{
                // Crear un nuevo objeto Type y asignar datos básicos.
                Types newType = new Types(typeList.indexOf(tipo) + 1,tipo.optString("name"));
                // Guardar los tipos en la base de datos.
                typesService.saveTypes(newType);
                    }));
            // Cerrar el pool de hilos y esperar a ue finalicen
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
            System.out.println("No se pudo obtener información de los tipos.");
        }
    }

    /**
     * Obtiene los datos de los tipos desde la API de PokeAPI.
     *
     * @return JSONObject con los datos de los tipos, o null si ocurre un error.
     */
    public static JSONObject obtenerTipos() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/type");
    }


    /**
     * Realiza una solicitud HTTP GET a la URL proporcionada y devuelve los datos en formato JSONObject.
     *
     * @param url URL de la API a la que se realizará la solicitud.
     * @return JSONObject con los datos obtenidos, o null si ocurre un error.
     */
    public static JSONObject obtenerDatosDeUrl(String url) {
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
