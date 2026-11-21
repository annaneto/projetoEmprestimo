package com.projetos.emprestimo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "O nome do colaborador é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O e-mai do colaborador é obrigatório")
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank(message =  "O CEP do colaborador é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "O CEP deve ter 8 dígitos, sem traço")
    @Column(nullable = false)
    private String cep;

    @NotBlank(message = "O número é obrigatório")
    @Column(nullable = false)
    public String numero;

    // aqui vai ter o endereço


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}
