package org.example.web_vulnerables.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Data
@Entity
@Table(name="pets")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class PetEntity {
    @Id
    @Column(name="petname")
    private String petName;

    @Column(name="species")
    private String species;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "ownerLicenseId", referencedColumnName = "licenseId")
    private UserEntity owner;

    public void setPetName(String petName) {
        this.petName = petName;
    }
    public void setSpecies(String species) {
        this.species = species;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }
    public String getPetName() {
        return petName;
    }
    public String getSpecies() {
        return species;
    }
    public String getImagePath() {
        return imagePath;
    }

}
