# Trello Task Manager

Gestor de tareas de escritorio con tableros, listas, tarjetas, checklists, etiquetas y plantillas YAML. Desarrollado con Java, Spring Boot, JavaFX, JPA y H2.

## Requisitos

- JDK 21 y un entorno de escritorio.
- Conexión a Internet durante la primera ejecución para descargar Maven y las dependencias.

## Ejecutar

Desde la carpeta del proyecto:

```bash
./mvnw spring-boot:run
```

En Windows, utiliza `mvnw.cmd spring-boot:run`.

Introduce un correo como identificador y crea un tablero. Añade listas y tarjetas para organizar tus tareas. Las plantillas disponibles están en `src/main/resources/plantillas`.

La información se guarda en `data/trello_db`. La URL de cada tablero sirve para recuperarlo dentro de esta aplicación; no publica el tablero en el servicio de Trello.

## Pruebas

```bash
./mvnw test
```

## Autor

Arsenii Gladkykh — [arsenii-g](https://github.com/arsenii-g).
