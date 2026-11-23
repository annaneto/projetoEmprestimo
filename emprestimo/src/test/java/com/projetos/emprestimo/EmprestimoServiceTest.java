package com.projetos.emprestimo;

import com.projetos.dto.EmprestimoRequest;
import com.projetos.emprestimo.exception.ConflitoException;
import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.model.Equipamento;
import com.projetos.emprestimo.model.TipoEquipamento;
import com.projetos.emprestimo.repository.EmprestimoRepository;
import com.projetos.emprestimo.service.ColaboradorService;
import com.projetos.emprestimo.service.EmprestimoService;
import com.projetos.emprestimo.service.EquipamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository repository;

    @Mock
    private ColaboradorService colaboradorService;

    @Mock
    private EquipamentoService equipamentoService;

    @InjectMocks
    private EmprestimoService service;

    private Equipamento equipamento;
    private Colaborador colaborador;

    @BeforeEach
    void setUp() {
        equipamento = new Equipamento();
        equipamento.setId(1L);
        equipamento.setPatrimonio("PAT-001");
        equipamento.setNome("Notebook Dell");
        equipamento.setTipo(TipoEquipamento.NOTEBOOK);
        equipamento.setDisponivel(true);

        colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setNome("João");
        colaborador.setEmail("joao@email.com");
        colaborador.setCep("12345678");
        colaborador.setNumero("100");
    }

    @Test
    void deveEmprestarComSucessoQuandoEquipamentoDisponivel() {
        EmprestimoRequest request = new EmprestimoRequest(1L, 1L);
        when(equipamentoService.buscar(1L)).thenReturn(equipamento);
        when(colaboradorService.buscar(1L)).thenReturn(colaborador);
        when(repository.findByColaboradorId(1L)).thenReturn(Collections.emptyList());
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emprestimo resultado = service.emprestar(request);

        assertNotNull(resultado);
        assertEquals(equipamento, resultado.getEquipamento());
        assertEquals(colaborador, resultado.getColaborador());
        assertFalse(equipamento.isDisponivel());
        assertEquals(LocalDate.now(), resultado.getDataEmprestimo());
        assertEquals(LocalDate.now().plusDays(14), resultado.getDataPrevistaDevolucao());
        verify(repository).save(any(Emprestimo.class));
    }

    @Test
    void deveLancarConflitoQuandoEquipamentoEstiverIndisponivel() {
        equipamento.setDisponivel(false);
        EmprestimoRequest request = new EmprestimoRequest(1L, 1L);
        when(equipamentoService.buscar(1L)).thenReturn(equipamento);
        when(colaboradorService.buscar(1L)).thenReturn(colaborador);

        ConflitoException exception = assertThrows(ConflitoException.class, () -> service.emprestar(request));
        assertEquals("Equipamento indisponível", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarConflitoQuandoColaboradorTiverEmprestimoAtrasado() {
        Emprestimo emprestimoAtrasado = new Emprestimo();
        emprestimoAtrasado.setDataPrevistaDevolucao(LocalDate.now().minusDays(2));
        emprestimoAtrasado.setDataDevolucao(null);

        EmprestimoRequest request = new EmprestimoRequest(1L, 1L);
        when(equipamentoService.buscar(1L)).thenReturn(equipamento);
        when(colaboradorService.buscar(1L)).thenReturn(colaborador);
        when(repository.findByColaboradorId(1L)).thenReturn(List.of(emprestimoAtrasado));

        ConflitoException exception = assertThrows(ConflitoException.class, () -> service.emprestar(request));
        assertTrue(exception.getMessage().contains("atraso"));
        verify(repository, never()).save(any());
    }

    @Test
    void deveDevolverComSucesso() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(10L);
        equipamento.setDisponivel(false);
        emprestimo.setEquipamento(equipamento);
        emprestimo.setDataDevolucao(null);

        when(repository.findById(10L)).thenReturn(Optional.of(emprestimo));
        when(repository.save(any(Emprestimo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Emprestimo resultado = service.devolver(10L);

        assertNotNull(resultado.getDataDevolucao());
        assertTrue(equipamento.isDisponivel());
        verify(repository).save(emprestimo);
    }

    @Test
    void deveLancarConflitoAoDevolverEmprestimoJaDevolvido() {
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setId(10L);
        emprestimo.setDataDevolucao(LocalDate.now().minusDays(1));

        when(repository.findById(10L)).thenReturn(Optional.of(emprestimo));

        ConflitoException exception = assertThrows(ConflitoException.class, () -> service.devolver(10L));
        assertEquals("Empréstimo já foi devolvido", exception.getMessage());
        verify(repository, never()).save(any());
    }
}
