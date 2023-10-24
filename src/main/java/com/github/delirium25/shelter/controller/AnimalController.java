package com.github.delirium25.shelter.controller;

import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.service.AnimalNotFoundException;
import com.github.delirium25.shelter.service.AnimalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/animals")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @PostMapping
    public ResponseEntity saveAnimal(@RequestBody Animal animal) {
        Animal savedAnimal = animalService.save(animal);
        return ResponseEntity
                .status(CREATED)
                .header("location", "/animals/" + savedAnimal.getId())
                .build();
    }

    @PutMapping("{id}")
    public ResponseEntity<String> putAnimal(@RequestBody Animal newanimal, @PathVariable UUID id) {
        try {
            newanimal.setId(id);
            animalService.update(newanimal);
            return new ResponseEntity<>("Animal successfully updated!", HttpStatus.OK);
        } catch (AnimalNotFoundException e) {
            return new ResponseEntity<>("Animal not found!", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAnimal(@PathVariable UUID id) {
        animalService.delete(id);
        return new ResponseEntity<>("Animal deleted successfully!", HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Animal> getAllAnimals(@RequestParam Optional<Boolean> adoptable) {
        return adoptable
                .map(isAdoptable -> {
                    if (isAdoptable) {
                        return animalService.getAdoptable();
                    } else {
                        return animalService.getAdopted();
                    }
                })
                .orElse(animalService.getAll());
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Animal getAnimal(@PathVariable UUID id) {
        return animalService.getAnimal(id);
    }
}