package com.projetos.emprestimo.service;

import com.projetos.emprestimo.exception.ConflitoException;
import com.projetos.emprestimo.exception.NaoEncontradoException;
import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {
    private final ColaboradorRepository repository;

    public ColaboradorService(ColaboradorRepository repository){
        this.repository = repository;
    }

    public List<Colaborador> listar(){
        return repository.findAll();
    }
    public Colaborador buscar(Long id){
        return repository.findById(id).orElseThrow(()-> new NaoEncontradoException("Colaborador não encontrado"));
    }

    public Colaborador Cadastrar(Colaborador colaborador){
        if(repository.existsByEmail(colaborador.getEmail())){
            throw new ConflitoException("Email já cadastrado");
        }
        return repository.save(colaborador);
    }


}
