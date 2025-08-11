package com.wikigroup.demo.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// IMPORTS PARA CUANDO CESAR IMPLEMENTE JPA
// import com.wikigroup.demo.model.Contacto; 
// import com.wikigroup.demo.repository.ContactoRepository;
// import org.springframework.beans.factory.annotation.Autowired;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Controller
public class ContactoController {

    // ===== DESCOMENTAR CUANDO CESAR LO IMPLEMENTE =====
    // private final ContactoRepository repo;
    //
    // @Autowired
    // public ContactoController(ContactoRepository repo) {
    //     this.repo = repo;
    // }

    public ContactoController() {}

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    // Clase temporal para validaciones — se reemplazará por la Entidad JPA de Cesar
    public static class Contacto {
        @NotBlank @Size(max = 100)
        private String nombres;
        @NotBlank @Size(max = 100)
        private String apellidos;
        @NotBlank @Size(max = 100)
        private String correo;
        @Min(0) @Max(16)
        private Integer semestre;
        @NotBlank @Size(max = 500)
        private String descripcion;

        public String getNombres() { return nombres; }
        public void setNombres(String nombres) { this.nombres = nombres; }
        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public Integer getSemestre() { return semestre; }
        public void setSemestre(Integer semestre) { this.semestre = semestre; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    }

    @GetMapping("/contactenos")
    public String mostrarFormulario(Model model,
                                    @RequestParam(value = "success", required = false) String ok) {
        if (!model.containsAttribute("contacto")) {
            model.addAttribute("contacto", new Contacto());
        }
        if ("1".equals(ok)) {
            model.addAttribute("successMessage", "¡Tu mensaje fue enviado correctamente!");
        }
        return "contactenos";
    }

    @PostMapping("/contactenos")
    public String procesar(@Valid @ModelAttribute("contacto") Contacto contacto,
                           BindingResult result,
                           RedirectAttributes ra) {

        validarSinAcentos("nombres", contacto.getNombres(), result);
        validarSinAcentos("apellidos", contacto.getApellidos(), result);

        if (contacto.getCorreo() != null) {
            String original = contacto.getCorreo();
            String upper = original.toUpperCase();
            if (!original.equals(upper)) {
                result.addError(new FieldError("contacto", "correo", "El correo debe estar en MAYÚSCULAS."));
            }
            if (contieneAcentosONie(original)) {
                result.addError(new FieldError("contacto", "correo", "El correo no debe contener tildes ni ñ."));
            }
            if (original.contains(" ")) {
                result.addError(new FieldError("contacto", "correo", "El correo no debe contener espacios."));
            }
            Pattern patron = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$");
            if (!patron.matcher(original).matches()) {
                result.addError(new FieldError("contacto", "correo", "Formato de correo inválido (debe incluir @ y al menos un punto después)."));
            }
        }

        if (contacto.getSemestre() != null) {
            int s = contacto.getSemestre();
            if (s < 0 || s > 16) {
                result.addError(new FieldError("contacto", "semestre", "El semestre debe estar entre 0 y 16."));
            }
        }

        if (result.hasErrors()) {
            return "contactenos";
        }

        // ===== DESCOMENTAR CUANDO CESAR LO IMPLEMENTE =====
        // repo.save(contacto);

        ra.addAttribute("success", "1");
        return "redirect:/contactenos";
    }

    private void validarSinAcentos(String field, String valor, BindingResult result) {
        if (valor == null) return;
        if (contieneAcentosONie(valor)) {
            result.addError(new FieldError("contacto", field, "No se permiten tildes ni la letra ñ/Ñ."));
        }
    }

    private boolean contieneAcentosONie(String s) {
        String sinAcentos = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return !sinAcentos.equals(s) || s.contains("ñ") || s.contains("Ñ");
    }
}
