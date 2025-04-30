package com.example.chetos.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.chetos.model.Stock;
import com.example.chetos.repository.StockRepository;
import com.example.chetos.service.StockService;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {
    
    @Autowired StockRepository stockRepository;

    @Override
    public List<Stock> findAll() {
        return stockRepository.findAll(); 
    }

    @Override
    public Stock findById(long id) {
        return stockRepository.findById(id).get(); 
    }

    @Override
    public Stock save(Stock stock) {
        return stockRepository.save(stock); 
    }
    
     // Método que utiliza el repositorio para encontrar el stock por producto, color y talle
     public Stock findStockByProductoYColorYTalle(long productoId, String color, String talle) {
        return stockRepository.findByProductoIdAndColorAndTalle(productoId, color, talle);
    }

}
