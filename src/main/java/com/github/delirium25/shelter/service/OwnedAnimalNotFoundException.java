package com.github.delirium25.shelter.service;

import java.util.List;
import java.util.UUID;

public class OwnedAnimalNotFoundException extends RuntimeException {

    public OwnedAnimalNotFoundException(List<UUID> animalIds) {
        super("Cannot find owned animals: " + animalIds);
    }
}
