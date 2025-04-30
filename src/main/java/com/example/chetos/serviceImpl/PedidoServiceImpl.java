package com.example.chetos.serviceImpl;

import com.example.chetos.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.chetos.repository.PedidoRepository;
import java.util.List;
import com.example.chetos.model.Pedido;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    PedidoRepository pedidoRepository;

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }
    @Override
    public Pedido findById(long id) {
        return pedidoRepository.findById(id).get();  // Manejo adecuado del valor nulo
    }

    @Override
    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }


}