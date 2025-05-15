package com.example.chetos.controller;

import com.example.chetos.model.Producto;
import com.example.chetos.model.ImagenCarrusel;
import com.example.chetos.repository.ProductoRepository;
import com.example.chetos.repository.ImagenCarruselRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProductoController {
    @Autowired
    ProductoRepository productoRepository;

      @Autowired
    ImagenCarruselRepository imagenCarruselRepository; 

    @RequestMapping(value="/inicioP", method = RequestMethod.GET)
    public String inicio() { return "home"; }

    @RequestMapping(value="/nuevoProducto", method=RequestMethod.GET)
    public String nuevoProducto() {
        return "registrarProducto";
    }

    @RequestMapping(value="/nuevoProducto", method=RequestMethod.POST)
    public String registroProducto(@Valid Producto producto, BindingResult result, RedirectAttributes msg,
     @RequestParam("file") MultipartFile foto, @RequestParam("file1") MultipartFile foto1, @RequestParam("file2") MultipartFile foto2) {
        if (result.hasErrors()) {
            // Imprimir los errores en la consola
            result.getFieldErrors().forEach(error -> {
                System.out.println("Campo con error: " + error.getField() + " - " + error.getDefaultMessage());
            });
    
            // Agregar un mensaje genérico para el usuario
            msg.addFlashAttribute("error", "Error al registrar. Por favor, complete todos los campos correctamente.");
            return "redirect:/nuevoProducto";
        }

        producto.setFecha_alta(LocalDate.now());
        producto.setEstado("Activo");

        try {
            if (!foto.isEmpty()) {
                byte[] bytes = foto.getBytes();
                Path caminho = Paths.get("./src/main/resources/static/img/"+foto.getOriginalFilename());
                Files.write(caminho, bytes);
                producto.setFoto(foto.getOriginalFilename());
            }
            if (!foto1.isEmpty()) {
                byte[] bytes1 = foto1.getBytes();
                Path caminho1 = Paths.get("./src/main/resources/static/img/"+foto1.getOriginalFilename());
                Files.write(caminho1, bytes1);
                producto.setFoto1(foto1.getOriginalFilename());
            }

            if (!foto2.isEmpty()) {
                byte[] bytes2 = foto2.getBytes();
                Path caminho2 = Paths.get("./src/main/resources/static/img/"+foto2.getOriginalFilename());
                Files.write(caminho2, bytes2);
                producto.setFoto2(foto2.getOriginalFilename());
            }


        } catch (IOException e) {
            System.out.println("Error foto");
        }

        productoRepository.save(producto);
        msg.addFlashAttribute("suceso", "Producto registrado.");

        return "redirect:/nuevoProducto";
    }

    @RequestMapping(value="/foto/{foto}", method=RequestMethod.GET)
    @ResponseBody
    public byte[] getFotos(@PathVariable("foto") String foto) throws IOException {
        File caminho = new File ("./src/main/resources/static/img/"+foto);
        if (foto != null || foto.trim().length() > 0) {
            return Files.readAllBytes(caminho.toPath());
        }
        return null;
    }

    @GetMapping("/index")
public ModelAndView listarProductos(
        @RequestParam(required = false) String talle,
        @RequestParam(required = false) String color,
        @RequestParam(required = false) String tipo,
        @RequestParam(required = false) String genero,
        @RequestParam(required = false) String nombre
) {
    List<Producto> productos = productoRepository.findByEstado("Activo");

    // Filtrado manual en memoria (puedes optimizar esto con consultas dinámicas en BD si necesitas)
    if (talle != null && !talle.isEmpty()) {
        productos = productos.stream()
                .filter(p -> p.getTalle() != null && p.getTalle().equalsIgnoreCase(talle))
                .collect(Collectors.toList());
    }

    if (color != null && !color.isEmpty()) {
        productos = productos.stream()
                .filter(p -> p.getColor() != null && p.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }

    if (tipo != null && !tipo.isEmpty()) {
        productos = productos.stream()
                .filter(p -> p.getTipo() != null && p.getTipo().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    if (genero != null && !genero.isEmpty()) {
        productos = productos.stream()
                .filter(p -> p.getGenero() != null && p.getGenero().equalsIgnoreCase(genero))
                .collect(Collectors.toList());
    }

    if (nombre != null && !nombre.isEmpty()) {
        productos = productos.stream()
                .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    ModelAndView mav = new ModelAndView("index");
    mav.addObject("productos", productos);

// Obtener las imágenes del carrusel
      Optional<ImagenCarrusel> carruselOptional = imagenCarruselRepository.findFirstBy();
        List<String> carruselImagesList = new ArrayList<>();

        if (carruselOptional.isPresent()) {
            ImagenCarrusel carrusel = carruselOptional.get();
            if (carrusel.getFoto() != null && !carrusel.getFoto().isEmpty()) {
                carruselImagesList.add(carrusel.getFoto());
            }
            if (carrusel.getFoto1() != null && !carrusel.getFoto1().isEmpty()) {
                carruselImagesList.add(carrusel.getFoto1());
            }
            if (carrusel.getFoto2() != null && !carrusel.getFoto2().isEmpty()) {
                carruselImagesList.add(carrusel.getFoto2());
            }
            if (carrusel.getFoto3() != null && !carrusel.getFoto3().isEmpty()) {
                carruselImagesList.add(carrusel.getFoto3());
            }
            if (carrusel.getFoto4() != null && !carrusel.getFoto4().isEmpty()) {
                carruselImagesList.add(carrusel.getFoto4());
            }
        }

        mav.addObject("carruselImages", carruselImagesList);
    

    // También puedes enviar las listas para poblar los selects
    mav.addObject("talles", productoRepository.findAllTalles());
    mav.addObject("colores", productoRepository.findAllColores());
    mav.addObject("tipos", productoRepository.findAllTipos());
    mav.addObject("generos", productoRepository.findAllGeneros());

    return mav;
}


    


    @RequestMapping(value="/editarProducto/{id}", method=RequestMethod.GET)
    public ModelAndView editar(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("editarProducto");
        Optional<Producto> producto = productoRepository.findById(id);
        mv.addObject("nombre", producto.get().getNombre());
        mv.addObject("codigo", producto.get().getCodigo());
        mv.addObject("descripcion", producto.get().getDescripcion());
        mv.addObject("precio", producto.get().getPrecio());
        mv.addObject("descuento", producto.get().getDescuento());
        mv.addObject("genero", producto.get().getGenero());
        mv.addObject("tipo", producto.get().getTipo());
        mv.addObject("talle", producto.get().getTalle());
        mv.addObject("color", producto.get().getColor());
        mv.addObject("estado", producto.get().getEstado());
        mv.addObject("stock", producto.get().getStock());
        mv.addObject("id", producto.get().getId());
        return mv;
    }

    @RequestMapping(value="/editarProducto/{id}", method=RequestMethod.POST)
    public String editarLivroBanco(Producto producto, RedirectAttributes msg) {
        Producto productoExistente = productoRepository.findById(producto.getId()).orElse(null);
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setCodigo(producto.getCodigo());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setPrecio(producto.getPrecio());
        productoExistente.setDescuento(producto.getDescuento());
        productoExistente.setGenero(producto.getGenero());
        productoExistente.setTipo(producto.getTipo());
        productoExistente.setTalle(producto.getTalle());
        productoExistente.setColor(producto.getColor());
        productoExistente.setEstado(producto.getEstado());
        productoExistente.setStock(producto.getStock());
        productoRepository.save(productoExistente);
        return "redirect:/listar-productos";
    }



    @RequestMapping(value="/vermasProducto/{id}", method=RequestMethod.GET)
    public ModelAndView vermasProducto(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("vermasProducto");
        Optional<Producto> productos = productoRepository.findById(id);
        mv.addObject("nombre", productos.get().getNombre());
        mv.addObject("codigo", productos.get().getCodigo());
        mv.addObject("descripcion", productos.get().getDescripcion());
        mv.addObject("precio", productos.get().getPrecio());
        mv.addObject("descuento", productos.get().getDescuento());
        mv.addObject("talle", productos.get().getTalle());
        mv.addObject("color", productos.get().getColor());
        mv.addObject("foto", productos.get().getFoto());
        mv.addObject("foto1", productos.get().getFoto1());
        mv.addObject("foto2", productos.get().getFoto2());
        mv.addObject("stock", productos.get().getStock());

        return mv;
    }

    @RequestMapping(value="/novedades", method=RequestMethod.GET)
    public ModelAndView novedades() {
        ModelAndView mv = new ModelAndView("novedades");
        LocalDate hace7Dias = LocalDate.now().minusDays(7);
    List<Producto> productos = productoRepository.findProductosUltimos7Dias(hace7Dias);
        mv.addObject("productos", productos);
        return mv;
    }

    @RequestMapping(value="/ultimasOportunidades", method=RequestMethod.GET)
    public ModelAndView ultimasOportunidades() {
        List<Producto> productos = productoRepository.findProductosConDescuento();
        ModelAndView mv = new ModelAndView("ultimasOportunidades");
        mv.addObject("productos", productos);
        return mv;
    }

    @RequestMapping(value="/femenino", method=RequestMethod.GET)
    public ModelAndView femenino() {
        ModelAndView mv = new ModelAndView("femenino");
        List<Producto> productos = productoRepository.findByGenero("Femenino");
        mv.addObject("productos", productos);
        return mv;
    }


 @RequestMapping(value="/masculino", method=RequestMethod.GET)
    public ModelAndView masculino() {
        ModelAndView mv = new ModelAndView("masculino");
        List<Producto> productos = productoRepository.findByGenero("Masculino");
        mv.addObject("productos", productos);
        return mv;
    }



}
