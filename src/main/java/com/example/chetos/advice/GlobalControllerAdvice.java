package com.example.chetos.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.chetos.repository.EmpresaRepository;
import com.example.chetos.model.Empresa;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final EmpresaRepository empresaRepository;

    @Autowired
    public GlobalControllerAdvice(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @ModelAttribute("empresa")
    public Empresa empresa() {
        return empresaRepository.findById(1L).orElse(new Empresa());
    }
}
