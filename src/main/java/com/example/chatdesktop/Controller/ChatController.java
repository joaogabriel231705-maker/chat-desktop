package com.example.chatdesktop.Controller;

import com.example.chatdesktop.service.GroqService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatController {

    @FXML
    private VBox mensagensContainer;

    @FXML
    private TextField mensagemField;

    @FXML
    private Button enviarButton;

    @FXML
    private ScrollPane scrollPane;

    private final GroqService groqService;

    public ChatController() {
        groqService = new GroqService();
    }

    @FXML
    private void initialize() {

        mensagemField.setOnAction(event -> enviarMensagem());

        mensagensContainer.heightProperty().addListener(
                (observable, oldValue, newValue) ->
                        scrollPane.setVvalue(1.0)
        );
    }

    @FXML
    private void enviarMensagem() {

        String mensagem = mensagemField.getText();

        if (mensagem == null || mensagem.isBlank()) {
            return;
        }

        mensagem = mensagem.trim();

        adicionarMensagemUsuario(mensagem);

        mensagemField.clear();

        enviarButton.setDisable(true);

        adicionarDigitando();

        String mensagemFinal = mensagem;

        Thread thread = new Thread(() -> {

            try {

                String resposta =
                        groqService.enviarMensagem(
                                mensagemFinal
                        );

                Platform.runLater(() -> {

                    removerDigitando();

                    adicionarMensagemIA(resposta);

                    enviarButton.setDisable(false);

                    mensagemField.requestFocus();
                });

            } catch (Exception e) {

                Platform.runLater(() -> {

                    removerDigitando();

                    adicionarMensagemIA(
                            "Ocorreu um erro ao conversar com a IA:\n\n"
                                    + e.getMessage()
                    );

                    enviarButton.setDisable(false);

                    mensagemField.requestFocus();
                });
            }

        });

        thread.setDaemon(true);

        thread.start();
    }

    private void adicionarMensagemUsuario(
            String mensagem
    ) {

        VBox mensagemBox = new VBox();

        mensagemBox.getStyleClass().add(
                "user-message-container"
        );

        Label nome = new Label("Você");

        nome.getStyleClass().add(
                "message-name"
        );

        Label texto = new Label(mensagem);

        texto.setWrapText(true);

        texto.getStyleClass().add(
                "user-message"
        );

        mensagemBox.getChildren().addAll(
                nome,
                texto
        );

        HBox linha = new HBox(
                mensagemBox
        );

        linha.setAlignment(
                Pos.CENTER_RIGHT
        );

        linha.getStyleClass().add(
                "message-row"
        );

        mensagensContainer.getChildren().add(
                linha
        );
    }

    private void adicionarMensagemIA(
            String resposta
    ) {

        VBox mensagemBox = new VBox();

        mensagemBox.setSpacing(10);

        mensagemBox.getStyleClass().add(
                "ai-message-container"
        );

        Label nome = new Label("✦  Nexa AI");

        nome.getStyleClass().add(
                "ai-name"
        );

        Label texto = new Label(resposta);

        texto.setWrapText(true);

        texto.getStyleClass().add(
                "ai-message"
        );

        Button copiarButton =
                new Button("📋  Copiar");

        copiarButton.getStyleClass().add(
                "copy-button"
        );

        copiarButton.setOnAction(event -> {

            copiarTexto(resposta);

            copiarButton.setText(
                    "✓  Copiado!"
            );

            Thread thread = new Thread(() -> {

                try {

                    Thread.sleep(1500);

                } catch (InterruptedException ignored) {
                }

                Platform.runLater(() ->
                        copiarButton.setText(
                                "📋  Copiar"
                        )
                );
            });

            thread.setDaemon(true);

            thread.start();
        });

        HBox botoes = new HBox(
                copiarButton
        );

        botoes.setAlignment(
                Pos.CENTER_RIGHT
        );

        mensagemBox.getChildren().addAll(
                nome,
                texto,
                botoes
        );

        HBox linha = new HBox(
                mensagemBox
        );

        linha.setAlignment(
                Pos.CENTER_LEFT
        );

        linha.getStyleClass().add(
                "message-row"
        );

        mensagensContainer.getChildren().add(
                linha
        );
    }

    private void copiarTexto(
            String texto
    ) {

        Clipboard clipboard =
                Clipboard.getSystemClipboard();

        ClipboardContent content =
                new ClipboardContent();

        content.putString(texto);

        clipboard.setContent(content);
    }

    private void adicionarDigitando() {

        HBox digitando =
                new HBox();

        digitando.setId(
                "digitando"
        );

        digitando.getStyleClass().add(
                "typing-container"
        );

        Label texto =
                new Label(
                        "✦  Nexa AI está pensando..."
                );

        texto.getStyleClass().add(
                "typing-text"
        );

        digitando.getChildren().add(
                texto
        );

        mensagensContainer.getChildren().add(
                digitando
        );
    }

    private void removerDigitando() {

        mensagensContainer
                .getChildren()
                .removeIf(
                        node ->
                                "digitando".equals(
                                        node.getId()
                                )
                );
    }

    @FXML
    private void novaConversa() {

        mensagensContainer
                .getChildren()
                .clear();

        mostrarMensagemInicial();

        mensagemField.clear();

        mensagemField.requestFocus();
    }

    @FXML
    private void limparConversa() {

        mensagensContainer
                .getChildren()
                .clear();

        mostrarMensagemInicial();
    }

    private void mostrarMensagemInicial() {

        VBox inicio = new VBox();

        inicio.setAlignment(
                Pos.CENTER
        );

        inicio.setSpacing(12);

        inicio.getStyleClass().add(
                "welcome-container"
        );

        Label icone =
                new Label("✦");

        icone.getStyleClass().add(
                "welcome-icon"
        );

        Label titulo =
                new Label(
                        "Como posso ajudar?"
                );

        titulo.getStyleClass().add(
                "welcome-title"
        );

        Label descricao =
                new Label(
                        "Converse, crie e descubra com a Nexa AI."
                );

        descricao.getStyleClass().add(
                "welcome-description"
        );

        inicio.getChildren().addAll(
                icone,
                titulo,
                descricao
        );

        mensagensContainer
                .getChildren()
                .add(inicio);
    }
}