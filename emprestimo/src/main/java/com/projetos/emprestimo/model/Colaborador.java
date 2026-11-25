package com.projetos.emprestimo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class Colaborador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do colaborador é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O e-mail do colaborador é obrigatório")
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private String cargo;

    // endereço (vai que o funcionario não devolve e a gente precisa ir lá buscar o equipamento)

    @Pattern(regexp = "\\d{8}", message = "O CEP deve ter 8 dígitos, sem traço")
    private String cep;

    private String numero;
    private String bairro;
    private String cidade;
    private String uf;

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

    public String getCargo() {return cargo;}

    public void setCargo(String cargo) {this.cargo = cargo;}

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


    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
