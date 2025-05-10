package com.example.chetos.controller;

import com.example.chetos.model.Empresa;
import com.example.chetos.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
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
    public String actualizarEmpresa(@ModelAttribute Empresa empresa, BindingResult result,
            @RequestParam("file") MultipartFile logo,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar la empresa: " + result.getAllErrors());
            redirectAttributes.addFlashAttribute("clase", "danger");
            return "redirect:/empresas";
        }
        try {
            // Verificamos si se sube un archivo de logo
            if (!logo.isEmpty()) {
                byte[] bytes = logo.getBytes();
                Path path = Paths.get("src/main/resources/static/img/" + logo.getOriginalFilename());
                Files.write(path, bytes);
                empresa.setLogo(logo.getOriginalFilename()); // Guardamos la ruta del logo

            }

        } catch (IOException e) {
            // Mensaje de error
            System.out.println("Error al guardar la imagen: " + e.getMessage());
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar la imagen: " + e.getMessage());
            redirectAttributes.addFlashAttribute("clase", "danger");
            return "redirect:/empresas";
        }

        empresaService.guardarEmpresa(empresa);

        // Mensaje de éxito
        redirectAttributes.addFlashAttribute("mensaje", "¡La empresa fue actualizada correctamente!");
        redirectAttributes.addFlashAttribute("clase", "success");

        return "redirect:/empresas";
    }

    // Metodo para mostrar el logo en index
   @RequestMapping(value="/logo/{logo}", method=RequestMethod.GET)
    @ResponseBody

     public byte[] getLogo(@PathVariable("logo") String logo) throws IOException {
        File path = new File("./src/main/resources/static/img/" + logo);
        if (logo != null || logo.trim().length() > 0) {
            return Files.readAllBytes(path.toPath());
        }
        return null;

    }

}
