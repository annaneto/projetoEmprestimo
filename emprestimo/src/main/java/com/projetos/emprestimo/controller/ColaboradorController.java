package com.projetos.emprestimo.controller;

import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.service.ColaboradorService;
import com.projetos.emprestimo.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    private final ColaboradorService service;
    private final EmprestimoService emprestimoService;

    public ColaboradorController(
            ColaboradorService service,
            EmprestimoService emprestimoService) {
        this.service = service;
        this.emprestimoService = emprestimoService;
    }

    @GetMapping
    public List<Colaborador> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Colaborador buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Colaborador cadastrar(@RequestBody @Valid Colaborador colaborador) {
        return service.cadastrar(colaborador);
    }

    @PutMapping("/{id}")
    public Colaborador atualizar(@PathVariable Long id, @RequestBody @Valid Colaborador dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }

    @GetMapping("/{id}/emprestimos")
    public List<Emprestimo> listarEmprestimos(@PathVariable Long id) {
        return emprestimoService.listarDoColaborador(id);
    }
}