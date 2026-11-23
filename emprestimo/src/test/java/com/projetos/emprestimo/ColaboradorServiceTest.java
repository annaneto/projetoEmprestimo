package com.projetos.emprestimo;

import com.projetos.emprestimo.exception.ConflitoException;
import com.projetos.emprestimo.exception.NaoEncontradoException;
import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.repository.ColaboradorRepository;
import com.projetos.emprestimo.repository.EmprestimoRepository;
import com.projetos.emprestimo.service.ColaboradorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ColaboradorServiceTest {

    @Mock
    private ColaboradorRepository repository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @InjectMocks
    private ColaboradorService service;

    private Colaborador colaborador;

    @BeforeEach
    void setUp() {
        colaborador = new Colaborador();
        colaborador.setId(1L);
        colaborador.setNome("Maria Silva");
        colaborador.setEmail("maria@email.com");
        colaborador.setCep("01001000");
        colaborador.setNumero("123");
    }

    @Test
    void deveCadastrarColaboradorComSucesso() {
        when(repository.existsByEmail("maria@email.com")).thenReturn(false);
        when(repository.save(any(Colaborador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Colaborador salvo = service.cadastrar(colaborador);

        assertNotNull(salvo);
        assertEquals("Maria Silva", salvo.getNome());
        verify(repository).save(colaborador);
    }

    @Test
    void deveLancarConflitoAoCadastrarEmailJaExistente() {
        when(repository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThrows(ConflitoException.class, () -> service.cadastrar(colaborador));
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarNaoEncontradoAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NaoEncontradoException.class, () -> service.buscar(99L));
    }

    @Test
    void deveLancarConflitoAoRemoverColaboradorComEmprestimos() {
        when(repository.findById(1L)).thenReturn(Optional.of(colaborador));
        when(emprestimoRepository.findByColaboradorId(1L)).thenReturn(List.of(mock(Emprestimo.class)));

        assertThrows(ConflitoException.class, () -> service.remover(1L));
        verify(repository, never()).delete(any());
    }

    @Test
    void deveRemoverComSucessoQuandoNaoPossuiEmprestimos() {
        when(repository.findById(1L)).thenReturn(Optional.of(colaborador));
        when(emprestimoRepository.findByColaboradorId(1L)).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> service.remover(1L));
        verify(repository).delete(colaborador);
    }
}
