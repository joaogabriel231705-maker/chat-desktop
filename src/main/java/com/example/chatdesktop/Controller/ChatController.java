package com.example.chatdesktop.Controller;

import com.example.chatdesktop.config.PreferenciasConfig;
import com.example.chatdesktop.dao.ConversaDAO;
import com.example.chatdesktop.dao.Database;
import com.example.chatdesktop.dao.MensagemDAO;
import com.example.chatdesktop.model.Conversa;
import com.example.chatdesktop.model.mensagem;
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
    /* SQLITE */
    /* ========================================= */

    private final ConversaDAO conversaDAO =
            new ConversaDAO();

    private final MensagemDAO mensagemDAO =
            new MensagemDAO();


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
         * Cria o banco e as tabelas,
         * caso ainda não existam.
         */

        Database.criarTabelas();

        conversaAtual = new Conversa();
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


        /*
         * Carrega o tema salvo.
         */

        carregarTemaSalvo();


        /*
         * Mostra a tela inicial.
         */

        mostrarMensagemInicial();


        /*
         * Carrega as conversas do SQLite.
         */

        carregarHistorico();
    }


    /* ========================================= */
    /* CARREGAR HISTÓRICO DO SQLITE */
    /* ========================================= */

    private void carregarHistorico() {

        conversas.clear();

        List<Conversa> conversasSalvas =
                conversaDAO.listar();


        for (Conversa conversa :
                conversasSalvas) {

            List<mensagem> mensagens =
                    mensagemDAO.listarPorConversa(
                            conversa.getId()
                    );


            conversa.getMensagens().addAll(
                    mensagens
            );


            conversas.add(
                    conversa
            );
        }


        atualizarHistorico();


        System.out.println(
                "Histórico carregado: "
                        + conversas.size()
                        + " conversa(s)."
        );
    }


    /* ========================================= */
    /* CARREGAR TEMA SALVO */
    /* ========================================= */

    private void carregarTemaSalvo() {

        Platform.runLater(() -> {

            Scene scene =
                    mensagensContainer.getScene();


            if (scene == null) {

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


            String temaSalvo =
                    PreferenciasConfig.carregarTema();


            scene.getStylesheets().clear();


            /* ===================================== */
            /* TEMA CLARO */
            /* ===================================== */

            if ("light".equals(temaSalvo)) {

                scene.getStylesheets().add(
                        lightUrl.toExternalForm()
                );


                temaClaro = true;


                if (temaButton != null) {

                    temaButton.setText("🌙");
                }


                System.out.println(
                        "Tema salvo carregado: claro"
                );
            }


            /* ===================================== */
            /* TEMA ESCURO */
            /* ===================================== */

            else {

                scene.getStylesheets().add(
                        darkUrl.toExternalForm()
                );


                temaClaro = false;


                if (temaButton != null) {

                    temaButton.setText("☀");
                }


                System.out.println(
                        "Tema salvo carregado: escuro"
                );
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

            PreferenciasConfig.salvarTema(
                    "light"
            );


            System.out.println(
                    "Tema claro ativado e salvo!"
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

            PreferenciasConfig.salvarTema(
                    "dark"
            );


            System.out.println(
                    "Tema escuro ativado e salvo!"
            );
        }
    }


    /* ========================================= */
    /* ENVIAR MENSAGEM */
    /* ========================================= */

    @FXML
    private void enviarMensagem() {

        String mensagemTexto =
                mensagemField.getText();


        if (mensagemTexto == null ||
                mensagemTexto.isBlank()) {

            return;
        }


        mensagemTexto =
                mensagemTexto.trim();


        ultimaPergunta =
                mensagemTexto;


        /* ===================================== */
        /* CRIAR CONVERSA NO SQLITE */
        /* ===================================== */

        if (!tituloGerado) {

            if (!conversaAtual.isTituloEditado()) {

                gerarTituloConversa(
                        mensagemTexto
                );
            }


            tituloGerado = true;


            /*
             * A conversa só é criada no banco
             * quando a primeira mensagem é enviada.
             */

            if (!conversas.contains(conversaAtual)) {

                long id =
                        conversaDAO.criar(
                                conversaAtual
                        );


                if (id == -1) {

                    Alert alerta =
                            new Alert(
                                    Alert.AlertType.ERROR
                            );

                    alerta.setTitle(
                            "Erro"
                    );

                    alerta.setHeaderText(
                            "Não foi possível salvar a conversa"
                    );

                    alerta.setContentText(
                            "O banco de dados não conseguiu criar a conversa."
                    );

                    alerta.showAndWait();

                    return;
                }


                conversas.add(
                        0,
                        conversaAtual
                );
            }


            atualizarHistorico();
        }


        /* ===================================== */
        /* MENSAGEM DO USUÁRIO */
        /* ===================================== */

        mensagem mensagemUsuario =
                new mensagem(
                        mensagemTexto,
                        true
                );


        conversaAtual
                .getMensagens()
                .add(
                        mensagemUsuario
                );


        mensagemDAO.salvar(
                conversaAtual.getId(),
                mensagemUsuario
        );


        adicionarMensagemUsuario(
                mensagemTexto
        );


        mensagemField.clear();


        enviarButton.setDisable(true);


        adicionarDigitando();


        String mensagemFinal =
                mensagemTexto;


        Conversa conversaDaMensagem =
                conversaAtual;


        Thread thread =
                new Thread(() -> {

                    try {

                        String resposta =
                                ragService.responder(
                                        mensagemFinal
                                );


                        Platform.runLater(() -> {

                            removerDigitando();


                            /*
                             * Mensagem da IA
                             */

                            mensagem mensagemIA =
                                    new mensagem(
                                            resposta,
                                            false
                                    );


                            conversaDaMensagem
                                    .getMensagens()
                                    .add(
                                            mensagemIA
                                    );


                            /*
                             * Salva a resposta
                             * no SQLite.
                             */

                            mensagemDAO.salvar(
                                    conversaDaMensagem.getId(),
                                    mensagemIA
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


                            mensagem mensagemErroIA =
                                    new mensagem(
                                            erroFinal,
                                            false
                                    );


                            conversaDaMensagem
                                    .getMensagens()
                                    .add(
                                            mensagemErroIA
                                    );


                            /*
                             * Também salva o erro no histórico.
                             */

                            mensagemDAO.salvar(
                                    conversaDaMensagem.getId(),
                                    mensagemErroIA
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


        /*
         * Remove a última resposta da memória.
         */

        if (!conversaAtual
                .getMensagens()
                .isEmpty()) {

            int ultimoIndice =
                    conversaAtual
                            .getMensagens()
                            .size() - 1;


            mensagem ultimaMensagem =
                    conversaAtual
                            .getMensagens()
                            .get(
                                    ultimoIndice
                            );


            if (!ultimaMensagem.isUsuario()) {

                conversaAtual
                        .getMensagens()
                        .remove(
                                ultimoIndice
                        );


                /*
                 * Remove também do SQLite.
                 */

                mensagemDAO
                        .excluirUltimaMensagemIA(
                                conversaAtual.getId()
                        );
            }
        }


        enviarButton.setDisable(true);


        adicionarDigitando();


        String pergunta =
                ultimaPergunta;


        Conversa conversaDaMensagem =
                conversaAtual;


        Thread thread =
                new Thread(() -> {

                    try {

                        String novaResposta =
                                ragService.responder(
                                        pergunta
                                );


                        Platform.runLater(() -> {

                            removerDigitando();


                            mensagem novaMensagem =
                                    new mensagem(
                                            novaResposta,
                                            false
                                    );


                            conversaDaMensagem
                                    .getMensagens()
                                    .add(
                                            novaMensagem
                                    );


                            /*
                             * Salva a nova resposta.
                             */

                            mensagemDAO.salvar(
                                    conversaDaMensagem.getId(),
                                    novaMensagem
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


                            mensagem mensagemErro =
                                    new mensagem(
                                            erro,
                                            false
                                    );


                            conversaDaMensagem
                                    .getMensagens()
                                    .add(
                                            mensagemErro
                                    );


                            mensagemDAO.salvar(
                                    conversaDaMensagem.getId(),
                                    mensagemErro
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
    /* REMOVER ÚLTIMA MENSAGEM DA IA DA TELA */
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


        conversaAtual.setTitulo(
                titulo
        );


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


        for (Conversa conversa :
                conversas) {

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


            Button botaoConversa =
                    new Button();


            botaoConversa.setText(
                    "💬  " +
                            conversa.getTitulo()
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


            linha.getChildren().addAll(
                    botaoConversa,
                    editarButton,
                    removerButton
            );


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
                        conversa.getTitulo()
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


                    conversa.setTitulo(
                            novoTitulo
                    );


                    conversa.setTituloEditado(
                            true
                    );


                    /*
                     * Atualiza o título no SQLite.
                     */

                    conversaDAO.atualizarTitulo(
                            conversa
                    );


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
                    conversa.getTitulo()
            );
        }


        /*
         * Mostra todas as mensagens salvas.
         */

        for (
                mensagem mensagem :
                conversa.getMensagens()
        ) {

            if (mensagem.isUsuario()) {

                adicionarMensagemUsuario(
                        mensagem.getTexto()
                );

            } else {

                adicionarMensagemIA(
                        mensagem.getTexto()
                );
            }
        }


        /*
         * Recupera a última pergunta.
         */

        for (
                int i =
                conversa.getMensagens().size() - 1;

                i >= 0;

                i--
        ) {

            mensagem mensagem =
                    conversa
                            .getMensagens()
                            .get(i);


            if (mensagem.isUsuario()) {

                ultimaPergunta =
                        mensagem.getTexto();

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
                        conversa.getTitulo() +
                        "\" será removida do histórico."
        );


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


        confirmacao.showAndWait().ifPresent(
                resposta -> {

                    if (resposta !=
                            excluirButton) {

                        return;
                    }


                    /*
                     * Exclui primeiro do SQLite.
                     */

                    conversaDAO.excluir(
                            conversa.getId()
                    );


                    /*
                     * Remove da memória.
                     */

                    conversas.remove(
                            conversa
                    );


                    /*
                     * Se era a conversa atual,
                     * volta para uma conversa vazia.
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


                    atualizarHistorico();


                    System.out.println(
                            "Conversa removida: " +
                                    conversa.getTitulo()
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
         * Se a conversa já existe no banco,
         * remove do SQLite.
         */

        if (conversaAtual.getId() > 0) {

            conversaDAO.excluir(
                    conversaAtual.getId()
            );
        }


        /*
         * Remove da lista em memória.
         */

        conversas.remove(
                conversaAtual
        );


        /*
         * Cria uma nova conversa vazia.
         */

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


        atualizarHistorico();


        System.out.println(
                "Conversa atual limpa."
        );
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
}