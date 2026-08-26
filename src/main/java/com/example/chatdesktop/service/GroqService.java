package com.example.chatdesktop.service;

import com.example.chatdesktop.config.GroqConfig;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class GroqService {

    private final HttpClient httpClient;

    public GroqService() {

        httpClient = HttpClient.newBuilder()
                .connectTimeout(
                        Duration.ofSeconds(20)
                )
                .build();
    }

    // ==========================================
    // RESPOSTA NORMAL DA IA
    // ==========================================

    public String enviarMensagem(
            String mensagem
    ) throws Exception {

        try {

            String json =
                    criarJson(mensagem);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            GroqConfig.GROQ_URL
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(60)
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " +
                                            GroqConfig.getApiKey()
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

            verificarErroHttp(
                    response.statusCode(),
                    response.body()
            );

            return extrairResposta(
                    response.body()
            );

        } catch (ConnectException e) {

            throw new Exception(
                    "SEM_INTERNET"
            );

        } catch (java.net.http.HttpTimeoutException e) {

            throw new Exception(
                    "TIMEOUT"
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new Exception(
                    "COMUNICACAO"
            );

        } catch (IOException e) {

            throw new Exception(
                    "COMUNICACAO"
            );
        }
    }


    // ==========================================
    // GERAR TÍTULO DA CONVERSA
    // ==========================================

    public String gerarTitulo(
            String mensagem
    ) throws Exception {

        try {

            String prompt =
                    "Crie um título curto para uma conversa " +
                            "com base na mensagem abaixo. " +
                            "O título deve ter no máximo 5 palavras. " +
                            "Não use aspas, emojis ou pontuação. " +
                            "Responda somente com o título.\n\n" +
                            "Mensagem: " +
                            mensagem;

            String json =
                    criarJson(prompt);

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            GroqConfig.GROQ_URL
                                    )
                            )
                            .timeout(
                                    Duration.ofSeconds(30)
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " +
                                            GroqConfig.getApiKey()
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

            verificarErroHttp(
                    response.statusCode(),
                    response.body()
            );

            String titulo =
                    extrairResposta(
                            response.body()
                    );

            titulo = titulo.trim();

            // Remove aspas caso a IA coloque
            titulo = titulo
                    .replace("\"", "")
                    .replace("'", "");

            return titulo;

        } catch (ConnectException e) {

            throw new Exception(
                    "SEM_INTERNET"
            );

        } catch (java.net.http.HttpTimeoutException e) {

            throw new Exception(
                    "TIMEOUT"
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new Exception(
                    "COMUNICACAO"
            );

        } catch (IOException e) {

            throw new Exception(
                    "COMUNICACAO"
            );
        }
    }


    // ==========================================
    // VERIFICAR ERROS HTTP
    // ==========================================

    private void verificarErroHttp(
            int statusCode,
            String body
    ) throws Exception {

        // ======================================
        // CHAVE DA API INVÁLIDA
        // ======================================

        if (statusCode == 401) {

            throw new Exception(
                    "CHAVE_INVALIDA"
            );
        }


        // ======================================
        // SEM PERMISSÃO
        // ======================================

        if (statusCode == 403) {

            throw new Exception(
                    "SEM_PERMISSAO"
            );
        }


        // ======================================
        // LIMITE DA API
        // ======================================

        if (statusCode == 429) {

            throw new Exception(
                    "LIMITE_API"
            );
        }


        // ======================================
        // ERRO DO SERVIDOR
        // ======================================

        if (statusCode >= 500 &&
                statusCode <= 599) {

            throw new Exception(
                    "SERVIDOR"
            );
        }


        // ======================================
        // OUTROS ERROS
        // ======================================

        if (statusCode < 200 ||
                statusCode >= 300) {

            throw new Exception(
                    "COMUNICACAO"
            );
        }
    }


    // ==========================================
    // CRIAR JSON
    // ==========================================

    private String criarJson(
            String mensagem
    ) {

        return "{"
                + "\"model\":\""
                + GroqConfig.MODEL
                + "\","
                + "\"messages\":["
                + "{"
                + "\"role\":\"user\","
                + "\"content\":\""
                + escaparJson(mensagem)
                + "\""
                + "}"
                + "]"
                + "}";
    }


    // ==========================================
    // EXTRAIR RESPOSTA
    // ==========================================

    private String extrairResposta(
            String body
    ) throws Exception {

        String marcador =
                "\"content\":\"";

        int inicio =
                body.indexOf(marcador);

        if (inicio == -1) {

            throw new Exception(
                    "RESPOSTA_INVALIDA"
            );
        }

        inicio += marcador.length();

        StringBuilder resposta =
                new StringBuilder();

        boolean escapado = false;

        for (
                int i = inicio;
                i < body.length();
                i++
        ) {

            char c =
                    body.charAt(i);

            if (escapado) {

                switch (c) {

                    case 'n':
                        resposta.append('\n');
                        break;

                    case 'r':
                        resposta.append('\r');
                        break;

                    case 't':
                        resposta.append('\t');
                        break;

                    case '"':
                        resposta.append('"');
                        break;

                    case '\\':
                        resposta.append('\\');
                        break;

                    default:
                        resposta.append(c);
                }

                escapado = false;

                continue;
            }

            if (c == '\\') {

                escapado = true;

                continue;
            }

            if (c == '"') {

                return resposta.toString();
            }

            resposta.append(c);
        }

        throw new Exception(
                "RESPOSTA_INVALIDA"
        );
    }


    // ==========================================
    // ESCAPAR JSON
    // ==========================================

    private String escaparJson(
            String texto
    ) {

        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}