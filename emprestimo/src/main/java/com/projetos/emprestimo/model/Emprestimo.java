package com.projetos.emprestimo.model;

import jakarta.persistence.*;

@Entity
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
