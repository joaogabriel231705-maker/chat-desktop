# 🤖 Chat IA — JavaFX + Groq

Aplicação de chat desenvolvida em **Java** utilizando **JavaFX** para a interface gráfica e a **API da Groq** para comunicação com um modelo de Inteligência Artificial.

## 📌 Sobre o projeto

Este projeto tem como objetivo criar um chat simples e moderno onde o usuário pode enviar perguntas e receber respostas geradas por Inteligência Artificial.

### Tecnologias utilizadas

* ☕ Java 21
* 🎨 JavaFX
* 🤖 Groq API
* 🌐 HTTP Client do Java
* 🧠 Modelo `openai/gpt-oss-20b`
* 💻 IntelliJ IDEA

## ⚙️ Funcionalidades

* [x] Interface gráfica com JavaFX
* [x] Campo para digitar mensagens
* [x] Botão para enviar mensagens
* [x] Área para exibir a conversa
* [x] Comunicação com a API da Groq
* [x] Respostas geradas por IA
* [ ] Histórico de conversas
* [ ] Botão para limpar conversa
* [ ] Personalização do tema
* [ ] Indicador de carregamento

## 🚀 Como executar o projeto

### 1. Requisitos

Antes de executar o projeto, tenha instalado:

* Java JDK 21 ou superior
* JavaFX
* IntelliJ IDEA
* Uma chave da API da Groq

### 2. Configurar a API

Crie uma conta na Groq e gere uma chave de API.

**Importante:** não coloque sua chave real diretamente no GitHub ou em arquivos públicos.

Recomenda-se utilizar uma variável de ambiente:

```text
GROQ_API_KEY=sua_chave_aqui
```

No código Java, a chave pode ser obtida através de:

```java
String GROQ_API_KEY = System.getenv("GROQ_API_KEY");
```

### 3. Configuração do modelo

O projeto utiliza atualmente:

```text
openai/gpt-oss-20b
```

O endereço utilizado para comunicação é:

```text
https://api.groq.com/openai/v1/chat/completions
```

### 4. Executar

Abra o projeto no IntelliJ IDEA e execute a classe:

```text
HelloApplication.java
```

A janela do aplicativo será aberta e o usuário poderá digitar uma mensagem no campo de texto e enviá-la para a IA.

## 🖥️ Estrutura básica

```text
ChatDesktop
│
├── src
│   └── main
│       └── java
│           └── com.example.chatdesktop
│               └── HelloApplication.java
│
├── pom.xml
└── README.md
```

## 💬 Como utilizar

1. Abra o aplicativo.
2. Digite uma pergunta no campo de mensagem.
3. Clique no botão **Enviar**.
4. A aplicação envia a mensagem para a API da Groq.
5. A resposta da Inteligência Artificial aparece na área do chat.

## 🔐 Segurança

A chave da API é uma informação privada.

**Nunca publique sua chave no GitHub, envie para outras pessoas ou deixe uma chave real diretamente no código.**

Caso uma chave tenha sido exposta, o recomendado é revogá-la e gerar uma nova.

## 🎯 Objetivo

O projeto foi desenvolvido como uma aplicação prática para estudar:

* Programação Java
* JavaFX
* Interfaces gráficas
* APIs REST
* Requisições HTTP
* JSON
* Inteligência Artificial
* Integração entre aplicações Java e serviços externos

## 👨‍💻 Desenvolvedor

Projeto desenvolvido para fins de estudo e aprendizado em programação Java.

---

⭐ **Chat IA — JavaFX + Groq**
