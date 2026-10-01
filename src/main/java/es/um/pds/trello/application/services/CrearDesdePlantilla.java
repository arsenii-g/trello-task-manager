package es.um.pds.trello.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.TableroMapper;
import es.um.pds.trello.application.dto.template.TemplateDTO;
import es.um.pds.trello.application.ports.in.ICrearDesdePlantilla;
import es.um.pds.trello.application.ports.out.TableroRepository;
import es.um.pds.trello.application.ports.out.TrazaRepository;
import es.um.pds.trello.application.ports.out.UsuarioRepository;
import es.um.pds.trello.domain.model.*;
import es.um.pds.trello.domain.services.GeneradorTrazas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CrearDesdePlantilla implements ICrearDesdePlantilla {

    private final TableroRepository tableroRepo;
    private final UsuarioRepository usuarioRepo;
    private final TrazaRepository trazaRepo;
    private final GeneradorTrazas generadorTrazas;
    private final ObjectMapper yamlMapper;

    public CrearDesdePlantilla(TableroRepository tableroRepo,
                               UsuarioRepository usuarioRepo,
                               TrazaRepository trazaRepo,
                               GeneradorTrazas generadorTrazas) {
        this.tableroRepo = tableroRepo;
        this.usuarioRepo = usuarioRepo;
        this.trazaRepo = trazaRepo;
        this.generadorTrazas = generadorTrazas;
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    @Override
    @Transactional
    public TableroDTO ejecutar(String emailDueño, String nombreDueño, String yamlContent) {
        try {
            TemplateDTO template = yamlMapper.readValue(yamlContent, TemplateDTO.class);

            Email email = new Email(emailDueño);
            Usuario dueño = usuarioRepo.findById(email)
                    .orElseGet(() -> usuarioRepo.save(new Usuario(email, nombreDueño)));

            Tablero nuevoTablero = new Tablero(template.titulo(), dueño.getId());

            for (TemplateDTO.TemplateListaDTO listaTemplate : template.listas()) {
                nuevoTablero.añadirLista(listaTemplate.titulo(), listaTemplate.esListaCompletadas(), listaTemplate.limite());

                ListaTareas listaActual = nuevoTablero.getListas().get(nuevoTablero.getListas().size() - 1);

                for (TemplateDTO.TemplateTarjetaDTO tarjetaTemplate : listaTemplate.tarjetas()) {
                    Tarjeta tarjeta;
                    if ("CHECKLIST".equalsIgnoreCase(tarjetaTemplate.tipo())) {
                        tarjeta = new Checklist(tarjetaTemplate.titulo(), tarjetaTemplate.descripcion());
                    } else {
                        tarjeta = new Tarea(tarjetaTemplate.titulo(), tarjetaTemplate.descripcion());
                    }

                    if (tarjetaTemplate.etiquetas() != null) {
                        for (String nombreEtiqueta : tarjetaTemplate.etiquetas()) {
                            tarjeta.asignarEtiqueta(new Etiqueta(nombreEtiqueta));
                        }
                    }

                    listaActual.añadirTarjeta(tarjeta);
                }
            }

            tableroRepo.save(nuevoTablero);

            Traza traza = generadorTrazas.crearTrazaTablero(dueño, nuevoTablero, TipoAccion.CREAR);
            trazaRepo.save(traza);

            return TableroMapper.toDTO(nuevoTablero);

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la plantilla YAML: " + e.getMessage(), e);
        }
    }
}
