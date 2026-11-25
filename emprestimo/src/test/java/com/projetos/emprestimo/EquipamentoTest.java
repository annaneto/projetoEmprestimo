package com.projetos.emprestimo;

import com.projetos.emprestimo.model.Equipamento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipamentoTest {

    @Test
    void emprestarEquipamentoDisponivelMudaParaIndisponivel() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(true);

        equipamento.emprestar();

        assertFalse(equipamento.isDisponivel());
    }

    @Test
    void emprestarEquipamentoIndisponivelLancaExcecao() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(false);

        assertThrows(IllegalStateException.class, () -> equipamento.emprestar());
    }

    @Test
    void devolverEquipamentoTornaEleDisponivelNovamente() {
        Equipamento equipamento = new Equipamento();
        equipamento.setDisponivel(false);

        equipamento.devolver();

        assertTrue(equipamento.isDisponivel());
    }
}
