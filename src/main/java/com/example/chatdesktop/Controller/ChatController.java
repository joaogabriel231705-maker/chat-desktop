package com.example.chatdesktop.Controller;

import com.example.chatdesktop.model.Mensagem;
import com.example.chatdesktop.service.GroqService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatController {

    @FXML
    private VBox mensagens;

    @FXML
    private TextField inputField;

    @FXML
    private Button sendButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button limparButton;

    private final GroqService groqService =
            new GroqService();

    @FXML
    public void initialize() {

        adicionarMensagem(
                new Mensagem(
                        "Olá! 👋\n\n" +
                                "Eu sou a Nexa AI. " +
                                "Como posso ajudar você hoje?",
                        false
                )
        );
    }

    @FXML
    private void enviarMensagem() {

        String mensagem =
                inputField.getText().trim();

        if (mensagem.isEmpty()) {
            return;
        }

        adicionarMensagem(
                new Mensagem(
                        mensagem,
                        true
                )
        );

        inputField.clear();

        sendButton.setDisable(true);
        inputField.setDisable(true);

        Label digitando =
                new Label(
                        "Nexa AI está digitando..."
                );

        digitando.setStyle(
                "-fx-text-fill: #7d8597;" +
                        "-fx-font-size: 12px;"
        );

        digitando.setPadding(
                new Insets(
                        0,
                        0,
                        0,
                        10
                )
        );

        mensagens.getChildren()
                .add(digitando);

        rolarParaBaixo();

        Thread thread = new Thread(() -> {

            try {

                String resposta =
                        groqService.enviarMensagem(
                                mensagem
                        );

                Platform.runLater(() -> {

                    mensagens.getChildren()
                            .remove(digitando);

                    adicionarMensagem(
                            new Mensagem(
                                    resposta,
                                    false
                            )
                    );

                    sendButton.setDisable(false);
                    inputField.setDisable(false);

                    inputField.requestFocus();
                });

            } catch (Exception e) {

                Platform.runLater(() -> {

                    mensagens.getChildren()
                            .remove(digitando);

                    adicionarMensagem(
                            new Mensagem(
                                    "❌ Ocorreu um erro:\n\n" +
                                            e.getMessage(),
                                    false
                            )
                    );

                    sendButton.setDisable(false);
                    inputField.setDisable(false);

                    inputField.requestFocus();
                });
            }

        });

        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void limparConversa() {

        mensagens.getChildren().clear();

        adicionarMensagem(
                new Mensagem(
                        "Olá! 👋\n\n" +
                                "Conversa nova iniciada. " +
                                "Como posso ajudar?",
                        false
                )
        );
    }

    private void adicionarMensagem(
            Mensagem mensagem
    ) {

        HBox linha =
                new HBox();

        linha.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox balao =
                new VBox(3);

        balao.setMaxWidth(500);

        Label nome =
                new Label(
                        mensagem.isUsuario()
                                ? "Você"
                                : "Nexa AI"
                );

        nome.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        (
                                mensagem.isUsuario()
                                        ? "#8ea2ff"
                                        : "#23d18b"
                        ) +
                        ";"
        );

        Label texto =
                new Label(
                        mensagem.getTexto()
                );

        texto.setWrapText(true);

        texto.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #e6e9ef;"
        );

        balao.getChildren()
                .addAll(
                        nome,
                        texto
                );

        balao.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14
                )
        );

        if (mensagem.isUsuario()) {

            balao.setStyle(
                    "-fx-background-color: #5865F2;" +
                            "-fx-background-radius: " +
                            "15 15 4 15;"
            );

            texto.setStyle(
                    "-fx-font-size: 14px;" +
                            "-fx-text-fill: white;"
            );

            linha.setAlignment(
                    Pos.CENTER_RIGHT
            );

        } else {

            balao.setStyle(
                    "-fx-background-color: #1b1f27;" +
                            "-fx-background-radius: " +
                            "15 15 15 4;" +
                            "-fx-border-color: #292e38;" +
                            "-fx-border-radius: " +
                            "15 15 15 4;"
            );

            linha.setAlignment(
                    Pos.CENTER_LEFT
            );
        }

        linha.getChildren()
                .add(balao);

        mensagens.getChildren()
                .add(linha);

        rolarParaBaixo();
    }

    private void rolarParaBaixo() {

        Platform.runLater(() -> {

            scrollPane.layout();

            scrollPane.setVvalue(1.0);
        });
    }
}