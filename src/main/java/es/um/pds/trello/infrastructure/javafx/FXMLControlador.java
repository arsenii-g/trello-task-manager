package es.um.pds.trello.infrastructure.javafx;

import javafx.scene.paint.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import es.um.pds.trello.application.dto.TableroDTO;
import es.um.pds.trello.application.dto.request.CrearTableroRequest;
import es.um.pds.trello.application.ports.in.IAccederTablero;
import es.um.pds.trello.application.ports.in.ICrearDesdePlantilla;
import es.um.pds.trello.application.ports.in.ICrearTablero;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

@Component
public class FXMLControlador {
	@Autowired
	private ApplicationContext context;

	@Autowired
	private ICrearTablero crearTablero;

	@Autowired
	private IAccederTablero accederTablero;

	@Autowired
	private ICrearDesdePlantilla crearDesdePlantilla;

	@FXML
	private TextField txtEmail;

	@FXML
	private TextField txtTituloTablero;

	@FXML
	private TextField txtUrlTablero;

	@FXML
	private Label lblMensaje;

	@FXML
	private Label lblArchivoSeleccionado;

	private File archivoYAMLSeleccionado;

	public FXMLControlador(ApplicationContext context, ICrearTablero crearTablero, IAccederTablero accederTablero,
			ICrearDesdePlantilla crearDesdePlantilla) {
		this.context = context;
		this.crearTablero = crearTablero;
		this.accederTablero = accederTablero;
		this.crearDesdePlantilla = crearDesdePlantilla;
	}

	@FXML
	void handleCrearTablero(ActionEvent event) {
		String email = txtEmail.getText();
		String titulo = txtTituloTablero.getText();

		if(email == null || email.trim().isEmpty()) {
			mostrarMensaje("Error: Debes introducir un email para identificarte.");
			return;
		}

		if(titulo == null || titulo.trim().isEmpty()) {
			mostrarMensaje("Error: El tablero tiene que tener un nombre.");
			return;
		}

		try {
			TableroDTO tablero = crearTablero.ejecutar(new CrearTableroRequest(email, email, titulo));

			abrirVistaTablero(event, tablero.url(), email);
		}
		catch(Exception e) {
			mostrarMensaje("Error al crear: " + e.getMessage());
		}
	}

	@FXML
	void handleAccederTablero(ActionEvent event) {
		String url = txtUrlTablero.getText();
		String email = txtEmail.getText();

		if(email == null || email.trim().isEmpty()) {
			mostrarMensaje("Error: Debes introducir un email para identificarte y registrar tus acciones.");
			return;
		}

		if(url == null || url.trim().isEmpty()) {
			mostrarMensaje("Error: Debes introducir la URL del tablero.");
			return;
		}

		try {
			TableroDTO tablero = accederTablero.ejecutar(url);
			if(tablero != null) {
				abrirVistaTablero(event, tablero.url(), email);
			}
		}
		catch(Exception e) {
			mostrarMensaje("URL inválida o tablero inexistente.");
		}
	}

	@FXML
	void handleSeleccionarArchivo(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Seleccionar archivo YAML");
		fileChooser.getExtensionFilters().addAll(
			new FileChooser.ExtensionFilter("Archivos YAML", "*.yaml", "*.yml"),
			new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
		);

		File archivoSeleccionado = fileChooser.showOpenDialog(stage);

		if (archivoSeleccionado != null) {
			archivoYAMLSeleccionado = archivoSeleccionado;
			lblArchivoSeleccionado.setText("Archivo " + archivoSeleccionado.getName());
			limpiarMensaje();
		}
	}

	@FXML
	void handleImportarYAML(ActionEvent event) {
		String email = txtEmail.getText();

		if(email == null || email.trim().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Email requerido");
			alert.setHeaderText("Debes introducir tu email");
			alert.setContentText("Para importar un tablero desde una plantilla YAML, es necesario que proporciones tu correo electrónico.\n\nEsto permite identificarte como propietario del tablero y registrar las acciones realizadas.");
			alert.showAndWait();
			return;
		}

		if(archivoYAMLSeleccionado == null) {
			mostrarMensaje("Error: Debes seleccionar un archivo YAML primero.");
			return;
		}

		try {

			String contenidoYAML = new String(Files.readAllBytes(archivoYAMLSeleccionado.toPath()), StandardCharsets.UTF_8);

			TableroDTO tablero = crearDesdePlantilla.ejecutar(email, email, contenidoYAML);
			limpiarMensaje();
			abrirVistaTablero(event, tablero.url(), email);
		}
		catch(Exception e) {
			mostrarMensaje("Error al importar: " + e.getMessage());
		}
	}

	private void abrirVistaTablero(ActionEvent event, String url, String email) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TableroView.fxml"));
		loader.setControllerFactory(context::getBean);
		Parent root = loader.load();

		TableroControlador controller = loader.getController();
		controller.inicializarDatos(url, email);

		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setScene(new Scene(root, 1200, 600));
		stage.centerOnScreen();
		stage.show();
	}

	private void mostrarMensaje(String mensaje) {
		lblMensaje.setText(mensaje);
		lblMensaje.setTextFill(Color.RED);
	}

	private void limpiarMensaje() {
		lblMensaje.setText("");
	}
}