package org.example.web_vulnerables.Controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping
public class ReadFileController {
    //public String BASE_URL="/target/classes/file_system/";
    @PostMapping("/api/read")
    public String readFile(@RequestParam String filePath) {
        try {

            File file = new File( filePath);
            System.out.println(file.getAbsoluteFile());
            if (!file.exists() || !file.isFile()) {
                return "File not found!";
            }
            return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}
