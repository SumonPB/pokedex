package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import ec.edu.uce.pokedex.Service.*;
import ec.edu.uce.pokedex.jpa.*;
import ec.edu.uce.pokedex.repositories.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Clase DriverPokemon
 *
 * Servicio de Spring que se encarga de obtener y procesar datos de Pokémon desde la API pública de PokeAPI.
 * Utiliza concurrencia para mejorar el rendimiento al realizar múltiples solicitudes HTTP en paralelo.
 * Los datos obtenidos se almacenan en una base de datos mediante servicios inyectados.
 */
@Service
public class DriverPokemon {

    private final RestTemplate restTemplate; // Cliente HTTP para realizar solicitudes.
    private final ExecutorService executorService; // Pool de hilos para ejecutar tareas concurrentes.

    @Autowired
    private PokemonService pokemonService; // Servicio para gestionar la persistencia de Pokémon.
    @Autowired
    private TypesService typesService; // Servicio para gestionar la persistencia de tipos de Pokémon.
    @Autowired
    private HabitatService habitatService; // Servicio para gestionar la persistencia de hábitats de Pokémon.
    @Autowired
    private RegionService regionService; // Servicio para gestionar la persistencia de regiones de Pokémon.
    @Autowired
    private AbilitiesService abilitiesService; // Servicio para gestionar la persistencia de habilidades de Pokémon.

    private CargaDatosListener cargaDatosMoveListener; // Observador para notificar cuando la carga de datos ha finalizado.

