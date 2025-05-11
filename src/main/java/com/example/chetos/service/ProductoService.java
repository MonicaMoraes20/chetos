package com.example.chetos.service;

import com.example.chetos.model.Producto;
import java.util.List;

public interface ProductoService {
    List<Producto> findAll();
    Producto findById(long id);

    Producto save(Producto producto);
    Producto obtenerProductoPorId(Long id);
     

    List<Producto> findByGenero(String genero);


}
