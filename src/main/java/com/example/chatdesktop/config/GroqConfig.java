package com.example.chatdesktop.config;

public class GroqConfig {

    public static final String GROQ_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    public static final String MODEL =
            "openai/gpt-oss-20b";

    public static String getApiKey() {

        String apiKey =
                System.getenv(
                        "GROQ_API_KEY"
                );

        if (
                apiKey == null ||
                        apiKey.isBlank()
        ) {

            throw new RuntimeException(
                    "A variável GROQ_API_KEY " +
                            "não foi configurada."
            );
        }

        return apiKey;
    }
}
