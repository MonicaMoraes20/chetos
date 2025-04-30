package com.example.chetos.serviceImpl;
import java.util.List;
import com.example.chetos.model.DetallePedido;
import com.example.chetos.repository.DetallePedidoRepository;
import com.example.chetos.service.DetallePedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {
  
    @Autowired DetallePedidoRepository detallePedidoRepository;


    @Override
    public List<DetallePedido> findAll() {
        return detallePedidoRepository.findAll();
    }

    @Override
    public DetallePedido findById(long id) {
        return detallePedidoRepository.findById(id).get();
    }

    @Override
    public DetallePedido save(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    
}
}
