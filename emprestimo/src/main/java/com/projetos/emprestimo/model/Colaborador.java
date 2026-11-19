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





}
