package com.example.javafx_cafetera;

/**
 * Clase que representa a un camarero en la simulación de la cafetería.
 * Hereda directamente de Thread para ejecutar la lógica de forma concurrente.
 * Gestiona el ciclo de atención de clientes.
 */
public class Camarero extends Thread {
    private final String nombre;
    private final Cafeteria cafeteria;
    private volatile String estado = "Disponible";

    /**
     * Constructor del camarero.
     * @param nombre El nombre del camarero.
     * @param cafeteria La instancia de la cafetería a la que pertenece.
     */
    public Camarero(String nombre, Cafeteria cafeteria) {
        this.nombre = nombre;
        this.cafeteria = cafeteria;
        setDaemon(false);
    }

    /**
     * Obtiene el nombre del camarero.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el estado actual del camarero.
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Método ejecutado en el hilo del camarero.
     * Gestiona el ciclo de atención de clientes de forma continua.
     */
    @Override
    public void run() {
        cafeteria.registrar(nombre + " ha empezado a trabajar.");

        while (cafeteria.estaEnEjecucion() && !Thread.currentThread().isInterrupted()) {
            try {
                Cliente cliente = cafeteria.obtenerClienteDesCola(500);

                if (cliente != null) {
                    estado = "Sirviendo a " + cliente.getNombre();
                    cafeteria.registrar(nombre + " atiende a " + cliente.getNombre() + ".");

                    // Simular tiempo de preparación del café (2-8 segundos)
                    Thread.sleep(2000 + (int) (Math.random() * 6000));

                    // Notificar cliente atendido (usar wait/notify en el cliente)
                    cliente.notificarAtendido();
                    cafeteria.notificarClienteAtendido(cliente, nombre);
                    estado = "Disponible";
                } else {
                    // No hay cliente en este momento, pero seguimos esperando
                    estado = "Esperando";
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        cafeteria.registrar(nombre + " ha dejado de trabajar.");
    }
}