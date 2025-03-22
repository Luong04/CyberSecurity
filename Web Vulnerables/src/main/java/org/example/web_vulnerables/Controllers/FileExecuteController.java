package org.example.web_vulnerables.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileExecuteController {

    @GetMapping("/execute")
    public String execute(@RequestParam String file) {
        return file;
    }
}
