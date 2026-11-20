package com.ex.self_alert.model.usuariomodel;

public abstract class Usuario {
    // Encapsulamento: os atributos são privados e acessados por getters e setters.
    private Long id;
    private String nome;
    private String email;
    private String senha;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void exibirPerfil() {
        System.out.println("Nome: " + nome);
        System.out.println("E-mail: " + email);
    }
}

