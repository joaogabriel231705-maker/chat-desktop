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
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    /* ========================================= */
    /* COMPONENTES DO FXML */
    /* ========================================= */

    @FXML
    private VBox mensagensContainer;

    @FXML
    private VBox historicoContainer;

    @FXML
    private TextField mensagemField;

    @FXML
    private Button enviarButton;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button temaButton;

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
    /* ÚLTIMA PERGUNTA */
    /* ========================================= */

    private String ultimaPergunta = null;


    /* ========================================= */
    /* HISTÓRICO */
    /* ========================================= */

    private final List<Conversa> conversas =
            new ArrayList<>();


    /* ========================================= */
    /* CONVERSA ATUAL */
    /* ========================================= */

    private Conversa conversaAtual;


    /* ========================================= */
    /* CONSTRUTOR */
    /* ========================================= */

    public ChatController() {

        ragService = new RagService();

        /*
         * Cria a primeira conversa.
         */

        conversaAtual =
                new Conversa();
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


        /*
         * Mostra a tela inicial.
         */

        mostrarMensagemInicial();


        /*
         * O histórico começa vazio.
         */

        atualizarHistorico();
    }


    /* ========================================= */
    /* TEMA ESCURO INICIAL */
    /* ========================================= */

    private void carregarTemaEscuro() {

        /*
         * Durante o initialize(), o Scene pode
         * ainda não estar disponível.
         */

        Platform.runLater(() -> {

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


            if (temaButton != null) {

                temaButton.setText("☀");
            }
        });
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


        /* ===================================== */
        /* TEMA CLARO */
        /* ===================================== */

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


        /* ===================================== */
        /* TEMA ESCURO */
        /* ===================================== */

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


        mensagem =
                mensagem.trim();


        /* ===================================== */
        /* SALVA A ÚLTIMA PERGUNTA */
        /* ===================================== */

        ultimaPergunta =
                mensagem;


        /* ===================================== */
        /* PRIMEIRA MENSAGEM DA CONVERSA */
        /* ===================================== */

        if (!tituloGerado) {

            /*
             * Só gera automaticamente o título
             * se o usuário ainda não tiver
             * colocado um título manual.
             */

            if (!conversaAtual.tituloEditado) {

                gerarTituloConversa(
                        mensagem
                );
            }


            tituloGerado = true;


            /*
             * Adiciona a conversa ao histórico.
             */

            if (!conversas.contains(conversaAtual)) {

                conversas.add(
                        0,
                        conversaAtual
                );
            }


            atualizarHistorico();
        }


        /* ===================================== */
        /* SALVA MENSAGEM DO USUÁRIO */
        /* ===================================== */

        conversaAtual.mensagens.add(
                new Mensagem(
                        true,
                        mensagem
                )
        );


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


                            /*
                             * Salva a resposta da IA.
                             */

                            conversaAtual.mensagens.add(
                                    new Mensagem(
                                            false,
                                            resposta
                                    )
                            );


                            adicionarMensagemIA(
                                    resposta
                            );


                            enviarButton.setDisable(
                                    false
                            );


                            mensagemField.requestFocus();


                            atualizarHistorico();
                        });


                    } catch (Exception e) {

                        String mensagemErro;


                        String erro =
                                e.getMessage();


                        if (erro == null) {

                            erro = "";
                        }


                        switch (erro) {

                            case "SEM_INTERNET":

                                mensagemErro =
                                        "🌐 Sem conexão com a internet.\n\n" +
                                                "Verifique sua conexão e tente novamente.";

                                break;


                            case "CHAVE_INVALIDA":

                                mensagemErro =
                                        "🔑 Chave da API inválida.\n\n" +
                                                "Verifique a configuração da chave da Groq.";

                                break;


                            case "SEM_PERMISSAO":

                                mensagemErro =
                                        "🔒 A API não permitiu esta solicitação.\n\n" +
                                                "Verifique sua configuração da Groq.";

                                break;


                            case "LIMITE_API":

                                mensagemErro =
                                        "🚦 Limite da API atingido.\n\n" +
                                                "Aguarde alguns instantes e tente novamente.";

                                break;


                            case "TIMEOUT":

                                mensagemErro =
                                        "⏱️ A comunicação demorou muito.\n\n" +
                                                "Tente enviar sua mensagem novamente.";

                                break;


                            case "SERVIDOR":

                                mensagemErro =
                                        "🔧 O servidor da Groq está temporariamente indisponível.\n\n" +
                                                "Tente novamente mais tarde.";

                                break;


                            case "COMUNICACAO":

                                mensagemErro =
                                        "🔌 Não foi possível se comunicar com o servidor.\n\n" +
                                                "Verifique sua conexão e tente novamente.";

                                break;


                            case "RESPOSTA_INVALIDA":

                                mensagemErro =
                                        "⚠️ A resposta da IA não pôde ser processada.\n\n" +
                                                "Tente enviar sua mensagem novamente.";

                                break;


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


                            conversaAtual.mensagens.add(
                                    new Mensagem(
                                            false,
                                            erroFinal
                                    )
                            );


                            adicionarMensagemIA(
                                    erroFinal
                            );


                            enviarButton.setDisable(
                                    false
                            );


                            mensagemField.requestFocus();


                            atualizarHistorico();
                        });
                    }

                });


        thread.setDaemon(true);


        thread.start();
    }


    /* ========================================= */
    /* REGENERAR RESPOSTA */
    /* ========================================= */

    private void regenerarResposta() {

        if (ultimaPergunta == null ||
                ultimaPergunta.isBlank()) {

            return;
        }


        removerUltimaMensagemIA();


        if (!conversaAtual.mensagens.isEmpty()) {

            int ultimoIndice =
                    conversaAtual.mensagens.size() - 1;


            Mensagem ultimaMensagem =
                    conversaAtual.mensagens.get(
                            ultimoIndice
                    );


            if (!ultimaMensagem.usuario) {

                conversaAtual.mensagens.remove(
                        ultimoIndice
                );
            }
        }


        enviarButton.setDisable(true);


        adicionarDigitando();


        String pergunta =
                ultimaPergunta;


        Thread thread =
                new Thread(() -> {

                    try {

                        String novaResposta =
                                ragService.responder(
                                        pergunta
                                );


                        Platform.runLater(() -> {

                            removerDigitando();


                            conversaAtual.mensagens.add(
                                    new Mensagem(
                                            false,
                                            novaResposta
                                    )
                            );


                            adicionarMensagemIA(
                                    novaResposta
                            );


                            enviarButton.setDisable(
                                    false
                            );


                            mensagemField.requestFocus();


                            atualizarHistorico();
                        });


                    } catch (Exception e) {

                        Platform.runLater(() -> {

                            removerDigitando();


                            String erro =
                                    "❌ Não foi possível regenerar a resposta.\n\n" +
                                            "Tente novamente.";


                            conversaAtual.mensagens.add(
                                    new Mensagem(
                                            false,
                                            erro
                                    )
                            );


                            adicionarMensagemIA(
                                    erro
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
    /* REMOVER ÚLTIMA MENSAGEM DA IA */
    /* ========================================= */

    private void removerUltimaMensagemIA() {

        for (
                int i =
                mensagensContainer
                        .getChildren()
                        .size() - 1;

                i >= 0;

                i--
        ) {

            javafx.scene.Node node =
                    mensagensContainer
                            .getChildren()
                            .get(i);


            if (
                    node instanceof HBox &&
                            node.getStyleClass().contains(
                                    "message-row"
                            )
            ) {

                HBox linha =
                        (HBox) node;


                if (
                        !linha.getChildren().isEmpty() &&
                                linha.getChildren().get(0)
                                        instanceof VBox
                ) {

                    VBox caixa =
                            (VBox) linha
                                    .getChildren()
                                    .get(0);


                    if (
                            caixa.getStyleClass().contains(
                                    "ai-message-container"
                            )
                    ) {

                        mensagensContainer
                                .getChildren()
                                .remove(i);


                        break;
                    }
                }
            }
        }
    }


    /* ========================================= */
    /* GERAR TÍTULO DA CONVERSA */
    /* ========================================= */

    private void gerarTituloConversa(
            String mensagem
    ) {

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


        conversaAtual.titulo =
                titulo;


        if (chatTitle != null) {

            chatTitle.setText(
                    titulo
            );
        }


        System.out.println(
                "Título da conversa: " +
                        titulo
        );
    }


    /* ========================================= */
    /* ATUALIZAR HISTÓRICO LATERAL */
    /* ========================================= */

    private void atualizarHistorico() {

        if (historicoContainer == null) {

            return;
        }


        historicoContainer
                .getChildren()
                .clear();


        /*
         * Mostra as conversas da mais recente
         * para a mais antiga.
         */

        for (Conversa conversa :
                conversas) {

            /* ================================= */
            /* LINHA DA CONVERSA */
            /* ================================= */

            HBox linha =
                    new HBox();


            linha.setSpacing(
                    4
            );


            linha.setAlignment(
                    Pos.CENTER_LEFT
            );


            linha.setMaxWidth(
                    Double.MAX_VALUE
            );


            linha.getStyleClass().add(
                    "history-item"
            );


            /* ================================= */
            /* BOTÃO DA CONVERSA */
            /* ================================= */

            Button botaoConversa =
                    new Button();


            botaoConversa.setText(
                    "💬  " +
                            conversa.titulo
            );


            botaoConversa.setMaxWidth(
                    Double.MAX_VALUE
            );


            botaoConversa.setMnemonicParsing(
                    false
            );


            botaoConversa.setTooltip(
                    new Tooltip(
                            "Abrir conversa"
                    )
            );


            botaoConversa.getStyleClass().add(
                    "conversation-button"
            );


            HBox.setHgrow(
                    botaoConversa,
                    javafx.scene.layout.Priority.ALWAYS
            );


            botaoConversa.setOnAction(
                    event ->
                            abrirConversa(
                                    conversa
                            )
            );


            /* ================================= */
            /* BOTÃO EDITAR TÍTULO */
            /* ================================= */

            Button editarButton =
                    new Button(
                            "✏️"
                    );


            editarButton.setMnemonicParsing(
                    false
            );


            editarButton.setTooltip(
                    new Tooltip(
                            "Editar título"
                    )
            );


            editarButton.getStyleClass().add(
                    "edit-conversation-button"
            );


            editarButton.setOnAction(
                    event ->
                            editarTituloConversa(
                                    conversa
                            )
            );


            /* ================================= */
            /* BOTÃO EXCLUIR */
            /* ================================= */

            Button removerButton =
                    new Button(
                            "🗑"
                    );


            removerButton.setMnemonicParsing(
                    false
            );


            removerButton.setTooltip(
                    new Tooltip(
                            "Remover conversa"
                    )
            );


            removerButton.getStyleClass().add(
                    "delete-conversation-button"
            );


            removerButton.setOnAction(
                    event ->
                            removerConversa(
                                    conversa
                            )
            );


            /* ================================= */
            /* ADICIONA OS BOTÕES */
            /* ================================= */

            linha.getChildren().addAll(
                    botaoConversa,
                    editarButton,
                    removerButton
            );


            /* ================================= */
            /* ADICIONA NO HISTÓRICO */
            /* ================================= */

            historicoContainer
                    .getChildren()
                    .add(
                            linha
                    );
        }
    }


    /* ========================================= */
    /* EDITAR TÍTULO DA CONVERSA */
    /* ========================================= */

    private void editarTituloConversa(
            Conversa conversa
    ) {

        TextInputDialog dialog =
                new TextInputDialog(
                        conversa.titulo
                );


        dialog.setTitle(
                "Editar conversa"
        );


        dialog.setHeaderText(
                "Editar título da conversa"
        );


        dialog.setContentText(
                "Novo título:"
        );


        TextField campo =
                dialog.getEditor();


        campo.selectAll();


        dialog.showAndWait().ifPresent(
                novoTitulo -> {

                    novoTitulo =
                            novoTitulo.trim();


                    if (novoTitulo.isBlank()) {

                        Alert alerta =
                                new Alert(
                                        Alert.AlertType.WARNING
                                );


                        alerta.setTitle(
                                "Título inválido"
                        );


                        alerta.setHeaderText(
                                null
                        );


                        alerta.setContentText(
                                "O título não pode ficar vazio."
                        );


                        alerta.showAndWait();


                        return;
                    }


                    novoTitulo =
                            novoTitulo.replace(
                                    "\n",
                                    " "
                            );


                    novoTitulo =
                            novoTitulo.replace(
                                    "\r",
                                    " "
                            );


                    novoTitulo =
                            novoTitulo.replaceAll(
                                    "\\s+",
                                    " "
                            );


                    if (novoTitulo.length() > 40) {

                        novoTitulo =
                                novoTitulo.substring(
                                        0,
                                        40
                                ).trim();
                    }


                    conversa.titulo =
                            novoTitulo;


                    conversa.tituloEditado =
                            true;


                    if (conversa ==
                            conversaAtual) {

                        if (chatTitle != null) {

                            chatTitle.setText(
                                    novoTitulo
                            );
                        }
                    }


                    atualizarHistorico();


                    System.out.println(
                            "Título alterado para: " +
                                    novoTitulo
                    );
                }
        );
    }


    /* ========================================= */
    /* ABRIR CONVERSA */
    /* ========================================= */

    private void abrirConversa(
            Conversa conversa
    ) {

        if (conversa ==
                conversaAtual) {

            return;
        }


        conversaAtual =
                conversa;


        tituloGerado = true;


        ultimaPergunta = null;


        mensagensContainer
                .getChildren()
                .clear();


        if (chatTitle != null) {

            chatTitle.setText(
                    conversa.titulo
            );
        }


        for (
                Mensagem mensagem :
                conversa.mensagens
        ) {

            if (mensagem.usuario) {

                adicionarMensagemUsuario(
                        mensagem.texto
                );

            } else {

                adicionarMensagemIA(
                        mensagem.texto
                );
            }
        }


        for (
                int i =
                conversa.mensagens.size() - 1;

                i >= 0;

                i--
        ) {

            Mensagem mensagem =
                    conversa.mensagens.get(
                            i
                    );


            if (mensagem.usuario) {

                ultimaPergunta =
                        mensagem.texto;

                break;
            }
        }


        atualizarHistorico();


        mensagemField.clear();


        mensagemField.requestFocus();


        Platform.runLater(
                () ->
                        scrollPane.setVvalue(
                                1.0
                        )
        );
    }


    /* ========================================= */
    /* REMOVER CONVERSA COM CONFIRMAÇÃO */
    /* ========================================= */

    private void removerConversa(
            Conversa conversa
    ) {

        /*
         * =====================================
         * CRIA A JANELA DE CONFIRMAÇÃO
         * =====================================
         */

        Alert confirmacao =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmacao.setTitle(
                "Excluir conversa"
        );


        confirmacao.setHeaderText(
                "Excluir esta conversa?"
        );


        confirmacao.setContentText(
                "A conversa \"" +
                        conversa.titulo +
                        "\" será removida do histórico."
        );


        /*
         * =====================================
         * CRIA OS BOTÕES
         * =====================================
         */

        ButtonType excluirButton =
                new ButtonType(
                        "Excluir"
                );


        ButtonType cancelarButton =
                new ButtonType(
                        "Cancelar",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );


        confirmacao.getButtonTypes().setAll(
                excluirButton,
                cancelarButton
        );


        /*
         * =====================================
         * ESPERA A ESCOLHA DO USUÁRIO
         * =====================================
         */

        confirmacao.showAndWait().ifPresent(
                resposta -> {

                    /*
                     * Se clicou em cancelar,
                     * simplesmente não faz nada.
                     */

                    if (resposta !=
                            excluirButton) {

                        return;
                    }


                    /*
                     * =================================
                     * REMOVE A CONVERSA
                     * =================================
                     */

                    conversas.remove(
                            conversa
                    );


                    /*
                     * =================================
                     * SE ERA A CONVERSA ABERTA
                     * =================================
                     */

                    if (conversa ==
                            conversaAtual) {

                        conversaAtual =
                                new Conversa();


                        tituloGerado = false;


                        ultimaPergunta = null;


                        mensagensContainer
                                .getChildren()
                                .clear();


                        if (chatTitle != null) {

                            chatTitle.setText(
                                    "Nexa AI"
                            );
                        }


                        mostrarMensagemInicial();


                        mensagemField.clear();


                        mensagemField.requestFocus();
                    }


                    /*
                     * =================================
                     * ATUALIZA O HISTÓRICO
                     * =================================
                     */

                    atualizarHistorico();


                    System.out.println(
                            "Conversa removida: " +
                                    conversa.titulo
                    );
                }
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
                new Label(
                        "Você"
                );


        nome.getStyleClass().add(
                "message-name"
        );


        Label texto =
                new Label(
                        mensagem
                );


        texto.setWrapText(
                true
        );


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


        mensagensContainer
                .getChildren()
                .add(
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


        mensagemBox.setSpacing(
                10
        );


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
                new Label(
                        resposta
                );


        texto.setWrapText(
                true
        );


        texto.getStyleClass().add(
                "ai-message"
        );


        /* ===================================== */
        /* BOTÃO COPIAR */
        /* ===================================== */

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


                    thread.setDaemon(
                            true
                    );


                    thread.start();
                }
        );


        /* ===================================== */
        /* BOTÃO REGENERAR */
        /* ===================================== */

        Button regenerarButton =
                new Button(
                        "↻  Regenerar"
                );


        regenerarButton.getStyleClass().add(
                "regenerate-button"
        );


        regenerarButton.setOnAction(
                event ->
                        regenerarResposta()
        );


        /* ===================================== */
        /* CONTAINER DOS BOTÕES */
        /* ===================================== */

        HBox botoes =
                new HBox(
                        10,
                        regenerarButton,
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


        mensagensContainer
                .getChildren()
                .add(
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


        mensagensContainer
                .getChildren()
                .add(
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

        /*
         * A conversa atual continua no histórico.
         */

        conversaAtual =
                new Conversa();


        tituloGerado = false;


        ultimaPergunta = null;


        /*
         * Limpa a área de mensagens.
         */

        mensagensContainer
                .getChildren()
                .clear();


        /*
         * Volta o título.
         */

        if (chatTitle != null) {

            chatTitle.setText(
                    "Nexa AI"
            );
        }


        /*
         * Mostra mensagem inicial.
         */

        mostrarMensagemInicial();


        /*
         * Atualiza o histórico.
         */

        atualizarHistorico();


        mensagemField.clear();


        mensagemField.requestFocus();


        System.out.println(
                "Nova conversa criada."
        );
    }


    /* ========================================= */
    /* LIMPAR CONVERSA ATUAL */
    /* ========================================= */

    @FXML
    private void limparConversa() {

        /*
         * Limpa as mensagens.
         */

        conversaAtual.mensagens.clear();


        /*
         * Reseta o título.
         */

        conversaAtual.titulo =
                "Conversa atual";


        conversaAtual.tituloEditado =
                false;


        tituloGerado = false;


        ultimaPergunta = null;


        /*
         * Limpa a tela.
         */

        mensagensContainer
                .getChildren()
                .clear();


        if (chatTitle != null) {

            chatTitle.setText(
                    "Nexa AI"
            );
        }


        mostrarMensagemInicial();


        /*
         * Se a conversa estava no histórico,
         * remove ela porque ficou vazia.
         */

        conversas.remove(
                conversaAtual
        );


        mensagemField.clear();


        mensagemField.requestFocus();


        atualizarHistorico();
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
                new Label(
                        "✦"
                );


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


    /* ========================================= */
    /* CLASSE CONVERSA */
    /* ========================================= */

    private static class Conversa {

        private String titulo =
                "Conversa atual";


        /*
         * Indica se o usuário alterou
         * manualmente o título.
         */

        private boolean tituloEditado =
                false;


        private final List<Mensagem> mensagens =
                new ArrayList<>();
    }


    /* ========================================= */
    /* CLASSE MENSAGEM */
    /* ========================================= */

    private static class Mensagem {

        private final boolean usuario;

        private final String texto;


        private Mensagem(
                boolean usuario,
                String texto
        ) {

            this.usuario =
                    usuario;

            this.texto =
                    texto;
        }
    }
}