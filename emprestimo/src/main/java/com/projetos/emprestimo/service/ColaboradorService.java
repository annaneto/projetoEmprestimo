package com.projetos.emprestimo.service;

import com.projetos.emprestimo.exception.ConflitoException;
import com.projetos.emprestimo.exception.NaoEncontradoException;
import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.repository.ColaboradorRepository;
import com.projetos.emprestimo.repository.EmprestimoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {
    private final ColaboradorRepository repository;
    private final EmprestimoRepository emprestimoRepository;

    public ColaboradorService(ColaboradorRepository repository, EmprestimoRepository emprestimoRepository){
        this.repository = repository;
        this.emprestimoRepository = emprestimoRepository;
    }

    public List<Colaborador> listar(){
        return repository.findAll();
    }

    public Colaborador buscar(Long id){
        return repository.findById(id).orElseThrow(()-> new NaoEncontradoException("Colaborador não encontrado"));
    }

    public Colaborador cadastrar(Colaborador colaborador){
        if(repository.existsByEmail(colaborador.getEmail())){
            throw new ConflitoException("Email já cadastrado");
        }
        return repository.save(colaborador);
    }

    public Colaborador atualizar(Long id, Colaborador dados){
        Colaborador colaborador = buscar(id);
        if(dados.getEmail() != null && repository.existsByEmailAndIdNot(dados.getEmail(), id)){
            throw new ConflitoException("Email já cadastrado para outro colaborador");
        }
        colaborador.setNome(dados.getNome());
        colaborador.setEmail(dados.getEmail());
        colaborador.setCargo(dados.getCargo());
        colaborador.setCep(dados.getCep());
        colaborador.setNumero(dados.getNumero());
        colaborador.setBairro(dados.getBairro());
        colaborador.setCidade(dados.getCidade());
        colaborador.setUf(dados.getUf());
        return repository.save(colaborador);
    }

    public void remover(Long id){
        Colaborador colaborador = buscar(id);
        boolean possuiEmprestimos = !emprestimoRepository.findByColaboradorId(id).isEmpty();
        if(possuiEmprestimos){
            throw new ConflitoException("Colaborador possui empréstimos registrados e não pode ser excluído");
        }
        repository.delete(colaborador);
    }
}
