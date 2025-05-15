package com.example.chetos.service;

import com.example.chetos.model.ImagenCarrusel;
import java.util.List;

public interface ImagenCarruselService {

    List<ImagenCarrusel>findAll();

    ImagenCarrusel findById(Long id);
    ImagenCarrusel save(ImagenCarrusel imagenCarrusel);

    
}
