# 🤖 Nexa AI — Chat Inteligente com RAG

O **Nexa AI** é um aplicativo de chat inteligente desenvolvido em **Java e JavaFX**, integrado à API da **Groq** e com suporte à arquitetura **RAG (Retrieval-Augmented Generation)**.

O sistema permite que o usuário converse com a IA, utilize documentos locais como fonte de conhecimento e, quando necessário, utilize uma fonte externa para complementar as informações.

---

## 🚀 Funcionalidades

### 🧠 RAG Interna e Externa

O sistema possui uma arquitetura capaz de trabalhar com duas fontes de informação.

#### 📁 RAG Interna

A RAG interna pesquisa informações nos documentos disponibilizados dentro do sistema.

```text
Pergunta do usuário
        ↓
Busca nos documentos
        ↓
Informação encontrada?
   ↓              ↓
 SIM             NÃO
   ↓              ↓
Contexto       RAG Externa
   ↓              ↓
   └──────→ IA ←─┘
            ↓
         Resposta
```

Quando uma informação relevante é encontrada nos documentos, ela é enviada como contexto para o modelo de IA.

#### 🌐 RAG Externa

Caso a RAG interna não encontre informações suficientes, o sistema pode utilizar uma fonte externa para complementar a resposta.

Isso permite que o chat não fique limitado apenas aos documentos internos.

---

## 📌 Indicador de origem da resposta

Cada resposta pode apresentar sua origem para que o usuário saiba como a informação foi obtida.

Exemplos:

* 📁 **RAG Interna**
* 🌐 **RAG Externa**
* 🤖 **IA**

Isso proporciona maior transparência durante a utilização do sistema.

---

## 📄 Arquivo utilizado como fonte

Quando a resposta utiliza informações encontradas através da RAG interna, o sistema pode mostrar o documento utilizado.

Exemplo:

```text
📁 Fonte: manual_sistema.pdf
```

Dessa forma, o usuário consegue identificar de qual arquivo vieram as informações utilizadas na resposta.

---

## 🔄 Regenerar resposta

Foi adicionado o botão **"Regenerar resposta"**.

Essa função permite gerar uma nova resposta para a mesma pergunta sem que o usuário precise digitá-la novamente.

### Funcionamento

1. O usuário envia uma pergunta.
2. A IA gera uma resposta.
3. O usuário seleciona **Regenerar resposta**.
4. A pergunta é processada novamente.
5. Uma nova resposta é exibida.

---

## 🗂️ Histórico de conversas

O sistema possui gerenciamento do histórico de conversas.

É possível:

* Criar novas conversas.
* Visualizar conversas anteriores.
* Continuar conversas existentes.
* Renomear conversas.
* Excluir conversas.

---

## ✏️ Renomear conversa

O usuário pode alterar o nome de uma conversa para facilitar sua identificação no histórico.

Exemplo:

```text
Conversa 1
```

pode ser alterada para:

```text
Pesquisa sobre Inteligência Artificial
```

Isso facilita a organização de várias conversas.

---

## 🗑️ Excluir conversa

O usuário pode remover uma conversa do histórico.

A exclusão remove a conversa da lista de conversas disponíveis.

---

## ⚠️ Confirmação ao apagar conversa

Para evitar exclusões acidentais, o sistema solicita uma confirmação antes de apagar uma conversa.

Exemplo:

```text
Deseja realmente excluir esta conversa?

Esta ação não poderá ser desfeita.

[Cancelar]     [Excluir]
```

A conversa somente é removida após a confirmação do usuário.

---

# 🌓 Tema claro e escuro

O aplicativo possui suporte a **tema claro e tema escuro**.

O usuário pode alternar entre os temas através do botão de configuração da interface.

### 🌙 Tema escuro

O tema escuro utiliza uma interface com cores mais escuras, proporcionando uma aparência moderna para utilização em ambientes com pouca iluminação.

### ☀️ Tema claro

O tema claro utiliza uma interface predominantemente clara, facilitando a utilização em ambientes bem iluminados.

A troca de tema ocorre diretamente na interface sem necessidade de reiniciar o aplicativo.

---

# ⌨️ Atalhos de teclado

O sistema possui atalhos de teclado para agilizar a utilização do chat.

Exemplos de funcionalidades que podem ser acessadas através do teclado:

| Atalho                  | Função             |
| ----------------------- | ------------------ |
| `Enter`                 | Enviar mensagem    |
| `Shift + Enter`         | Inserir nova linha |
| `Ctrl + N`              | Nova conversa      |
| `Ctrl + R`              | Regenerar resposta |
| `Ctrl + Shift + D`      | Alternar tema      |
| `Ctrl + Shift + Delete` | Excluir conversa   |

Os atalhos tornam a utilização do sistema mais rápida e prática.

---

# 📝 Título automático

O sistema possui geração automática de título para as conversas.

Quando uma nova conversa é iniciada, o sistema pode analisar a primeira mensagem enviada pelo usuário e gerar um título relacionado ao assunto.

### Exemplo

Usuário:

```text
Como funciona inteligência artificial?
```

O sistema pode gerar automaticamente:

```text
Funcionamento da Inteligência Artificial
```

Isso facilita a identificação das conversas no histórico sem que o usuário precise renomeá-las manualmente.

