package org.example.web_vulnerables.DTO;
import java.util.List;

    public class UserDTO {
        private String username;
        private int licenseId;
        private List<PetDTO> listPet;

        public UserDTO(String username, int licenseId, List<PetDTO> listPet) {
            this.username = username;
            this.licenseId = licenseId;
            this.listPet = listPet;
        }

        // Getter
        public String getUsername() { return username; }
        public int getLicenseId() { return licenseId; }
        public List<PetDTO> getListPet() { return listPet; }
    }

