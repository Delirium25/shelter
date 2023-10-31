package com.github.delirium25.shelter.service;

import com.couchbase.client.core.deps.com.google.common.collect.Lists;
import com.github.delirium25.shelter.model.AdoptionDetails;
import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.AnimalStatus;
import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.AdoptionDetailsRepo;
import com.github.delirium25.shelter.repository.AnimalRepo;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdoptionService {

    private final NewOwnerRepo newOwnerRepo;
    private final AnimalRepo animalRepo;
    private final AdoptionDetailsRepo adoptionDetailsRepo;

    public List<AdoptionDetails> findByOwnerId(UUID ownerId) {
        return adoptionDetailsRepo.findByOwnerId(ownerId);
    }

    @NotNull
    private static Function<Animal, AdoptionDetails> toAdoptionDetails(NewOwner newOwner, LocalDateTime adoptionDate) {
        return animal -> AdoptionDetails.builder()
                .animalId(animal.getId())
                .ownerId(newOwner.getId())
                .ownerName(newOwner.getName())
                .animalName(animal.getName())
                .animalType(animal.getAnimalType())
                .adoptionDate(adoptionDate)
                .build();
    }

    public List<AdoptionDetails> findByAnimalId(UUID animalId) {
        return adoptionDetailsRepo.findByAnimalId(animalId);
    }

    public List<AdoptionDetails> adopt(UUID ownerId, List<UUID> animalIds, Optional<LocalDateTime> adoptionDate) {
        List<Animal> animals = animalRepo.findAllById(animalIds);
        Optional<NewOwner> owner = newOwnerRepo.findById(ownerId);

        validateAnimals(animalIds, animals);
        validateOwner(ownerId, owner);

        LocalDateTime actualAdoptionDate = adoptionDate.orElse(LocalDateTime.now());
        animals.forEach(a -> a.setStatus(AnimalStatus.ADOPTED));

        animalRepo.saveAll(animals);
        return Lists.newArrayList(adoptionDetailsRepo.saveAll(animals
                .stream()
                .map(toAdoptionDetails(owner.get(), actualAdoptionDate))
                .collect(Collectors.toList())));
    }

    private static void validateOwner(UUID ownerId, Optional<NewOwner> owner) {
        if (owner.isEmpty()) {
            throw new OwnerNotFoundException(ownerId);
        }
    }

    private static void validateAnimals(List<UUID> animalIds, List<Animal> animals) {
        if (animalIds.size() != animals.size()) {
            throw new AnimalNotFoundException(animalIds);
        }
        if (animals.stream().anyMatch(Animal::isAdopted)) {
            throw new AnimalAlreadyAdoptedException();
        }
    }

    public void delete(UUID animalId, UUID ownerId) {
        Animal animal = animalRepo.findById(animalId)
                .orElseThrow(() -> new AnimalNotFoundException(animalId));

        AdoptionDetails adoptionDetails = adoptionDetailsRepo.findByAnimalAndOwnerId(animalId, ownerId)
                .orElseThrow(() -> new AdoptionDetailsNotFoundException(animalId, ownerId));

        animal.setStatus(AnimalStatus.ADOPTABLE);
        animalRepo.save(animal);
        adoptionDetailsRepo.delete(adoptionDetails);
    }
}
