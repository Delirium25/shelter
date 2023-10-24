package com.github.delirium25.shelter.service;

import com.github.delirium25.shelter.model.AdoptionDetails;
import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.AdoptionDetailsRepo;
import com.github.delirium25.shelter.repository.AnimalRepo;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AdoptionServiceTest {

    @Mock
    private NewOwnerRepo newOwnerRepo;
    @Mock
    private AnimalRepo animalRepo;
    @Mock
    private AdoptionDetailsRepo adoptionRepo;
    @InjectMocks
    private AdoptionService adoptionService;

    @Test
    void findByOwnerId() {
        //given
        UUID ownerId = UUID.randomUUID();
        List<AdoptionDetails> adoptionDetails = List.of(new AdoptionDetails());
        when(adoptionRepo.findByOwnerId(ownerId)).thenReturn(adoptionDetails);
        //when
        List<AdoptionDetails> result = adoptionService.findByOwnerId(ownerId);
        //then
        assertEquals(adoptionDetails, result);
    }


    @Test
    void findByAnimalId() {
        //given
        UUID animalId = UUID.randomUUID();
        List<AdoptionDetails> adoptionDetails = List.of(new AdoptionDetails());
        when(adoptionRepo.findByAnimalId(animalId)).thenReturn(adoptionDetails);
        //when
        List<AdoptionDetails> result = adoptionService.findByAnimalId(animalId);
        //then
        assertEquals(adoptionDetails, result);
    }
}