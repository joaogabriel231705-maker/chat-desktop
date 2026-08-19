package com.example.chatdesktop.model;

public class Mensagem {

    private final String texto;
    private final boolean usuario;

    public Mensagem(
            String texto,
            boolean usuario
    ) {

        this.texto = texto;
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isUsuario() {
        return usuario;
    }
}