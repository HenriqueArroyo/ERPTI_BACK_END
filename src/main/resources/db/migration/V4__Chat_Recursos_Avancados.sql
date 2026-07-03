-- ==============================================================================
-- V4 - RECURSOS AVANÇADOS DO BATE-PAPO
-- ==============================================================================

ALTER TABLE mensagens_comunicacao
    ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT 'TEXTO',
    ADD COLUMN url_arquivo VARCHAR(255),
    ADD COLUMN nome_arquivo_original VARCHAR(255),
    ADD COLUMN editada BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN editada_em TIMESTAMP,
    ADD COLUMN excluida BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN excluida_em TIMESTAMP;

CREATE TABLE mensagem_leituras (
    id BIGSERIAL PRIMARY KEY,
    mensagem_id BIGINT NOT NULL REFERENCES mensagens_comunicacao(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    lida_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(mensagem_id, usuario_id)
);
CREATE INDEX idx_mensagem_leituras_usuario ON mensagem_leituras(usuario_id);
CREATE INDEX idx_mensagem_leituras_mensagem ON mensagem_leituras(mensagem_id);

CREATE TABLE mensagem_reacoes (
    id BIGSERIAL PRIMARY KEY,
    mensagem_id BIGINT NOT NULL REFERENCES mensagens_comunicacao(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    emoji VARCHAR(16) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(mensagem_id, usuario_id, emoji)
);
CREATE INDEX idx_mensagem_reacoes_mensagem ON mensagem_reacoes(mensagem_id);

CREATE TABLE canal_usuario_preferencias (
    canal_id BIGINT NOT NULL REFERENCES canais_comunicacao(id) ON DELETE CASCADE,
    usuario_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    fixado BOOLEAN NOT NULL DEFAULT FALSE,
    favorito BOOLEAN NOT NULL DEFAULT FALSE,
    removido BOOLEAN NOT NULL DEFAULT FALSE,
    limpo_em TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (canal_id, usuario_id)
);
CREATE INDEX idx_canal_prefs_usuario ON canal_usuario_preferencias(usuario_id);

ALTER TABLE canais_comunicacao
    ADD COLUMN criado_por BIGINT REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN descricao VARCHAR(255);