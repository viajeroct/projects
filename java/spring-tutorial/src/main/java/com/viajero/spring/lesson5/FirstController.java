package com.viajero.spring.lesson5;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/*
Version 1.0:
import javax.servlet.http.HttpServletRequest;
*/

@Controller
@RequestMapping("/root")
public class FirstController {
    /*
    Главное отличие:
    если не будет параметров, то страница
    не будет загружена и мы получим Error 404.
    Если мы всё же не хотим такого поведения,
    то нужно дописать: required = false.
     */
    @GetMapping("/hello")
    public String helloPage(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "surname", required = false) String surname,
                            Model model) {
        String text = "Hello: " + name + " " + surname;
        System.out.println(text);

        // send to model
        model.addAttribute("msg", text);

        return "first/hello";
    }

    /*
    Version 1.0:
    @GetMapping("/hello")
    public String helloPage(HttpServletRequest request) {
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        System.out.println("Hello: " + name + " " + surname);
        return "first/hello";
    }
    */

    @GetMapping("/bye")
    public String byePage() {
        return "first/bye";
    }
}
