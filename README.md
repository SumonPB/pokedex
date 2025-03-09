# Pokedex API

Este proyecto es una aplicación web desarrollada con Spring Boot y consumiendo la api de PokedexApi que permite buscar e interactuar con información sobre Pokémon.

## Características
- **Búsqueda de Pokémon**: Se puede buscar un Pokémon por su nombre o ID.
- **Filtro de Pokémon**: Se pueden filtrar los Pokémon por tipo, región, habilidad o hábitat.
- **Visualización detallada**: Al seleccionar un Pokémon, se muestra una vista detallada con su información.


## Estructura del Proyecto

### Directorios y Paquetes

- **java/pokedex**
  - **Controller**
    - `PokemonController`: Controlador principal para gestionar las peticiones relacionadas con los Pokémon contiene metodos para cargar la base de datos en caso de ser la primera vez que se usa o interactuar con las diferentes pantallas que existen.
  - **DAO**
    - `PokemonDAO`: Transformacion de un pokemon de la entidad en uno que no interactue para mejorar la seguridad del sistema.
  - **DataCharger**
    - `AppConfig`: Configuración general de la aplicación.
    - `DriverAbility`, `DriverHabitad`, `DriverPokemon`, `DriverRegion`, `DriverTypes`: Carga de datos iniciales.
  - **DTO**
    - `PokemonDTO`: Data Transfer Object para Pokémon.
  - **jpa**
    - `Abilities`, `Habitat`, `Pokemon`, `Region`, `Types`: Entidades JPA para la base de datos.
  - **Observer**
    - `CargaDatosListener`: Interfaz para la observación de carga de datos.
  - **Repositories**
    - `AbilityRepository`, `HabitadRepository`, `PokemonRepository`, `RegionRepository`, `TypesRepository`: Repositorios para el acceso a datos.
  - **Services**
    - `CargarDatos`, `AbilityService`, `HabitadService`, `PokemonService`, `RegionService`, `TypesService`: Servicios para la lógica de negocio.
  - **Clase principal**
    - `PokedexApiApplication`: Inicializa la aplicación Spring Boot.

- **resources/**
  - **application.properties**: Configuración de la aplicación.
  - **static/**
    - `pokemon_sprites/`: Contiene las imágenes de los Pokémon.
    - `style.css`, `style2.css`: Archivos de estilos CSS.
  - **templates/**
    - `error.html`: Página de error.
    - `index.html`: Página principal con buscador y botón de filtros.
    - `pokemonList.html`: Lista de Pokémon filtrados con enlaces a detalles.

## Instalación y Ejecución

1. Clonar el repositorio:
   ```sh
   git clone <URL_DEL_REPOSITORIO>
   ```
2. Navegar al directorio del proyecto:
   ```sh
   cd pokedex
   ```
3. Compilar y ejecutar con Maven:
   ```sh
   mvn spring-boot:run
   ```
4. Acceder a la aplicación con /pokemon si la base de datos esta cargada caso contrario acceder primero a /cargarDatos esperar unos segundos y seguir usando la aplicacion con normalidad:
   ```
   http://localhost:8080/pokedex/pokemon
   http://localhost:8080/pokedex/cargarDatos
   ```

## Uso

1. En la página principal (`index.html`), ingrese el ID o nombre de un Pokémon y haga clic en buscar.
2. Para filtrar, haga clic en el botón "Filtros" y seleccione una categoría en el combolist (tipo, región, habilidad o hábitat).
3. Al hacer clic en un Pokémon de la lista filtrada, se mostrará su información detallada.

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot**
- **JPA/Hibernate**
- **Thymeleaf**
- **Maven**
- **CSS**

## Nota:
La base de datos usada fue postgres en caso de requerirlo configurar el archivo properties para adaptar otra base de datos y descargar su respectiva dependencia.

## Contacto
Para cualquier duda o sugerencia, puedes abrir un issue en el repositorio.

---
Proyecto desarrollado para la gestión y exploración de Pokémon mediante Spring Boot.
