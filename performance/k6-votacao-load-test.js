import http from 'k6/http';
import { check, sleep } from 'k6';

// ============================================================================
// Script de Teste de Performance e Carga - Votação de Pautas
// ============================================================================
// Cenário:
// 1. Ramp-up de usuários simultâneos (VUs)
// 2. Disparo de requisições POST /api/v1/pautas/{id}/votos com CPFs únicos
// 3. Validação de SLAs (95% das requisições < 200ms, taxa de erro < 1%)
// ============================================================================

export const options = {
  stages: [
    { duration: '30s', target: 50 },    // Aquecimento (Ramp-up)
    { duration: '1m', target: 200 },    // Carga sustentada (200 usuários concorrentes)
    { duration: '30s', target: 500 },   // Pico de votação (Spike test)
    { duration: '30s', target: 0 },     // Encerramento (Ramp-down)
  ],
  thresholds: {
    http_req_duration: ['p(95)<200', 'p(99)<500'], // SLA de latência
    http_req_failed: ['rate<0.01'],                 // SLA de erro (< 1%)
  },
};

const BASE_URL = __ENV.API_URL || 'http://localhost:8080/api/v1';
const PAUTA_ID = __ENV.PAUTA_ID || '2';

// Função para gerar CPF único para o VU e iteração
function gerarCpfUnico(vu, iter) {
  const base = 10000000000 + (vu * 100000) + (iter % 100000);
  return `${base}`.substring(0, 11);
}

export default function () {
  const cpf = gerarCpfUnico(__VU, __ITER);
  const opcaoVoto = Math.random() > 0.5 ? 'SIM' : 'NAO';

  const payload = JSON.stringify({
    cpf: cpf,
    voto: opcaoVoto
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(`${BASE_URL}/pautas/${PAUTA_ID}/votos`, payload, params);

  // Validação: status 201 Created para voto novo ou 409 caso haja colisão
  check(res, {
    'status é 201 Created': (r) => r.status === 201,
    'resposta contém ID do voto': (r) => {
      if (r.status === 201) {
        const body = JSON.parse(r.body);
        return body.id !== undefined && body.pautaId !== undefined;
      }
      return false;
    },
  });

  // Pausa curta simulando comportamento real do usuário
  sleep(0.05);
}