---

# 🛡️ Tratamento de erros

O sistema possui tratamento de erros para evitar que problemas internos encerrem o aplicativo inesperadamente.

Entre os casos tratados estão:

* ❌ Falha na conexão com a API.
* ❌ API indisponível.
* ❌ Erro durante uma requisição.
* ❌ Erro na resposta da API.
* ❌ Documento não encontrado.
* ❌ Erro durante a leitura de documentos.
* ❌ Nenhuma informação relevante encontrada.
* ❌ Falha na RAG interna.
* ❌ Falha na RAG externa.
* ❌ Erros durante a regeneração.
* ❌ Erros ao excluir conversas.
* ❌ Erros ao carregar recursos da interface.
* ❌ Respostas vazias ou inválidas.

Quando ocorre um problema, o sistema apresenta uma mensagem amigável ao usuário.

Exemplo:

```text
⚠️ Não foi possível obter uma resposta.

Verifique sua conexão e tente novamente.
```

---

# 🔀 Fluxo completo do sistema

O funcionamento geral do Nexa AI pode ser representado da seguinte maneira:

```text
                    ┌───────────────┐
                    │    USUÁRIO    │
                    └───────┬───────┘
                            │
                            ↓
                    ┌───────────────┐
                    │    PERGUNTA   │
                    └───────┬───────┘
                            │
                            ↓
                  ┌───────────────────┐
                  │    RAG INTERNA    │
                  │ Documentos locais │
                  └─────────┬─────────┘
                            │
                   ┌────────┴────────┐
                   │                 │
                ENCONTROU         NÃO ENCONTROU
                   │                 │
                   ↓                 ↓
             ┌───────────┐   ┌────────────────┐
             │ Contexto  │   │   RAG EXTERNA  │
             │ do arquivo│   │ Fonte externa  │
             └─────┬─────┘   └───────┬────────┘
                   │                 │
                   └────────┬────────┘
                            ↓
                     ┌───────────────┐
                     │  GROQ / IA    │
                     └───────┬───────┘
                             ↓
                     ┌───────────────┐
                     │    RESPOSTA   │
                     └───────┬───────┘
                             ↓
                ┌────────────────────────┐
                │ Origem + Fonte + Texto │
                └────────────────────────┘
```

---

# 🧩 Tecnologias utilizadas

* **Java**
* **JavaFX**
* **FXML**
* **CSS**
* **Maven**
* **Groq API**
* **RAG (Retrieval-Augmented Generation)**

---

# 📂 Estrutura do projeto

Estrutura aproximada:

```text
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── chatdesktop/
│   │               ├── Controller/
│   │               │   ├── ChatController.java
│   │               │   └── TelaInicialController.java
│   │               │
│   │               ├── config/
│   │               │   └── GroqConfig.java
│   │               │
│   │               ├── model/
│   │               │   └── Mensagem.java
│   │               │
│   │               ├── service/
│   │               │   ├── GroqService.java
│   │               │   └── RagService.java
│   │               │
│   │               └── Main.java
│   │
│   └── resources/
│       ├── css/
│       │   ├── style.css
│       │   └── inicio.css
│       │
│       ├── fxml/
│       │   ├── hello-view.fxml
│       │   └── tela-inicial.fxml
│       │
│       └── documents/
│
├── module-info.java
└── pom.xml
```

---

# 🔐 Segurança

A chave utilizada para acessar a API não deve ser exposta publicamente.

Recomenda-se utilizar variáveis de ambiente ou outro método seguro para armazenar credenciais.

**Nunca publique sua chave da API no GitHub ou diretamente no código-fonte.**

---

# 🎯 Objetivo do projeto

O objetivo do Nexa AI é desenvolver um assistente inteligente capaz de combinar **inteligência artificial, documentos locais e fontes externas**, proporcionando respostas mais contextualizadas.

O projeto busca oferecer:

* 🤖 Conversação com IA.
* 📁 Consulta a documentos internos.
* 🌐 Consulta externa.
* 📌 Identificação da origem das respostas.
* 📄 Identificação dos arquivos utilizados.
* 🔄 Regeneração de respostas.
* 🗂️ Gerenciamento do histórico.
* ✏️ Renomeação de conversas.
* 🗑️ Exclusão segura de conversas.
* 🌓 Tema claro e escuro.
* ⌨️ Atalhos de teclado.
* 📝 Títulos automáticos.
* 🛡️ Tratamento de erros.

---

# 📈 Melhorias futuras

Algumas funcionalidades que podem ser implementadas futuramente:

* 🔎 Busca semântica avançada.
* 🧠 Banco de dados vetorial.
* 📚 Suporte para mais formatos de documentos.
* 💾 Persistência avançada das conversas.
* 👤 Sistema de usuários.
* 📊 Estatísticas de utilização.
* ⚙️ Configurações avançadas da IA.
* 🔐 Gerenciamento aprimorado de credenciais.
* 📌 Mais fontes externas para a RAG.

---

# 🚧 Status

**Em desenvolvimento**

O Nexa AI continua recebendo melhorias na interface, gerenciamento das conversas, tratamento de erros e arquitetura de RAG.

---

# 📜 Licença

Projeto desenvolvido para fins educacionais e de desenvolvimento de software.
