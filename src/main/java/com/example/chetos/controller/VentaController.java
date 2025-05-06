package com.example.chetos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import com.example.chetos.model.Venta;
import com.example.chetos.repository.VentaRepository;
import java.util.List;

@Controller
public class VentaController {
    @Autowired
    private VentaRepository ventaRepository;

  

@RequestMapping(value="/listarVentas", method=RequestMethod.GET)
    public ModelAndView getVentas() {
        ModelAndView mv = new ModelAndView("listarVentas");
        List<Venta> ventas = ventaRepository.findAll();
        mv.addObject("ventas", ventas);
        return mv;
    }

}


