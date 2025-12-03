package com.example.javafx_cafetera;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controlador de la interfaz gráfica de la cafetería.
 * Gestiona la interacción del usuario y actualiza la vista según el estado del modelo.
 */
public class CafeteriaController {
    @FXML
    private ListView<String> listaCamareros;

    @FXML
    private ListView<String> listaBaristas;

    @FXML
    private ListView<Cliente> listaClientes;

    @FXML
    private TextArea areaRegistro;

    @FXML
    private Button botonIniciar;

    @FXML
    private Button botonReiniciar;

    @FXML
    private Button botonAnadirCliente;

    @FXML
    private ProgressBar barraPedidos;

    @FXML
    private Label labelPedidos;

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

        // Configurar CellFactory para listaClientes mostrando barra de paciencia
        listaClientes.setCellFactory(new javafx.util.Callback<ListView<Cliente>, ListCell<Cliente>>() {
            @Override
            public ListCell<Cliente> call(ListView<Cliente> param) {
                return new ListCell<Cliente>() {
                    @Override
                    protected void updateItem(Cliente item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            // Crear HBox con nombre y barra de paciencia
                            HBox celda = new HBox(8);
                            celda.setPadding(new Insets(5));

                            // Etiqueta con nombre y estado
                            Label etiqueta = new Label(item.getNombre() + " - " + item.getEstado());
                            etiqueta.setStyle("-fx-font-size: 11;");

                            // ProgressBar de paciencia
                            ProgressBar barraEspera = new ProgressBar();
                            barraEspera.setPrefWidth(80);
                            barraEspera.setStyle("-fx-padding: 2;");

                            // Calcular progreso: (tiempo transcurrido) / (paciencia total)
                            if (item.getTiempoInicioEspera() > 0 && item.getEstado().contains("Esperando")) {
                                long tiempoTranscurrido = System.currentTimeMillis() - item.getTiempoInicioEspera();
                                double progreso = (double) tiempoTranscurrido / item.getPacienciaMs();
                                barraEspera.setProgress(Math.min(progreso, 1.0));
                            } else {
                                barraEspera.setProgress(0);
                            }

                            celda.getChildren().addAll(etiqueta, barraEspera);
                            HBox.setHgrow(barraEspera, Priority.ALWAYS);
                            setGraphic(celda);
                        }
                    }
                };
            }
        });

        if (barraPedidos != null) {
            barraPedidos.setProgress(0);
        }
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
    public void actualizarListaClientes(List<Cliente> clientesActuales) {
        if (Platform.isFxApplicationThread()) {
            listaClientes.getItems().setAll(clientesActuales);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listaClientes.getItems().setAll(clientesActuales);
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
     * Actualiza la lista de baristas en la interfaz.
     */
    public void actualizarListaBaristas(List<String> estadosBaristas) {
        if (Platform.isFxApplicationThread()) {
            listaBaristas.getItems().setAll(estadosBaristas);
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    listaBaristas.getItems().setAll(estadosBaristas);
                }
            });
        }
    }

    /**
     * Actualiza la barra de progreso de pedidos pendientes.
     */
    public void actualizarBarraPedidos(int actual, int maximo) {
        if (Platform.isFxApplicationThread()) {
            if (barraPedidos != null) {
                double progreso = (double) actual / maximo;
                barraPedidos.setProgress(Math.min(progreso, 1.0));
            }
            if (labelPedidos != null) {
                labelPedidos.setText("Pedidos: " + actual + "/" + maximo);
            }
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (barraPedidos != null) {
                        double progreso = (double) actual / maximo;
                        barraPedidos.setProgress(Math.min(progreso, 1.0));
                    }
                    if (labelPedidos != null) {
                        labelPedidos.setText("Pedidos: " + actual + "/" + maximo);
                    }
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