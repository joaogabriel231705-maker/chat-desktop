package com.example.chatdesktop.dao;

import com.example.chatdesktop.model.Conversa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConversaDAO {

    public long criar(Conversa conversa) {

        String sql = """
                INSERT INTO conversas
                (titulo, titulo_editado)
                VALUES (?, ?)
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(
                    1,
                    conversa.getTitulo()
            );

            statement.setInt(
                    2,
                    conversa.isTituloEditado()
                            ? 1
                            : 0
            );

            statement.executeUpdate();

            try (
                    ResultSet keys =
                            statement.getGeneratedKeys()
            ) {

                if (keys.next()) {

                    long id =
                            keys.getLong(1);

                    conversa.setId(id);

                    return id;
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao criar conversa: "
                            + e.getMessage()
            );
        }

        return -1;
    }


    public List<Conversa> listar() {

        List<Conversa> conversas =
                new ArrayList<>();

        String sql = """
                SELECT id, titulo, titulo_editado
                FROM conversas
                ORDER BY id DESC
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                Conversa conversa =
                        new Conversa(
                                result.getLong("id"),
                                result.getString("titulo"),
                                result.getInt(
                                        "titulo_editado"
                                ) == 1
                        );

                conversas.add(conversa);
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao carregar conversas: "
                            + e.getMessage()
            );
        }

        return conversas;
    }


    public void atualizarTitulo(
            Conversa conversa
    ) {

        String sql = """
                UPDATE conversas
                SET titulo = ?,
                    titulo_editado = ?
                WHERE id = ?
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    conversa.getTitulo()
            );

            statement.setInt(
                    2,
                    conversa.isTituloEditado()
                            ? 1
                            : 0
            );

            statement.setLong(
                    3,
                    conversa.getId()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao atualizar título: "
                            + e.getMessage()
            );
        }
    }


    public void excluir(long id) {

        String sql = """
                DELETE FROM conversas
                WHERE id = ?
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao excluir conversa: "
                            + e.getMessage()
            );
        }
    }
}