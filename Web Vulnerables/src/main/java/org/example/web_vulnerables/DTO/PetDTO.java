package org.example.web_vulnerables.DTO;

public class PetDTO {
    private String petName;
    private String species;
    private String imagePath;

    public PetDTO(String petName, String species, String imagePath) {
        this.petName = petName;
        this.species = species;
        this.imagePath = imagePath;
    }

    public String getPetName() { return petName; }
    public String getSpecies() { return species; }
    public String getImagePath() { return imagePath; }
}
