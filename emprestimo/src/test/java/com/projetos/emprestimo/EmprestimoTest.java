package com.projetos.emprestimo;

import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.model.Equipamento;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    @Test
    void criarEmprestimoDefineDataEmprestimoEPrazoDevolucaoPara14DiasDepois() {
        LocalDate hoje = LocalDate.of(2026, 11, 14);
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), hoje);

        assertEquals(LocalDate.of(2026, 11, 14), emprestimo.getDataEmprestimo());
        assertEquals(LocalDate.of(2026, 11, 28), emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void devolverEmprestimoAbertoRegistraDataDevolucao() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));
        LocalDate dataDevolucao = LocalDate.of(2026, 11, 20);

        emprestimo.devolver(dataDevolucao);

        assertEquals(dataDevolucao, emprestimo.getDataDevolucao());
    }

    @Test
    void devolverDuasVezesLancaExcecao() {
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));
        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertThrows(IllegalStateException.class, () -> emprestimo.devolver(LocalDate.of(2026, 11, 21)));
    }

    @Test
    void emprestimoNaoDevolvidoAposDataPrevistaEstaAtrasado() {
        // Criado em 27/10 (prazo 10/11), não devolvido, consultado em 14/11 -> atrasado
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 27));

        assertTrue(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoDentroDoPrazoNaoEstaAtrasado() {
        // Criado em 06/11 (prazo 20/11), não devolvido, consultado em 14/11 -> não atrasado
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 6));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoJaDevolvidoNaoEstaAtrasadoMesmoAposPrazo() {
        // Criado em 27/10 (prazo 10/11), devolvido em 12/11, consultado em 14/11 -> não atrasado
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 27));
        emprestimo.devolver(LocalDate.of(2026, 11, 12));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoNoDiaDoPrazoAindaNaoEstaAtrasado() {
        // Criado em 31/10 (prazo 14/11), consultado em 14/11 -> não atrasado
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 31));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }
}
