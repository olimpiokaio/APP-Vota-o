# Testes de Performance e Carga da API de Votação

Este documento e os artefatos neste diretório fornecem suporte completo para execução e análise de performance do endpoint de votação (`POST /api/v1/pautas/{id}/votos`).

---

## 1. Testes Automatizados no JUnit (Concorrência & Carga Interna)

A aplicação conta com a suite de testes de integração concorrente `VotacaoPerformanceIntegrationTest`:

- **Arquivo**: `src/test/java/com/example/demo/web/controller/VotacaoPerformanceIntegrationTest.java`
- **Cenários Cobertos**:
  1. **Disparo Concorrente em Massa (Multi-threading com CountDownLatch & ExecutorService)**:
     - 100 requisições simultâneas distribuídas em 20 threads.
     - Validação de throughput (req/s), latência média, latência mínima e máxima.
     - Verificação de persistência atômica e coerência de apuração no banco de dados.
  2. **Idempotência e Bloqueio sob Race Condition**:
     - 20 requisições simultâneas disputando o voto do **mesmo associado (mesmo CPF)** no exato mesmo milissegundo.
     - Garante que exatamente **1 requisição receba `201 Created`** e as outras **19 recebam `409 Conflict`**.

---

## 2. Testes de Carga com k6 (Cenários Externos e Alta Volumetria)

O arquivo `k6-votacao-load-test.js` simula picos de tráfego com centenas/milhares de usuários virtuais (VUs).

### Pré-requisitos
Instalar o **k6**:
- **Windows (Chocolatey / Winget)**: `winget install k6` ou `choco install k6`
- **macOS (Homebrew)**: `brew install k6`
- **Linux**: `sudo apt install k6` ou via container Docker: `grafana/k6`

### Execução do Teste

1. Inicie a aplicação Spring Boot localmente (porta 8080).
2. Execute o script k6 na raiz do projeto:

```bash
k6 run performance/k6-votacao-load-test.js
```

### Parametrização via Variáveis de Ambiente
Você pode customizar o ID da pauta ou o endereço do servidor:

```bash
k6 run -e API_URL=http://localhost:8080/api/v1 -e PAUTA_ID=2 performance/k6-votacao-load-test.js
```

### SLAs e Thresholds Configurados no Script:
- **Latência p95**: 95% das requisições devem responder em menos de `200ms`.
- **Latência p99**: 99% das requisições devem responder em menos de `500ms`.
- **Taxa de Erro**: Menor que `1%`.
