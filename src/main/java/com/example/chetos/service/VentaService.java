package com.example.chetos.service;
import java.util.List;
import com.example.chetos.model.Venta;


public interface VentaService {

    
List<Venta> findAll();

Venta findById(long id);
Venta save(Venta venta);

 
    
}
