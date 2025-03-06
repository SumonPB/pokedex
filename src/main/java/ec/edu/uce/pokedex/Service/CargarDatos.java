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

@Service
public class CargarDatos implements CargaDatosListener {

    private AtomicInteger driversCompletados;
    private LocalDateTime inicioCarga;
    private LocalDateTime finCarga;

    @Autowired
    private DriveAbility driveAbilityService;
    @Autowired
    private DriverTypes driverTypesService;
    @Autowired
    private DriverRegion driverRegionService;
    @Autowired
    private DriverHabitad driverHabitadService;
    @Autowired
    private DriverPokemon driverPokemonService;

    public CargarDatos() {
        driversCompletados = new AtomicInteger(0);
    }

    public void cargar() {
        // Capturar el tiempo de inicio de la carga
        inicioCarga = LocalDateTime.now();
        System.out.println("Inicio de la carga: " + formatearFechaHora(inicioCarga));

        // Configurar los listeners para cada driver
        driveAbilityService.setCargaDatosListener(this);
        driverTypesService.setCargaDatosListener(this);
        driverRegionService.setCargaDatosListener(this);
        driverHabitadService.setCargaDatosListener(this);
        driverPokemonService.setCargaDatosListener(this);

        // Ejecutar los primeros cuatro procesos de forma asíncrona
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(driveAbilityService::ejecutar);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(driverTypesService::ejecutar);
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(driverRegionService::ejecutar);
        CompletableFuture<Void> future4 = CompletableFuture.runAsync(driverHabitadService::ejecutar);

        // Cuando todos los anteriores terminen, se ejecuta la carga de Pokémon
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

    @Override
    public void onCargaCompleta() {
        int completados = driversCompletados.incrementAndGet();
        System.out.println("Carga de un driver completada. Total completados: " + completados);

        if (completados == 5) {
            // Capturar el tiempo de fin de la carga
            finCarga = LocalDateTime.now();
            System.out.println("Fin de la carga: " + formatearFechaHora(finCarga));

            // Calcular la duración total de la carga
            Duration duracion = Duration.between(inicioCarga, finCarga);
            System.out.println("Duración total de la carga: " + formatearDuracion(duracion));

            System.out.println("¡Todos los drivers han terminado la carga!");
        }
    }

    // Método para formatear la fecha y hora
    private String formatearFechaHora(LocalDateTime fechaHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return fechaHora.format(formatter);
    }

    // Método para formatear la duración
    private String formatearDuracion(Duration duracion) {
        long horas = duracion.toHours();
        long minutos = duracion.toMinutesPart();
        long segundos = duracion.toSecondsPart();
        return String.format("%02d:%02d:%02d", horas, minutos, segundos);
    }
}