package es.um.pds.trello.infrastructure.javafx;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import es.um.pds.trello.application.dto.ListaDTO;
import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.TarjetaDTO;
import es.um.pds.trello.application.dto.TrazaDTO;
import es.um.pds.trello.application.dto.request.BloquearTableroRequest;
import es.um.pds.trello.application.dto.request.ConfigurarLimiteListaRequest;
import es.um.pds.trello.application.dto.request.ConfigurarWorkflowRequest;
import es.um.pds.trello.application.dto.request.CrearListaTareasRequest;
import es.um.pds.trello.application.dto.request.EliminarListaTareasRequest;
import es.um.pds.trello.application.ports.in.IAccederTablero;
import es.um.pds.trello.application.ports.in.IBloquearTablero;
import es.um.pds.trello.application.ports.in.IConfigurarLimiteLista;
import es.um.pds.trello.application.ports.in.IConfigurarWorkflow;
import es.um.pds.trello.application.ports.in.ICrearListaTareas;
import es.um.pds.trello.application.ports.in.IEliminarListaTareas;
import es.um.pds.trello.application.ports.in.IFiltrarTarjetas;
import es.um.pds.trello.application.ports.in.IVerTrazas;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonBar;

@Component
public class TableroControlador {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private IAccederTablero accederTablero;

    @Autowired
    private IBloquearTablero bloquearTablero;

    @Autowired
    private IVerTrazas verTrazas;

    @Autowired
    private IFiltrarTarjetas filtrarTarjetas;

    @Autowired
    private ICrearListaTareas crearListaTareas;

    @Autowired
    private IEliminarListaTareas eliminarListaTareas;

    @Autowired
    private IConfigurarLimiteLista configurarLimiteServicio;

    @Autowired
    private IConfigurarWorkflow configurarWorkflowServicio;

    @FXML
    private Label lblTituloTablero;

    @FXML
    private TextField txtUrlTablero;

    @FXML
    private Label lblUsuarioActual;

    @FXML
    private Label lblDueñoTablero;

    @FXML
    private Label lblEstado;

    @FXML
    private HBox contenedorListas;

    @FXML
    private CheckBox chkEsCompletada;

    @FXML
    private Button btnBloquear;

    @FXML
    private TextField txtFiltroEtiquetas;

    private String urlActual;
    private String emailUsuario;

    public void inicializarDatos(String url, String email) {
        this.urlActual = url;
        this.emailUsuario = email;
        this.txtUrlTablero.setText(url);
        cargarContenido();
    }

    public boolean isEsCompletada() {
    	return chkEsCompletada.isSelected();
    }

    void handleCambiarLimiteLista(String listaId) {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Configurar Límite");
        dialog.setHeaderText("Introduce el número máximo de tarjetas para esta lista");
        dialog.setContentText("Límite (deja vacío para quitarlo):");

        dialog.showAndWait().ifPresent(resultado -> {
            try {
                Integer nuevoLimite = null;
                if (!resultado.trim().isEmpty()) {
                    nuevoLimite = Integer.parseInt(resultado.trim());
                    if (nuevoLimite < 0) throw new NumberFormatException();
                }

                ConfigurarLimiteListaRequest request = new ConfigurarLimiteListaRequest(
                    this.urlActual,
                    listaId,
                    this.emailUsuario,
                    nuevoLimite
                );

                configurarLimiteServicio.ejecutar(request);

                cargarContenido();

            } catch (NumberFormatException e) {
                mostrarMensaje("Error", "Por favor, introduce un número válido.");
            } catch (Exception e) {
                mostrarMensaje("Restricción de Regla", e.getMessage());
            }
        });
    }

    private void cargarContenido() {
        try {
            TableroDTO tablero = accederTablero.ejecutar(urlActual);
            lblTituloTablero.setText(tablero.titulo());
            lblUsuarioActual.setText(this.emailUsuario);
            lblDueñoTablero.setText(tablero.dueñoEmail());

            String estadoNormalizado = (tablero.estado() != null) ? tablero.estado().trim().toUpperCase() : "ACTIVO";
            boolean estaBloqueado = "BLOQUEADO".equals(estadoNormalizado);

            actualizarEstadoVisual(estaBloqueado);
            renderizarListas(tablero, estaBloqueado);
        }
        catch(Exception e) {
            mostrarMensaje("Error", "No se pudo cargar el tablero: " + e.getMessage());
        }
    }

