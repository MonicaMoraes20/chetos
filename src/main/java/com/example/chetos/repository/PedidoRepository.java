package com.example.chetos.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.chetos.model.Pedido;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findAllByOrderByFechaDesc();
}