    /**
     * Constructor de la clase.
     *
     * @param restTemplate Cliente HTTP para realizar solicitudes.
     */
    public DriverPokemon(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Crear un ExecutorService con un número fijo de hilos (40 en este caso).
        this.executorService = Executors.newFixedThreadPool(40);
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
     * Método principal que inicia la carga de datos de Pokémon.
     * Realiza una solicitud a la API para obtener la lista de todos los Pokémon y luego procesa cada Pokémon de manera concurrente.
     */
    public void ejecutar() {
        String allPokemonUrl = "https://pokeapi.co/api/v2/pokemon?limit=1304";

        // Obtener la lista de todos los Pokémon.
        JSONObject allPokemonData = obtenerDatosDeUrl(allPokemonUrl);

        if (allPokemonData != null && allPokemonData.has("results")) {
            JSONArray pokemonList = allPokemonData.getJSONArray("results");

            // Lista para almacenar las tareas de ejecución concurrente.
            List<Callable<Void>> tasks = new ArrayList<>();

            // Procesar cada Pokémon en la lista.
            pokemonList.toList().forEach(pokemonObj -> {
                tasks.add(() -> {
                    JSONObject pokemon = new JSONObject((Map<?, ?>) pokemonObj);
                    String pokemonUrl = pokemon.getString("url");

                    // Obtener los datos del Pokémon específico.
                    JSONObject pokemonData = obtenerDatosDeUrl(pokemonUrl);
                    if (pokemonData == null) return null;

                    int pokemonId = pokemonData.getInt("id");

                    // Ignorar Pokémon con ID mayor a 1304.
                    if (pokemonId > 1304) {
                        return null;
                    }

                    // Crear un nuevo objeto Pokémon y asignar datos básicos.
                    Pokemon nuevoPokemon = new Pokemon();
                    nuevoPokemon.setId(pokemonData.getInt("id"));
                    nuevoPokemon.setName(pokemonData.getString("name"));
                    nuevoPokemon.setHeight(pokemonData.getInt("height"));
                    nuevoPokemon.setWeight(pokemonData.getInt("weight"));

                    // Inicialización de sets y mapas para almacenar IDs relacionados.
                    Set<Integer> regionIds = new LinkedHashSet<>();
                    Set<Integer> typeIds = new LinkedHashSet<>();
                    Set<Integer> locationIds = new LinkedHashSet<>();
                    Set<Integer> moveIds = new LinkedHashSet<>();
                    Set<Integer> abilityIds = new LinkedHashSet<>();
                    Integer habitatId = null;
                    List<Integer> evolutionIds = new ArrayList<>();

                    // Obtener los tipos del Pokémon.
                    if (pokemonData.has("types")) {
                        typeIds.addAll(pokemonData.getJSONArray("types").toList().stream()
                                .map(typeObj -> {
                                    JSONObject typeInfo = new JSONObject((Map<?, ?>) typeObj).getJSONObject("type");
                                    return extraerIdDesdeUrl(typeInfo.getString("url"));
                                })
                                .collect(Collectors.toSet()));
                    }

                    // Obtener el ID del hábitat y la cadena de evolución.
                    if (pokemonData.has("species")) {
                        JSONObject species = pokemonData.getJSONObject("species");
                        String speciesUrl = species.getString("url");
                        JSONObject speciesData = obtenerDatosDeUrl(speciesUrl);

                        habitatId = obtenerHabitatId(speciesData);
                        evolutionIds = obtenerEvolutionIds(speciesData);
                    }

                    // Obtener los IDs de las ubicaciones.
                    if (pokemonData.has("location_area_encounters")) {
                        String locationAreaUrl = pokemonData.getString("location_area_encounters");
                        JSONArray locationAreaData = obtenerDatosDeEncuentros(locationAreaUrl);

                        if (locationAreaData != null) {
                            locationIds.addAll(locationAreaData.toList().stream()
                                    .flatMap(encounterObj -> {
                                        JSONObject encounter = new JSONObject((Map<?, ?>) encounterObj);
                                        JSONObject locationArea = encounter.getJSONObject("location_area");
                                        String locationAreaUrlFromEncounter = locationArea.getString("url");
                                        JSONObject locationAreaDataResponse = obtenerDatosDeUrl(locationAreaUrlFromEncounter);

                                        if (locationAreaDataResponse != null && locationAreaDataResponse.has("location")) {
                                            JSONObject location = locationAreaDataResponse.getJSONObject("location");
                                            String locationUrl = location.getString("url");
                                            JSONObject locationData = obtenerDatosDeUrl(locationUrl);

                                            if (locationData != null && locationData.has("region")) {
                                                JSONObject region = locationData.getJSONObject("region");
                                                regionIds.add(extraerIdDesdeUrl(region.getString("url")));
                                            }
                                        }
                                        return locationIds.stream();
                                    }).collect(Collectors.toSet()));
                        }
                    }

                    // Obtener los IDs de los movimientos.
                    if (pokemonData.has("moves")) {
                        moveIds.addAll(pokemonData.getJSONArray("moves").toList().stream()
                                .map(moveObj -> {
                                    JSONObject moveInfo = new JSONObject((Map<?, ?>) moveObj).getJSONObject("move");
                                    return extraerIdDesdeUrl(moveInfo.getString("url"));
                                })
                                .collect(Collectors.toSet()));
                    }

                    // Obtener los IDs de las habilidades.
                    if (pokemonData.has("abilities")) {
                        abilityIds.addAll(pokemonData.getJSONArray("abilities").toList().stream()
                                .map(abilityObj -> {
                                    JSONObject abilityInfo = new JSONObject((Map<?, ?>) abilityObj).getJSONObject("ability");
                                    return extraerIdDesdeUrl(abilityInfo.getString("url"));
                                })
                                .collect(Collectors.toSet()));
                    }

                    // Obtener los stats del Pokémon y asignarlos a variables.
                    if (pokemonData.has("stats")) {
                        JSONArray statsArray = pokemonData.getJSONArray("stats");
                        for (int i = 0; i < statsArray.length(); i++) {
                            JSONObject statObj = statsArray.getJSONObject(i);
                            String statName = statObj.getJSONObject("stat").getString("name");
                            int statValue = statObj.getInt("base_stat");

                            switch (statName) {
                                case "hp":
                                    nuevoPokemon.setStats_hp(statValue);
                                    break;
                                case "attack":
                                    nuevoPokemon.setStats_attack(statValue);
                                    break;
                                case "defense":
                                    nuevoPokemon.setStats_defense(statValue);
                                    break;
                                case "special-attack":
                                    nuevoPokemon.setStats_special_attack(statValue);
                                    break;
                                case "special-defense":
                                    nuevoPokemon.setStats_special_defense(statValue);
                                    break;
                                case "speed":
                                    nuevoPokemon.setStats_speed(statValue);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    // Asignar el hábitat al Pokémon.
                    if (habitatId != null) {
                        Habitat newHabitat = habitatService.findById(habitatId);
                        nuevoPokemon.setHabitat(newHabitat);
                    }

                    // Asignar los tipos al Pokémon.
                    List<Types> tiposList = new ArrayList<>();
                    for (Integer typeId : typeIds) {
                        Types newTypes = typesService.findById(typeId);
                        tiposList.add(newTypes);
                    }

                    // Asignar las regiones al Pokémon.
                    List<Region> regionesList = new ArrayList<>();
                    for (Integer regiones : regionIds) {
                        Region newRegion = regionService.findById(regiones);
                        regionesList.add(newRegion);
                    }

                    // Asignar las habilidades al Pokémon.
                    List<Abilities> abilidadesList = new ArrayList<>();
                    for (Integer abilidades : abilityIds) {
                        Abilities newAbilidades = abilitiesService.findById(abilidades);
                        abilidadesList.add(newAbilidades);
                    }

                    // Asignar las listas al Pokémon.
                    nuevoPokemon.setAbilities(abilidadesList);
                    nuevoPokemon.setRegions(regionesList);
                    nuevoPokemon.setTypes(tiposList);
                    nuevoPokemon.setEnvoles(evolutionIds);

                    // Guardar el Pokémon en la base de datos.
                    pokemonService.savePokemon(nuevoPokemon);
                    return null;
                });
            });

            try {
                // Ejecutar las tareas de manera asíncrona.
                List<Future<Void>> futures = executorService.invokeAll(tasks);
                // Esperar a que todas las tareas finalicen.
                for (Future<Void> future : futures) {
                    future.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error al ejecutar tareas en paralelo: " + e.getMessage());
            }

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
        }
    }

    /**
     * Realiza una solicitud HTTP GET a la URL proporcionada y devuelve los datos en formato JSONObject.
     *
     * @param url URL de la API a la que se realizará la solicitud.
     * @return JSONObject con los datos obtenidos, o null si ocurre un error.
     */
    private JSONObject obtenerDatosDeUrl(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONObject(jsonResponse);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Realiza una solicitud HTTP GET a la URL proporcionada y devuelve los datos en formato JSONArray.
     *
     * @param url URL de la API a la que se realizará la solicitud.
     * @return JSONArray con los datos obtenidos, o null si ocurre un error.
     */
    private JSONArray obtenerDatosDeEncuentros(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONArray(jsonResponse);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae el ID de un recurso a partir de su URL.
     *
     * @param url URL de la que se extraerá el ID.
     * @return Entero que representa el ID extraído.
     */
    private int extraerIdDesdeUrl(String url) {
        int lastSlashIndex = url.lastIndexOf('/');
        int secondLastSlashIndex = url.lastIndexOf('/', lastSlashIndex - 1);
        return Integer.parseInt(url.substring(secondLastSlashIndex + 1, lastSlashIndex));
    }

    /**
     * Extrae el ID del hábitat de un Pokémon a partir de los datos de la especie.
     *
     * @param speciesData JSONObject que contiene los datos de la especie del Pokémon.
     * @return Entero que representa el ID del hábitat, o null si no se encuentra.
     */
    private Integer obtenerHabitatId(JSONObject speciesData) {
        if (speciesData != null && speciesData.has("habitat") && !speciesData.isNull("habitat")) {
            JSONObject habitat = speciesData.getJSONObject("habitat");
            if (habitat.has("url")) {
                return extraerIdDesdeUrl(habitat.getString("url"));
            }
        }
        return null;
    }

    /**
     * Obtiene los IDs de las evoluciones de un Pokémon a partir de los datos de la especie.
     *
     * @param speciesData JSONObject que contiene los datos de la especie del Pokémon.
     * @return Lista de enteros que representan los IDs de las evoluciones.
     */
    private List<Integer> obtenerEvolutionIds(JSONObject speciesData) {
        List<Integer> evolutionIds = new ArrayList<>();

        if (speciesData != null && speciesData.has("evolution_chain") && !speciesData.isNull("evolution_chain")) {
            String evolutionChainUrl = speciesData.getJSONObject("evolution_chain").getString("url");
            JSONObject evolutionChainData = obtenerDatosDeUrl(evolutionChainUrl);

            if (evolutionChainData != null && evolutionChainData.has("chain")) {
                JSONObject chain = evolutionChainData.getJSONObject("chain");
                extraerIdsDeEvolucion(chain, evolutionIds);
            }
        }

        return evolutionIds;
    }

    /**
     * Método recursivo que extrae los IDs de las evoluciones de un Pokémon a partir de la cadena de evolución.
     *
     * @param chain JSONObject que representa la cadena de evolución.
     * @param evolutionIds Lista donde se almacenarán los IDs de las evoluciones.
     */
    private void extraerIdsDeEvolucion(JSONObject chain, List<Integer> evolutionIds) {
        if (chain != null && chain.has("species")) {
            JSONObject species = chain.getJSONObject("species");
            if (species != null && species.has("url")) {
                String speciesUrl = species.getString("url");
                if (speciesUrl != null && !speciesUrl.isEmpty()) {
                    int speciesId = extraerIdDesdeUrl(speciesUrl);
                    evolutionIds.add(speciesId);
                }
            }
        }

        if (chain != null && chain.has("evolves_to")) {
            JSONArray evolvesToArray = chain.getJSONArray("evolves_to");
            for (int i = 0; i < evolvesToArray.length(); i++) {
                JSONObject nextEvolution = evolvesToArray.optJSONObject(i);
                if (nextEvolution != null) {
                    extraerIdsDeEvolucion(nextEvolution, evolutionIds);
                }
            }
        }
    }
}