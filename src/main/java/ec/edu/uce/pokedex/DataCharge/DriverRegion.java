package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import ec.edu.uce.pokedex.Service.RegionService;
import ec.edu.uce.pokedex.jpa.Region;
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
 * Clase DriverRegion
 *
 * Servicio de Spring que se encarga de obtener y procesar datos de regiones de Pokémon desde la API pública de PokeAPI.
 * Utiliza concurrencia para mejorar el rendimiento al realizar múltiples solicitudes HTTP en paralelo.
 * Los datos obtenidos se almacenan en una base de datos mediante el servicio RegionService.
 */
@Service
public class DriverRegion {

    private final ExecutorService executor; // Pool de hilos para ejecutar tareas concurrentes.
    private CargaDatosListener cargaDatosMoveListener; // Observador para notificar cuando la carga de datos ha finalizado.

    @Autowired
    private RegionService regionService; // Servicio para gestionar la persistencia de regiones.

    /**
     * Constructor de la clase.
     * Inicializa el ExecutorService con un pool de 10 hilos.
     */
    public DriverRegion() {
        this.executor = Executors.newFixedThreadPool(10);
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
     * Método principal que inicia la carga de datos de regiones.
     * Realiza una solicitud a la API para obtener la lista de regiones y luego procesa cada región de manera concurrente.
     */
    public void ejecutar() {
        // Consultar las regiones de Pokémon desde la API.
        JSONObject regionData = obtenerRegion();

        if (regionData != null) {
            // Extraer la lista de regiones del JSON obtenido.
            JSONArray region = regionData.getJSONArray("results");
            List<JSONObject> regionList = Stream.iterate(0, i -> i + 1)
                    .limit(region.length())
                    .map(region::getJSONObject)
                    .collect(Collectors.toList());

            // Procesar cada región en paralelo.
            regionList.stream().parallel().forEach(regions -> executor.execute(() -> {
                // Crear un nuevo objeto Region y asignar datos básicos.
                Region newRegion = new Region(regionList.indexOf(regions) + 1, regions.optString("name"));
                // Guardar la región en la base de datos.
                regionService.saveRegion(newRegion);
            }));

            // Cerrar el pool de hilos y esperar a que finalicen.
            executor.shutdown();
            try {
                if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    // Notificar al observador que la carga de datos ha finalizado.
                    if (cargaDatosMoveListener != null) {
                        cargaDatosMoveListener.onCargaCompleta();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No se pudo obtener información de las regiones.");
        }
    }

    /**
     * Obtiene los datos de las regiones desde la API de PokeAPI.
     *
     * @return JSONObject con los datos de las regiones, o null si ocurre un error.
     */
    public JSONObject obtenerRegion() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/region");
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