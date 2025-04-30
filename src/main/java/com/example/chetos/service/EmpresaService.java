package com.example.chetos.service;

import com.example.chetos.model.Empresa;
import com.example.chetos.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    public List<Empresa> listarEmpresas() {
        return empresaRepository.findAll();
    }

    public Optional<Empresa> obtenerEmpresaPorId(Long id) {
        return empresaRepository.findById(id);
    }

    public Empresa guardarEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }
}