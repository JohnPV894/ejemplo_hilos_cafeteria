package com.example.javafx_cafetera;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

/**
 * Aplicación principal de JavaFX para la simulación de cafetería.
 * Inicia la ventana principal y carga la vista FXML.
 */
public class CafeteriaApp extends Application {
    @Override
    public void start(Stage escena) throws Exception {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("CafeteriaView.fxml"));
        Parent raiz = cargador.load();
        Scene vista = new Scene(raiz, 1000, 600);
        // Cargar estilos globales
        try {
            String cssResource = getClass().getResource("/estilos.css").toExternalForm();
            vista.getStylesheets().add(cssResource);
        } catch (Exception e) {
            System.err.println("No se pudo cargar estilos.css: " + e.getMessage());
        }
        escena.setTitle("Simulación Cafetería - JavaFX (MVC)");
        escena.setScene(vista);
        escena.show();
    }

}