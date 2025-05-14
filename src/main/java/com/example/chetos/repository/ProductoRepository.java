package com.example.chetos.repository;

import com.example.chetos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

@Query("SELECT DISTINCT p.talle FROM Producto p WHERE p.talle IS NOT NULL")
List<String> findAllTalles();

@Query("SELECT DISTINCT p.color FROM Producto p WHERE p.color IS NOT NULL")
List<String> findAllColores();

@Query("SELECT DISTINCT p.tipo FROM Producto p WHERE p.tipo IS NOT NULL")
List<String> findAllTipos();

@Query("SELECT DISTINCT p.genero FROM Producto p WHERE p.genero IS NOT NULL")
List<String> findAllGeneros();

@Query("SELECT p FROM Producto p WHERE p.fecha_alta >= :fechaInicio")
List<Producto> findProductosUltimos7Dias(@Param("fechaInicio") LocalDate fechaInicio);


@Query("SELECT p FROM Producto p WHERE p.descuento > 0")
List<Producto> findProductosConDescuento();


List<Producto> findByGenero(String genero);
List<Producto> findByEstado(String estado);

}
