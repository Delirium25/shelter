package com.github.delirium25.shelter.controller;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CreateAdoptionRequest {

    private UUID ownerId;
    private List<UUID> animalIds;
    private LocalDateTime adoptionDate;
}
