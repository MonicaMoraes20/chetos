package com.example.chetos.controller;

import com.example.chetos.model.Empresa;
import com.example.chetos.model.Producto;
import com.example.chetos.model.Usuario;
import com.example.chetos.service.EmpresaService;
import com.example.chetos.service.ProductoService;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ProductoService productoService;

    // Verifica si hay usuario logueado y si es ADMIN
    private boolean isAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        return usuario != null && "ADMIN".equalsIgnoreCase(usuario.getRol());
    }

    @GetMapping("/panel-admin")
    public String mostrarPanelAdmin(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        return "PanelControlAdmin";
    }

    @GetMapping("/datos-empresa")
    public String mostrardatosEmpresa(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        Empresa empresa = empresaService.obtenerEmpresaPorId(1L).orElse(new Empresa());
        model.addAttribute("empresa", empresa);
        return "datosEmpresa";
    }

    @GetMapping("/listar-productos")
    public String listarProductos(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        List<Producto> productos = productoService.findAll();
        model.addAttribute("productos", productos);
        return "listarProductos";
    }

    @GetMapping("/eliminar-producto")
    public String eliminarProducto(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        return "eliminarProducto";
    }

    @GetMapping("/registrar-producto")
    public String registrarProducto(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        return "registrarProducto";
    }

    @GetMapping("/editar-producto")
    public String editarProducto(HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        return "editarProducto";
    }

    @GetMapping("/editar-producto/{id}")
    public String editarProducto(@PathVariable Long id, Model model, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login?error=unauthorized";
        }
        Producto producto = productoService.findById(id);
        if (producto == null) {
            return "redirect:/listar-productos";
        }
        model.addAttribute("producto", producto);
        return "editarProducto";
    }





}
