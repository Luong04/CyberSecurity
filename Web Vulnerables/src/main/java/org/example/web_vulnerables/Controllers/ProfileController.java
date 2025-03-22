package org.example.web_vulnerables.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.web_vulnerables.DTO.PetDTO;
import org.example.web_vulnerables.DTO.UserDTO;
import org.example.web_vulnerables.Entities.PetEntity;
import org.example.web_vulnerables.Entities.UserEntity;
import org.example.web_vulnerables.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProfileController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public UserDTO getProfile(HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return null;  // Hoặc throw exception
        }

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        // Chuyển danh sách PetEntity sang PetDTO để tránh vòng lặp
        List<PetDTO> petList = user.getListPet().stream()
                .map(pet -> new PetDTO(pet.getPetName(), pet.getSpecies(), pet.getImagePath()))
                .collect(Collectors.toList());

        // Trả về DTO
        return new UserDTO(user.getUsername(), user.getLicenseId(), petList);
    }
}
