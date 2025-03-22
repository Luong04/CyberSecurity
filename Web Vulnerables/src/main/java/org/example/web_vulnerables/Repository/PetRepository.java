package org.example.web_vulnerables.Repository;

import org.example.web_vulnerables.Entities.PetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<PetEntity, Long> {
}
