package com.projetos.emprestimo.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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



}
