package com.github.delirium25.shelter.service;

import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.AnimalStatus;
import com.github.delirium25.shelter.repository.AnimalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AnimalService {

    @Autowired
    private AnimalRepo animalRepo;

    public Animal save(Animal animal) {
        if (animal.getStatus() == null) {
            animal.setStatus(AnimalStatus.ADOPTABLE);
        }
        return animalRepo.save(animal);
    }

    public Animal update(Animal animal) {
        if (animalRepo.existsById(animal.getId())) {
            return animalRepo.save(animal);
        } else {
            throw new AnimalNotFoundException(animal.getId());
        }
    }

    public void delete(UUID animalId) {
        try {
            animalRepo.deleteById(animalId);
        } catch (Exception e) {
            throw new AnimalNotFoundException(animalId);
        }
    }

    public List<Animal> getAll() {
        return animalRepo.findAllOrderedByName();
    }

    public Animal getAnimal(UUID animalId) {
        return animalRepo.findById(animalId).orElseThrow(() -> new AnimalNotFoundException(animalId));
    }

    public List<Animal> getAdoptable() {
        return animalRepo.findAll().stream()
                .filter(animal -> !animal.isAdopted())
                .collect(Collectors.toList());
    }

    public List<Animal> getAdopted() {
        return animalRepo.findAll().stream()
                .filter(Animal::isAdopted)
                .collect(Collectors.toList());
    }
}
