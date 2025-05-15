package com.example.chetos.model;

import jakarta.persistence.*;


@Entity
@Table(name="imagen_Carrusel")
public class ImagenCarrusel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    @Column(nullable = true)
    private String foto;
    @Column(nullable = true)
    private String foto1;
    @Column(nullable = true)
    private String foto2;
    @Column(nullable = true)
    private String foto3;
    @Column(nullable = true)
    private String foto4;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFoto1() {
        return foto1;
    }

    public void setFoto1(String foto1) {
        this.foto1 = foto1;
    }

    public String getFoto2() {
        return foto2;
    }

    public void setFoto2(String foto2) {
        this.foto2 = foto2;
    }

    public String getFoto3() {
        return foto3;
    }

    public void setFoto3(String foto3) {
        this.foto3 = foto3;
    }

    public String getFoto4() {
        return foto4;
    }

    public void setFoto4(String foto4) {
        this.foto4 = foto4;
    }
    
 }
  
   
    