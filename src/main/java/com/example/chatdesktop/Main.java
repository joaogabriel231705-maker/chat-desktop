package com.example.chatdesktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Main extends Application {

    // =========================================================
    // GROQ
    // =========================================================

    private static final String GROQ_API_KEY =
            "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";

    private static final String MODEL =
            "openai/gpt-oss-20b";

    private static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    // =========================================================
    // COMPONENTES
    // =========================================================

    private VBox mensagens;
    private TextField inputField;
    private Button sendButton;
    private ScrollPane scrollPane;

    // =========================================================
    // INICIAR
    // =========================================================

    @Override
    public void start(Stage stage) {

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #0f1117;"
        );

        // =====================================================
        // CABEÇALHO
        // =====================================================

        HBox header = new HBox(12);

        header.setAlignment(Pos.CENTER_LEFT);

        header.setPadding(
                new Insets(15, 20, 15, 20)
        );

        header.setStyle(
                "-fx-background-color: #171a21;" +
                        "-fx-border-color: #252a34;" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // Ícone da IA

        StackPane avatarContainer = new StackPane();

        Circle avatar = new Circle(
                22,
                Color.web("#5865F2")
        );

        Label robo = new Label("🤖");

        robo.setStyle(
                "-fx-font-size: 20px;"
        );

        avatarContainer.getChildren().addAll(
                avatar,
                robo
        );

        // Nome e status

        VBox info = new VBox(2);

        Label nome = new Label("Nexa AI");

        nome.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 17px;" +
                        "-fx-font-weight: bold;"
        );

        HBox statusBox = new HBox(6);

        statusBox.setAlignment(Pos.CENTER_LEFT);

        Circle online = new Circle(
                4,
                Color.web("#23d18b")
        );

        Label status = new Label("Online");

        status.setStyle(
                "-fx-text-fill: #8b93a7;" +
                        "-fx-font-size: 12px;"
        );

        statusBox.getChildren().addAll(
                online,
                status
        );

        info.getChildren().addAll(
                nome,
                statusBox
        );

        // Espaço

        Region espaco = new Region();

        HBox.setHgrow(
                espaco,
                Priority.ALWAYS
        );

        // Botão limpar

        Button limparButton = new Button("🗑");

        limparButton.setTooltip(
                new Tooltip("Limpar conversa")
        );

        limparButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: #8b93a7;" +
                        "-fx-font-size: 18px;" +
                        "-fx-cursor: hand;"
        );

        limparButton.setOnAction(event ->
                limparConversa()
        );

        header.getChildren().addAll(
                avatarContainer,
                info,
                espaco,
                limparButton
        );

        // =====================================================
        // ÁREA DE MENSAGENS
        // =====================================================

        mensagens = new VBox(14);

        mensagens.setPadding(
                new Insets(20)
        );

        mensagens.setFillWidth(true);

        // Scroll

        scrollPane = new ScrollPane(
                mensagens
        );

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background: #0f1117;" +
                        "-fx-background-color: #0f1117;" +
                        "-fx-border-color: transparent;"
        );

        // =====================================================
        // MENSAGEM INICIAL
        // =====================================================

        adicionarMensagem(
                "Olá! 👋\n\n" +
                        "Eu sou a Nexa AI. " +
                        "Como posso ajudar você hoje?",
                false
        );

        // =====================================================
        // ÁREA INFERIOR
        // =====================================================

        VBox bottom = new VBox();

        bottom.setPadding(
                new Insets(12, 18, 18, 18)
        );

        bottom.setStyle(
                "-fx-background-color: #0f1117;"
        );

        // Campo

        HBox inputBox = new HBox(10);

        inputBox.setAlignment(
                Pos.CENTER
        );

        inputField = new TextField();

        inputField.setPromptText(
                "Digite sua mensagem..."
        );

        inputField.setPrefHeight(48);

        inputField.setStyle(
                "-fx-background-color: #1b1f27;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #2b303b;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: #70788a;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 0 16px;"
        );

        HBox.setHgrow(
                inputField,
                Priority.ALWAYS
        );

        // Botão enviar

        sendButton = new Button("➤");

        sendButton.setPrefSize(
                48,
                48
        );

        sendButton.setStyle(
                "-fx-background-color: #5865F2;" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        sendButton.setOnAction(
                event -> enviarMensagem()
        );

        inputField.setOnAction(
                event -> enviarMensagem()
        );

        inputBox.getChildren().addAll(
                inputField,
                sendButton
        );

        Label aviso = new Label(
                "Nexa AI pode cometer erros. Verifique informações importantes."
        );

        aviso.setStyle(
                "-fx-text-fill: #555d6e;" +
                        "-fx-font-size: 10px;"
        );

        aviso.setMaxWidth(
                Double.MAX_VALUE
        );

        aviso.setAlignment(
                Pos.CENTER
        );

        VBox.setMargin(
                aviso,
                new Insets(7, 0, 0, 0)
        );

        bottom.getChildren().addAll(
                inputBox,
                aviso
        );

        // =====================================================
        // MONTAR TELA
        // =====================================================

        root.setTop(header);
        root.setCenter(scrollPane);
        root.setBottom(bottom);

        // =====================================================
        // CENA
        // =====================================================

        Scene scene = new Scene(
                root,
                800,
                600
        );

        stage.setTitle(
                "Nexa AI"
        );

        stage.setMinWidth(600);
        stage.setMinHeight(450);

        stage.setScene(scene);

        stage.show();

        inputField.requestFocus();
    }

    // =========================================================
    // ADICIONAR MENSAGEM
    // =========================================================

    private void adicionarMensagem(
            String texto,
            boolean usuario
    ) {

        HBox linha = new HBox();

        linha.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox balao = new VBox();

        balao.setMaxWidth(500);

        Label nome = new Label(
                usuario ? "Você" : "Nexa AI"
        );

        nome.setStyle(
                "-fx-text-fill: " +
                        (usuario ? "#8ea2ff" : "#23d18b") +
                        ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        Label textoLabel = new Label(
                texto
        );

        textoLabel.setWrapText(true);

        textoLabel.setStyle(
                "-fx-text-fill: #e6e9ef;" +
                        "-fx-font-size: 14px;"
        );

        balao.getChildren().addAll(
                nome,
                textoLabel
        );

        balao.setPadding(
                new Insets(10, 14, 10, 14)
        );

        if (usuario) {

            balao.setStyle(
                    "-fx-background-color: #5865F2;" +
                            "-fx-background-radius: 15 15 4 15;"
            );

            textoLabel.setStyle(
                    "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;"
            );

            linha.setAlignment(
                    Pos.CENTER_RIGHT
            );

        } else {

            balao.setStyle(
                    "-fx-background-color: #1b1f27;" +
                            "-fx-background-radius: 15 15 15 4;" +
                            "-fx-border-color: #292e38;" +
                            "-fx-border-radius: 15 15 15 4;"
            );

            linha.setAlignment(
                    Pos.CENTER_LEFT
            );
        }

        linha.getChildren().add(
                balao
        );

        mensagens.getChildren().add(
                linha
        );

        rolarParaBaixo();
    }

    // =========================================================
    // ENVIAR MENSAGEM
    // =========================================================

    private void enviarMensagem() {

        String mensagem =
                inputField.getText().trim();

        if (mensagem.isEmpty()) {
            return;
        }

        // Mostrar mensagem do usuário

        adicionarMensagem(
                mensagem,
                true
        );

        inputField.clear();

        sendButton.setDisable(true);
        inputField.setDisable(true);

        // =====================================================
        // "DIGITANDO..."
        // =====================================================

        Label digitando = new Label(
                "Nexa AI está digitando..."
        );

        digitando.setStyle(
                "-fx-text-fill: #7d8597;" +
                        "-fx-font-size: 12px;"
        );

        digitando.setPadding(
                new Insets(0, 0, 0, 10)
        );

        mensagens.getChildren().add(
                digitando
        );

        rolarParaBaixo();

        // =====================================================
        // THREAD
        // =====================================================

        Thread thread = new Thread(() -> {

            try {

                String resposta =
                        chamarGroq(mensagem);

                Platform.runLater(() -> {

                    mensagens.getChildren().remove(
                            digitando
                    );

                    adicionarMensagem(
                            resposta,
                            false
                    );

                    sendButton.setDisable(false);
                    inputField.setDisable(false);

                    inputField.requestFocus();
                });

            } catch (Exception e) {

                Platform.runLater(() -> {

                    mensagens.getChildren().remove(
                            digitando
                    );

                    adicionarMensagem(
                            "❌ Ocorreu um erro:\n\n" +
                                    e.getMessage(),
                            false
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

    // =========================================================
    // LIMPAR CONVERSA
    // =========================================================

    private void limparConversa() {

        mensagens.getChildren().clear();

        adicionarMensagem(
                "Olá! 👋\n\n" +
                        "Conversa nova iniciada. " +
                        "Como posso ajudar?",
                false
        );
    }

    // =========================================================
    // ROLAR PARA BAIXO
    // =========================================================

    private void rolarParaBaixo() {

        Platform.runLater(() -> {

            scrollPane.layout();

            scrollPane.setVvalue(
                    1.0
            );
        });
    }

    // =========================================================
    // GROQ
    // =========================================================

    private String chamarGroq(
            String mensagem
    ) throws Exception {

        String json =
                "{"
                        + "\"model\":\"" + MODEL + "\","
                        + "\"messages\":["
                        + "{"
                        + "\"role\":\"user\","
                        + "\"content\":\""
                        + escaparJson(mensagem)
                        + "\""
                        + "}"
                        + "]"
                        + "}";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(GROQ_URL)
                        )
                        .timeout(
                                Duration.ofSeconds(60)
                        )
                        .header(
                                "Authorization",
                                "Bearer " + GROQ_API_KEY
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(json)
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );

        if (response.statusCode() != 200) {

            throw new Exception(
                    "Erro da API (" +
                            response.statusCode() +
                            "): " +
                            response.body()
            );
        }

        String body =
                response.body();

        String marcador =
                "\"content\":\"";

        int inicio =
                body.indexOf(
                        marcador
                );

        if (inicio == -1) {

            throw new Exception(
                    "Não foi possível encontrar " +
                            "a resposta da IA."
            );
        }

        inicio += marcador.length();

        int fim =
                body.indexOf(
                        "\"",
                        inicio
                );

        if (fim == -1) {

            throw new Exception(
                    "Resposta da API inválida."
            );
        }

        String resposta =
                body.substring(
                        inicio,
                        fim
                );

        resposta = resposta
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");

        return resposta;
    }

    // =========================================================
    // ESCAPAR JSON
    // =========================================================

    private String escaparJson(
            String texto
    ) {

        return texto
                .replace(
                        "\\",
                        "\\\\"
                )
                .replace(
                        "\"",
                        "\\\""
                )
                .replace(
                        "\n",
                        "\\n"
                )
                .replace(
                        "\r",
                        "\\r"
                )
                .replace(
                        "\t",
                        "\\t"
                );
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {

        launch();
    }
}