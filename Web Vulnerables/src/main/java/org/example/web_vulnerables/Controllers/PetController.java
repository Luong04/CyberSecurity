package org.example.web_vulnerables.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.web_vulnerables.Entities.PetEntity;
import org.example.web_vulnerables.Entities.UserEntity;
import org.example.web_vulnerables.Repository.PetRepository;
import org.example.web_vulnerables.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class PetController {
    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private final String UPLOAD_DIR = "src/main/webapp/WEB-INF/views";
    // Thư mục lưu ảnh

    @PostMapping("/addPet")
    public ResponseEntity<?> addPet(@RequestParam("name") String name,
                                    @RequestParam("species") String species,
                                    @RequestParam("image") MultipartFile image,
                                    HttpSession session) {

        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(401).body("Bạn chưa đăng nhập!");
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("Không tìm thấy người dùng!");
        }

        // Lưu file ảnh
        String savePath = UPLOAD_DIR + image.getOriginalFilename();
        File file = new File(savePath);
        System.out.println("File đã lưu tại: " + file.getAbsolutePath());

        try {
            image.transferTo(file.getAbsoluteFile());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi khi lưu ảnh!");
        }

        // Lưu thông tin thú cưng vào database
        PetEntity pet = new PetEntity();
        pet.setPetName(name);
        pet.setSpecies(species);
        pet.setImagePath(image.getOriginalFilename());
        pet.setOwner(user);

        petRepository.save(pet);

        return ResponseEntity.ok("Thêm thú cưng thành công!");
    }
}
