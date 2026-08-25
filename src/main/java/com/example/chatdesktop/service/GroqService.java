

package com.example.chatdesktop.service;

import com.example.chatdesktop.config.GroqConfig;

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

    public String enviarMensagem(
            String mensagem
    ) throws Exception {

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

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new Exception(
                    "Erro da API (" +
                            response.statusCode() +
                            "): " +
                            response.body()
            );
        }

        return extrairResposta(
                response.body()
        );
    }

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

    private String extrairResposta(
            String body
    ) throws Exception {

        String marcador =
                "\"content\":\"";

        int inicio =
                body.indexOf(marcador);

        if (inicio == -1) {

            throw new Exception(
                    "Não foi possível encontrar " +
                            "a resposta da IA."
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
                "Resposta da API inválida."
        );
    }

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
