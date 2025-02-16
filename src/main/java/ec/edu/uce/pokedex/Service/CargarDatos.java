package ec.edu.uce.pokedex.Service;

import ec.edu.uce.pokedex.DataCharge.*;
import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CargarDatos implements CargaDatosListener {

    private AtomicInteger driversCompletados;

    @Autowired
    private DriveAbility driveAbilityService;
    @Autowired
    private DriverTypes driverTypesService;
    @Autowired
    private DriverRegion driverRegionService;
    @Autowired
    private DriverHabitad driverHabitadService;
    @Autowired
    private DriverMove driverMoveService;
    @Autowired
    private DriverPokemon driverPokemonService;

    public CargarDatos() {
        driversCompletados = new AtomicInteger(0);
    }

    public void cargar() {
        // Ejecutar los primeros cinco procesos de forma asíncrona
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(driveAbilityService::ejecutar);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(driverTypesService::ejecutar);
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(driverRegionService::ejecutar);
        CompletableFuture<Void> future4 = CompletableFuture.runAsync(driverHabitadService::ejecutar);
        CompletableFuture<Void> future5 = CompletableFuture.runAsync(driverMoveService::ejecutar);

        // Cuando todos los anteriores terminen, se ejecuta la carga de Pokémon
        CompletableFuture.allOf(future1, future2, future3, future4, future5)
                .thenRun(() -> {
                    System.out.println("Las primeras 5 cargas han finalizado. Iniciando carga de Pokémon...");
                    driverPokemonService.ejecutar();
                });
    }

    @Override
    public void onCargaCompleta() {
        int completados = driversCompletados.incrementAndGet();
        System.out.println("Carga de un driver completada. Total completados: " + completados);

        if (completados == 6) {
            System.out.println("¡Todos los drivers han terminado la carga!");
        }
    }
}
