package org.example.web_vulnerables.Controllers;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class FileController {

    private final Path rootLocation = Paths.get("src/main/webapp/WEB-INF/views");

    @GetMapping("/{filename}")
    public Resource getFile(@PathVariable String filename) throws IOException {
        Path filePath = rootLocation.resolve(filename).normalize();
        System.out.println(filePath);
        return new UrlResource(filePath.toUri());
    }
}
