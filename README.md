# 🗳️ API de Gestão de Votações em Assembleias Cooperativas

> **Desafio Técnico Backend** — Sistema RESTful para gerenciamento de pautas, controle de sessões de votação, apuração de resultados em tempo real, validação de associados e suporte a *Server-Driven UI (SDUI)*.

---

## 📌 1. Visão Geral do Projeto

No cooperativismo, cada associado possui direito a um voto e as decisões estratégicas são tomadas em assembleias. Esta aplicação fornece uma solução backend robusta, escalável e segura para orquestrar o processo de votação digital de pautas assembleares.

### Principais Capacidades da Aplicação:
- **Gestão de Pautas**: Cadastro e listagem de pautas a serem submetidas à deliberação.
- **Controle de Sessões de Votação**: Abertura de sessões com tempo de validade parametrizável (tempo padrão: **1 minuto**).
- **Registro Seguro de Votos**:
  - Aceite de votos válidos (`SIM` ou `NAO`).
  - **Garantia de Voto Único**: Cada associado (identificado por CPF) pode votar apenas uma vez por pauta.
  - **Validação de Janela Temporal**: Bloqueio de votos quando a sessão estiver encerrada.
- **Apuração de Resultados**: Contabilização instantânea de votos e emissão de veredito final (*APROVADA*, *REJEITADA* ou *EMPATE*).
- **Diferenciais & Bônus**:
  - 🌐 **Integração Externa (Bônus 1)**: Validação matemática e simulação de serviço externo de elegibilidade de CPF (`ABLE_TO_VOTE` / `UNABLE_TO_VOTE`).
  - ⚡ **Performance & Alta Concorrência (Bônus 2)**: Testes de estresse com cenários de alta concorrência (*Race Condition*, multi-threading via JUnit e script de carga com **k6** para validação de SLAs de latência e throughput).
  - 🔄 **Versionamento de API (Bônus 3)**: Estrutura preparada para evolução contínua sob o prefixo `/api/v1/`.
  - 📱 **Server-Driven UI - SDUI (Anexo 1)**: Fornecimento de esquemas visuais dinâmicos para renderização declarativa em aplicativos clientes.
  - 🌍 **Multi-Environment / Profiles**: Gerenciamento de ambientes `des` (Desenvolvimento), `tes` (Homologação/Testes) e `prod` (Produção).

---

## 🚀 2. Tecnologias e Versões Utilizadas

A aplicação foi desenvolvida utilizando a versão LTS mais recente da plataforma Java e as melhores práticas do ecossistema Spring:

| Tecnologia | Versão | Finalidade |
| :--- | :--- | :--- |
| **Java** | `21 (LTS)` | Linguagem de programação principal (Virtual Threads ready, Pattern Matching, Records) |
| **Spring Boot** | `3.4.3` | Framework corporativo base para a criação do microserviço REST |
| **Spring Boot Starter Web** | `3.4.3` | Camada RESTful, Spring MVC e servidor Tomcat embutido |
| **Spring Boot Starter Data JPA** | `3.4.3` | Abstração de persistência relacional com Hibernate 6 |
| **Spring Boot Starter Validation** | `3.4.3` | Validação declarativa de entrada via Jakarta Bean Validation |
| **H2 Database** | `2.3.232` | Banco de dados relacional em memória (des/tes) e persistido em arquivo (prod) |
| **Springdoc OpenAPI / Swagger UI** | `2.8.5` | Documentação viva interativa e especificação OpenAPI 3 |
| **Lombok** | `1.18.36` | Produtividade e redução de boilerplate de código Java |
| **Spring Boot Starter Test / JUnit 5** | `3.4.3` / `5.11+` | Suíte de testes unitários, testes de integração (`MockMvc`) e performance concorrente |
| **k6 (Grafana Labs)** | `0.45+` / `Latest` | Ferramenta de teste de carga e performance em JavaScript |
| **Apache Maven** | `3.9+` | Gerenciador de dependências e automação de build |

