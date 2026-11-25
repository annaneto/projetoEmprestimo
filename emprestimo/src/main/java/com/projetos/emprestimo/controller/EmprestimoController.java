package com.projetos.emprestimo.controller;

import com.projetos.dto.EmprestimoRequest;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = { "/emprestimos", "/emprestimo" })
public class EmprestimoController {
    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Emprestimo> listar(@RequestParam(required = false) Boolean atrasados) {
        return service.listar(atrasados);
    }

    @GetMapping("/{id}")
    public Emprestimo buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(@RequestBody @Valid EmprestimoRequest pedido) {
        return service.emprestar(pedido);
    }

    @PostMapping(params = { "equipamentoId", "colaboradorId" })
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestarPorParametros(
            @RequestParam Long equipamentoId,
            @RequestParam Long colaboradorId) {
        return service.emprestar(new EmprestimoRequest(equipamentoId, colaboradorId));
    }

    @PutMapping("/{id}/devolucao")
    public Emprestimo devolver(@PathVariable Long id) {
        return service.devolver(id);
    }
}
