package com.projetos.emprestimo.controller;

import com.projetos.emprestimo.model.Colaborador;
import com.projetos.emprestimo.service.ColaboradorService;
import com.projetos.emprestimo.service.EmprestimoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}