---

## 🏛️ 3. Arquitetura e Padrões de Projeto

O projeto adota uma arquitetura em camadas desacopladas (*Layered Architecture*), seguindo os princípios de *Clean Code*, *SOLID* e boas práticas corporativas:

```
src/main/java/com/example/demo/
├── config/                  # Configurações do ecossistema (OpenAPI, Profiles, Runners)
│   └── environment/         # Implementação de Strategy polimórfica para os perfis DES, TES e PROD
├── domain/                  # Núcleo de domínio da aplicação
│   ├── model/               # Entidades JPA (Pauta, SessaoVotacao, Voto) e Enums ricos
│   └── repository/          # Interfaces Spring Data JPA para acesso a dados com consultas otimizadas
├── exception/               # Hierarquia de exceções de negócio e Global Exception Handler (RFC 7807)
├── facade/                  # Padrão Facade: isola e orquestra a camada Web das regras de negócio
├── infrastructure/          # Adaptadores e serviços externos (validação e cálculo de CPF)
│   ├── client/              # Simulação de cliente HTTP externo para validação de associados
│   └── util/                # Utilitários algorítmicos (dígitos verificadores de CPF)
├── service/                 # Regras de negócio puras (Pauta, Sessão, Votação e SDUI)
└── web/                     # Camada de apresentação e entrada
    ├── controller/          # Controladores REST com documentação OpenAPI detalhada
    └── dto/                 # Data Transfer Objects com validação Jakarta Bean Validation
        └── sdui/            # Contratos de telas dinâmicas (Server-Driven UI)
```

### Destaques Arquiteturais:
1. **Padrão Facade (`PautaFacade`, `SduiFacade`)**: Garante o desacoplamento entre os controladores e as regras de serviço, simplificando a orquestração e testes.
2. **RFC 7807 (Problem Details for HTTP APIs)**: Tratamento unificado de erros com `ProblemDetail`, retornando payloads semânticos, códigos de status adequados (`400`, `404`, `409`, `422`, `500`) e rastreabilidade com *timestamp*.
3. **Controle de Concorrência e Idempotência**: Restrição de unicidade no banco de dados (`UK_pauta_cpf`) combinada com verificação em nível de serviço para mitigar requisições concorrentes disparadas no mesmo milissegundo.
4. **Isolamento de Ambientes por Profiles**: Injeção dinâmica do bean `AmbienteService` correspondente ao perfil ativo, personalizando logs de inicialização, documentação OpenAPI e políticas do banco de dados.

---

## ⚙️ 4. Perfis de Ambiente (Profiles)

A aplicação conta com configurações customizadas por ambiente:

| Profile | Descrição | Banco de Dados | Console H2 | Swagger UI |
| :--- | :--- | :--- | :---: | :---: |
| **`des`** *(Padrão)* | Desenvolvimento local | H2 em memória (`jdbc:h2:mem:votedb_des`) | Ativo (`/h2-console`) | Ativo (`/swagger-ui.html`) |
| **`tes`** | Homologação / Testes integrados | H2 em memória (`jdbc:h2:mem:votedb_tes`) | Inativo | Ativo |
| **`prod`** | Produção | H2 persistido em disco (`./data/votedb_prod`) | Inativo | Parametrizável (`SWAGGER_ENABLED`) |

---

## 🛠️ 5. Pré-requisitos para Execução Local

