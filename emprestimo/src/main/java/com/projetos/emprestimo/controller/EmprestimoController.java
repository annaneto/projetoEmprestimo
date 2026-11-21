package com.projetos.emprestimo.controller;

import com.projetos.dto.EmprestimoRequest;
import com.projetos.emprestimo.model.Emprestimo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.projetos.emprestimo.service.EmprestimoService;

@RestController
@RequestMapping("/emprestimo")
public class EmprestimoController {
    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(
            @RequestBody @Valid EmprestimoRequest pedido) {
        return service.emprestar(pedido);
    }

    @PutMapping("/{id/devolucao")
    public Emprestimo devolver(@PathVariable Long id){
        return service.devolver(id);
    }



}
