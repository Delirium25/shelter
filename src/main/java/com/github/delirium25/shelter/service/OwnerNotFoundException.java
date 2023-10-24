package com.github.delirium25.shelter.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(UUID ownerId) {
        this("Cannot find owner with id: " + ownerId);
    }

    public OwnerNotFoundException(String message) {
        super(message);
    }
}
