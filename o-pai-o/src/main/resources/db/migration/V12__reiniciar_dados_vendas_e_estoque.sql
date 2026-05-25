TRUNCATE TABLE
    usuarios,
    fornecedores,
    categorias,
    produtos,
    contas_pagar,
    estoque,
    formas_de_pagamentos,
    comandas,
    itens_comanda,
    vendas,
    itens_vendas,
    contas_receber,
    movimentacoes
RESTART IDENTITY CASCADE;


ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS email VARCHAR(200);

ALTER TABLE vendas ALTER COLUMN status TYPE VARCHAR(20);
ALTER TABLE estoque ALTER COLUMN status TYPE VARCHAR(20);


ALTER TABLE vendas DROP CONSTRAINT IF EXISTS chk_status_venda;
ALTER TABLE vendas ADD CONSTRAINT chk_status_venda
    CHECK (status IN ('ABERTA', 'PAGA', 'CANCELADA'));

ALTER TABLE estoque DROP CONSTRAINT IF EXISTS chk_status_estoque;
ALTER TABLE estoque ADD CONSTRAINT chk_status_estoque
    CHECK (status IN ('NORMAL', 'BAIXO', 'ESGOTADO', 'CRITICO'));


INSERT INTO usuarios (nome, login, email, hash, perfil, ativo, data_cadastro) VALUES
                                                                                  ('Allyson Admin', 'allyson.admin', 'canalsttyrf@gmail.com', '$2a$10$e0MYzXy6Xz2Hj1bA1M7vO.N0vSfe2mZ1B7UeWR.1M2B3C4D5E6F7G', 'ADMIN', true, CURRENT_TIMESTAMP),
                                                                                  ('Amanda Rezende', 'amanda.user', 'amanda@bar.com', '$2a$10$e0MYzXy6Xz2Hj1bA1M7vO.N0vSfe2mZ1B7UeWR.1M2B3C4D5E6F7G', 'USUARIO', true, CURRENT_TIMESTAMP),
                                                                                  ('Debora Nascimento', 'debora.gerente', 'debora@bar.com', '$2a$10$e0MYzXy6Xz2Hj1bA1M7vO.N0vSfe2mZ1B7UeWR.1M2B3C4D5E6F7G', 'GERENTE', true, CURRENT_TIMESTAMP),
                                                                                  ('Cleriston Lima', 'cleristom.admin', 'cleriston@bar.com', '$2a$10$e0MYzXy6Xz2Hj1bA1M7vO.N0vSfe2mZ1B7UeWR.1M2B3C4D5E6F7G', 'ADMIN', true, CURRENT_TIMESTAMP);


INSERT INTO fornecedores (nome, cnpj, telefone, email, endereco, ativo, data_cadastro) VALUES
                                                                                           ('Ambev Distribuidora', '03.012.345/0001-99', '(34) 3211-0001', 'pedidos@ambev.com.br', 'Av. Industrial, 1000', true, CURRENT_TIMESTAMP),
                                                                                           ('Hortifruti Central', '12.345.678/0001-00', '(34) 3222-5555', 'vendas@horticentral.com', 'Rua das Verduras, 45', true, CURRENT_TIMESTAMP),
                                                                                           ('Atacadão de Bebidas Aliança', '98.765.432/0001-11', '(34) 3233-9999', 'comercial@alianca.com', 'Av. Rondon Pacheco, 2500', true, CURRENT_TIMESTAMP);

INSERT INTO categorias (nome, descricao, data_criacao) VALUES
                                                           ('Cervejas', 'Cervejas artesanais, long necks e de garrafa', CURRENT_TIMESTAMP),
                                                           ('Drinks', 'Coquetéis, caipirinhas e destilados', CURRENT_TIMESTAMP),
                                                           ('Porções', 'Petiscos quentes, batata frita e carnes na chapa', CURRENT_TIMESTAMP),
                                                           ('Não Alcoólicos', 'Refrigerantes, sucos e água', CURRENT_TIMESTAMP);

