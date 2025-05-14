package com.example.chetos.controller;

import java.time.LocalDate;

import com.example.chetos.repository.PedidoRepository;
import com.example.chetos.repository.VentaRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.example.chetos.model.*;
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

    @Autowired
    private VentaRepository ventaRepository;

    // Mostrar todos los productos disponibles
    @RequestMapping(value = "/index", method = RequestMethod.GET)
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
        float total = 0f;
        for (DetallePedido detalle : carrito) {
            total += detalle.getPrecio_unit() * detalle.getCantidad();
        }
        model.addAttribute("total", total);

        return "carrito";
    }

    @PostMapping("/eliminar")
public String eliminarDelCarrito(@RequestParam int index, HttpSession session) {
    List<DetallePedido> carrito = (List<DetallePedido>) session.getAttribute("carrito");
    if (carrito != null && index >= 0 && index < carrito.size()) {
        carrito.remove(index);
        session.setAttribute("carrito", carrito);
    }
    return "redirect:/carrito/ver";
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
        pedido.setDetallePedidoList(carrito); // Asignas la lista de detalles al pedido

        // **Itera sobre los detalles del carrito y asigna el pedido a cada uno**
        for (DetallePedido detalle : carrito) {
            detalle.setPedido(pedido); // Establece la relación con el pedido
        }

        pedidoService.save(pedido);

        // Limpiar el carrito de la sesión
        session.removeAttribute("carrito");

        String urlWhatsapp = "https://wa.me/59898499679?text=" + generarMensajeWhatsapp(pedido);
        return "redirect:" + urlWhatsapp;
    }

    // Generar mensaje para WhatsApp
    private String generarMensajeWhatsapp(Pedido pedido) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("Hola! Quiero realizar un pedido:\n\n");
        mensaje.append("Nombre: ").append(pedido.getNombre()).append("\n");
        mensaje.append("Teléfono: ").append(pedido.getTelefono()).append("\n");
        mensaje.append("Dirección: ").append(pedido.getDireccion()).append("\n\n");
        mensaje.append("Detalles del Pedido:\n\n");

        for (DetallePedido detalle : pedido.getDetallePedidoList()) {
            float subtotal = detalle.getCantidad() * detalle.getPrecio_unit();
            String totalFormateado = String.format("R$ %.2f", subtotal).replace('.', ',');

            mensaje.append("Producto: ").append(detalle.getProducto().getNombre()).append("\n");
            mensaje.append("Talle: ").append(detalle.getTalle()).append("\n");
            mensaje.append("Color: ").append(detalle.getColor()).append("\n");
            mensaje.append("Cantidad: ").append(detalle.getCantidad()).append("\n");
            mensaje.append("Total: ").append(totalFormateado).append("\n\n");
        }

        // Codificar correctamente los saltos de línea
        return mensaje.toString().replace("\n", "%0A");
    }

    // Visualizar pedidos para el administrador
    @RequestMapping(value = "/administrar_pedidos", method = RequestMethod.GET)
    public ModelAndView administrarPedidos() {
        ModelAndView mv = new ModelAndView("administrar_pedidos");
        List<Pedido> pedidos = pedidoRepository.findAllByOrderByFechaDesc();

        mv.addObject("pedidos", pedidos);
        return mv; // Plantilla para gestionar pedidos
    }

    // Aceptar un pedido
    @PostMapping("/aceptar-pedido/{pedidoId}")
    public String aceptarPedido(@PathVariable long pedidoId, Model model) {
        Pedido pedido = pedidoService.findById(pedidoId);
        pedido.setEstado("Aceptado");

        float totalVenta = 0f;
        List<DetalleVenta> detalleVentaList = new ArrayList<>();

        for (DetallePedido detalle : pedido.getDetallePedidoList()) {
            // Obtener el producto
            Producto producto = productoService.findById(detalle.getProducto().getId());
            int cantidadComprada = detalle.getCantidad();

            // Actualizar el stock del producto
            producto.setStock(producto.getStock() - cantidadComprada);
            productoService.save(producto); // Guardar la actualización del producto

            // Crear detalle de venta
            DetalleVenta dv = new DetalleVenta();
            dv.setProducto(detalle.getProducto());
            dv.setCantidad(detalle.getCantidad());
            dv.setPrecio_unit(detalle.getPrecio_unit());
            dv.setTalle(detalle.getTalle());
            dv.setColor(detalle.getColor());
            detalleVentaList.add(dv);

            // Acumular total
            totalVenta += detalle.getCantidad() * detalle.getPrecio_unit();
        }

        // Crear venta
        Venta venta = new Venta();
        venta.setFecha(LocalDate.now());
        venta.setNombreCliente(pedido.getNombre());
        venta.setTelefono(pedido.getTelefono());
        venta.setDireccion(pedido.getDireccion());
        venta.setTotal(totalVenta);
        venta.setPedido(pedido);

        // Relacionar detalleVenta con venta
        for (DetalleVenta dv : detalleVentaList) {
            dv.setVenta(venta);
        }

        venta.setDetalleVentaList(detalleVentaList);
        ventaRepository.save(venta);

        pedidoService.save(pedido);
        return "redirect:/carrito/administrar_pedidos";
    }

    // Rechazar un pedido
    @PostMapping("/rechazar-pedido/{pedidoId}")
    public String rechazarPedido(@PathVariable long pedidoId, Model model) {
        Pedido pedido = pedidoService.findById(pedidoId);
        pedido.setEstado("Rechazado");
        pedidoService.save(pedido);
        return "redirect:/carrito/administrar_pedidos";
    }

}
