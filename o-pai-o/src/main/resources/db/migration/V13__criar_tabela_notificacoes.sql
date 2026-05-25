CREATE TABLE IF NOT EXISTS notificacoes (
                              id BIGSERIAL PRIMARY KEY,
                              titulo VARCHAR(100) NOT NULL,
                              mensagem VARCHAR(255) NOT NULL,
                              lida BOOLEAN DEFAULT FALSE,
                              data_criacao TIMESTAMP NOT NULL
);