package com.github.delirium25.shelter.controller;

import com.github.delirium25.shelter.model.AdoptionDetails;
import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.AnimalStatus;
import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.AdoptionDetailsRepo;
import com.github.delirium25.shelter.repository.AnimalRepo;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/admin")
public class Admin {

    @Autowired
    AnimalRepo animalRepo;

    @Autowired
    NewOwnerRepo ownerRepo;

    @Autowired
    AdoptionDetailsRepo adoptionDetailsRepo;


    @PostMapping("/generateAdoptionDocuments")
    public ResponseEntity<Object> generateAdoptionDocuments() {
        List<Animal> animals = animalRepo.findAllOrderedByName();

        for (Animal animal : animals) {
            try {
                if (animal.getAdoptionDate() == null) {
                    animal.setStatus(AnimalStatus.ADOPTABLE);
                    animalRepo.save(animal);
                } else {
                    Optional<NewOwner> owner = ownerRepo.findByAdoptedAnimals(animal.getId());
                    animal.setStatus(AnimalStatus.ADOPTED);
                    owner.get().removeAdoptedAnimal(animal.getId());
                    AdoptionDetails adoptionDetails = AdoptionDetails.builder()
                            .animalId(animal.getId())
                            .ownerId(owner.get().getId())
                            .animalName(animal.getName())
                            .ownerName(owner.get().getName())
                            .animalType(animal.getAnimalType())
                            .adoptionDate(animal.getAdoptionDate())
                            .build();
                    animal.setAdoptionDate(null);
                    ownerRepo.save(owner.get());
                    animalRepo.save(animal);
                    adoptionDetailsRepo.save(adoptionDetails);
                }
            } catch (Exception e) {
                log.error("Failed to migrate entity with id" + animal.getId(), e);
            }
        }
        return ResponseEntity.ok().body("Migration finished");
    }

}