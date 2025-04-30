package com.example.chetos.serviceImpl;
    
import com.example.chetos.model.Venta;
import com.example.chetos.repository.VentaRepository;
import com.example.chetos.service.VentaService;
import java.util.List;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
@Service
public class VentaServiceImpl implements VentaService {
    @Autowired
    VentaRepository ventaRepository;

    @Override
    public List<Venta> findAll() {
        
        return ventaRepository.findAll(); 
    }

    @Override
    public Venta findById(long id) {
        
        return ventaRepository.findById(id).get(); 
    }

    @Override
    public Venta save(Venta venta) {
      
        return ventaRepository.save(venta); 
    }




}
