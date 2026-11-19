package com.projetos.emprestimo.repository;

import com.projetos.emprestimo.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColaboradorRepository extends JpaRepository <Colaborador, Long>{

    boolean existisByEmail(String email);

}
