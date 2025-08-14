package com.wikigroup.demo.controllers;

import com.wikigroup.demo.models.ContactoEntity;
import com.wikigroup.demo.repositories.ContactoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContactoController {

    @Autowired
    private ContactoRepository contactoRepositorio;

    // Mostrar formulario
    @GetMapping("/contactenos")
    public String mostrarFormulario(Model model) {
        model.addAttribute("contacto", new ContactoEntity());
        return "contactenos"; // nombre del template Thymeleaf (contactenos.html)
    }

    // Procesar formulario
    @PostMapping("/contactenos")
    public String guardarContacto(
            @Valid @ModelAttribute("contacto") ContactoEntity contacto,
            BindingResult bindingResult,
            Model model) {

        // Validaciones adicionales en backend
        if (contacto.getCorreo() != null) {
            String correo = contacto.getCorreo().toUpperCase();
            if (!correo.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$")
                    || correo.contains("Ñ")
                    || correo.matches(".*[ÁÉÍÓÚÜ].*")
                    || correo.contains(" ")) {
                bindingResult.rejectValue("correo", "error.contacto", "Correo inválido");
            }
            contacto.setCorreo(correo); // forzar mayúsculas
        }

        if (bindingResult.hasErrors()) {
            return "contactenos"; // vuelve al formulario con errores
        }

        // Guardar en la base de datos
        contactoRepositorio.save(contacto);

        // Mensaje de éxito
        model.addAttribute("mensaje", "¡Formulario enviado con éxito!");
        model.addAttribute("contacto", new ContactoEntity()); // limpia el formulario
        return "contactenos";
    }
}

