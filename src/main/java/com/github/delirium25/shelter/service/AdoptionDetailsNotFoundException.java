package com.github.delirium25.shelter.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdoptionDetailsNotFoundException extends RuntimeException {
    public AdoptionDetailsNotFoundException(UUID animalId, UUID ownerId) {
        super("Cannot find adoption with animalId " + animalId + "and ownerId " + ownerId);
    }
}
