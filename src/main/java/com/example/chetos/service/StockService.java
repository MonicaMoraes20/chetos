package com.example.chetos.service;

import java.util.List;

import com.example.chetos.model.Stock;

public interface StockService  {

    List<Stock> findAll();
    Stock findById(long id);
    Stock save(Stock stock);
    Stock findStockByProductoYColorYTalle(long productoId, String color, String talle);

}
