package com.example.chatdesktop.Controller;

import com.example.chatdesktop.service.GroqService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URL;

public class ChatController {

    @FXML
    private VBox mensagensContainer;

    @FXML
    private TextField mensagemField;

    @FXML
    private Button enviarButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button temaButton;

    private final GroqService groqService;

    private boolean temaClaro = false;

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

        carregarTemaEscuro();
    }

    private void carregarTemaEscuro() {

        Scene scene = mensagensContainer.getScene();

        if (scene == null) {
            return;
        }

        URL darkUrl = getClass().getResource(
                "/com/example/chatdesktop/css/style.css"
        );

        if (darkUrl == null) {
            System.out.println("ERRO: style.css não encontrado!");
            return;
        }

        scene.getStylesheets().clear();

        scene.getStylesheets().add(
                darkUrl.toExternalForm()
        );

        temaClaro = false;
    }

    /* ========================================= */
    /* TEMA CLARO / ESCURO */
    /* ========================================= */

    @FXML
    private void alternarTema() {

        Scene scene = temaButton.getScene();

        if (scene == null) {
            System.out.println("ERRO: Scene não encontrada!");
            return;
        }

        URL lightUrl = getClass().getResource(
                "/com/example/chatdesktop/css/light.css"
        );

        URL darkUrl = getClass().getResource(
                "/com/example/chatdesktop/css/style.css"
        );

        if (lightUrl == null) {
            System.out.println("ERRO: light.css não encontrado!");
            return;
        }

        if (darkUrl == null) {
            System.out.println("ERRO: style.css não encontrado!");
            return;
        }

        String lightCss = lightUrl.toExternalForm();
        String darkCss = darkUrl.toExternalForm();

        // Remove qualquer tema que esteja carregado
        scene.getStylesheets().clear();

        // Se está escuro, muda para claro
        if (!temaClaro) {

            scene.getStylesheets().add(lightCss);

            temaClaro = true;

            // Agora o botão oferece voltar para o escuro
            temaButton.setText("🌙");

            System.out.println("Tema claro ativado!");

        } else {

            // Volta para o tema escuro
            scene.getStylesheets().add(darkCss);

            temaClaro = false;

            // Agora o botão oferece mudar para o claro
            temaButton.setText("☀");

            System.out.println("Tema escuro ativado!");
        }
    }
    /* ========================================= */
    /* ENVIAR MENSAGEM */
    /* ========================================= */

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

    /* ========================================= */
    /* MENSAGEM DO USUÁRIO */
    /* ========================================= */

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

    /* ========================================= */
    /* MENSAGEM DA IA */
    /* ========================================= */

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

    /* ========================================= */
    /* COPIAR */
    /* ========================================= */

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

    /* ========================================= */
    /* DIGITANDO */
    /* ========================================= */

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

    /* ========================================= */
    /* NOVA CONVERSA */
    /* ========================================= */

    @FXML
    private void novaConversa() {

        mensagensContainer
                .getChildren()
                .clear();

        mostrarMensagemInicial();

        mensagemField.clear();

        mensagemField.requestFocus();
    }

    /* ========================================= */
    /* LIMPAR CONVERSA */
    /* ========================================= */

    @FXML
    private void limparConversa() {

        mensagensContainer
                .getChildren()
                .clear();

        mostrarMensagemInicial();
    }

    /* ========================================= */
    /* MENSAGEM INICIAL */
    /* ========================================= */

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