Antes de iniciar, certifique-se de possuir instalado em sua máquina:
- **Java Development Kit (JDK) 21** instalado ([Eclipse Temurin](https://adoptium.net/) ou [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21)).
- **Apache Maven 3.9+** instalado e configurado no `PATH` do sistema.
- *(Opcional)* **k6** instalado para execução dos testes de carga externos ([Download k6](https://k6.io/docs/get-started/installation/)).
- *(Opcional)* **Git** para clonar o repositório.

Para verificar suas instalações:
```bash
java -version
mvn -version
```

---

## 💻 6. Como Rodar o Projeto Localmente

### Passo 1: Obter o Código-Fonte
Se estiver clonando via Git:
```bash
git clone <URL_DO_REPOSITORIO>
cd demo/demo
```
*(Certifique-se de estar no diretório que contém o arquivo `pom.xml`)*

---

### Passo 2: Compilar o Projeto e Rodar os Testes
Para compilar as classes e verificar toda a suíte de testes automatizados:
```bash
mvn clean test
```

---

### Passo 3: Inicializar a Aplicação

#### Opção A: Executar via Plugin do Spring Boot (Recomendado para Dev)
```bash
mvn spring-boot:run
```
> Por padrão, a aplicação inicializará com o perfil **`des`** na porta **`8080`**.

Para especificar um perfil diferente na inicialização:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=tes
```

#### Opção B: Gerar e Executar o Pacote JAR
```bash
# 1. Gerar o pacote JAR
mvn clean package -DskipTests

# 2. Executar a aplicação
java -jar target/demo-0.0.1-SNAPSHOT.jar

# 3. Ou executar definindo um profile específico (ex: prod):
java -Dspring.profiles.active=prod -jar target/demo-0.0.1-SNAPSHOT.jar
```

#### Opção C: Executar pela IDE (IntelliJ IDEA / Eclipse / VS Code)
1. Abra o projeto na sua IDE favorita.
2. Aguarde a sincronização das dependências do Maven.
3. Localize e execute a classe principal:
   `com.example.demo.DemoApplication`

---

## 📖 7. Documentação da API e Ferramentas Interativas

Com a aplicação rodando localmente, acesse em seu navegador:

- **Swagger UI (Documentação Interativa e Teste de Endpoints)**:  
  👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) ou [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON Spec**:  
  👉 [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
- **H2 Database Web Console** (disponível no profile `des`):  
  👉 [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  - **JDBC URL**: `jdbc:h2:mem:votedb_des`
  - **User Name**: `sa`
  - **Password**: *(deixar em branco)*

---

## 🔌 8. Endpoints da API e Exemplos de Uso

Abaixo estão listados os endpoints REST com exemplos práticos utilizando `curl` e payloads JSON:

### 8.1. Pautas (`/api/v1/pautas`)

#### ➕ Cadastrar uma Nova Pauta
- **Método**: `POST`
- **URL**: `http://localhost:8080/api/v1/pautas`
- **Headers**: `Content-Type: application/json`

**Exemplo cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Aprovação do orçamento anual para investimentos em tecnologia 2026"
  }'
```

**Resposta (`201 Created`):**
```json
{
  "id": 1,
  "descricao": "Aprovação do orçamento anual para investimentos em tecnologia 2026",
  "dataCriacao": "2026-09-28T16:00:00",
  "statusSessao": "NAO_ABERTA"
}
```

---

#### 📋 Listar Todas as Pautas
- **Método**: `GET`
- **URL**: `http://localhost:8080/api/v1/pautas`

**Exemplo cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/pautas
```

---

#### 🔍 Consultar Pauta por ID
- **Método**: `GET`
- **URL**: `http://localhost:8080/api/v1/pautas/1`

**Exemplo cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/pautas/1
```

---

### 8.2. Sessões de Votação (`/api/v1/pautas/{id}/sessao`)

#### ⏱️ Abrir Sessão de Votação na Pauta
- **Método**: `POST`
- **URL**: `http://localhost:8080/api/v1/pautas/{id}/sessao`
- **Regra**: O campo `duracaoMinutos` é opcional. Caso não seja enviado (ou seja nulo/vazio), assume **1 minuto** como padrão.

**Exemplo com duração personalizada (ex: 5 minutos):**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessao \
  -H "Content-Type: application/json" \
  -d '{
    "duracaoMinutos": 5
  }'
```

**Exemplo adotando o tempo padrão (1 minuto):**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessao \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Resposta (`201 Created`):**
```json
{
  "id": 1,
  "pautaId": 1,
  "dataHoraInicio": "2026-09-28T16:05:00",
  "dataHoraFim": "2026-09-28T16:06:00",
  "status": "ABERTA",
  "duracaoMinutos": 1
}
```

---

#### 🕒 Consultar Sessão de Votação
- **Método**: `GET`
- **URL**: `http://localhost:8080/api/v1/pautas/1/sessao`

**Exemplo cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/pautas/1/sessao
```

---

### 8.3. Votação (`/api/v1/pautas/{id}/votos`)

#### 🗳️ Registrar Voto do Associado
- **Método**: `POST`
- **URL**: `http://localhost:8080/api/v1/pautas/{id}/votos`
- **Validações Aplicadas**:
  - Sessão precisa estar aberta no momento da votação (`422 Unprocessable Entity` se fechada).
  - O CPF deve ser válido matematicamente (`400 Bad Request` se inválido).
  - O associado deve estar apto (`422 Unprocessable Entity` caso o serviço externo retorne `UNABLE_TO_VOTE`).
  - O associado só pode votar uma única vez na pauta (`409 Conflict` se já votou).

**Exemplo cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "08123456789",
    "voto": "SIM"
  }'
```

**Resposta (`201 Created`):**
```json
{
  "id": 1,
  "pautaId": 1,
  "associadoCpf": "08123456789",
  "opcaoVoto": "SIM",
  "dataHoraVoto": "2026-09-28T16:05:30"
}
```

---

### 8.4. Apuração e Resultado (`/api/v1/pautas/{id}/resultado`)

#### 📊 Obter Resultado da Votação
- **Método**: `GET`
- **URL**: `http://localhost:8080/api/v1/pautas/1/resultado`

**Exemplo cURL:**
```bash
curl -X GET http://localhost:8080/api/v1/pautas/1/resultado
```

**Resposta (`200 OK`):**
```json
{
  "pautaId": 1,
  "descricaoPauta": "Aprovação do orçamento anual para investimentos em tecnologia 2026",
  "statusSessao": "ENCERRADA",
  "totalVotos": 45,
  "totalVotosSim": 30,
  "totalVotosNao": 15,
  "resultado": "APROVADA"
}
```

---

### 8.5. Server-Driven UI - SDUI (`/api/v1/sdui`)

Endpoints desenvolvidos para fornecer contratos visuais flexíveis consumidos por clientes móveis/web:

| Endpoint | Tipo de Tela | Descrição |
| :--- | :--- | :--- |
| `GET /api/v1/sdui/telas/pauta-form` | `FORMULARIO` | Tela dinâmica com campo de texto e botão de ação para cadastrar pauta |
| `GET /api/v1/sdui/telas/pautas` | `SELECAO` | Tela dinâmica com lista de pautas e botões de navegação |
| `GET /api/v1/sdui/telas/pautas/{id}/sessao-form` | `FORMULARIO` | Tela dinâmica para configurar e abrir a sessão de votação |
| `GET /api/v1/sdui/telas/pautas/{id}/votar` | `FORMULARIO` | Tela dinâmica com campo de CPF e opções de voto Sim/Não |

---

## 🏆 9. Diferenciais e Tarefas Bônus Implementadas

### 🎯 Bônus 1: Integração com Serviço de Validação de CPF
- Implementação da classe `CpfValidationServiceImpl` integrando a regra de validação de elegibilidade do associado.
- Contém a checagem algorítmica real dos dígitos verificadores através da classe `CpfUtil` e simulação de retorno do status (`ABLE_TO_VOTE` / `UNABLE_TO_VOTE`).

### ⚡ Bônus 2: Performance, Testes de Carga e Alta Concorrência
- **Testes de Concorrência Nativos no JUnit (`VotacaoPerformanceIntegrationTest`)**:
  - Teste com `ExecutorService` e `CountDownLatch` disparando 100 requisições simultâneas em 20 threads concorrentes.
  - Teste de **Race Condition**: 20 requisições simultâneas disputando o voto com o mesmo CPF no exato mesmo instante, validando que exatamente **1 requisição tem sucesso (`201`)** e as outras **19 retornam conflito (`409`)**, sem corrupção de estado.
- **Script de Carga com k6 (`performance/k6-votacao-load-test.js`)**:
  - Simulação de ramp-up com até 500 usuários virtuais concorrentes (VUs).
  - SLA configurado para latência $p(95) < 200\text{ms}$, $p(99) < 500\text{ms}$ e taxa de falhas $< 1\%$.

### 🔄 Bônus 3: Versionamento da API
- Todos os contratos RESTful foram criados sob o namespace padronizado `/api/v1/`, permitindo a evolução futura da API para `/api/v2/` sem quebrar clientes legados.

### 📱 Anexo 1: Server-Driven UI (SDUI)
- Módulo completo de contratos declarativos de telas (`SduiController`, `SduiService`, `SduiTelaDTO`), permitindo alterações dinâmicas na interface do usuário diretamente pelo backend.

---

## 🧪 10. Execução de Testes Automatizados

### 10.1. Rodando a Suíte Completa de Testes no Maven
```bash
mvn clean test
```
A suíte inclui:
- **Testes Unitários**: Validação isolada de Services, Facades, utilitários e validações de CPF.
- **Testes de Integração Web (`@SpringBootTest` / `MockMvc`)**: Validação de fluxo de ponta a ponta dos controllers, respostas HTTP, serialização JSON e violações de integridade.
- **Testes de Profiles**: Verificação de carregamento dos beans de ambiente (`DES`, `TES`, `PROD`).
- **Testes de Concorrência**: `VotacaoPerformanceIntegrationTest`.

### 10.2. Executando os Testes de Carga com o k6
Para simular carga massiva de votação na aplicação rodando localmente:

1. Inicie a aplicação:
   ```bash
   mvn spring-boot:run
   ```

2. Em outro terminal, execute o script k6 a partir da raiz do projeto:
   ```bash
   k6 run performance/k6-votacao-load-test.js
   ```

3. Customizando parâmetros via variáveis de ambiente:
   ```bash
   k6 run -e API_URL=http://localhost:8080/api/v1 -e PAUTA_ID=1 performance/k6-votacao-load-test.js
   ```

---

## 🛡️ 11. Padrão de Resposta de Erros (RFC 7807)

Quando ocorre uma falha na requisição, a API responde com um payload padronizado no formato `application/problem+json`:

```json
{
  "type": "about:blank",
  "title": "Conflito de Dados",
  "status": 409,
  "detail": "O associado com CPF 08123456789 já registrou seu voto nesta pauta.",
  "instance": "/api/v1/pautas/1/votos",
  "timestamp": "2026-09-28T16:10:00"
}
```

### Principais Códigos de Retorno:
- `200 OK`: Requisição processada com sucesso.
- `201 Created`: Recurso cadastrado com sucesso (Pauta, Sessão, Voto).
- `400 Bad Request`: Dados inválidos, campos obrigatórios ausentes ou formato incorreto.
- `404 Not Found`: Pauta ou sessão inexistente.
- `409 Conflict`: Voto duplicado para a mesma pauta.
- `422 Unprocessable Entity`: Sessão de votação encerrada/fechada ou associado inapto para votar.
- `500 Internal Server Error`: Erro interno inesperado do servidor.

---

## 👨‍💻 Autor & Informações do Teste Técnico

Projeto desenvolvido como solução do **Desafio Técnico para Engenharia de Software Backend**.

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.4.3
- **Licença**: Apache 2.0
