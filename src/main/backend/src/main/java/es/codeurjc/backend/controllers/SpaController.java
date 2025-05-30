package es.codeurjc.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/new", "/new/**/{path:[^\\.]*}", "/{path:new[^\\.]*}"})
    public String redirect() {
        return "forward:/new/index.html";
    }
}
