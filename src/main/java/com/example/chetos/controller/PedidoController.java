package com.example.chetos.controller;

import java.time.LocalDate;

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
    public String verCarrito(Model model) {
        // Aquí asumimos que la información del carrito está en la sesión
        List<DetallePedido> carrito = (List<DetallePedido>) model.getAttribute("carrito");
        model.addAttribute("carrito", carrito);
        return "carrito";  // Plantilla del carrito
    }

    // Añadir un producto al carrito
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam long productoId, @RequestParam String talle, @RequestParam String color, @RequestParam int cantidad, Model model) {
        Producto producto = productoService.findById(productoId);
        // Validar disponibilidad en el stock
        Stock stock = stockService.findStockByProductoYColorYTalle(productoId, color, talle);
        
        if (stock != null && stock.getCantidad() >= cantidad) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setTalle(talle);
            detalle.setColor(color);
            detalle.setCantidad(cantidad);
            detalle.setPrecio_unit(Float.parseFloat(producto.getPrecio()));
            // Añadir al carrito en la sesión (simulado aquí como una lista)
            List<DetallePedido> carrito = (List<DetallePedido>) model.getAttribute("carrito");
            carrito.add(detalle);
            model.addAttribute("carrito", carrito);
        } else {
            // Manejar el caso de stock insuficiente
            model.addAttribute("error", "No hay suficiente stock para el producto seleccionado.");
        }
        return "redirect:/carrito/ver";
    }

    // Crear un pedido
    @PostMapping("/realizar-pedido")
    public String realizarPedido(@RequestParam String nombre, @RequestParam String telefono, @RequestParam String direccion, Model model) {
        // Crear un nuevo pedido
        Pedido pedido = new Pedido();
        pedido.setNombre(nombre);
        pedido.setTelefono(telefono);
        pedido.setDireccion(direccion);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado("Pendiente");
        
        // Guardar los detalles del pedido
        List<DetallePedido> carrito = (List<DetallePedido>) model.getAttribute("carrito");
        pedido.setDetallePedidoList(carrito);

        // Guardar en la base de datos
        pedidoService.save(pedido);

        // Redirigir al usuario a WhatsApp para continuar la compra
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
    @GetMapping("/administrar-pedidos")
    public String administrarPedidos(Model model) {
        List<Pedido> pedidos = pedidoService.findAll();
        model.addAttribute("pedidos", pedidos);
        return "administrar_pedidos";  // Plantilla para gestionar pedidos
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
