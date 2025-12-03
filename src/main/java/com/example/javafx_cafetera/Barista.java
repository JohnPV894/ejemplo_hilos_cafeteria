package com.example.javafx_cafetera;

/**
 * Barista: consumidor que prepara los pedidos de la cola de pedidos pendientes.
 * Hereda de Thread y notifica al Cliente cuando el café está listo.
 */
public class Barista extends Thread {
    private final String nombre;
    private final Cafeteria cafeteria;
    private volatile String estado = "Disponible";

    public Barista(String nombre, Cafeteria cafeteria) {
        this.nombre = nombre;
        this.cafeteria = cafeteria;
        setDaemon(false);
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public void run() {
        cafeteria.registrar(nombre + " ha empezado como Barista.");

        while (cafeteria.estaEnEjecucion() && !Thread.currentThread().isInterrupted()) {
            try {
                // Obtener pedido con timeout
                Pedido pedido = cafeteria.obtenerPedidoDesCola(1000);
                if (pedido != null) {
                    Cliente cliente = pedido.getCliente();
                    estado = "Preparando café para " + cliente.getNombre();
                    cafeteria.registrar(nombre + " está preparando " + pedido.getBebida() + " para " + cliente.getNombre() + ".");

                    // Simular tiempo de preparación 2-8 segundos
                    long duracion = 2000 + (int) (Math.random() * 6000);
                    Thread.sleep(duracion);

                    // Notificar cliente que su café está listo
                    cliente.notificarAtendido();

                    cafeteria.registrar(nombre + " ha finalizado el café de " + cliente.getNombre() + ".");
                    estado = "Disponible";
                } else {
                    estado = "Esperando pedidos";
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        cafeteria.registrar(nombre + " ha dejado de ser Barista.");
    }
}
