package com.example.chetos.repository;
import com.example.chetos.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
       // Método para buscar stock por producto, color y talle
       Stock findByProductoIdAndColorAndTalle(Long productoId, String color, String talle);
}
