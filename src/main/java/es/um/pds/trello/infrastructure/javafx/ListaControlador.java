package es.um.pds.trello.infrastructure.javafx;

import org.springframework.stereotype.Component;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Component
public class ListaControlador {
    @FXML
    private TextField txtNombreLista;

    @FXML
    private CheckBox chkEsCompletada;

    @FXML
    private TextField txtLimite;

    private String nombreResultado = null;
    private boolean esCompletadaResultado = false;
    private Integer limiteResultado = null;

    @FXML
    void handleCrear() {
        if (txtNombreLista.getText().trim().isEmpty()) {
            return;
        }

        try {
            nombreResultado = txtNombreLista.getText();
            esCompletadaResultado = chkEsCompletada.isSelected();

            String limiteStr = txtLimite.getText().trim();
            if (limiteStr.isEmpty()) {
                limiteResultado = null;
            } else {
                limiteResultado = Integer.parseInt(limiteStr);
                if (limiteResultado < 0) throw new NumberFormatException();
            }

            cerrar();
        } catch (NumberFormatException e) {
            txtLimite.setStyle("-fx-border-color: red;");
            System.err.println("El límite debe ser un número positivo.");
        }
    }

    @FXML
    void handleCancelar() {
        nombreResultado = null;
        cerrar();
    }

    private void cerrar() {
        ((Stage) txtNombreLista.getScene().getWindow()).close();
    }

    public String getNombreResultado() { return nombreResultado; }
    public boolean isEsCompletada() { return esCompletadaResultado; }
    public Integer getLimiteResultado() { return limiteResultado; }
}