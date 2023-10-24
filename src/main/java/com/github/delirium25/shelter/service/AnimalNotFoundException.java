package com.github.delirium25.shelter.service;

import com.github.delirium25.shelter.model.Animal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AnimalNotFoundException extends RuntimeException {

    public AnimalNotFoundException(UUID animalId) {
        super("Cannot find animal with id: " + animalId);
    }

    public AnimalNotFoundException(List<UUID> animalIds) {
        super("Cannot find animals with id: " + animalIds);
    }
}
