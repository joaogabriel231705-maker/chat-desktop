package com.example.chatdesktop.dao;

import com.example.chatdesktop.model.mensagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensagemDAO {

    public void salvar(
            long conversaId,
            mensagem mensagem
    ) {

        String sql = """
                INSERT INTO mensagens
                (conversa_id, usuario, texto)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    conversaId
            );

            statement.setInt(
                    2,
                    mensagem.isUsuario()
                            ? 1
                            : 0
            );

            statement.setString(
                    3,
                    mensagem.getTexto()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao salvar mensagem: "
                            + e.getMessage()
            );
        }
    }


    public List<mensagem> listarPorConversa(
            long conversaId
    ) {

        List<mensagem> mensagens =
                new ArrayList<>();

        String sql = """
                SELECT usuario, texto
                FROM mensagens
                WHERE conversa_id = ?
                ORDER BY id ASC
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    conversaId
            );

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                while (result.next()) {

                    mensagens.add(
                            new mensagem(
                                    result.getString("texto"),
                                    result.getInt("usuario") == 1
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao carregar mensagens: "
                            + e.getMessage()
            );
        }

        return mensagens;
    }


    public void excluirUltimaMensagemIA(
            long conversaId
    ) {

        String sql = """
                DELETE FROM mensagens
                WHERE id = (
                    SELECT id
                    FROM mensagens
                    WHERE conversa_id = ?
                    AND usuario = 0
                    ORDER BY id DESC
                    LIMIT 1
                )
                """;

        try (
                Connection connection =
                        Database.conectar();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    conversaId
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao remover mensagem da IA: "
                            + e.getMessage()
            );
        }
    }
}