    private void actualizarEstadoVisual(boolean estaBloqueado) {
        if(estaBloqueado) {
            lblEstado.setText("BLOQUEADO");
            lblEstado.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 5;");
            btnBloquear.setText("Desbloquear");
        }
        else {
            lblEstado.setText("ACTIVO");
            lblEstado.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-padding: 3 10; -fx-background-radius: 5;");
            btnBloquear.setText("Bloquear");
        }
    }

    private void renderizarListas(TableroDTO tablero, boolean estaBloqueado) {
        contenedorListas.getChildren().clear();

        tablero.listas().forEach(lista -> {
            boolean esListaCompletada = lista.esListaCompletadas();

            VBox vBoxLista = new VBox(10);
            vBoxLista.setMinWidth(270);
            vBoxLista.setMaxWidth(270);

            if(esListaCompletada) {
                vBoxLista.setStyle("-fx-background-color: #dbeddb; -fx-background-radius: 5; -fx-padding: 10; -fx-border-radius: 5;");
            } else {
                vBoxLista.setStyle("-fx-background-color: #ebecf0; -fx-background-radius: 5; -fx-padding: 10;");
            }

            String textoTitulo = esListaCompletada ? "COMPLETADA: " + lista.titulo().toUpperCase() : lista.titulo().toUpperCase();
            Label lblTitulo = new Label(textoTitulo);
            lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14;" + (esListaCompletada ? "-fx-text-fill: green;" : ""));
            vBoxLista.getChildren().add(lblTitulo);

            if (lista.predecesorasIds() != null && !lista.predecesorasIds().isEmpty()) {
                List<String> nombresPre = tablero.listas().stream()
                    .filter(l -> lista.predecesorasIds().contains(l.id()))
                    .map(l -> l.titulo().toUpperCase())
                    .toList();

                Label lblWorkflowInfo = new Label("DEPENDE DE: " + String.join(", ", nombresPre));
                lblWorkflowInfo.setWrapText(true);
                lblWorkflowInfo.setStyle("-fx-font-size: 9; -fx-text-fill: #eb5a46; -fx-font-weight: bold; -fx-padding: 0 0 5 0;");
                vBoxLista.getChildren().add(lblWorkflowInfo);
            }

            String textoLimite = (lista.limiteTarjetas() == null) ?
                "Tarjetas: " + lista.tarjetas().size() :
                "Tarjetas: " + lista.tarjetas().size() + " / " + lista.limiteTarjetas();

            Label lblLimite = new Label(textoLimite);
            lblLimite.setStyle("-fx-font-size: 11; -fx-text-fill: #5e6c84;");
            vBoxLista.getChildren().add(lblLimite);

            HBox hbConfig = new HBox(5);

            Button btnConfigLimite = new Button("Límite");
            btnConfigLimite.setDisable(estaBloqueado);
            btnConfigLimite.setStyle("-fx-font-size: 10; -fx-background-color: #dfe1e6; -fx-cursor: hand;");
            btnConfigLimite.setOnAction(e -> handleCambiarLimiteLista(lista.id()));

            Button btnConfigWorkflow = new Button("Workflow");
            btnConfigWorkflow.setDisable(estaBloqueado);
            btnConfigWorkflow.setStyle("-fx-font-size: 10; -fx-background-color: #dfe1e6; -fx-cursor: hand;");
            btnConfigWorkflow.setOnAction(e -> handleConfigurarWorkflow(lista, tablero.listas()));

            hbConfig.getChildren().addAll(btnConfigLimite, btnConfigWorkflow);
            vBoxLista.getChildren().add(hbConfig);

            VBox contenedorTarjetas = new VBox(5);
            VBox.setVgrow(contenedorTarjetas, Priority.ALWAYS);

            lista.tarjetas().forEach(tarjeta -> {
                VBox card = new VBox(3);
                String colorFondo = tarjeta.completada() ? "#f8f9fa" : "white";
                card.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 3; -fx-padding: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1); -fx-cursor: hand;");

                Label lblCardTitulo = new Label(tarjeta.titulo());
                lblCardTitulo.setWrapText(true);
                String estiloTitulo = "-fx-font-size: 13; -fx-text-fill: #172b4d;";
                if(tarjeta.completada()) {
                    estiloTitulo += "-fx-strikethrough: true; -fx-text-fill: green;";
                }
                lblCardTitulo.setStyle(estiloTitulo);

                HBox hbEtiquetas = new HBox(4);
                hbEtiquetas.setPrefHeight(15);
                hbEtiquetas.setMinHeight(15);

                tarjeta.etiquetas().forEach(info -> {
                    String nombreEt = info;
                    String colorEt = "#0079BF";
                    if (info.contains("|")) {
                        String[] parts = info.split("\\|");
                        nombreEt = parts[0];
                        colorEt = parts[1];
                    }
                    Label lblEt = new Label(nombreEt);
                    lblEt.setStyle("-fx-background-color: " + colorEt + "; -fx-text-fill: white; -fx-font-size: 9; -fx-padding: 2 6; -fx-background-radius: 3; -fx-font-weight: bold;");
                    hbEtiquetas.getChildren().add(lblEt);
                });

                String subTextoStr = "[" + tarjeta.tipo() + "]";
                Label lblSub = new Label(subTextoStr);
                lblSub.setStyle("-fx-text-fill: #6b778c; -fx-font-size: 10;");
                lblSub.setWrapText(true);

                if (tarjeta.etiquetas().isEmpty()) {
                    card.getChildren().addAll(lblCardTitulo, lblSub);
                } else {
                    card.getChildren().addAll(hbEtiquetas, lblCardTitulo, lblSub);
                }
                card.setOnMouseClicked(e -> abrirDetalleTarjeta(tarjeta.id()));

                contenedorTarjetas.getChildren().add(card);
            });

            vBoxLista.getChildren().add(contenedorTarjetas);

            Button btnAddCard = new Button("+ Añadir una tarjeta");
            btnAddCard.setDisable(estaBloqueado);
            btnAddCard.setMaxWidth(Double.MAX_VALUE);
            btnAddCard.setAlignment(Pos.CENTER_LEFT);
            btnAddCard.setStyle("-fx-background-color: transparent; -fx-text-fill: #0052cc; -fx-cursor: hand; -fx-font-weight: bold;");
            btnAddCard.setOnAction(e -> abrirVentanaCrearTarjeta(lista.id()));
            vBoxLista.getChildren().add(btnAddCard);

            Separator sep = new Separator();
            vBoxLista.getChildren().add(sep);

            Button btnDelList = new Button("Eliminar esta lista");
            btnDelList.setMaxWidth(Double.MAX_VALUE);
            btnDelList.setAlignment(Pos.CENTER);
            btnDelList.setStyle("-fx-background-color: transparent; -fx-text-fill: #eb5a46; -fx-cursor: hand; -fx-font-size: 11;");

            String idLista = lista.id();
            btnDelList.setOnAction(e -> {
                try {
                    eliminarListaTareas.ejecutar(new EliminarListaTareasRequest(urlActual, idLista, emailUsuario));
                    cargarContenido();
                } catch(Exception ex) {
                    mostrarMensaje("Error", "No se pudo eliminar: " + ex.getMessage());
                }
            });
            vBoxLista.getChildren().add(btnDelList);

            contenedorListas.getChildren().add(vBoxLista);
        });

