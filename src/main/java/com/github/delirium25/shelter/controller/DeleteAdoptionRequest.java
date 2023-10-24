package com.github.delirium25.shelter.controller;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeleteAdoptionRequest {

    private UUID ownerId;
    private UUID animalId;
}
