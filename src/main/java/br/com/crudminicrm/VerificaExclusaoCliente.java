package br.com.crudminicrm;

import br.com.crudminicrm.dao.ClienteDao;
import br.com.crudminicrm.entities.Cliente;
import br.com.crudminicrm.util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VerificaExclusaoCliente {

    private static final String SUFIXO = "@teste.local";
    private static final long BASE = System.currentTimeMillis();
    private static int aprovados = 0;
    private static int reprovados = 0;
    private static int pedidoTesteId = -1;

    public static void main(String[] args) throws SQLException {
        ClienteDao dao = new ClienteDao();
        try {
            Cliente a = criar(dao, "Teste A", 1);
            Cliente b = criar(dao, "Teste B", 2);

            int totalAntes = dao.listarTodos().size();

            // 1 a 4: exclusão normal
            verificar("1. excluir devolve true para cliente existente", dao.excluir(a.getId()));
            verificar("2. cliente excluído não é mais encontrado",
                    dao.buscarPorId(a.getId()).isEmpty());

            int totalDepois = dao.listarTodos().size();
            verificar("3. só uma linha sumiu da tabela inteira (antes=" + totalAntes
                    + ", depois=" + totalDepois + ")", totalDepois == totalAntes - 1);
            verificar("4. o outro cliente continua no banco",
                    dao.buscarPorId(b.getId()).isPresent());

            // 5 a 7: excluir de novo e id inexistente
            verificar("5. excluir de novo o mesmo id devolve false", !dao.excluir(a.getId()));
            int totalAntesFantasma = dao.listarTodos().size();
            verificar("6. id inexistente devolve false", !dao.excluir(Integer.MAX_VALUE));
            verificar("7. excluir id inexistente não apaga ninguém",
                    dao.listarTodos().size() == totalAntesFantasma);

            // 8 a 10: cliente que tem pedido (ON DELETE SET NULL)
            Cliente c = criar(dao, "Teste C", 3);
            pedidoTesteId = inserirPedido(c.getId());
            verificar("8. pedido de teste criado com cliente",
                    estadoPedido(pedidoTesteId).equals("COM_CLIENTE"));
            verificar("9. excluir cliente que tem pedido funciona", dao.excluir(c.getId()));
            verificar("10. pedido continua no banco, com id_cliente nulo",
                    estadoPedido(pedidoTesteId).equals("SEM_CLIENTE"));

            // 11: conexões
            int conexoesAntes = conexoesAbertas();
            for (int i = 0; i < 10; i++) {
                Cliente t = criar(dao, "Teste Loop " + i, 100 + i);
                dao.excluir(t.getId());
            }
            int conexoesDepois = conexoesAbertas();
            verificar("11. conexões não vazam (antes=" + conexoesAntes
                    + ", depois=" + conexoesDepois + ")", conexoesDepois <= conexoesAntes);
        } finally {
            limpar();
        }
        System.out.println("\nAprovados: " + aprovados + " | Reprovados: " + reprovados);
    }

    private static Cliente criar(ClienteDao dao, String nome, int n) throws SQLException {
        Cliente c = new Cliente(nome, "T" + (BASE + n), "t" + (BASE + n) + SUFIXO,
                "(17) 90000-000" + n);
        dao.cadastrar(c);
        return c;
    }

    // Insere um pedido por SQL direto (a tabela pedido ainda não tem DAO)
    private static int inserirPedido(int idCliente) throws SQLException {
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO pedido (id_cliente) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idCliente);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    // NAO_EXISTE, SEM_CLIENTE (id_cliente nulo) ou COM_CLIENTE
    private static String estadoPedido(int idPedido) throws SQLException {
        try (Connection c = Conexao.getConexao();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id_cliente FROM pedido WHERE id_pedido = ?")) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "NAO_EXISTE";
                rs.getInt("id_cliente");
                return rs.wasNull() ? "SEM_CLIENTE" : "COM_CLIENTE";
            }
        }
    }

    private static void verificar(String nome, boolean ok) {
        if (ok) aprovados++; else reprovados++;
        System.out.println((ok ? "[OK]    " : "[FALHA] ") + nome);
    }

    private static int conexoesAbertas() throws SQLException {
        try (Connection c = Conexao.getConexao();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SHOW STATUS LIKE 'Threads_connected'")) {
            rs.next();
            return rs.getInt(2);
        }
    }

    // Apaga o pedido de teste (pelo id) e os clientes com e-mail @teste.local
    private static void limpar() throws SQLException {
        try (Connection c = Conexao.getConexao()) {
            if (pedidoTesteId > 0) {
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM pedido WHERE id_pedido = ?")) {
                    ps.setInt(1, pedidoTesteId);
                    System.out.println("Limpeza: " + ps.executeUpdate() + " pedido de teste removido.");
                }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM cliente WHERE email_cliente LIKE ?")) {
                ps.setString(1, "%" + SUFIXO);
                System.out.println("Limpeza: " + ps.executeUpdate() + " clientes de teste removidos.");
            }
        }
    }
}