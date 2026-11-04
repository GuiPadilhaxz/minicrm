package br.com.crudminicrm.dao;

import br.com.crudminicrm.entities.Cliente;
import br.com.crudminicrm.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private static final String SELECT_BASE = "SELECT id_cliente, nome_cliente, cpf_cliente, email_cliente, "
            + "telefone_cliente, data_cadastro_cliente FROM cliente";
    private Cliente converter(ResultSet rs)  throws SQLException {
        Timestamp ts = rs.getTimestamp("data_cadastro_cliente");
        LocalDateTime data = (ts == null) ? null : ts.toLocalDateTime();
        return new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nome_cliente"),
                rs.getString("cpf_cliente"),
                rs.getString("email_cliente"),
                rs.getString("telefone_cliente"),
                data);
    }

    public List<Cliente> listarTodos() throws SQLException {
        String sql = SELECT_BASE + " ORDER BY nome_cliente";
        List<Cliente> clientes = new ArrayList<>();

        try(Connection conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){

            while(rs.next()){
                clientes.add(converter(rs));
            }
        }
        return clientes;
    }
    public Optional<Cliente> buscarPorId(Integer id) throws SQLException {
        String sql = SELECT_BASE + " WHERE id_cliente = ?";

        try (Connection conn =  Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)){
                 ps.setInt(1, id);

                 try(ResultSet rs = ps.executeQuery()){
                     if(rs.next()){
                         return Optional.of(converter(rs));
                     }
                     return Optional.empty();
                 }
        }
    }
    public List<Cliente> buscarNome(String nome) throws SQLException {
        String sql = SELECT_BASE + " WHERE nome_cliente LIKE ? ORDER BY nome_cliente";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
        PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, "%" + nome + "%");

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next()){
                    clientes.add(converter(rs));
                }
            }
        }
        return clientes;
    }
    public boolean atualizar(Cliente cliente) throws SQLException {
        if (cliente.getId() == null){
            throw new IllegalArgumentException("Não é possivel atualizar um cliente que não existe");
        }

        String sql = "UPDATE cliente SET nome_cliente = ? , cpf_cliente = ?, email_cliente = ?, telefone_cliente = ? WHERE id_cliente = ?";

        try (Connection conn = Conexao.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getTelefone());
            ps.setInt(5, cliente.getId());
            return ps.executeUpdate() > 0;
        }

    }
}