CREATE DATABASE IF NOT EXISTS crudminicrm
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE crudminicrm;

DROP TABLE IF EXISTS item_pedido;
DROP TABLE IF EXISTS pedido;
DROP TABLE IF EXISTS produto;
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente(
	id_cliente INT PRIMARY KEY AUTO_INCREMENT,
    nome_cliente VARCHAR(100) NOT NULL,
    cpf_cliente VARCHAR(14) NOT NULL,
    email_cliente VARCHAR(100) NOT NULL,
    telefone_cliente VARCHAR(20) NOT NULL,
    data_cadastro_cliente DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_cliente_cpf UNIQUE (cpf_cliente),
    CONSTRAINT uk_cliente_email UNIQUE (email_cliente)
);

CREATE TABLE produto(
	id_produto INT PRIMARY KEY AUTO_INCREMENT,
    nome_produto VARCHAR(100) NOT NULL,
    descricao_produto VARCHAR(255) NOT NULL,
    preco_produto DECIMAL(10,2) NOT NULL,
    quantidade_estoque_produto INT NOT NULL,
    
    CONSTRAINT uk_produto_nome UNIQUE (nome_produto),
    CONSTRAINT chk_produto_preco CHECK(preco_produto >= 0),
    CONSTRAINT chk_produto_estoque CHECK(quantidade_estoque_produto >= 0)
);

CREATE TABLE pedido(
	id_pedido INT PRIMARY KEY AUTO_INCREMENT,
    data_pedido DATETIME DEFAULT CURRENT_TIMESTAMP,
    status_pedido ENUM('ABERTO', 'PAGO', 'CANCELADO') NOT NULL DEFAULT 'ABERTO',
    id_cliente INT,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente)
    REFERENCES cliente(id_cliente) ON DELETE SET NULL 
);

CREATE TABLE item_pedido(
    id_item INT PRIMARY KEY AUTO_INCREMENT,
    id_pedido INT NOT NULL,
    id_produto INT NOT NULL,
    quantidade_item INT NOT NULL,
    preco_unitario_item DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT chk_item_pedido_quantidade CHECK (quantidade_item > 0),
    CONSTRAINT chk_item_pedido_preco CHECK (preco_unitario_item >= 0),
    CONSTRAINT uk_item_pedido_pedido_produto UNIQUE (id_pedido, id_produto),
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido) ON DELETE RESTRICT,
    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto) ON DELETE RESTRICT
);