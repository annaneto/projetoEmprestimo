package com.projetos.emprestimo.service;

import com.projetos.dto.EmprestimoRequest;
import com.projetos.emprestimo.exception.ConflitoException;
import com.projetos.emprestimo.exception.NaoEncontradoException;
import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.model.Equipamento;
import com.projetos.emprestimo.repository.EmprestimoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {
    private final EmprestimoRepository repository;
    private final ColaboradorService colaboradorService;
    private final EquipamentoService equipamentoService;
    private static final int PRAZO_EM_DIAS = 14;

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

    @Transactional
    public Emprestimo emprestar(EmprestimoRequest pedido){
        Equipamento equipamento = equipamentoService.buscar(pedido.equipamentoId());
        Colaborador colaborador = colaboradorService.buscar(pedido.colaboradorId());

        if(!equipamento.isDisponivel()){
            throw new ConflitoException("Equipamento indisponível");
        }
        equipamento.setDisponivel(false);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setEquipamento(equipamento);
        emprestimo.setColaborador(colaborador);
        emprestimo.setDataEmprestimo(LocalDate.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(PRAZO_EM_DIAS));
        return repository.save(emprestimo);

    }

    public Emprestimo devolver(Long id){
        Emprestimo emprestimo = buscar(id);
        if(emprestimo.getDataDevolucao() != null){
            throw new ConflitoException("Está devolvido");
        }
        emprestimo.setDataDevolucao(LocalDate.now());
        emprestimo.getEquipamento().setDisponivel(true);
        return repository.save(emprestimo);
    }

    public List<Emprestimo> listar(Boolean atrasados){
        if(Boolean.TRUE.equals(atrasados)){
            return repository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return repository.findAll();
    }

}
