package com.example.chetos.controller;

import com.example.chetos.model.Usuario;
import com.example.chetos.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login"; // este es tu login.html
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                 @RequestParam String password,
                                 HttpSession session) {
        Usuario usuario = usuarioRepository.findByEmailAndPassword(email, password);
        if (usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol())) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/panel-admin";
        } else {
            return "redirect:/login?error=true";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
