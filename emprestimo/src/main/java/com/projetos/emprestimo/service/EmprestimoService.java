package com.projetos.emprestimo.service;

import com.projetos.emprestimo.exception.NaoEncontradoException;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.repository.EmprestimoRepository;

import java.util.List;

public class EmprestimoService {
    private final EmprestimoRepository repository;
    private final ColaboradorService colaboradorService;
    private final EquipamentoService equipamentoService;

    public EmprestimoService(EmprestimoRepository repository, ColaboradorService colaboradorSerivice,
                             EquipamentoService equipamentoService){
        this.repository = repository;
        this.colaboradorService = colaboradorSerivice;
        this.equipamentoService = equipamentoService;
    }
    public Emprestimo buscar(Long id){
        return repository.findById(id).orElseThrow(()->new NaoEncontradoException("Empréstimo não encontrado"));
    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId){
        colaboradorService.buscar(colaboradorId);
        return repository.findByColaboradorId(colaboradorId);
    }

}
