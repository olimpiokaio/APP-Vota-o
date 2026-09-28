-- =========================================================================
-- Script SQL para População de Dados Fake no Banco H2 para Testes de Integração
-- =========================================================================

-- Limpeza prévia para garantir idempotência
DELETE FROM tb_voto;
DELETE FROM tb_sessao_votacao;
DELETE FROM tb_pauta;

-- -------------------------------------------------------------------------
-- 1. Inserção de Pautas Fake
-- -------------------------------------------------------------------------
-- ID 1: Pauta sem sessão de votação aberta (Status: NAO_INICIADA)
INSERT INTO tb_pauta (id, titulo, descricao, data_criacao) 
VALUES (1, 'Pauta Fake Sem Sessao', 'Pauta criada para testar criacao e abertura de sessao de votacao', '2026-01-01 10:00:00');

-- ID 2: Pauta com sessão de votação atualmente aberta (Status: ABERTA)
INSERT INTO tb_pauta (id, titulo, descricao, data_criacao) 
VALUES (2, 'Pauta Fake Com Sessao Aberta', 'Pauta com sessao aberta disponivel para receber votos dos associados', '2026-01-01 10:00:00');

-- ID 3: Pauta com sessão encerrada e resultado APROVADA (Status: ENCERRADA)
INSERT INTO tb_pauta (id, titulo, descricao, data_criacao) 
VALUES (3, 'Pauta Fake Encerrada Aprovada', 'Pauta com votacao concluida e apuracao favoravel (Aprovada)', '2026-01-01 08:00:00');

-- ID 4: Pauta com sessão encerrada e resultado REJEITADA (Status: ENCERRADA)
INSERT INTO tb_pauta (id, titulo, descricao, data_criacao) 
VALUES (4, 'Pauta Fake Encerrada Rejeitada', 'Pauta com votacao concluida e apuracao desfavoravel (Rejeitada)', '2026-01-01 08:00:00');

-- ID 5: Pauta com sessão encerrada e resultado EMPATE (Status: ENCERRADA)
INSERT INTO tb_pauta (id, titulo, descricao, data_criacao) 
VALUES (5, 'Pauta Fake Encerrada Empate', 'Pauta com votacao concluida com igualdade de votos (Empate)', '2026-01-01 08:00:00');

-- ID 6: Pauta com sessão encerrada e SEM VOTOS (Status: ENCERRADA)
INSERT INTO tb_pauta (id, titulo, descricao, data_criacao) 
VALUES (6, 'Pauta Fake Encerrada Sem Votos', 'Pauta com sessao finalizada porem nenhum voto foi computado', '2026-01-01 08:00:00');

-- -------------------------------------------------------------------------
-- 2. Inserção de Sessões de Votação Fake
-- -------------------------------------------------------------------------
-- Sessão ID 2 vinculada à Pauta 2: Aberta (inicio no passado, fim em 2099)
INSERT INTO tb_sessao_votacao (id, pauta_id, data_hora_inicio, data_hora_fim) 
VALUES (2, 2, '2020-01-01 00:00:00', '2099-12-31 23:59:59');

-- Sessão ID 3 vinculada à Pauta 3: Encerrada no passado
INSERT INTO tb_sessao_votacao (id, pauta_id, data_hora_inicio, data_hora_fim) 
VALUES (3, 3, '2026-01-01 08:00:00', '2026-01-01 09:00:00');

-- Sessão ID 4 vinculada à Pauta 4: Encerrada no passado
INSERT INTO tb_sessao_votacao (id, pauta_id, data_hora_inicio, data_hora_fim) 
VALUES (4, 4, '2026-01-01 08:00:00', '2026-01-01 09:00:00');

-- Sessão ID 5 vinculada à Pauta 5: Encerrada no passado
INSERT INTO tb_sessao_votacao (id, pauta_id, data_hora_inicio, data_hora_fim) 
VALUES (5, 5, '2026-01-01 08:00:00', '2026-01-01 09:00:00');

-- Sessão ID 6 vinculada à Pauta 6: Encerrada no passado
INSERT INTO tb_sessao_votacao (id, pauta_id, data_hora_inicio, data_hora_fim) 
VALUES (6, 6, '2026-01-01 08:00:00', '2026-01-01 09:00:00');

-- -------------------------------------------------------------------------
-- 3. Inserção de Votos Fake
-- -------------------------------------------------------------------------
-- Pauta 2 (Sessão Aberta): 1 voto prévio do associado 52998224725
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (1, 2, '52998224725', 'SIM', '2026-01-01 10:05:00');

-- Pauta 3 (Sessão Encerrada - Aprovada: 2 SIM, 1 NAO)
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (2, 3, '52998224725', 'SIM', '2026-01-01 08:10:00');
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (3, 3, '12345678909', 'SIM', '2026-01-01 08:15:00');
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (4, 3, '11144477735', 'NAO', '2026-01-01 08:20:00');

-- Pauta 4 (Sessão Encerrada - Rejeitada: 1 SIM, 2 NAO)
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (5, 4, '52998224725', 'NAO', '2026-01-01 08:10:00');
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (6, 4, '12345678909', 'NAO', '2026-01-01 08:15:00');
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (7, 4, '11144477735', 'SIM', '2026-01-01 08:20:00');

-- Pauta 5 (Sessão Encerrada - Empate: 1 SIM, 1 NAO)
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (8, 5, '52998224725', 'SIM', '2026-01-01 08:10:00');
INSERT INTO tb_voto (id, pauta_id, associado_cpf, opcao_voto, data_hora_voto) 
VALUES (9, 5, '12345678909', 'NAO', '2026-01-01 08:15:00');

-- -------------------------------------------------------------------------
-- 4. Ajuste dos Geradores de ID para Novas Inserções
-- -------------------------------------------------------------------------
ALTER TABLE tb_pauta ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tb_sessao_votacao ALTER COLUMN id RESTART WITH 100;
ALTER TABLE tb_voto ALTER COLUMN id RESTART WITH 100;
