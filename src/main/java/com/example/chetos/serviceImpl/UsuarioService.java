package com.example.chetos.serviceImpl;

import com.example.chetos.model.Usuario;
import java.util.Optional;

public interface UsuarioService {

    // Método para buscar un usuario por su email
    Optional<Usuario> findByEmail(String email);

    // Método para registrar un nuevo usuario
    Usuario save(Usuario usuario);

    // Método para actualizar la información de un usuario
    Usuario update(Usuario usuario);

    // Método para eliminar un usuario
    void delete(Long id);
}
