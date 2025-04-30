package com.example.chetos.controller;

import com.example.chetos.model.Empresa;
import com.example.chetos.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;

    // Mostrar la empresa en la página de inicio
    @GetMapping("/")
    public String index(Model model) {
        Empresa empresa = empresaService.obtenerEmpresaPorId(1L).orElse(new Empresa());
        model.addAttribute("empresa", empresa);
        return "index";
    }

    // Mostrar la vista de la empresa para editar (supuesto que es una sola empresa)
    @GetMapping
    public String mostrarVistaEmpresa(Model model) {
        Empresa empresa = empresaService.obtenerEmpresaPorId(1L).orElse(new Empresa());
        model.addAttribute("empresa", empresa);
        return "datosEmpresa";
    }

    // Método para actualizar los datos de la empresa, incluyendo la imagen
    @PostMapping("/actualizar")
    public String actualizarEmpresa(@ModelAttribute Empresa empresa,
                                    @RequestParam("logo") MultipartFile logoFile,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Verificamos si se sube un archivo de logo
            if (!logoFile.isEmpty()) {
                // Guardamos el archivo en un directorio específico
                String uploadDir = "src/main/resources/static/logos/"; // Directorio donde almacenarás el logo
                String fileName = logoFile.getOriginalFilename(); // Obtiene el nombre original del archivo
                Path filePath = Paths.get(uploadDir + fileName);  // Ruta completa del archivo

                // Guardar el archivo en el sistema de archivos
                Files.copy(logoFile.getInputStream(), filePath);

                // Actualizamos la entidad empresa con la ruta del logo
                empresa.setLogoPath("/logos/" + fileName);  // La ruta relativa que se guardará en la base de datos
            } else {
                // Si no se sube un logo, mantenemos la ruta existente (si hay alguna)
                Empresa existente = empresaService.obtenerEmpresaPorId(empresa.getId()).orElse(null);
                if (existente != null) {
                    empresa.setLogoPath(existente.getLogoPath()); // Usamos la ruta del logo existente
                }
            }

            // Guardamos la empresa en la base de datos
            empresaService.guardarEmpresa(empresa);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("mensaje", "¡La empresa fue actualizada correctamente!");
            redirectAttributes.addFlashAttribute("clase", "success");

        } catch (IOException e) {
            // Mensaje de error
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar la empresa: " + e.getMessage());
            redirectAttributes.addFlashAttribute("clase", "danger");
        }

        return "redirect:/empresas";
    }

    
}