INSERT INTO formas_de_pagamentos (nome, descricao, tipo, permit_parcelamento, ativo, data_criacao, data_modificacao) VALUES
                                                                                                                         ('Dinheiro', 'Pagamento em espécie', 'DINHEIRO', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ('Cartão de Débito', 'Débito à vista', 'CARTAO', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ('Cartão de Crédito', 'Crédito direto', 'CARTAO', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                         ('Pix', 'Transferência instantânea via QR Code', 'PIX', false, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO produtos (nome, preco, unidade, categoria_id, fornecedor_id, ativo, data_criacao) VALUES
                                                                                                  ('Cerveja Heineken Long Neck 330ml', 12.00, 'UN', 1, 1, true, CURRENT_TIMESTAMP),
                                                                                                  ('Chopp Brahma Claro 300ml', 9.50, 'UN', 1, 1, true, CURRENT_TIMESTAMP),
                                                                                                  ('Caipirinha de Limão Tradicional', 18.00, 'UN', 2, 2, true, CURRENT_TIMESTAMP),
                                                                                                  ('Batata Frita com Queijo e Bacon', 38.90, 'PORCAO', 3, 2, true, CURRENT_TIMESTAMP),
                                                                                                  ('Picanha na Chapa com Mandioca', 89.90, 'PORCAO', 3, 2, true, CURRENT_TIMESTAMP),
                                                                                                  ('Coca-Cola Lata 350ml', 6.00, 'UN', 4, 3, true, CURRENT_TIMESTAMP);

INSERT INTO estoque (produto_id, quantidade, minimo, status, data_cadastro) VALUES
                                                                                (1, 120, 24, 'NORMAL', CURRENT_TIMESTAMP),
                                                                                (2, 50, 10, 'NORMAL', CURRENT_TIMESTAMP),
                                                                                (4, 30, 5, 'NORMAL', CURRENT_TIMESTAMP),
                                                                                (6, 80, 20, 'NORMAL', CURRENT_TIMESTAMP);

INSERT INTO contas_pagar (fornecedor_id, user_criacao_id, descricao, valor, data_vencimento, data_pagamento, categoria, status) VALUES
                                                                                                                                    (1, 1, 'Compra de engradados de Heineken e Chopp', 1500.00, CURRENT_DATE + INTERVAL '10 days', NULL, 'Bebidas', 'PENDENTE'),
                                                                                                                                    (2, 3, 'Reposição diária de hortifrúti (limão/hortelã)', 350.00, CURRENT_DATE, CURRENT_DATE, 'Insumos', 'PAGA');

INSERT INTO comandas (numero_mesa, nome_cliente, status, valor_total, data_abertura) VALUES
                                                                                         (5, 'Bruno Silva', 'ABERTA', 45.50, CURRENT_TIMESTAMP),
                                                                                         (12, 'Grupo de Sexta', 'ABERTA', 125.80, CURRENT_TIMESTAMP),
                                                                                         (3, 'Mariana Costa', 'ABERTA', 0.00, CURRENT_TIMESTAMP);

INSERT INTO itens_comanda (comanda_id, produto_id, quantidade, preco_unitario, subtotal) VALUES
                                                                                             (1, 1, 3, 12.00, 36.00),
                                                                                             (1, 2, 1, 9.50, 9.50),
                                                                                             (2, 4, 2, 38.90, 77.80),
                                                                                             (2, 3, 2, 18.00, 36.00),
                                                                                             (2, 6, 2, 6.00, 12.00);

INSERT INTO vendas (usuario_id, formaDePagamento_id, valor_total, status, data_criacao) VALUES
                                                                                            (2, 4, 57.50, 'ABERTA', CURRENT_TIMESTAMP),
                                                                                            (1, 1, 113.90, 'ABERTA', CURRENT_TIMESTAMP);

INSERT INTO itens_vendas (vendas_id, produto_id, quantidade, preco_unitario, sub_total) VALUES
                                                                                            (1, 1, 4, 12.00, 48.00),
                                                                                            (1, 2, 1, 9.50, 9.50),
                                                                                            (2, 5, 1, 89.90, 89.90),
                                                                                            (2, 1, 2, 12.00, 24.00);

INSERT INTO contas_receber (cliente, descricao, valor, data_vencimento, data_recebimento, status, comanda_id, user_criacao_id) VALUES
                                                                                                                                   ('Bruno Silva', 'Fechamento parcial Mesa 5', 45.50, CURRENT_DATE, NULL, 'PENDENTE', 1, 1),
                                                                                                                                   ('Evento Corporativo Sábado', 'Reserva antecipada de espaço', 500.00, CURRENT_DATE + INTERVAL '2 days', CURRENT_DATE, 'RECEBIDO', NULL, 3);

INSERT INTO movimentacoes (estoque_id, quantidade, tipo, motivo, usuario_responsavel, data_hora) VALUES
                                                                                                     (1, 120, 'ENTRADA', 'Compra conforme nota fiscal Ambev', 'allyson.admin', CURRENT_TIMESTAMP),
                                                                                                     (1, -4, 'SAIDA', 'Venda efetuada no Caixa Balcão', 'amanda.user', CURRENT_TIMESTAMP);