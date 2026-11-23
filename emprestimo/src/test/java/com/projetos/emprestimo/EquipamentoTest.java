package com.projetos.emprestimo;

import com.projetos.emprestimo.model.Equipamento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EquipamentoTest {

    @Test
    void equipamentoNovoNasceDisponivel(){

        Equipamento equipamento = new Equipamento();

        boolean disponivel = equipamento.isDisponivel();

        assertTrue(disponivel);

    }

}
