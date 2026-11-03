package br.com.crudminicrm.dao;

import br.com.crudminicrm.entities.Cliente;
import br.com.crudminicrm.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClienteDao {

    public void cadastrar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente "
                + "(nome_cliente, cpf_cliente, email_cliente, telefone_cliente) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefone());

            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }
        }
    }
}