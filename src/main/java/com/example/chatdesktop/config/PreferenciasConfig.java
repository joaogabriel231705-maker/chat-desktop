package com.example.chatdesktop.config;

import java.io.*;
import java.util.Properties;

public class PreferenciasConfig {

    private static final String ARQUIVO =
            System.getProperty("user.home") + File.separator + ".nexa-ai-preferencias.properties";

    private static final String CHAVE_TEMA = "tema";

    public static void salvarTema(String tema) {
        Properties propriedades = new Properties();

        try {
            File arquivo = new File(ARQUIVO);

            if (arquivo.exists()) {
                try (FileInputStream input = new FileInputStream(arquivo)) {
                    propriedades.load(input);
                }
            }

            propriedades.setProperty(CHAVE_TEMA, tema);

            try (FileOutputStream output = new FileOutputStream(arquivo)) {
                propriedades.store(output, "Preferencias do Nexa AI");
            }

        } catch (IOException e) {
            System.out.println("Erro ao salvar preferência de tema: " + e.getMessage());
        }
    }

    public static String carregarTema() {
        Properties propriedades = new Properties();

        try {
            File arquivo = new File(ARQUIVO);

            if (!arquivo.exists()) {
                return "dark";
            }

            try (FileInputStream input = new FileInputStream(arquivo)) {
                propriedades.load(input);
            }

            return propriedades.getProperty(CHAVE_TEMA, "dark");

        } catch (IOException e) {
            System.out.println("Erro ao carregar preferência de tema: " + e.getMessage());
            return "dark";
        }
    }
}