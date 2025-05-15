package com.example.chetos.controller;

import com.example.chetos.repository.ImagenCarruselRepository;
import com.example.chetos.model.ImagenCarrusel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Controller
public class ImagenCarruselController {

    @Autowired
    ImagenCarruselRepository imagenCarruselRepository;

    @GetMapping("/formulario-carrusel")
    public String mostrarFormularioCarrusel() {
        return "formulario-carrusel";
    }




    @PostMapping("/imagenesCarrusel")
    public String guardarImagenesCarrusel(
            RedirectAttributes msg,
            @RequestParam("file") MultipartFile foto,
            @RequestParam("file1") MultipartFile foto1,
            @RequestParam("file2") MultipartFile foto2,
            @RequestParam("file3") MultipartFile foto3,
            @RequestParam("file4") MultipartFile foto4) {

        try {
            Path uploadPath = Paths.get("./src/main/resources/static/uploads/");
            Files.createDirectories(uploadPath);

            // Buscar el primer carrusel existente o crear uno nuevo
            List<ImagenCarrusel> carruseles = imagenCarruselRepository.findAll();
            ImagenCarrusel imagenCarrusel = carruseles.isEmpty() ? new ImagenCarrusel() : carruseles.get(0);

            // Procesar cada archivo
            imagenCarrusel.setFoto(procesarArchivo(imagenCarrusel.getFoto(), foto, uploadPath));
            imagenCarrusel.setFoto1(procesarArchivo(imagenCarrusel.getFoto1(), foto1, uploadPath));
            imagenCarrusel.setFoto2(procesarArchivo(imagenCarrusel.getFoto2(), foto2, uploadPath));
            imagenCarrusel.setFoto3(procesarArchivo(imagenCarrusel.getFoto3(), foto3, uploadPath));
            imagenCarrusel.setFoto4(procesarArchivo(imagenCarrusel.getFoto4(), foto4, uploadPath));

            imagenCarruselRepository.save(imagenCarrusel);
            msg.addFlashAttribute("suceso", "Imágenes guardadas correctamente.");

        } catch (IOException e) {
            e.printStackTrace();
            msg.addFlashAttribute("error", "Error al guardar las imágenes: " + e.getMessage());
        }

        return "redirect:/formulario-carrusel";
    }

    @RequestMapping(value = "/editarCarrusel/{id}", method = RequestMethod.GET)
    public ModelAndView editarCarrusel(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("editarCarrusel");
        Optional<ImagenCarrusel> imagen = imagenCarruselRepository.findById(id);

        if (imagen.isPresent()) {
            mv.addObject("imagen", imagen.get());
        } else {
            mv.addObject("error", "No se encontró el carrusel con ID: " + id);
        }

        return mv;
    }

    /**
     * Método auxiliar para procesar archivos subidos, reemplazando y eliminando el anterior si corresponde.
     */
    private String procesarArchivo(String nombreAnterior, MultipartFile nuevoArchivo, Path uploadPath) throws IOException {
        if (!nuevoArchivo.isEmpty()) {
            // Eliminar archivo anterior
            if (nombreAnterior != null) {
                Path previousFile = uploadPath.resolve(nombreAnterior);
                Files.deleteIfExists(previousFile);
            }

            // Guardar nuevo archivo
            Path newPath = uploadPath.resolve(nuevoArchivo.getOriginalFilename());
            Files.write(newPath, nuevoArchivo.getBytes());
            return nuevoArchivo.getOriginalFilename();
        }
        return nombreAnterior;
    }
}
