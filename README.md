# 📚 BibliotecaGPS

Aplicativo Android para registrar livros encontrados, comprados, indicados ou lidos em determinados locais, combinando consulta a uma API pública de livros com geolocalização em tempo real e persistência em nuvem.

---

## 📋 Sobre o projeto

O BibliotecaGPS nasceu como avaliação final da disciplina de Desenvolvimento Mobile no Centro Universitário Facens, com o objetivo de aplicar na prática os principais recursos do desenvolvimento Android: consumo de API REST, captura de localização via GPS e persistência de dados na nuvem com Firebase Firestore.

A proposta é simples e útil: o usuário pesquisa um livro, seleciona o resultado retornado pela API, informa onde ou como o encontrou, registra seu status de leitura e salva tudo junto com as coordenadas geográficas do momento. O resultado é uma biblioteca pessoal geolocalizada, cada livro com sua própria história e localização.

---

## 🚀 Funcionalidades

- **Pesquisa de livros** via Google Books API por título, autor ou palavra-chave
- **Exibição de resultados** com título, autor, editora e ano de publicação
- **Captura de localização** com latitude e longitude reais do dispositivo Android
- **Solicitação de permissão** de localização em tempo de execução, com tratamento de negação
- **Cadastro geolocalizado** associando o livro à localização atual
- **Registro de situação** onde o livro foi encontrado: Biblioteca, Livraria, Aula, Indicação, Leitura em casa, Viagem ou Outro
- **Status de leitura**: Quero ler, Lendo ou Concluído
- **Observação pessoal** livre sobre o livro
- **Listagem** de todos os livros cadastrados com título, autor, status, situação e coordenadas
- **Edição** de status, observação e localização de livros já salvos
- **Exclusão** com diálogo de confirmação via clique longo
- **Persistência em nuvem** com Firebase Firestore — dados mantidos após fechar e reabrir o app

---

## 🛠️ Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java | Linguagem principal do projeto |
| Android SDK (min API 24) | Plataforma mobile |
| Retrofit 2 | Chamadas HTTP para a Google Books API |
| Gson | Desserialização do JSON retornado pela API |
| Google Books API | Fonte de dados dos livros |
| FusedLocationProviderClient | Captura de GPS do dispositivo |
| Firebase Firestore | Banco de dados NoSQL em nuvem |

---

## 🏗️ Arquitetura do projeto

O projeto segue uma estrutura simples baseada em Activities, adequada ao nível introdutório da disciplina.

```
BibliotecaGPS/
├── MainActivity.java          # Tela de pesquisa de livros
├── CadastroActivity.java      # Tela de cadastro com GPS e formulário
├── ListaActivity.java         # Listagem dos livros salvos
├── EdicaoActivity.java        # Edição de status, observação e localização
├── Livro.java                 # Modelo de dados principal
├── GoogleBooksService.java    # Interface Retrofit com os endpoints da API
├── GoogleBooksResposta.java   # DTOs para desserialização do JSON
├── LivroAdapter.java          # Adapter do RecyclerView de pesquisa
└── LivroSalvoAdapter.java     # Adapter do RecyclerView de listagem
```

### Fluxo de dados

```
Google Books API  ──►  MainActivity  ──►  CadastroActivity  ──►  Firebase Firestore
                                               ▲
                                          GPS do celular

Firebase Firestore  ──►  ListaActivity  ──►  EdicaoActivity  ──►  Firebase Firestore
```

O objeto `Livro` acumula dados de três fontes distintas ao longo do fluxo:
- **Da API**: título, autor, editora, ano de publicação
- **Do GPS**: latitude e longitude
- **Do usuário**: situação onde encontrou, status de leitura e observação pessoal

---

## 📱 Telas do aplicativo

### Pesquisa
Campo de busca e lista de resultados retornados pela Google Books API em tempo real.

### Cadastro
Exibe os dados do livro selecionado, permite capturar a localização atual via GPS, selecionar a situação e o status de leitura, e adicionar uma observação pessoal antes de salvar no Firebase.

### Meus Livros
Lista todos os livros cadastrados com suas informações completas. Clique curto abre a edição; clique longo exibe opção de exclusão com confirmação.

### Edição
Permite atualizar o status de leitura, a observação pessoal e a localização geográfica do livro já salvo.

---

## ⚙️ Como executar o projeto

### Pré-requisitos

- Android Studio instalado e atualizado
- Dispositivo Android físico ou emulador com API 24+
- Conta Google para acesso ao Firebase

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/usuario/BibliotecaGPS.git
```

2. Abra o projeto no Android Studio e aguarde a sincronização do Gradle.

3. Crie um projeto no [Firebase Console](https://console.firebase.google.com), ative o **Firestore Database** em modo de teste e baixe o arquivo `google-services.json`.

4. Coloque o `google-services.json` dentro da pasta `app/` do projeto.

5. Crie uma chave de API para a **Google Books API** no [Google Cloud Console](https://console.cloud.google.com) e substitua em `MainActivity.java`:
```java
private static final String GOOGLE_BOOKS_API_KEY = "SUA_CHAVE_AQUI";
```

6. Compile e execute no dispositivo ou emulador.

---

## 🗄️ Estrutura do Firestore

Cada livro é salvo como um documento na coleção `livros` com os seguintes campos:

| Campo | Tipo | Origem |
|---|---|---|
| `id` | String | Gerado pelo Firestore |
| `titulo` | String | Google Books API |
| `autor` | String | Google Books API |
| `editora` | String | Google Books API |
| `anoPublicacao` | String | Google Books API |
| `latitude` | Double | GPS do dispositivo |
| `longitude` | Double | GPS do dispositivo |
| `situacaoEncontrado` | String | Usuário |
| `statusLeitura` | String | Usuário |
| `observacao` | String | Usuário |

---

## 👨‍💻 Autores

**Kauê Felippe Tiburcio** — [GitHub](https://github.com/kauefelippet/) · [LinkedIn](https://linkedin.com/in/kaue-felippe-tiburcio/)

**Gabriel dos Santos Campelo** — [GitHub](https://github.com/CampeloGabriel1/) · [LinkedIn](https://linkedin.com/in/campelogabriell/)

---

## 📄 Contexto acadêmico

Projeto desenvolvido como Avaliação Final da disciplina de **Desenvolvimento Mobile** — curso de Engenharia de Software / Ciência da Computação.

Os principais critérios avaliados foram:
- Explicação clara do objetivo do app e das tecnologias utilizadas
- Demonstração do consumo real de API pública em tempo de execução
- Uso do GPS do dispositivo Android com tratamento de permissão
- Persistência funcional com Firebase Firestore (cadastro, consulta, edição e exclusão)
- Domínio do código e da estrutura do projeto pelos integrantes

---

*Desenvolvido com Java e Android Studio.*