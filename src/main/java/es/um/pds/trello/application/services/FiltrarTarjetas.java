package es.um.pds.trello.application.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.um.pds.trello.application.dto.ListaDTO;
import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.TableroMapper;
import es.um.pds.trello.application.dto.TarjetaDTO;
import es.um.pds.trello.application.ports.in.IFiltrarTarjetas;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.domain.model.Tablero;
import es.um.pds.trello.domain.model.URL;

@Service
public class FiltrarTarjetas implements IFiltrarTarjetas {

    private final TableroRepository tableroRepo;

    public FiltrarTarjetas(TableroRepository tableroRepo) {
        this.tableroRepo = tableroRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public TableroDTO ejecutar(String urlTablero, List<String> nombresEtiquetas) {
        URL url = new URL(urlTablero);
        Tablero tablero = tableroRepo.findById(url)
                .orElseThrow(() -> new IllegalArgumentException("El tablero con URL " + urlTablero + " no existe."));

        TableroDTO dtoCompleto = TableroMapper.toDTO(tablero);

        if (nombresEtiquetas == null || nombresEtiquetas.isEmpty()) {
            return dtoCompleto;
        }

        List<ListaDTO> listasFiltradas = dtoCompleto.listas().stream()
                .map(lista -> {
                    List<TarjetaDTO> tarjetasFiltradas = lista.tarjetas().stream()
                            .filter(tarjeta -> tarjeta.etiquetas().stream()
                                    .map(info -> info.contains("|") ? info.split("\\|")[0] : info)
                            		.anyMatch(nombreEtiqueta -> nombresEtiquetas.stream()
                                            .anyMatch(filtro -> filtro.equalsIgnoreCase(nombreEtiqueta))))
                            .collect(Collectors.toList());

                    return new ListaDTO(
                            lista.id(),
                            lista.titulo(),
                            lista.posicion(),
                            lista.esListaCompletadas(),
                            tarjetasFiltradas,
                            lista.limiteTarjetas(),
                            lista.predecesorasIds()
                    );
                })
                .toList();

        return new TableroDTO(
                dtoCompleto.titulo(),
                dtoCompleto.url(),
                dtoCompleto.dueñoEmail(),
                dtoCompleto.estado(),
                listasFiltradas
        );
    }
}
