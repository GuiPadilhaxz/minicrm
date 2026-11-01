USE crudminicrm;

-- ============================================================
-- FASE 1: dados válidos (todos devem funcionar)
-- ============================================================
INSERT INTO cliente (nome_cliente, cpf_cliente, email_cliente, telefone_cliente) VALUES
('Joao Silva',  '111.111.111-11', 'joao@email.com',  '(17) 99999-0001'),
('Maria Souza', '222.222.222-22', 'maria@email.com', '(17) 99999-0002');

INSERT INTO produto (nome_produto, descricao_produto, preco_produto, quantidade_estoque_produto) VALUES
('Camiseta', 'Camiseta de algodao', 49.90, 10),
('Cafe',     'Cafe torrado 500g',   22.50, 30);

INSERT INTO pedido (id_cliente, status_pedido) VALUES (1, 'ABERTO');

INSERT INTO item_pedido (id_pedido, id_produto, quantidade_item, preco_unitario_item)
VALUES (1, 1, 2, 49.90);

SELECT * FROM cliente;
SELECT * FROM produto;
SELECT * FROM pedido;
SELECT * FROM item_pedido;

-- ============================================================
-- FASE 2: violações (TODAS devem dar ERRO)
-- ============================================================

-- 2.01 CPF duplicado -> uk_cliente_cpf
INSERT INTO cliente (nome_cliente, cpf_cliente, email_cliente, telefone_cliente)
VALUES ('Outro', '111.111.111-11', 'outro@email.com', '(17) 99999-0003');

-- 2.02 E-mail duplicado -> uk_cliente_email
INSERT INTO cliente (nome_cliente, cpf_cliente, email_cliente, telefone_cliente)
VALUES ('Outro', '333.333.333-33', 'joao@email.com', '(17) 99999-0003');

-- 2.03 E-mail duplicado só com maiúsculas -> uk_cliente_email
INSERT INTO cliente (nome_cliente, cpf_cliente, email_cliente, telefone_cliente)
VALUES ('Outro', '333.333.333-33', 'JOAO@EMAIL.COM', '(17) 99999-0003');

-- 2.04 Nome nulo -> "cannot be null"
INSERT INTO cliente (nome_cliente, cpf_cliente, email_cliente, telefone_cliente)
VALUES (NULL, '333.333.333-33', 'novo@email.com', '(17) 99999-0003');

-- 2.05 Telefone nulo -> "cannot be null"
INSERT INTO cliente (nome_cliente, cpf_cliente, email_cliente, telefone_cliente)
VALUES ('Outro', '333.333.333-33', 'novo@email.com', NULL);

-- 2.06 Nome de produto repetido, só maiúscula -> uk_produto_nome
INSERT INTO produto (nome_produto, descricao_produto, preco_produto, quantidade_estoque_produto)
VALUES ('camiseta', 'teste', 10.00, 5);

-- 2.07 Nome de produto repetido, só acento -> uk_produto_nome
INSERT INTO produto (nome_produto, descricao_produto, preco_produto, quantidade_estoque_produto)
VALUES ('Café', 'teste', 10.00, 5);

-- 2.08 Preço negativo -> chk_produto_preco
INSERT INTO produto (nome_produto, descricao_produto, preco_produto, quantidade_estoque_produto)
VALUES ('Teste preco', 'teste', -1.00, 5);

-- 2.09 Estoque negativo -> chk_produto_estoque
INSERT INTO produto (nome_produto, descricao_produto, preco_produto, quantidade_estoque_produto)
VALUES ('Teste estoque', 'teste', 10.00, -1);

-- 2.10 Item com quantidade 0 -> chk_item_pedido_quantidade
INSERT INTO item_pedido (id_pedido, id_produto, quantidade_item, preco_unitario_item)
VALUES (1, 2, 0, 22.50);

-- 2.11 Item com preço negativo -> chk_item_pedido_preco
INSERT INTO item_pedido (id_pedido, id_produto, quantidade_item, preco_unitario_item)
VALUES (1, 2, 1, -5.00);

-- 2.12 Mesmo produto duas vezes no mesmo pedido -> uk_item_pedido_pedido_produto
INSERT INTO item_pedido (id_pedido, id_produto, quantidade_item, preco_unitario_item)
VALUES (1, 1, 1, 49.90);

-- 2.13 Status inválido -> "Data truncated" (o ENUM não tem nome de constraint)
INSERT INTO pedido (id_cliente, status_pedido) VALUES (1, 'ENVIADO');

-- 2.14 Pedido para cliente inexistente -> fk_pedido_cliente
INSERT INTO pedido (id_cliente, status_pedido) VALUES (999, 'ABERTO');

-- 2.15 Item para pedido inexistente -> fk_item_pedido_pedido
INSERT INTO item_pedido (id_pedido, id_produto, quantidade_item, preco_unitario_item)
VALUES (999, 1, 1, 10.00);

-- 2.16 Item para produto inexistente -> fk_item_pedido_produto
INSERT INTO item_pedido (id_pedido, id_produto, quantidade_item, preco_unitario_item)
VALUES (1, 999, 1, 10.00);

-- ============================================================
-- FASE 3: valores DEFAULT (devem funcionar)
-- ============================================================
-- Sem data e sem status: devem vir preenchidos (data atual e 'ABERTO')
INSERT INTO pedido (id_cliente) VALUES (2);

SELECT id_pedido, id_cliente, data_pedido, status_pedido FROM pedido;
SELECT id_cliente, nome_cliente, data_cadastro_cliente FROM cliente;

-- ============================================================
-- FASE 4: exclusões (rodar por último, altera os dados)
-- ============================================================

-- 4.1 Excluir pedido que tem itens -> ERRO fk_item_pedido_pedido
DELETE FROM pedido WHERE id_pedido = 1;

-- 4.2 Excluir produto já vendido -> ERRO fk_item_pedido_produto
DELETE FROM produto WHERE id_produto = 1;

-- 4.3 Excluir produto que nunca foi vendido -> FUNCIONA
DELETE FROM produto WHERE id_produto = 2;

-- 4.4 Excluir cliente que tem pedidos -> FUNCIONA, pedido fica com id_cliente NULL
DELETE FROM cliente WHERE id_cliente = 1;

SELECT id_pedido, id_cliente, status_pedido FROM pedido;