        Button btnAddOtherList = new Button("+ Añadir otra lista");
        btnAddOtherList.setMinWidth(270);
        btnAddOtherList.setDisable(estaBloqueado);
        btnAddOtherList.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        btnAddOtherList.setOnAction(this::handleAñadirLista);
        contenedorListas.getChildren().add(btnAddOtherList);
    }

    private void abrirVentanaCrearTarjeta(String listaId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TarjetaView.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            TarjetaDetalleControlador controller = loader.getController();
            controller.prepararCreacion(urlActual, listaId, emailUsuario);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nueva tarjeta");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            cargarContenido();
        }
        catch(IOException e) {
            mostrarMensaje("Error", "No se pudo abrir la ventana de la tarjeta");
        }
    }

    private void abrirDetalleTarjeta(String idTarjeta) {
        try {
        	TableroDTO tablero = accederTablero.ejecutar(this.urlActual);
            TarjetaDTO tarjeta = tablero.listas().stream()
                    .flatMap(l -> l.tarjetas().stream())
                    .filter(t -> t.id().equals(idTarjeta))
                    .findFirst().orElse(null);

            if(tarjeta == null) {
            	return;
            }

            String estadoNormalizado = (tablero.estado() != null) ? tablero.estado().trim().toUpperCase() : "ACTIVO";
            boolean estaBloqueado = "BLOQUEADO".equals(estadoNormalizado);

            String fxmlDoc = "CHECKLIST".equalsIgnoreCase(tarjeta.tipo())
                    ? "/fxml/ChecklistDetalleView.fxml"
                    : "/fxml/TareaDetalleView.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlDoc));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            TarjetaDetalleControlador ctrl = loader.getController();
            ctrl.inicializarDatos(this.urlActual, idTarjeta, this.emailUsuario, estaBloqueado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Detalle de " + tarjeta.tipo());
            stage.showAndWait();

            cargarContenido();
        }
        catch(IOException e) {
            mostrarMensaje("Error", "No se pudo abrir el detalle.");
        }
    }

    @FXML
    void handleBloquear(ActionEvent event) {
        try {
            bloquearTablero.ejecutar(new BloquearTableroRequest(urlActual, emailUsuario));
            cargarContenido();
        }
        catch(Exception e) {
            mostrarMensaje("Error", "Error al procesar bloqueo: " + e.getMessage());
        }
    }

    @FXML
    void handleAñadirLista(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaView.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            ListaControlador controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nueva lista");
            stage.setScene(new Scene(root, 380, 250));
            stage.showAndWait();

            String nombre = controller.getNombreResultado();
            boolean esCompletada = controller.isEsCompletada();
            Integer limite = controller.getLimiteResultado();

            if(nombre != null && !nombre.trim().isEmpty()) {
                crearListaTareas.ejecutar(new CrearListaTareasRequest(
                    urlActual,
                    nombre,
                    emailUsuario,
                    esCompletada,
                    limite
                ));
                cargarContenido();
            }
        }
        catch(Exception e) {
            mostrarMensaje("Error", "Error al crear lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleConfigurarWorkflow(ListaDTO listaActual, List<ListaDTO> todasLasListas) {
        List<ListaDTO> posiblesPredecesoras = todasLasListas.stream()
                .filter(l -> !l.id().equals(listaActual.id()))
                .toList();

        if (posiblesPredecesoras.isEmpty()) {
            mostrarMensaje("Configuración", "No hay otras listas disponibles para establecer dependencias.");
            return;
        }

        ListView<ListaDTO> listView = new ListView<>();
        listView.getItems().addAll(posiblesPredecesoras);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(ListaDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.titulo());
                }
            }
        });

        posiblesPredecesoras.forEach(l -> {
            if (listaActual.predecesorasIds().contains(l.id())) {
                listView.getSelectionModel().select(l);
            }
        });

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Configurar Flujo de Trabajo");
        dialog.setHeaderText("Selecciona las listas que deben completarse antes que: " + listaActual.titulo());

        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(listView);

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                return listView.getSelectionModel().getSelectedItems().stream()
                        .map(ListaDTO::id)
                        .toList();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(seleccionadosIds -> {
            try {
                ConfigurarWorkflowRequest request = new ConfigurarWorkflowRequest(
                    this.urlActual,
                    listaActual.id(),
                    seleccionadosIds,
                    this.emailUsuario
                );

                configurarWorkflowServicio.ejecutar(request);

                cargarContenido();
                mostrarMensaje("Éxito", "Workflow actualizado correctamente.");
            } catch (Exception e) {
                e.printStackTrace();

                String errorMsg = (e.getMessage() != null && !e.getMessage().isEmpty())
                                  ? e.getMessage()
                                  : "Error interno: " + e.toString();
                mostrarMensaje("Error de Workflow", errorMsg);
            }
        });
    }

    @FXML
    void handleVerTrazas(ActionEvent event) {
        List<TrazaDTO> trazas = verTrazas.ejecutar(urlActual);

        if (trazas.isEmpty()) {
            mostrarMensaje("Historial de Acciones", "No hay acciones registradas en este tablero.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Historial de Acciones");
        dialog.setHeaderText("Acciones realizadas en el tablero");

        ListView<String> listView = new ListView<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        trazas.forEach(t -> {
            String fechaStr = t.fecha() != null ? t.fecha().format(formatter) : "Sin fecha";
            listView.getItems().add("[" + fechaStr + "] " + t.detalles());
        });

        listView.setPrefWidth(600);
        listView.setPrefHeight(400);

        dialog.getDialogPane().setContent(listView);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    @FXML
    void handleFiltrar(ActionEvent event) {
        String textoFiltro = txtFiltroEtiquetas.getText();
        if (textoFiltro == null || textoFiltro.trim().isEmpty()) {
            cargarContenido();
            return;
        }

        List<String> etiquetas = java.util.Arrays.stream(textoFiltro.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        try {
            TableroDTO tableroFiltrado = filtrarTarjetas.ejecutar(urlActual, etiquetas);

            String estadoNormalizado = (tableroFiltrado.estado() != null) ? tableroFiltrado.estado().trim().toUpperCase() : "ACTIVO";
            boolean estaBloqueado = "BLOQUEADO".equals(estadoNormalizado);

            renderizarListas(tableroFiltrado, estaBloqueado);
        } catch (Exception e) {
            mostrarMensaje("Error de Filtrado", "No se pudo aplicar el filtro: " + e.getMessage());
        }
    }

    @FXML
    void handleLimpiarFiltro(ActionEvent event) {
        txtFiltroEtiquetas.clear();
        cargarContenido();
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}