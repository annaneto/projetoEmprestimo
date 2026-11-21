package com.projetos.emprestimo.repository;

import com.projetos.emprestimo.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColaboradorRepository extends JpaRepository <Colaborador, Long>{

    boolean existsByEmail(String email);
    

}
