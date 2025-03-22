    package org.example.web_vulnerables.Entities;
    import jakarta.persistence.*;
    import org.springframework.stereotype.Component;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    import java.util.List;

    @Data
    @Entity
    @Table(name="users")
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Component
    public class UserEntity {
        @Id
        @Column(name = "username")
        private String username;

        @Column(name = "password")
        private String password;

        @Getter
        @Column(name = "licenseId", unique = true)
        private int licenseId;

        @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<PetEntity> listPet;

        public int getLicenseId() {
            return licenseId;
        }

        public String getUsername() {
            return username;
        }

        public List<PetEntity> getListPet() {

            return listPet;
        }
    }

