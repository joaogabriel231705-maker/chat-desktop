
package com.example.chatdesktop.model;

public class mensagem {

    private final String texto;
    private final boolean usuario;

    public mensagem(
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

