package com.example.chetos.service;

import java.util.List;
import com.example.chetos.model.Pedido; // Import the Pedido class

public interface PedidoService {
    
List<Pedido> findAll(); // Método para listar todos los pedidos

Pedido findById(long id); // Método para buscar un pedido por su ID
Pedido save(Pedido pedido); // Método para guardar un nuevo pedido
}
