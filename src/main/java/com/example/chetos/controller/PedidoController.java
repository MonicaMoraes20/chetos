package com.example.chetos.controller;

import java.time.LocalDate;

import com.example.chetos.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.chetos.model.*;
import com.example.chetos.model.DetallePedido;
import com.example.chetos.model.Pedido;
import com.example.chetos.model.Producto;
import com.example.chetos.model.Stock;
import com.example.chetos.service.PedidoService;
import com.example.chetos.service.ProductoService;
import com.example.chetos.service.StockService;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/carrito")
public class PedidoController {
        @Autowired
    private ProductoService productoService;

    @Autowired
    private StockService stockService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    // Mostrar todos los productos disponibles
    @RequestMapping(value="/index", method=RequestMethod.GET)
    public ModelAndView getProductos() {
        ModelAndView mv = new ModelAndView("index");
        List<Producto> productoList = productoService.findAll();
        mv.addObject("productos", productoList);
        return mv;
    }

    // Mostrar el carrito
    @GetMapping("/ver")
    public String verCarrito(HttpSession session, Model model) {
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new ArrayList<>();
        }
        model.addAttribute("carrito", carrito);
        return "carrito";
    }


    // Añadir un producto al carrito
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam long productoId,
                                   @RequestParam int cantidad,
                                   HttpSession session,
                                   Model model) {
        Producto producto = productoService.findById(productoId);

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setTalle(producto.getTalle());
            detalle.setColor(producto.getColor());
            detalle.setCantidad(cantidad);
            detalle.setPrecio_unit(Float.parseFloat(producto.getPrecio()));

            // Obtener o crear el carrito en la sesión
            List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
            if (carrito == null) {
                carrito = new ArrayList<>();
            }
            carrito.add(detalle);
            session.setAttribute("carrito", carrito);


        return "redirect:/carrito/ver";
    }


    // Crear un pedido
    @PostMapping("/realizar-pedido")
    public String realizarPedido(@RequestParam String nombre,
                                 @RequestParam String telefono,
                                 @RequestParam String direccion,
                                 HttpSession session,
                                 Model model) {
        List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            model.addAttribute("error", "El carrito está vacío.");
            return "carrito";
        }

        Pedido pedido = new Pedido();
        pedido.setNombre(nombre);
        pedido.setTelefono(telefono);
        pedido.setDireccion(direccion);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");
        pedido.setDetallePedidoList(carrito);

        pedidoService.save(pedido);

        // Limpiar el carrito de la sesión
        session.removeAttribute("carrito");

        String urlWhatsapp = "https://wa.me/59898499679?text=" + generarMensajeWhatsapp(pedido);
        return "redirect:" + urlWhatsapp;
    }


    // Generar mensaje para WhatsApp
    private String generarMensajeWhatsapp(Pedido pedido) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("¡Hola! Quiero realizar un pedido:\n");
        mensaje.append("Nombre: ").append(pedido.getNombre()).append("\n");
        mensaje.append("Teléfono: ").append(pedido.getTelefono()).append("\n");
        mensaje.append("Dirección: ").append(pedido.getDireccion()).append("\n");
        mensaje.append("Detalles del Pedido: \n");

        for (DetallePedido detalle : pedido.getDetallePedidoList()) {
            mensaje.append("Producto: ").append(detalle.getProducto().getNombre()).append("\n");
            mensaje.append("Talle: ").append(detalle.getTalle()).append(" | Color: ").append(detalle.getColor()).append(" | Cantidad: ").append(detalle.getCantidad()).append("\n");
        }
        return mensaje.toString();
    }

    // Visualizar pedidos para el administrador
    @RequestMapping(value="/administrar_pedidos", method=RequestMethod.GET)
    public ModelAndView administrarPedidos() {
        ModelAndView mv = new ModelAndView("administrar_pedidos");
        List<Pedido> pedidos = pedidoRepository.findAll();
        mv.addObject("pedidos", pedidos);
        return mv;  // Plantilla para gestionar pedidos
    }

    // Aceptar un pedido
    @PostMapping("/aceptar-pedido/{pedidoId}")
    public String aceptarPedido(@PathVariable long pedidoId, Model model) {
        Pedido pedido = pedidoService.findById(pedidoId);
        // Actualizar estado y reducir stock
        pedido.setEstado("Aceptado");
        for (DetallePedido detalle : pedido.getDetallePedidoList()) {
            Stock stock = stockService.findStockByProductoYColorYTalle(detalle.getProducto().getId(), detalle.getColor(), detalle.getTalle());
            stock.setCantidad(stock.getCantidad() - detalle.getCantidad());
            stockService.save(stock);
        }
        pedidoService.save(pedido);
        return "redirect:/carrito/administrar-pedidos";
    }

    // Rechazar un pedido
    @PostMapping("/rechazar-pedido/{pedidoId}")
    public String rechazarPedido(@PathVariable long pedidoId, Model model) {
        Pedido pedido = pedidoService.findById(pedidoId);
        pedido.setEstado("Rechazado");
        pedidoService.save(pedido);
        return "redirect:/carrito/administrar-pedidos";
    }
}
