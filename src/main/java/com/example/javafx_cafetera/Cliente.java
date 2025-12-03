package com.example.javafx_cafetera;

/**
 * Clase que representa a un cliente en la simulación de la cafetería.
 * Hereda directamente de Thread para ejecutar la lógica de forma concurrente.
 * Simula la llegada, espera (con paciencia limitada) y reacción tras ser atendido.
 */
public class Cliente extends Thread {
    private final String nombre;
    private final Cafeteria cafeteria;
    private volatile String estado = "Pendiente";

    // Paciencia en milisegundos (entre 5s y 15s)
    private final long pacienciaMs;

    // Indicador de si fue servido
    private boolean servido = false;

    /**
     * Constructor del cliente.
     * @param nombre El nombre del cliente.
     * @param cafeteria La instancia de la cafetería a la que pertenece.
     */
    public Cliente(String nombre, Cafeteria cafeteria) {
        this.nombre = nombre;
        this.cafeteria = cafeteria;
        this.pacienciaMs = 5000 + (long) (Math.random() * 10000); // 5s - 15s
        setDaemon(false);
    }

    /**
     * Obtiene el nombre del cliente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el estado actual del cliente.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Método ejecutado en el hilo del cliente.
     * Simula la llegada, espera con paciencia limitada y reacción según
     * haya sido notificado por el camarero o se haya agotado la paciencia.
     */
    @Override
    public void run() {
        try {
            // Llegada con cierto retardo aleatorio
            long retardoLlegada = 200 + (int) (Math.random() * 1500);
            Thread.sleep(retardoLlegada);

            estado = "Llegó";
            cafeteria.registrar(nombre + " ha llegado.");

            estado = "En cola";
            cafeteria.encolarCliente(this);

            // Esperar a ser atendido usando wait/notify con timeout de paciencia
            long inicio = System.currentTimeMillis();
            long restante = pacienciaMs;
            synchronized (this) {
                while (!servido && restante > 0) {
                    wait(restante);
                    long ahora = System.currentTimeMillis();
                    restante = pacienciaMs - (ahora - inicio);
                }
            }

            if (servido) {
                estado = "Atendido";
                cafeteria.registrar(nombre + " se fue con su café.");
            } else {
                estado = "Se fue sin café";
                cafeteria.registrar(nombre + " se fue sin su café (paciencia agotada).");
            }

            // Simular que el cliente se marcha luego de un breve tiempo
            Thread.sleep(300);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            estado = "Interrumpido";
            cafeteria.registrar(nombre + " fue interrumpido.");
        }
    }

    /**
     * Notifica al cliente que ha sido atendido por el camarero.
     * Utiliza notify() dentro de un bloque sincronizado para despertar al cliente.
     */
    public void notificarAtendido() {
        synchronized (this) {
            servido = true;
            notify();
        }
    }
}