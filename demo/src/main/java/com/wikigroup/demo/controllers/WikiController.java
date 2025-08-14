package com.wikigroup.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WikiController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/equipo")
    public String equipo() {
        return "equipo";
    }

    @GetMapping("/proyectos")
    public String proyectos() {
        return "proyectos";
    }

}
