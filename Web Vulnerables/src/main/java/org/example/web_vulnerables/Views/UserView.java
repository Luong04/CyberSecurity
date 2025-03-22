package org.example.web_vulnerables.Views;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping
public class UserView {
    @GetMapping("/home")
    public RedirectView home(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return new RedirectView("redirect:/static/login.html");
        }
        return new RedirectView("/static/home.html");
    }
    @GetMapping("/user/profile")
    public RedirectView profile(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return new RedirectView("redirect:/static/login.html");
        }
        return new RedirectView("/static/profile.html");
    }

    @GetMapping("/user/createPet")
    public RedirectView createPet(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return new RedirectView("redirect:/static/login.html");
        }
        return new RedirectView("/static/createPet.html");
    }

    @GetMapping("/user/readFile")
    public RedirectView readFile(HttpSession session) {
        return new RedirectView("/static/readFile.html");
    }

    @GetMapping("/")
    public RedirectView defaultMethod(HttpSession session) {
        return new RedirectView("/static/index.html");
    }
}
