package com.projetos.emprestimo;

import com.projetos.emprestimo.exception.ConflitoException;
import com.projetos.emprestimo.exception.NaoEncontradoException;
import com.projetos.emprestimo.model.Equipamento;
import com.projetos.emprestimo.model.TipoEquipamento;
import com.projetos.emprestimo.repository.EmprestimoRepository;
import com.projetos.emprestimo.repository.EquipamentoRepository;
import com.projetos.emprestimo.service.EquipamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipamentoServiceTest {

    @Mock
    private EquipamentoRepository repository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private EquipamentoService service;

    private Equipamento equipamento;

    @BeforeEach
    void setUp() {
        equipamento = new Equipamento();
        equipamento.setId(1L);
        equipamento.setPatrimonio("PAT-001");
        equipamento.setNome("Mouse Óptico");
        equipamento.setTipo(TipoEquipamento.MOUSE);
    }

    @Test
    void deveCadastrarEquipamentoComSucesso() {
        when(repository.existsByPatrimonio("PAT-001")).thenReturn(false);
        when(repository.save(any(Equipamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Equipamento salvo = service.cadastrar(equipamento);

        assertNotNull(salvo);
        assertTrue(salvo.isDisponivel());
        verify(repository).save(equipamento);
    }

    @Test
    void deveLancarConflitoAoCadastrarPatrimonioDuplicado() {
        when(repository.existsByPatrimonio("PAT-001")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.cadastrar(equipamento));
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> service.buscar(99L));
    }

    @Test
    void deveLancarConflitoAoRemoverEquipamentoComEmprestimos() {
        when(repository.findById(1L)).thenReturn(Optional.of(equipamento));
        when(emprestimoRepository.existsByEquipamentoId(1L)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.remover(1L));
        verify(repository, never()).delete(any());
    }
}
