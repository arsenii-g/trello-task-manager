package es.um.pds.trello.infrastructure.config;

import org.springframework.stereotype.Service;
import es.um.pds.trello.application.dto.template.PlantillaDisponibleDTO;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CargadorPlantillas {

    private static final String PLANTILLAS_RESOURCE_PATH = "plantillas";

    public List<PlantillaDisponibleDTO> cargarPlantillasDisponibles() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(PLANTILLAS_RESOURCE_PATH);

            if (resource == null) {
                return Collections.emptyList();
            }

            Path plantillasDir = Paths.get(resource.toURI());

            return Files.list(plantillasDir)
                    .filter(path -> path.toString().endsWith(".yaml") || path.toString().endsWith(".yml"))
                    .map(this::cargarPlantilla)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(PlantillaDisponibleDTO::nombre))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private PlantillaDisponibleDTO cargarPlantilla(Path archivoPlantilla) {
        try {
            String contenido = new String(Files.readAllBytes(archivoPlantilla), StandardCharsets.UTF_8);
            String nombre = archivoPlantilla.getFileName().toString();
            String titulo = extraerTituloDelYAML(contenido);

            return new PlantillaDisponibleDTO(nombre, titulo, contenido);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extraerTituloDelYAML(String contenidoYAML) {
        Pattern pattern = Pattern.compile("titulo:\\s*['\"]?([^'\"\\n]+)['\"]?");
        Matcher matcher = pattern.matcher(contenidoYAML);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "Plantilla sin título";
    }
}