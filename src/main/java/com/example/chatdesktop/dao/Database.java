package com.example.chatdesktop.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:chat.db";

    private Database() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void criarTabelas() {

        String sqlConversas = """
                CREATE TABLE IF NOT EXISTS conversas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    titulo_editado INTEGER NOT NULL DEFAULT 0,
                    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP
                )
                """;

        String sqlMensagens = """
                CREATE TABLE IF NOT EXISTS mensagens (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    conversa_id INTEGER NOT NULL,
                    usuario INTEGER NOT NULL,
                    texto TEXT NOT NULL,
                    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (conversa_id)
                        REFERENCES conversas(id)
                        ON DELETE CASCADE
                )
                """;

        try (
                Connection connection = conectar();
                Statement statement = connection.createStatement()
        ) {

            // Ativa exclusão em cascata
            statement.execute("PRAGMA foreign_keys = ON");

            statement.execute(sqlConversas);
            statement.execute(sqlMensagens);

            System.out.println(
                    "SQLite inicializado com sucesso!"
            );

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao criar banco SQLite: "
                            + e.getMessage()
            );
        }
    }
}