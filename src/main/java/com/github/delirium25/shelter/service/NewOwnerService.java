package com.github.delirium25.shelter.service;

import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NewOwnerService {

    private final NewOwnerRepo newOwnerRepo;

    public void delete(UUID id) {
        try {
            newOwnerRepo.deleteById(id);
        } catch (Exception e) {
            throw new OwnerNotFoundException(id);
        }
    }

    public NewOwner save(NewOwner newOwner) {
        return newOwnerRepo.save(newOwner);
    }

    public List<NewOwner> getAll() {
        return newOwnerRepo.findAll();
    }

    public NewOwner getByAnimaId(UUID animalId) {
        return newOwnerRepo.findByAdoptedAnimals(animalId)
                .orElseThrow(()-> new OwnerNotFoundException("Owner not found for animal. Animal id:" + animalId));
    }
}

