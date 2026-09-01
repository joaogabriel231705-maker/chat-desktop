package com.example.chatdesktop.model;

import java.util.ArrayList;
import java.util.List;

public class Conversa {

    private long id;

    private String titulo = "Conversa atual";

    private boolean tituloEditado = false;

    private final List<mensagem> mensagens =
            new ArrayList<>();


    public Conversa() {
    }


    public Conversa(
            long id,
            String titulo,
            boolean tituloEditado
    ) {

        this.id = id;
        this.titulo = titulo;
        this.tituloEditado = tituloEditado;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getTitulo() {
        return titulo;
    }


    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    public boolean isTituloEditado() {
        return tituloEditado;
    }


    public void setTituloEditado(
            boolean tituloEditado
    ) {

        this.tituloEditado = tituloEditado;
    }


    public List<mensagem> getMensagens() {
        return mensagens;
    }
}