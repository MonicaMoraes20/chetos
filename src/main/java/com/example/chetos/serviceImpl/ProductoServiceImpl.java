package com.example.chetos.serviceImpl;

import com.example.chetos.model.Producto;
import com.example.chetos.repository.ProductoRepository;
import com.example.chetos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {
    @Autowired
    ProductoRepository productoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Producto findById(long id) {
        return productoRepository.findById(id).orElse(null);  // Manejo adecuado del valor nulo
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id).orElse(null);  // Implementación del método solicitado
    }
}
