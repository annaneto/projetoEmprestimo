package com.projetos.emprestimo.controller;

import com.projetos.dto.EmprestimoRequest;
import com.projetos.emprestimo.model.Emprestimo;
import com.projetos.emprestimo.model.Equipamento;
import com.projetos.emprestimo.model.TipoEquipamento;
import com.projetos.emprestimo.service.EquipamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {
    private final EquipamentoService service;
    public EquipamentoController(EquipamentoService service) { this.service = service; }

    @GetMapping
    public List<Equipamento> listar(@RequestParam(required = false) TipoEquipamento tipo) {
        return service.listar(tipo);
    }
    @GetMapping("/{id}")
    public Equipamento buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipamento cadastrar(@RequestBody Equipamento equipamento) {
        return service.cadastrar(equipamento);
    }

    @PutMapping("/{id}")
    public Equipamento atualizar(@PathVariable Long id, @RequestBody @Valid Equipamento dados){
        return service.atualizar(id, dados);
    }



    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id){
        service.remover(id);
    }


}