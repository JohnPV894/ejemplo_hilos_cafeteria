package com.example.javafx_cafetera;

/**
 * Representa un pedido realizado por un Cliente.
 * Contiene el cliente asociado y el identificador de la bebida.
 */
public class Pedido {
    private final Cliente cliente;
    private final String bebida;

    public Pedido(Cliente cliente, String bebida) {
        this.cliente = cliente;
        this.bebida = bebida;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getBebida() {
        return bebida;
    }
}
