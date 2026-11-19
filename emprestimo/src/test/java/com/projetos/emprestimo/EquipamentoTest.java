package com.projetos.emprestimo;

import com.projetos.emprestimo.model.Equipamento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EquipamentoTest {

    @Test
    void equipamentoNovoNasceDisponivel(){
        // preparando o cenário
        Equipamento equipamento = new Equipamento();

        // verifica a ação que está sendo testada
        boolean disponivel = equipamento.isDisponivel();

        // compara com o esperado
        assertTrue(disponivel);

    }

}
