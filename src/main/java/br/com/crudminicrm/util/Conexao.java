package br.com.crudminicrm.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexao {

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Path.of("config.properties"))) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Não foi possível ler o config.properties na pasta do projeto.", e);
        }
        URL = obrigatoria(props, "db.url");
        USER = obrigatoria(props, "db.user");
        PASSWORD = obrigatoria(props, "db.password");
    }

    private Conexao() {
    }

    private static String obrigatoria(Properties props, String chave) {
        String valor = props.getProperty(chave);
        if (valor == null) {
            throw new IllegalStateException(
                    "Chave ausente no config.properties: " + chave);
        }
        return valor;
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}