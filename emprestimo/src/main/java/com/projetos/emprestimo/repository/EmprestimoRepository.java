package com.projetos.emprestimo.repository;

import com.projetos.emprestimo.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository <Emprestimo, Long>{
    boolean existByEquipamento(Long equipamentoId);

    List<Emprestimo> findByColaboradorId(Long colaboradorId);

    List<Emprestimo> findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefora(LocalDate data);
}
