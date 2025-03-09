package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.DataCharge.*;
import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase CargarDatos
 *
 * Servicio de Spring que coordina la carga de datos de Pokémon y sus entidades relacionadas (habilidades, tipos, regiones, hábitats).
 * Implementa la interfaz CargaDatosListener para recibir notificaciones cuando cada proceso de carga finaliza.
 * Utiliza concurrencia para ejecutar los procesos de carga de manera asíncrona y optimizar el tiempo de ejecución.
 */
@Service
public class CargarDatos implements CargaDatosListener {

    private final AtomicInteger driversCompletados; // Contador atómico para rastrear cuántos drivers han completado su carga.
    private LocalDateTime inicioCarga; // Marca de tiempo para el inicio de la carga.
    private LocalDateTime finCarga; // Marca de tiempo para el fin de la carga.

    @Autowired
    private DriveAbility driveAbilityService; // Servicio para cargar datos de habilidades.
    @Autowired
    private DriverTypes driverTypesService; // Servicio para cargar datos de tipos.
    @Autowired
    private DriverRegion driverRegionService; // Servicio para cargar datos de regiones.
    @Autowired
    private DriverHabitad driverHabitadService; // Servicio para cargar datos de hábitats.
    @Autowired
    private DriverPokemon driverPokemonService; // Servicio para cargar datos de Pokémon.

    /**
     * Constructor de la clase.
     * Inicializa el contador de drivers completados.
     */
    public CargarDatos() {
        driversCompletados = new AtomicInteger(0);
    }

    /**
     * Método principal que inicia la carga de datos.
     * Ejecuta los procesos de carga de habilidades, tipos, regiones y hábitats de manera asíncrona.
     * Una vez que estos procesos finalizan, inicia la carga de Pokémon.
     */
    public void cargar() {
        // Capturar el tiempo de inicio de la carga.
        inicioCarga = LocalDateTime.now();
        System.out.println("Inicio de la carga: " + formatearFechaHora(inicioCarga));

        // Configurar los listeners para cada driver.
        driveAbilityService.setCargaDatosListener(this);
        driverTypesService.setCargaDatosListener(this);
        driverRegionService.setCargaDatosListener(this);
        driverHabitadService.setCargaDatosListener(this);
        driverPokemonService.setCargaDatosListener(this);

        // Ejecutar los primeros cuatro procesos de forma asíncrona.
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(driveAbilityService::ejecutar);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(driverTypesService::ejecutar);
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(driverRegionService::ejecutar);
        CompletableFuture<Void> future4 = CompletableFuture.runAsync(driverHabitadService::ejecutar);

        // Cuando todos los anteriores terminen, se ejecuta la carga de Pokémon.
        CompletableFuture.allOf(future1, future2, future3, future4)
                .thenRun(() -> {
                    System.out.println("Las primeras 4 cargas han finalizado. Iniciando carga de Pokémon...");
                    driverPokemonService.ejecutar();
                })
                .exceptionally(ex -> {
                    System.err.println("Error durante la carga de datos: " + ex.getMessage());
                    return null;
                });
    }

    /**
     * Método de la interfaz CargaDatosListener.
     * Se ejecuta cuando un driver completa su carga.
     * Incrementa el contador de drivers completados y, si todos han terminado, calcula la duración total de la carga.
     */
    @Override
    public void onCargaCompleta() {
        int completados = driversCompletados.incrementAndGet();
        System.out.println("Carga de un driver completada. Total completados: " + completados);

        // Verificar si todos los drivers han completado su carga.
        if (completados == 5) {
            // Capturar el tiempo de fin de la carga.
            finCarga = LocalDateTime.now();
            System.out.println("Fin de la carga: " + formatearFechaHora(finCarga));

            // Calcular la duración total de la carga.
            Duration duracion = Duration.between(inicioCarga, finCarga);
            System.out.println("Duración total de la carga: " + formatearDuracion(duracion));

            System.out.println("¡Todos los drivers han terminado la carga!");
        }
    }

    /**
     * Método para formatear una fecha y hora en un formato legible.
     *
     * @param fechaHora Fecha y hora a formatear.
     * @return Cadena de texto con la fecha y hora formateada.
     */
    private String formatearFechaHora(LocalDateTime fechaHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fechaHora.format(formatter);
    }

    /**
     * Método para formatear una duración en un formato legible (HH:MM:SS).
     *
     * @param duracion Duración a formatear.
     * @return Cadena de texto con la duración formateada.
     */
    private String formatearDuracion(Duration duracion) {
        long horas = duracion.toHours();
        long minutos = duracion.toMinutesPart();
        long segundos = duracion.toSecondsPart();
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
}