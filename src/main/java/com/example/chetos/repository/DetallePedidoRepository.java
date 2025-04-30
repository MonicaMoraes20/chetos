package com.example.chetos.repository;
import  org.springframework.data.jpa.repository.JpaRepository;
import com.example.chetos.model.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    
    
}
