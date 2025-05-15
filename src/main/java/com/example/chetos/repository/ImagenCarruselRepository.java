package com.example.chetos.repository;

import com.example.chetos.model.ImagenCarrusel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ImagenCarruselRepository extends JpaRepository<ImagenCarrusel, Long> {

    @Query("SELECT ic FROM ImagenCarrusel ic")
    Optional<ImagenCarrusel> findFirstBy();
}