package com.github.delirium25.shelter.controller;


import com.github.delirium25.shelter.model.AdoptionDetails;
import com.github.delirium25.shelter.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/adoption")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<AdoptionDetails> generateAdoption(@RequestBody CreateAdoptionRequest createAdoptionRequest) {
        return adoptionService.adopt(
                createAdoptionRequest.getOwnerId(),
                createAdoptionRequest.getAnimalIds(),
                Optional.ofNullable(createAdoptionRequest.getAdoptionDate()));
    }

    @GetMapping
    public List<AdoptionDetails> getAdoptionDetails(@RequestParam Optional<UUID> animalId,
                                                    @RequestParam Optional<UUID> ownerId) {
        return ownerId.map(id -> adoptionService.findByOwnerId(id))
                .or(() -> animalId.map(id -> adoptionService.findByAnimalId(id)))
                .orElseThrow(() -> new IllegalArgumentException("The 'animalId' or the 'ownerId' must be present!"));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdoption(@RequestBody DeleteAdoptionRequest deleteAdoptionRequest) {
        adoptionService.delete(deleteAdoptionRequest.getAnimalId(), deleteAdoptionRequest.getOwnerId());
    }
}
