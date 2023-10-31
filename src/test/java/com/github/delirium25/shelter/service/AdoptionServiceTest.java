package com.github.delirium25.shelter.service;

import com.github.delirium25.shelter.model.AdoptionDetails;
import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.AnimalStatus;
import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.AdoptionDetailsRepo;
import com.github.delirium25.shelter.repository.AnimalRepo;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
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
    @Captor
    private ArgumentCaptor<List<AdoptionDetails>> adoptionCapture;

    @Test
    void findByOwnerId() {
        //given
        UUID ownerId = UUID.randomUUID();
        List<AdoptionDetails> adoptionDetails = List.of(new AdoptionDetails());
        given(adoptionRepo.findByOwnerId(ownerId)).willReturn(adoptionDetails);
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

    @Test
    void findAdoptableAnimalsWhenOwnerNotFound() {
        //given
        UUID animalId = UUID.randomUUID();
        Animal animal = Animal.builder()
                .status(AnimalStatus.ADOPTABLE)
                .build();
        UUID ownerId = UUID.randomUUID();
        when(newOwnerRepo.findById(ownerId)).thenReturn(Optional.empty());
        when(animalRepo.findAllById(List.of(animalId))).thenReturn(List.of(animal));
        //when//then
        assertThrows(OwnerNotFoundException.class,
                () -> adoptionService.adopt(ownerId, List.of(animalId), Optional.empty()));
    }

    @Test
    void findAdoptableAnimalsWhenAnimalNotFound() {
        NewOwner owner = NewOwner.builder()
                .build();
        UUID ownerId = UUID.randomUUID();
        UUID animalId = UUID.randomUUID();
        when(newOwnerRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(animalRepo.findAllById(List.of(animalId))).thenReturn(Collections.emptyList());
        assertThrows(AnimalNotFoundException.class,
                () -> adoptionService.adopt(ownerId, List.of(animalId), Optional.empty()));
    }

    @Test
    void findAdoptableAnimalWhenAnimalAlreadyAdopted() {
        NewOwner owner = NewOwner.builder().build();
        Animal animal = Animal.builder()
                .status(AnimalStatus.ADOPTED)
                .build();
        UUID ownerId = UUID.randomUUID();
        UUID animalId = UUID.randomUUID();
        when(newOwnerRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(animalRepo.findAllById(List.of(animalId))).thenReturn(List.of(animal));
        assertThrows(AnimalAlreadyAdoptedException.class,
                () -> adoptionService.adopt(ownerId, List.of(animalId), Optional.empty()));
    }

    @Test
    void findAdoptableAnimalsWhenEverythingOk() {
        UUID ownerId = UUID.randomUUID();
        UUID animalId = UUID.randomUUID();
        NewOwner owner = NewOwner.builder()
                .id(ownerId)
                .name("Kira")
                .build();
        Animal animal = Animal.builder()
                .id(animalId)
                .name("Crookshanks")
                .animalType("macska")
                .status(AnimalStatus.ADOPTABLE)
                .build();
        AdoptionDetails adoptionDetails = AdoptionDetails.builder()
                .animalId(animalId)
                .ownerId(ownerId)
                .ownerName("Kira")
                .animalName("Crookshanks")
                .animalType("macska")
                .adoptionDate(LocalDateTime.now())
                .build();
        when(newOwnerRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(animalRepo.findAllById(List.of(animalId))).thenReturn(List.of(animal));
        when(adoptionRepo.saveAll(any())).thenReturn(List.of(adoptionDetails));

        List<AdoptionDetails> result = adoptionService.adopt(ownerId, List.of(animalId), Optional.empty());

        assertEquals(List.of(adoptionDetails), result);
        verify(animalRepo).saveAll(List.of(animal));
        verify(adoptionRepo).saveAll(adoptionCapture.capture());
        AdoptionDetails actualAdoption = adoptionCapture.getValue().get(0);
        assertEquals(animalId, actualAdoption.getAnimalId());
        assertEquals(ownerId, actualAdoption.getOwnerId());
        assertEquals(owner.getName(), actualAdoption.getOwnerName());
        assertEquals(animal.getName(), actualAdoption.getAnimalName());
        assertEquals(animal.getAnimalType(), actualAdoption.getAnimalType());
        assertEquals(actualAdoption.getAdoptionDate(), actualAdoption.getAdoptionDate());
    }
}