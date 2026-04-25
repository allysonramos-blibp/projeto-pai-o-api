-- =============================================================
-- DevLapa — Schema do Banco de Dados
-- Projetos Integrados 2 (VIA231) — 2026/1
-- =============================================================
-- Checkpoint: CP-1 (12/03)
-- Autor(es):
-- allyson Ramos, Cleriston Lima
-- O banco foi estruturado para um sistema de controle financeiro e de estoque, onde:
--   usuários acessam o sistema
--   produtos são organizados por categorias e fornecedores
--   contas a pagar controlam despesas com fornecedores

Usuarios --
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL,
    hash TEXT NOT NULL,
    data_cadastro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    perfil VARCHAR(100) DEFAULT 'USUARIO',
    data_modificacao TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
    );

Fornecedores --

CREATE  TABLE fornecedores(
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(100),
    telefone VARCHAR(100) NOT NULL,
    email VARCHAR(200),
    endereco VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_cadastro TIMESTAMP NOT NULL
);
Categorias --
CREATE TABLE categorias(
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) UNIQUE NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    data_criacao TIMESTAMP NOT NULL
);

Produtos --
CREATE TABLE produtos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    preco DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
    unidade VARCHAR(20) NOT NULL,
    categoria_id INT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    fornecedor_id INT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (categoria_id)
            REFERENCES categorias(id),

    CONSTRAINT fk_produto_fornecedor
        FOREIGN KEY (fornecedor_id)
            REFERENCES fornecedores(id)
);

CREATE INDEX idx_produto_fornecedor ON produtos(fornecedor_id);
CREATE INDEX idx_produto_categoria ON produtos(categoria_id);

Contas Pagar --

CREATE TABLE contas_pagar (
    id SERIAL PRIMARY KEY,
    fornecedor_id INT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    valor DECIMAL(12,2) NOT NULL CHECK (valor >= 0),
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    categoria VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',

    CONSTRAINT fk_conta_fornecedor
        FOREIGN KEY (fornecedor_id)
            REFERENCES fornecedores(id),

    CONSTRAINT chk_status_contas_pagar
        CHECK (status IN ('PENDENTE', 'PAGA', 'CANCELADA'))
);

-- Checkpoint: CP-1 (26/03)
-- Autor(es):
-- allyson Ramos, Cleriston Lima

--Essas tabelas complementam o sistema:
-- estoque → controle operacional (produtos e quantidades)
-- formas_de_pagamentos → controle financeiro (como os pagamentos são feitos)

Estoque --

CREATE TABLE estoque (
    id SERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    minimo INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    data_modificacao TIMESTAMP,
    CONSTRAINT fk_estoque_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

FormasPagamento --

CREATE TABLE formas_de_pagamentos(
    id SERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    tipo VARCHAR(255) NOT NULL,
    permit_parcelamento BOOLEAN NOT NULL DEFAULT FALSE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_modificacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);