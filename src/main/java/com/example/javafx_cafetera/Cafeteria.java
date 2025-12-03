package com.example.javafx_cafetera;

import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Clase modelo que gestiona la simulación de la cafetería.
 * Administra camareros, clientes, cola de atención y la lógica de simulación continua.
 */
public class Cafeteria {
    private final BlockingQueue<Cliente> cola = new LinkedBlockingQueue<>();
    private final List<Camarero> camareros = Collections.synchronizedList(new ArrayList<>());
    private final List<Cliente> clientes = Collections.synchronizedList(new ArrayList<>());

    private final ExecutorService ejecutor = Executors.newCachedThreadPool();
    private volatile boolean enEjecucion = false;

    private final CafeteriaController controlador;

    // Parámetros de la simulación
    private final int NUM_CAMAREROS = 5;
    private final int NUM_CLIENTES = 10;
    private int contadorClientesDinamicos = 0;

    /**
     * Constructor de la cafetería.
     * @param controlador El controlador que gestiona la interfaz gráfica.
     */
    public Cafeteria(CafeteriaController controlador) {
        this.controlador = controlador;
    }

    /**
     * Inicia la simulación de la cafetería.
     * Crea camareros y clientes, y comienza los hilos de ejecución.
     */
    public void iniciarSimulacion() {
        enEjecucion = true;

        // Crear camareros
        for (int i = 1; i <= NUM_CAMAREROS; i++) {
            Camarero c = new Camarero("Camarero-" + i, this);
            camareros.add(c);
            c.start();
        }

        // Crear clientes iniciales
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            Cliente cl = new Cliente("Cliente-" + i, this);
            clientes.add(cl);
            cl.start();
            // Pequeño desfase para simular llegadas
            dormirSinVerificacion(200);
        }

        // Actualizar interfaz periódicamente (Thread de actualización)
        Thread actualizadorUI = new Thread(new ActualizadorInterfaz());
        actualizadorUI.setDaemon(true);
        actualizadorUI.start();
    }

    /**
     * Detiene la simulación de forma segura.
     */
    public void detenerSimulacion() {
        enEjecucion = false;
        ejecutor.shutdownNow();
        try {
            if (!ejecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                // Forzar terminación si es necesario
            }
        } catch (InterruptedException ignorado) {
            Thread.currentThread().interrupt();
        }
        // Limpiar colecciones
        camareros.clear();
        clientes.clear();
        cola.clear();
        contadorClientesDinamicos = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controlador.simulacionFinalizada();
            }
        });
    }

    /**
     * Añade un nuevo cliente dinámicamente durante la simulación.
     */
    public void anadirClienteDinamico() {
        if (enEjecucion) {
            contadorClientesDinamicos++;
            Cliente nuevoCliente = new Cliente("Cliente-Dinámico-" + contadorClientesDinamicos, this);
            clientes.add(nuevoCliente);
            nuevoCliente.start();
            registrar("Nuevo cliente añadido: " + nuevoCliente.getNombre());
        }
    }

    /**
     * Verifica si la simulación está en ejecución.
     */
    public boolean estaEnEjecucion() {
        return enEjecucion;
    }

    /**
     * Añade un cliente a la cola de espera.
     */
    public void encolarCliente(Cliente c) throws InterruptedException {
        cola.put(c);
        registrar(String.format("%s se ha puesto en cola.", c.getNombre()));
        actualizarInterfaz();
    }

    /**
     * Obtiene un cliente de la cola con timeout.
     */
    public Cliente obtenerClienteDesCola(long tiempoEsperaMs) throws InterruptedException {
        return cola.poll(tiempoEsperaMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Registra un mensaje en el área de registro de la interfaz.
     */
    public void registrar(String mensaje) {
        String ts = String.format("[%tT] ", new Date());
        String completo = ts + mensaje;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controlador.anadirRegistro(completo);
            }
        });
    }

    /**
     * Notifica que un cliente ha sido atendido.
     */
    public void notificarClienteAtendido(Cliente c, String nombreCamarero) {
        registrar(String.format("%s atendido por %s.", c.getNombre(), nombreCamarero));
        actualizarInterfaz();
    }

    /**
     * Actualiza la interfaz gráfica con los estados actuales.
     */
    private void actualizarInterfaz() {
        // Construir listas de estados
        List<String> estadosClientes;
        synchronized (clientes) {
            estadosClientes = new ArrayList<>();
            for (Cliente cl : clientes) {
                estadosClientes.add(cl.getNombre() + " - " + cl.getEstado());
            }
        }

        List<String> estadosCamareros;
        synchronized (camareros) {
            estadosCamareros = new ArrayList<>();
            for (Camarero cm : camareros) {
                estadosCamareros.add(cm.getNombre() + " - " + cm.getEstado());
            }
        }

        // Llamadas seguras al controlador
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controlador.actualizarListaClientes(estadosClientes);
                controlador.actualizarListaCamareros(estadosCamareros);
            }
        });
    }

    /**
     * Duerme el hilo de forma segura sin lanzar excepción.
     */
    private void dormirSinVerificacion(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignorado) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Clase interna para actualizar la interfaz periódicamente.
     */
    private class ActualizadorInterfaz implements Runnable {
        @Override
        public void run() {
            while (enEjecucion) {
                actualizarInterfaz();
                dormirSinVerificacion(500);
            }
        }
    }
}