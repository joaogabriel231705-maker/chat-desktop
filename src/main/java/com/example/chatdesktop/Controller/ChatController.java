package com.example.chatdesktop.Controller;

import com.example.chatdesktop.service.RagService;

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

    /* ========================================= */
    /* COMPONENTES DO FXML */
    /* ========================================= */

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

    @FXML
    private Button conversaAtualButton;

    @FXML
    private Label chatTitle;


    /* ========================================= */
    /* SERVIÇO RAG */
    /* ========================================= */

    private final RagService ragService;


    /* ========================================= */
    /* CONTROLE DOS TEMAS */
    /* ========================================= */

    private boolean temaClaro = false;


    /* ========================================= */
    /* CONTROLE DO TÍTULO */
    /* ========================================= */

    private boolean tituloGerado = false;


    /* ========================================= */
    /* CONSTRUTOR */
    /* ========================================= */

    public ChatController() {

        ragService = new RagService();
    }


    /* ========================================= */
    /* INITIALIZE */
    /* ========================================= */

    @FXML
    private void initialize() {

        mensagemField.setOnAction(
                event -> enviarMensagem()
        );


        mensagensContainer.heightProperty().addListener(
                (observable, oldValue, newValue) ->
                        scrollPane.setVvalue(1.0)
        );


        carregarTemaEscuro();
    }


    /* ========================================= */
    /* TEMA ESCURO INICIAL */
    /* ========================================= */

    private void carregarTemaEscuro() {

        Scene scene =
                mensagensContainer.getScene();


        if (scene == null) {

            return;
        }


        URL darkUrl =
                getClass().getResource(
                        "/com/example/chatdesktop/css/style.css"
                );


        if (darkUrl == null) {

            System.out.println(
                    "ERRO: style.css não encontrado!"
            );

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

        Scene scene =
                temaButton.getScene();


        if (scene == null) {

            System.out.println(
                    "ERRO: Scene não encontrada!"
            );

            return;
        }


        URL lightUrl =
                getClass().getResource(
                        "/com/example/chatdesktop/css/light.css"
                );


        URL darkUrl =
                getClass().getResource(
                        "/com/example/chatdesktop/css/style.css"
                );


        if (lightUrl == null) {

            System.out.println(
                    "ERRO: light.css não encontrado!"
            );

            return;
        }


        if (darkUrl == null) {

            System.out.println(
                    "ERRO: style.css não encontrado!"
            );

            return;
        }


        String lightCss =
                lightUrl.toExternalForm();


        String darkCss =
                darkUrl.toExternalForm();


        scene.getStylesheets().clear();


        /* ------------------------------------- */
        /* TEMA CLARO */
        /* ------------------------------------- */

        if (!temaClaro) {

            scene.getStylesheets().add(
                    lightCss
            );


            temaClaro = true;


            temaButton.setText("🌙");


            System.out.println(
                    "Tema claro ativado!"
            );
        }


        /* ------------------------------------- */
        /* TEMA ESCURO */
        /* ------------------------------------- */

        else {

            scene.getStylesheets().add(
                    darkCss
            );


            temaClaro = false;


            temaButton.setText("☀");


            System.out.println(
                    "Tema escuro ativado!"
            );
        }
    }


    /* ========================================= */
    /* ENVIAR MENSAGEM */
    /* ========================================= */

    @FXML
    private void enviarMensagem() {

        String mensagem =
                mensagemField.getText();


        if (mensagem == null ||
                mensagem.isBlank()) {

            return;
        }


        mensagem = mensagem.trim();


        /* ===================================== */
        /* PRIMEIRA MENSAGEM */
        /* ===================================== */

        if (!tituloGerado) {

            gerarTituloConversa(mensagem);

            tituloGerado = true;
        }


        /* ===================================== */
        /* MOSTRA MENSAGEM DO USUÁRIO */
        /* ===================================== */

        adicionarMensagemUsuario(
                mensagem
        );


        mensagemField.clear();


        enviarButton.setDisable(true);


        adicionarDigitando();


        String mensagemFinal =
                mensagem;


        /* ===================================== */
        /* THREAD DA IA */
        /* ===================================== */

        Thread thread =
                new Thread(() -> {

                    try {

                        /*
                         * AQUI ESTÁ A PRINCIPAL MUDANÇA.
                         *
                         * Antes:
                         *
                         * groqService.enviarMensagem(...)
                         *
                         * Agora:
                         *
                         * ragService.responder(...)
                         *
                         * O RagService decide:
                         *
                         * DOCUMENTO → RAG
                         *
                         * SEM DOCUMENTO → GROQ
                         */

                        String resposta =
                                ragService.responder(
                                        mensagemFinal
                                );


                        Platform.runLater(() -> {

                            removerDigitando();


                            adicionarMensagemIA(
                                    resposta
                            );


                            enviarButton.setDisable(
                                    false
                            );


                            mensagemField.requestFocus();
                        });


                    } catch (Exception e) {

                        String mensagemErro;


                        String erro =
                                e.getMessage();


                        if (erro == null) {

                            erro = "";
                        }


                        switch (erro) {

                            /* ============================== */
                            /* SEM INTERNET */
                            /* ============================== */

                            case "SEM_INTERNET":

                                mensagemErro =
                                        "🌐 Sem conexão com a internet.\n\n" +
                                                "Verifique sua conexão e tente novamente.";

                                break;


                            /* ============================== */
                            /* CHAVE INVÁLIDA */
                            /* ============================== */

                            case "CHAVE_INVALIDA":

                                mensagemErro =
                                        "🔑 Chave da API inválida.\n\n" +
                                                "Verifique a configuração da chave da Groq.";

                                break;


                            /* ============================== */
                            /* SEM PERMISSÃO */
                            /* ============================== */

                            case "SEM_PERMISSAO":

                                mensagemErro =
                                        "🔒 A API não permitiu esta solicitação.\n\n" +
                                                "Verifique sua configuração da Groq.";

                                break;


                            /* ============================== */
                            /* LIMITE DA API */
                            /* ============================== */

                            case "LIMITE_API":

                                mensagemErro =
                                        "🚦 Limite da API atingido.\n\n" +
                                                "Aguarde alguns instantes e tente novamente.";

                                break;


                            /* ============================== */
                            /* TIMEOUT */
                            /* ============================== */

                            case "TIMEOUT":

                                mensagemErro =
                                        "⏱️ A comunicação demorou muito.\n\n" +
                                                "Tente enviar sua mensagem novamente.";

                                break;


                            /* ============================== */
                            /* SERVIDOR */
                            /* ============================== */

                            case "SERVIDOR":

                                mensagemErro =
                                        "🔧 O servidor da Groq está temporariamente indisponível.\n\n" +
                                                "Tente novamente mais tarde.";

                                break;


                            /* ============================== */
                            /* COMUNICAÇÃO */
                            /* ============================== */

                            case "COMUNICACAO":

                                mensagemErro =
                                        "🔌 Não foi possível se comunicar com o servidor.\n\n" +
                                                "Verifique sua conexão e tente novamente.";

                                break;


                            /* ============================== */
                            /* RESPOSTA INVÁLIDA */
                            /* ============================== */

                            case "RESPOSTA_INVALIDA":

                                mensagemErro =
                                        "⚠️ A resposta da IA não pôde ser processada.\n\n" +
                                                "Tente enviar sua mensagem novamente.";

                                break;


                            /* ============================== */
                            /* ERRO DESCONHECIDO */
                            /* ============================== */

                            default:

                                mensagemErro =
                                        "❌ Ocorreu um erro inesperado.\n\n" +
                                                "Tente novamente.";

                                break;
                        }


                        String erroFinal =
                                mensagemErro;


                        Platform.runLater(() -> {

                            removerDigitando();


                            adicionarMensagemIA(
                                    erroFinal
                            );


                            enviarButton.setDisable(
                                    false
                            );


                            mensagemField.requestFocus();
                        });
                    }

                });


        thread.setDaemon(true);


        thread.start();
    }


    /* ========================================= */
    /* GERAR TÍTULO DA CONVERSA */
    /* ========================================= */

    private void gerarTituloConversa(
            String mensagem
    ) {

        if (conversaAtualButton == null) {

            return;
        }


        String titulo =
                mensagem.trim();


        titulo =
                titulo.replace(
                        "\n",
                        " "
                );


        titulo =
                titulo.replace(
                        "\r",
                        " "
                );


        titulo =
                titulo.replaceAll(
                        "\\s+",
                        " "
                );


        if (titulo.length() > 30) {

            titulo =
                    titulo.substring(
                            0,
                            30
                    ).trim();


            titulo += "...";
        }


        conversaAtualButton.setText(
                "💬  " + titulo
        );


        System.out.println(
                "Título da conversa: " +
                        titulo
        );
    }


    /* ========================================= */
    /* MENSAGEM DO USUÁRIO */
    /* ========================================= */

    private void adicionarMensagemUsuario(
            String mensagem
    ) {

        VBox mensagemBox =
                new VBox();


        mensagemBox.getStyleClass().add(
                "user-message-container"
        );


        Label nome =
                new Label("Você");


        nome.getStyleClass().add(
                "message-name"
        );


        Label texto =
                new Label(mensagem);


        texto.setWrapText(true);


        texto.getStyleClass().add(
                "user-message"
        );


        mensagemBox.getChildren().addAll(
                nome,
                texto
        );


        HBox linha =
                new HBox(
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

        VBox mensagemBox =
                new VBox();


        mensagemBox.setSpacing(10);


        mensagemBox.getStyleClass().add(
                "ai-message-container"
        );


        Label nome =
                new Label(
                        "✦  Nexa AI"
                );


        nome.getStyleClass().add(
                "ai-name"
        );


        Label texto =
                new Label(resposta);


        texto.setWrapText(true);


        texto.getStyleClass().add(
                "ai-message"
        );


        Button copiarButton =
                new Button(
                        "📋  Copiar"
                );


        copiarButton.getStyleClass().add(
                "copy-button"
        );


        copiarButton.setOnAction(
                event -> {

                    copiarTexto(
                            resposta
                    );


                    copiarButton.setText(
                            "✓  Copiado!"
                    );


                    Thread thread =
                            new Thread(() -> {

                                try {

                                    Thread.sleep(
                                            1500
                                    );

                                } catch (
                                        InterruptedException ignored
                                ) {
                                }


                                Platform.runLater(
                                        () ->
                                                copiarButton.setText(
                                                        "📋  Copiar"
                                                )
                                );
                            });


                    thread.setDaemon(true);


                    thread.start();
                }
        );


        HBox botoes =
                new HBox(
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


        HBox linha =
                new HBox(
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
    /* COPIAR TEXTO */
    /* ========================================= */

    private void copiarTexto(
            String texto
    ) {

        Clipboard clipboard =
                Clipboard.getSystemClipboard();


        ClipboardContent content =
                new ClipboardContent();


        content.putString(
                texto
        );


        clipboard.setContent(
                content
        );
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


    /* ========================================= */
    /* REMOVER DIGITANDO */
    /* ========================================= */

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


        tituloGerado = false;


        if (chatTitle != null) {

            chatTitle.setText(
                    "Nexa AI"
            );
        }


        if (conversaAtualButton != null) {

            conversaAtualButton.setText(
                    "💬  Conversa atual"
            );
        }


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


        tituloGerado = false;


        if (chatTitle != null) {

            chatTitle.setText(
                    "Nexa AI"
            );
        }


        if (conversaAtualButton != null) {

            conversaAtualButton.setText(
                    "💬  Conversa atual"
            );
        }


        mostrarMensagemInicial();
    }


    /* ========================================= */
    /* MENSAGEM INICIAL */
    /* ========================================= */

    private void mostrarMensagemInicial() {

        VBox inicio =
                new VBox();


        inicio.setAlignment(
                Pos.CENTER
        );


        inicio.setSpacing(
                12
        );


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
                .add(
                        inicio
                );
    }
}