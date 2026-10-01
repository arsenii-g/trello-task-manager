package es.um.pds.trello.infrastructure.javafx;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import es.um.pds.trello.application.dto.EtiquetaDTO;
import es.um.pds.trello.application.dto.ListaDTO;
import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.TarjetaDTO;
import es.um.pds.trello.application.dto.request.*;
import es.um.pds.trello.application.ports.in.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

@Component
@Scope("prototype")
public class TarjetaDetalleControlador {
    @Autowired
    private IAccederTablero accederTablero;

    @Autowired
    private ICrearTarjeta crearTarjeta;

    @Autowired
    private IEliminarTarjeta eliminarTarjeta;

    @Autowired
    private IMoverTarjeta moverTarjeta;

    @Autowired
    private ICrearItemChecklist crearItemChecklist;

    @Autowired
    private IMarcarTarjetaCompletado marcarTarjetaCompletada;

    @Autowired
    private IAñadirEtiqueta añadirEtiquetaTarjeta;

    @Autowired
    private IMarcarItemCompletado marcarItemCompletado;

    @Autowired
    private IEliminarCompletadoItem eliminarCompletadoItem;

    @Autowired
    private IEliminarItemChecklist eliminarItemChecklist;

    @FXML
    private TextField txtTituloTarjeta;

    @FXML
    private TextField txtNombre;

    @FXML
    private RadioButton rbTarea;

    @FXML
    private RadioButton rbChecklist;

    @FXML
    private VBox panelTarea;

    @FXML
    private VBox panelChecklist;

    @FXML
    private TextArea txtAreaTarea;

    @FXML
    private ListView<String> listChecklistItems;

    @FXML
    private TextField txtNuevoItem;

    @FXML
    private ComboBox<ListaDTO> comboMoverLista;

    @FXML
    private FlowPane panelEtiquetas;

    @FXML
    private TextField txtNuevaEtiqueta;

    @FXML
    private ColorPicker colorPickerEtiqueta;

    private String urlTablero;
    private String idTarjeta;
    private String idListaActual;
    private String emailUsuario;
    private boolean tableroEstaBloqueado = false;

    @FXML
    public void initialize() {
    	if(comboMoverLista != null) {
            comboMoverLista.setConverter(new StringConverter<ListaDTO>() {
                @Override
                public String toString(ListaDTO lista) {
                    return (lista == null) ? "" : lista.titulo();
                }
                @Override
                public ListaDTO fromString(String string) {
                    return null;
                }
            });
        }

        if(panelTarea != null && panelChecklist != null) {
            panelTarea.managedProperty().bind(panelTarea.visibleProperty());
            panelChecklist.managedProperty().bind(panelChecklist.visibleProperty());
        }

        if(rbTarea != null && rbChecklist != null) {
            ToggleGroup group = new ToggleGroup();
            rbTarea.setToggleGroup(group);
            rbChecklist.setToggleGroup(group);
        }
        if(colorPickerEtiqueta != null) {
            colorPickerEtiqueta.setValue(Color.BLUE);
        }
        if(listChecklistItems != null) {
        	listChecklistItems.setOnMouseClicked(event -> {
        		if(event.getClickCount() == 2) {
        			handleToggleItemChecklist();
        		}
        	});
        	listChecklistItems.setOnKeyPressed(event -> {
        		if(event.getCode() == KeyCode.DELETE) {
        			handleEliminarItemChecklist();
        		}
        	});
        }
    }

    public void prepararCreacion(String url, String idLista, String email) {
        this.urlTablero = url;
        this.idListaActual = idLista;
        this.emailUsuario = email;
        this.idTarjeta = null;
    }

    public void inicializarDatos(String url, String idTarjeta, String email) {

        this.inicializarDatos(url, idTarjeta, email, this.tableroEstaBloqueado);
    }

