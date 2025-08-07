package com.wikigroup.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WikiController {

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("welcomeMessage", "Bienvenido a la Wiki de tu grupo");
        return "index.html";
    }

    @GetMapping("/equipo")
    public String equipo() {
        return "equipo";
    }

    @GetMapping("/proyectos")
    public String proyectos() {
        return "proyectos";
    }

   /*  @GetMapping("/contactenos")
    public String contacto(Model model) {
        model.addAttribute("contacto", new com.wikigroup.demo.models.Contacto()); // asegúrate que exista la clase Contacto
        return "contactenos";
    }
*/

    @GetMapping("/contactenos")
    public String contacto() {
        return "contactenos"; // una página sencilla de contacto
    }

    @GetMapping("/gracias")
    public String gracias() {
        return "gracias"; // una página sencilla de confirmación
    }
}
