package br.com.crudminicrm.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cliente {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Integer id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private final LocalDateTime dataCadastro;

    // Cliente novo: ainda não tem id nem data (o banco gera os dois)
    public Cliente(String nome, String cpf, String email, String telefone) {
        this(null, nome, cpf, email, telefone, null);
    }

    // Cliente vindo do banco: tem tudo
    public Cliente(Integer id, String nome, String cpf, String email,
                   String telefone, LocalDateTime dataCadastro) {
        this.id = id;
        setNome(nome);
        setCpf(cpf);
        setEmail(email);
        setTelefone(telefone);
        this.dataCadastro = dataCadastro;
    }

    private static String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        this.nome = obrigatorio(nome, "Nome");
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        this.cpf = obrigatorio(cpf, "CPF");
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        String valor = obrigatorio(email, "E-mail");
        if (!valor.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        this.email = valor;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) {
        this.telefone = obrigatorio(telefone, "Telefone");
    }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    @Override
    public String toString() {
        String data = (dataCadastro == null) ? "-" : dataCadastro.format(FORMATO);
        return "Cliente [id=" + id + ", nome=" + nome + ", cpf=" + cpf
                + ", email=" + email + ", telefone=" + telefone
                + ", cadastro=" + data + "]";
    }
}