package org.example.web_vulnerables.Views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping
public class AuthView{
    @GetMapping("/register")
    public RedirectView showRegisterPage() {
        return new RedirectView("/static/register.html");
    }

    @GetMapping("/login")
    public RedirectView showLoginPage() {
        return new RedirectView("/static/login.html");
    }
}
