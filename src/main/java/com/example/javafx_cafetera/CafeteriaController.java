package com.example.javafx_cafetera;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.List;

/**
 * Controlador de la interfaz gráfica de la cafetería.
 * Gestiona la interacción del usuario y actualiza la vista según el estado del modelo.
 */
public class CafeteriaController {
    @FXML
    private ListView<String> listaClientes;

    @FXML
    private ListView<String> listaCamareros;

    @FXML
    private TextArea areaRegistro;

    @FXML
    private Button botonIniciar;

    @FXML
    private Button botonReiniciar;

    @FXML
    private Button botonAnadirCliente;

    private Cafeteria cafeteria;

    private boolean enEjecucion = false;

    /**
     * Método de inicialización del controlador.
     * Configura los controles de la interfaz.
     */
    @FXML
    private void initialize() {
        areaRegistro.setEditable(false);
        areaRegistro.setWrapText(true);

        botonReiniciar.setDisable(true);
        botonAnadirCliente.setDisable(true);
    }

    /**
     * Manejador del clic en el botón "Iniciar Simulación".
     * Crea una nueva instancia de Cafeteria e inicia la simulación.
     */
    @FXML
    private void alIniciarClicked(MouseEvent evento) {
        if (!enEjecucion) {
            cafeteria = new Cafeteria(this);
            cafeteria.iniciarSimulacion();
            enEjecucion = true;
            botonIniciar.setDisable(true);
            botonReiniciar.setDisable(false);
            botonAnadirCliente.setDisable(false);
            anadirRegistro("Simulación iniciada.");
        }
    }

    /**
     * Manejador del clic en el botón "Reiniciar Simulación".
     * Detiene la simulación actual y la reinicia desde cero.
     */
    @FXML
    private void alReiniciarClicked(MouseEvent evento) {
        if (cafeteria != null) {
            cafeteria.detenerSimulacion();
            anadirRegistro("Simulación detenida (reinicio).");

            cafeteria = new Cafeteria(this);
            cafeteria.iniciarSimulacion();
            anadirRegistro("Simulación reiniciada.");
            botonAnadirCliente.setDisable(false);
        }
    }

    /**
     * Manejador del clic en el botón "Añadir Cliente".
     * Instancia un nuevo cliente y lo agrega a la simulación activa.
     */
    @FXML
    private void alAnadirClienteClicked(MouseEvent evento) {
        if (cafeteria != null && enEjecucion) {
            cafeteria.anadirClienteDinamico();
        }
    }

    /**
     * Añade un mensaje al área de registro.
     * Ejecutado en el hilo de JavaFX si es necesario.
     */
    public void anadirRegistro(String mensaje) {
        if (Platform.isFxApplicationThread()) {
            areaRegistro.appendText(mensaje + "\n");
            areaRegistro.setScrollTop(Double.MAX_VALUE);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    areaRegistro.appendText(mensaje + "\n");
                    areaRegistro.setScrollTop(Double.MAX_VALUE);
                }
            });
        }
    }

    /**
     * Actualiza la lista de clientes en la interfaz.
     */
    public void actualizarListaClientes(List<String> estadosClientes) {
        if (Platform.isFxApplicationThread()) {
            listaClientes.getItems().setAll(estadosClientes);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listaClientes.getItems().setAll(estadosClientes);
                }
            });
        }
    }

    /**
     * Actualiza la lista de camareros en la interfaz.
     */
    public void actualizarListaCamareros(List<String> estadosCamareros) {
        if (Platform.isFxApplicationThread()) {
            listaCamareros.getItems().setAll(estadosCamareros);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listaCamareros.getItems().setAll(estadosCamareros);
                }
            });
        }
    }

    /**
     * Método llamado al finalizar la simulación para actualizar el estado de los botones.
     */
    public void simulacionFinalizada() {
        enEjecucion = false;
        botonIniciar.setDisable(false);
        botonReiniciar.setDisable(true);
        botonAnadirCliente.setDisable(true);
    }
}