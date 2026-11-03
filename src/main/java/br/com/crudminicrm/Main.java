package br.com.crudminicrm;

import br.com.crudminicrm.util.Conexao;

import java.sql.Connection;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try (Connection c = Conexao.getConexao()) {
            System.out.println("Conectado");
        } catch (SQLException e) {
            System.out.println("Falha: " + e.getMessage());
        }
    }
}
