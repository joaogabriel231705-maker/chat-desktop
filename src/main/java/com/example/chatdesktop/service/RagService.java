package com.example.chatdesktop.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RagService {

    private final GroqService groqService;

    private final Path pastaDocumentos =
            Path.of("documentos");


    // ==========================================
    // CONSTRUTOR
    // ==========================================

    public RagService() {

        groqService = new GroqService();

        criarPastaDocumentos();
    }


    // ==========================================
    // MÉTODO PRINCIPAL DO RAG
    // ==========================================

    public String responder(String pergunta) throws Exception {

        if (pergunta == null || pergunta.isBlank()) {

            return "Digite uma pergunta.";
        }

        pergunta = pergunta.trim();


        // ======================================
        // PROCURA NOS DOCUMENTOS
        // ======================================

        List<String> documentos =
                buscarNosDocumentos(pergunta);


        // ======================================
        // ENCONTROU DOCUMENTO
        // ======================================

        if (!documentos.isEmpty()) {

            System.out.println(
                    "RAG: informação encontrada nos documentos."
            );

            String contexto =
                    montarContexto(documentos);


            String prompt =
                    criarPromptRag(
                            pergunta,
                            contexto
                    );


            return groqService.enviarMensagem(
                    prompt
            );
        }


        // ======================================
        // NÃO ENCONTROU
        // ======================================

        System.out.println(
                "RAG: nenhuma informação encontrada."
        );

        System.out.println(
                "RAG: usando Groq como fallback."
        );


        return groqService.enviarMensagem(
                pergunta
        );
    }


    // ==========================================
    // CRIAR PASTA DE DOCUMENTOS
    // ==========================================

    private void criarPastaDocumentos() {

        try {

            if (!Files.exists(pastaDocumentos)) {

                Files.createDirectories(
                        pastaDocumentos
                );

                System.out.println(
                        "Pasta 'documentos' criada."
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "Não foi possível criar a pasta documentos."
            );
        }
    }


    // ==========================================
    // BUSCAR NOS DOCUMENTOS
    // ==========================================

    private List<String> buscarNosDocumentos(
            String pergunta
    ) {

        List<String> resultados =
                new ArrayList<>();


        if (!Files.exists(pastaDocumentos)) {

            return resultados;
        }


        try {

            List<Path> arquivos =
                    Files.list(pastaDocumentos)
                            .filter(Files::isRegularFile)
                            .filter(this::arquivoSuportado)
                            .toList();


            for (Path arquivo : arquivos) {

                try {

                    String texto =
                            lerDocumento(
                                    arquivo.toFile()
                            );


                    if (texto == null ||
                            texto.isBlank()) {

                        continue;
                    }


                    List<String> trechos =
                            dividirTexto(texto);


                    for (String trecho : trechos) {

                        if (ehRelevante(
                                pergunta,
                                trecho
                        )) {

                            String resultado =
                                    "\nDOCUMENTO: " +
                                            arquivo.getFileName() +
                                            "\n\n" +
                                            trecho;


                            resultados.add(
                                    resultado
                            );
                        }
                    }

                } catch (Exception e) {

                    System.out.println(
                            "Erro ao ler: " +
                                    arquivo.getFileName()
                    );
                }
            }


        } catch (IOException e) {

            System.out.println(
                    "Erro ao procurar documentos."
            );
        }


        // ======================================
        // LIMITA O CONTEXTO
        // ======================================

        if (resultados.size() > 5) {

            return resultados.subList(
                    0,
                    5
            );
        }


        return resultados;
    }


    // ==========================================
    // VERIFICAR EXTENSÃO
    // ==========================================

    private boolean arquivoSuportado(
            Path arquivo
    ) {

        String nome =
                arquivo.getFileName()
                        .toString()
                        .toLowerCase();


        return nome.endsWith(".txt") ||
                nome.endsWith(".pdf") ||
                nome.endsWith(".docx");
    }


    // ==========================================
    // LER DOCUMENTO
    // ==========================================

    private String lerDocumento(
            File arquivo
    ) throws Exception {

        String nome =
                arquivo.getName()
                        .toLowerCase();


        // ======================================
        // TXT
        // ======================================

        if (nome.endsWith(".txt")) {

            return Files.readString(
                    arquivo.toPath(),
                    StandardCharsets.UTF_8
            );
        }


        // ======================================
        // PDF
        // ======================================

        if (nome.endsWith(".pdf")) {

            return lerPdf(arquivo);
        }


        // ======================================
        // DOCX
        // ======================================

        if (nome.endsWith(".docx")) {

            return lerDocx(arquivo);
        }


        return "";
    }


    // ==========================================
    // LER PDF
    // ==========================================

    private String lerPdf(
            File arquivo
    ) throws Exception {

        StringBuilder texto =
                new StringBuilder();


        try (PDDocument documento =
                     Loader.loadPDF(arquivo)) {

            PDFTextStripper stripper =
                    new PDFTextStripper();


            texto.append(
                    stripper.getText(documento)
            );
        }


        return texto.toString();
    }


    // ==========================================
    // LER DOCX
    // ==========================================

    private String lerDocx(
            File arquivo
    ) throws Exception {

        StringBuilder texto =
                new StringBuilder();


        try (
                FileInputStream input =
                        new FileInputStream(arquivo);

                XWPFDocument documento =
                        new XWPFDocument(input)
        ) {

            for (
                    XWPFParagraph paragrafo :
                    documento.getParagraphs()
            ) {

                texto.append(
                        paragrafo.getText()
                );

                texto.append("\n");
            }
        }


        return texto.toString();
    }


    // ==========================================
    // DIVIDIR DOCUMENTO EM TRECHOS
    // ==========================================

    private List<String> dividirTexto(
            String texto
    ) {

        List<String> trechos =
                new ArrayList<>();


        texto = texto
                .replace("\r", " ")
                .replace("\n", " ")
                .replaceAll("\\s+", " ")
                .trim();


        if (texto.isBlank()) {

            return trechos;
        }


        int tamanhoChunk = 1200;

        int inicio = 0;


        while (inicio < texto.length()) {

            int fim =
                    Math.min(
                            inicio + tamanhoChunk,
                            texto.length()
                    );


            String trecho =
                    texto.substring(
                            inicio,
                            fim
                    );


            trechos.add(
                    trecho
            );


            inicio = fim;
        }


        return trechos;
    }


    // ==========================================
    // VERIFICAR SE TRECHO É RELEVANTE
    // ==========================================

    private boolean ehRelevante(
            String pergunta,
            String trecho
    ) {

        String perguntaNormalizada =
                normalizar(pergunta);


        String trechoNormalizado =
                normalizar(trecho);


        String[] palavras =
                perguntaNormalizada.split("\\s+");


        int palavrasEncontradas = 0;


        for (String palavra : palavras) {

            if (palavra.length() < 3) {

                continue;
            }


            if (trechoNormalizado.contains(
                    palavra
            )) {

                palavrasEncontradas++;
            }
        }


        /*
         * Se pelo menos uma palavra importante
         * da pergunta aparecer no trecho,
         * consideramos o trecho relevante.
         */

        return palavrasEncontradas >= 1;
    }


    // ==========================================
    // NORMALIZAR TEXTO
    // ==========================================

    private String normalizar(
            String texto
    ) {

        return texto
                .toLowerCase()
                .replaceAll(
                        "[^a-záéíóúàâêôãõç0-9 ]",
                        " "
                )
                .replaceAll(
                        "\\s+",
                        " "
                )
                .trim();
    }


    // ==========================================
    // MONTAR CONTEXTO
    // ==========================================

    private String montarContexto(
            List<String> documentos
    ) {

        StringBuilder contexto =
                new StringBuilder();


        contexto.append(
                "INFORMAÇÕES ENCONTRADAS NOS DOCUMENTOS:\n\n"
        );


        for (String documento : documentos) {

            contexto.append(
                    documento
            );

            contexto.append(
                    "\n\n-------------------------\n\n"
            );
        }


        return contexto.toString();
    }


    // ==========================================
    // PROMPT DO RAG
    // ==========================================

    private String criarPromptRag(
            String pergunta,
            String contexto
    ) {

        return """
                Você é a Nexa AI.

                Responda à pergunta do usuário utilizando
                principalmente as informações encontradas
                nos documentos fornecidos.

                REGRAS:

                1. Use o contexto dos documentos como fonte
                   principal da resposta.

                2. Não invente informações que não estejam
                   no contexto.

                3. Se o documento não tiver informação
                   suficiente para responder, deixe isso
                   claro.

                4. Responda em português.

                5. Seja claro e objetivo.

                6. Não diga que você é um RAG.

                =============================

                CONTEXTO DOS DOCUMENTOS:

                %s

                =============================

                PERGUNTA DO USUÁRIO:

                %s
                """.formatted(
                contexto,
                pergunta
        );
    }
}