    public void inicializarDatos(String url, String idTarjeta, String email, boolean bloqueado) {
        this.urlTablero = url;
        this.idTarjeta = idTarjeta;
        this.emailUsuario = email;
        this.tableroEstaBloqueado = bloqueado;

        try {
            TableroDTO tablero = accederTablero.ejecutar(url);

            if(comboMoverLista != null) {
                comboMoverLista.getItems().setAll(tablero.listas());
            }

            tablero.listas().forEach(lista -> {
                lista.tarjetas().stream()
                    .filter(t -> t.id().equals(idTarjeta))
                    .findFirst()
                    .ifPresent(tarjeta -> {
                        this.idListaActual = lista.id();

                        if(txtNombre != null) {
                        	txtNombre.setText(tarjeta.titulo());
                        }

                        if(comboMoverLista != null) {
                            comboMoverLista.getItems().stream()
                                					  .filter(l -> l.id().equals(lista.id()))
                                					  .findFirst()
                                					  .ifPresent(l -> comboMoverLista.getSelectionModel().select(l));
                        }

                        cargarEtiquetasVisualmente(tarjeta.etiquetas());

                        if(txtAreaTarea != null) {
                            txtAreaTarea.setText(tarjeta.descripcion());
                        }

                        if(listChecklistItems != null) {

                        	actualizarListaChecklistUI(tarjeta);
                        }
                    });
            });

            if(this.tableroEstaBloqueado) {
                aplicarRestriccionesTablerobloqueado();
            }        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEtiquetasVisualmente(List<String> etiquetas) {
        if(panelEtiquetas == null || etiquetas == null) {
        	return;
        }
        panelEtiquetas.getChildren().clear();

        for(String info : etiquetas) {
            String colorFondo = "#0079BF";
            String textoMostrar = info;

            if (info.contains("|")) {
                String[] parts = info.split("\\|");
                textoMostrar = parts[0];
                colorFondo = parts[1];
            } else if(info.matches("^#[0-9A-Fa-f]{6}$")) {
                colorFondo = info;
            }

            Label lbl = new Label(textoMostrar);
            lbl.setStyle("-fx-background-color: " + colorFondo + "; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-weight: bold;");
            panelEtiquetas.getChildren().add(lbl);
        }
    }

    @FXML
    void handleGuardar() {
        try {

            if(this.tableroEstaBloqueado) {
                if(idTarjeta != null && comboMoverLista != null) {
                    ListaDTO destino = comboMoverLista.getValue();
                    if(destino != null && !destino.id().equals(idListaActual)) {
                        moverTarjeta.ejecutar(new MoverTarjetasRequest(urlTablero, idTarjeta, idListaActual, destino.id(), emailUsuario));
                        cerrarVentana();
                    }
                }
                return;
            }

            String titulo = (idTarjeta == null) ? txtTituloTarjeta.getText() : txtNombre.getText();
            String descripcion = (txtAreaTarea != null) ? txtAreaTarea.getText() : "";

            String tipoTarjeta = "TAREA";
            if(rbChecklist != null) {
                tipoTarjeta = rbChecklist.isSelected() ? "CHECKLIST" : "TAREA";
            }
            else if(idTarjeta != null) {
                tipoTarjeta = "TAREA";
            }

            if(titulo == null || titulo.trim().isEmpty()) {
                mostrarAlerta("Campo obligatorio", "El título no puede estar vacío.");
                return;
            }

            crearTarjeta.ejecutar(new CrearTarjetaRequest(
                urlTablero,
                idListaActual,
                idTarjeta,
                titulo,
                descripcion,
                emailUsuario,
                tipoTarjeta
            ));

            if(idTarjeta != null && comboMoverLista != null) {
                ListaDTO destino = comboMoverLista.getValue();
                if(destino != null && !destino.id().equals(idListaActual)) {
                    moverTarjeta.ejecutar(new MoverTarjetasRequest(urlTablero, idTarjeta, idListaActual, destino.id(), emailUsuario));
                }
            }
            cerrarVentana();
        }
        catch(Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
    private void cerrarVentana() {
        Platform.runLater(() -> {
            if(txtTituloTarjeta != null && txtTituloTarjeta.getScene() != null) {
                ((Stage) txtTituloTarjeta.getScene().getWindow()).close();
            }
            else if(txtNombre != null && txtNombre.getScene() != null) {
                ((Stage) txtNombre.getScene().getWindow()).close();
            }
        });
    }

    @FXML
    void handleAñadirEtiqueta() {
        if(txtNuevaEtiqueta == null || txtNuevaEtiqueta.getText().trim().isEmpty()) {
        	return;
        }

        if (idTarjeta == null) {
            mostrarAlerta("Tarjeta no guardada", "Debes guardar la tarjeta antes de añadir etiquetas.");
            return;
        }

        String nombre = txtNuevaEtiqueta.getText().trim();

        try {
            Color c = colorPickerEtiqueta.getValue();
            String colorHex = String.format("#%02X%02X%02X", (int)(c.getRed() * 255), (int)(c.getGreen() * 255), (int)(c.getBlue() * 255));

            EtiquetaDTO nuevaEt = new EtiquetaDTO(nombre, colorHex);

            añadirEtiquetaTarjeta.ejecutar(new AñadirEtiquetaRequest(urlTablero, idTarjeta, emailUsuario, nuevaEt));

            txtNuevaEtiqueta.clear();

            Platform.runLater(() -> inicializarDatos(urlTablero, idTarjeta, emailUsuario));
        }
        catch(Exception e) {
            mostrarAlerta("Error", "No se pudo añadir la etiqueta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleMarcarCompletada() {
        if(idTarjeta == null) {
        	return;
        }
        try {
            marcarTarjetaCompletada.ejecutar(new MarcarTarjetaCompletadoRequest(urlTablero, idTarjeta, emailUsuario));
            inicializarDatos(urlTablero, idTarjeta, emailUsuario);
        }
        catch(Exception e) {
        	mostrarAlerta("Checklist incompleta", "No se puede marcar como completada una tarjeta si no están todos sus ítems completados.");
        	e.printStackTrace();
        }
    }

    private void actualizarListaChecklistUI(TarjetaDTO tarjeta) {
        if(listChecklistItems == null || tarjeta.items() == null) {
        	return;
        }
        listChecklistItems.getItems().clear();
        for (TarjetaDTO.ItemChecklistDTO item : tarjeta.items()) {
            String prefix = item.completado() ? "[X] " : "[ ] ";
            listChecklistItems.getItems().add(prefix + item.contenido());
        }
    }

    private void handleToggleItemChecklist() {
    	int indice = listChecklistItems.getSelectionModel().getSelectedIndex();
    	if(indice < 0 || idTarjeta == null) {
    		return;
    	}

    	try {
    		TableroDTO tablero = accederTablero.ejecutar(urlTablero);
    		TarjetaDTO tarjetaActual = buscarTarjetaEnTablero(tablero, idTarjeta);

    		if(tarjetaActual != null && tarjetaActual.completada()) {
    			mostrarAlerta("Tarjeta completada", "Esta tarjeta ya está marcada como completada. No se pueden desmarcar ítems.");
    			return;
    		}

    		String textoItem = listChecklistItems.getItems().get(indice);

    		if(textoItem.startsWith("[X]")) {
    			eliminarCompletadoItem.ejecutar(new EliminarCompletadoItemRequest(urlTablero, idTarjeta, indice, emailUsuario));
    		}
    		else {
    			marcarItemCompletado.ejecutar(new MarcarItemCompletadoRequest(urlTablero, idTarjeta, indice, emailUsuario));
    		}
    		inicializarDatos(urlTablero, idTarjeta, emailUsuario);
    	}
    	catch(Exception e) {
    		mostrarAlerta("Error", "No se pudo cambiar el estado del ítem.");
    		e.printStackTrace();
    	}
    }

    private TarjetaDTO buscarTarjetaEnTablero(TableroDTO tablero, String idTarjeta) {
    	for(ListaDTO lista : tablero.listas()) {
    		for(TarjetaDTO tarjeta : lista.tarjetas()) {
    			if(tarjeta.id().equals(idTarjeta)) {
    				return tarjeta;
    			}
    		}
    	}
    	return null;
    }

    @FXML
    void handleAñadirItem() {
    	if(txtNuevoItem.getText().trim().isEmpty()) {
    		return;
    	}
        try {
            crearItemChecklist.ejecutar(new CrearItemChecklistRequest(urlTablero, idTarjeta, txtNuevoItem.getText(), emailUsuario));
            txtNuevoItem.clear();
            inicializarDatos(urlTablero, idTarjeta, emailUsuario);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleEliminarItemChecklist() {
    	int indice = listChecklistItems.getSelectionModel().getSelectedIndex();
    	if(indice < 0 || idTarjeta == null) {
    		mostrarAlerta("Selección vacía", "Selecciona un ítem de la lista para eliminarlo.");
    		return;
    	}

    	try {
    		eliminarItemChecklist.ejecutar(new EliminarItemChecklistRequest(urlTablero, idTarjeta, indice, emailUsuario));

    		inicializarDatos(urlTablero, idTarjeta, emailUsuario);
    	}
    	catch(Exception e) {
    		mostrarAlerta("Error", "No se pudo eliminar el ítem" + e.getMessage());
    	}
    }

    @FXML
    void handleEliminarTarjeta() {
        eliminarTarjeta.ejecutar(new EliminarTarjetaRequest(urlTablero, idTarjeta, emailUsuario));
        handleCerrar();
    }

    @FXML
    void handleCerrar() {
    	Node[] nodos = {txtNombre, txtTituloTarjeta, comboMoverLista, rbTarea, panelEtiquetas};

        for(Node nodo : nodos) {
            if(nodo != null && nodo.getScene() != null && nodo.getScene().getWindow() != null) {
                ((Stage) nodo.getScene().getWindow()).close();
                return;
            }
        }
    }

    private void aplicarRestriccionesTablerobloqueado() {

        if(txtNombre != null) {
            txtNombre.setEditable(false);
            txtNombre.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-background-color: #f0f0f0;");
        }

        if(txtAreaTarea != null) {
            txtAreaTarea.setEditable(false);
            txtAreaTarea.setStyle("-fx-control-inner-background: #f0f0f0; -fx-text-fill: #333;");
        }

        if(txtNuevaEtiqueta != null) {
            txtNuevaEtiqueta.setDisable(true);
        }
        if(colorPickerEtiqueta != null) {
            colorPickerEtiqueta.setDisable(true);
        }

        if(txtNuevoItem != null) {
            txtNuevoItem.setDisable(true);
        }
        if(listChecklistItems != null) {
            listChecklistItems.setDisable(true);
        }

        if(comboMoverLista != null && comboMoverLista.getScene() != null) {
            deshabilitarTodosLosBotones(comboMoverLista.getScene().getRoot());
        }
    }

    private void deshabilitarTodosLosBotones(Node raiz) {
        if(raiz instanceof Button) {
            Button btn = (Button) raiz;

            btn.setDisable(true);
        } else if(raiz instanceof Parent) {
            Parent parent = (Parent) raiz;
            for(Node hijo : parent.getChildrenUnmodifiable()) {
                deshabilitarTodosLosBotones(hijo);
            }
        }
    }
}