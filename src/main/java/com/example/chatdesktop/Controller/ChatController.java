package com.example.chatdesktop.Controller;

import com.example.chatdesktop.service.GroqService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ChatController {

    @FXML
    private TextArea conversaArea;

    @FXML
    private TextField mensagemField;

    @FXML
    private Button enviarButton;

    @FXML
    private Button limparButton;

    private final GroqService groqService;

    public ChatController() {
        groqService = new GroqService();
    }

    @FXML
    private void initialize() {
        mensagemField.setOnAction(event -> enviarMensagem());
    }

    @FXML
    private void enviarMensagem() {

        String mensagem = mensagemField.getText();

        if (mensagem == null || mensagem.isBlank()) {
            return;
        }

        mensagem = mensagem.trim();

        adicionarMensagem(
                "Você",
                mensagem
        );

        mensagemField.clear();

        enviarButton.setDisable(true);

        String mensagemFinal = mensagem;

        Thread thread = new Thread(() -> {

            try {

                String resposta =
                        groqService.enviarMensagem(
                                mensagemFinal
                        );

                Platform.runLater(() -> {

                    adicionarMensagem(
                            "Nexa AI",
                            resposta
                    );

                    enviarButton.setDisable(false);
                    mensagemField.requestFocus();
                });

            } catch (Exception e) {

                Platform.runLater(() -> {

                    adicionarMensagem(
                            "Erro",
                            e.getMessage()
                    );

                    enviarButton.setDisable(false);
                    mensagemField.requestFocus();
                });
            }

        });

        thread.setDaemon(true);
        thread.start();
    }

    private void adicionarMensagem(
            String remetente,
            String mensagem
    ) {

        conversaArea.appendText(
                remetente
                        + ":\n"
                        + mensagem
                        + "\n\n"
        );
    }

    @FXML
    private void copiarResposta() {

        String texto = conversaArea.getText();

        if (texto == null || texto.isBlank()) {
            return;
        }

        Clipboard clipboard =
                Clipboard.getSystemClipboard();

        ClipboardContent content =
                new ClipboardContent();

        content.putString(texto);

        clipboard.setContent(content);
    }

    @FXML
    private void limparConversa() {

        conversaArea.clear();

        mensagemField.clear();

        mensagemField.requestFocus();
    }
}