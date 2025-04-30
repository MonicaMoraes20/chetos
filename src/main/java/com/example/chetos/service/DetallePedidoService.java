package com.example.chetos.service;

import java.util.List;

import com.example.chetos.model.DetallePedido;

public interface DetallePedidoService {
    List<DetallePedido> findAll();

    DetallePedido findById(long id);
    DetallePedido save(DetallePedido detallePedido);
}
