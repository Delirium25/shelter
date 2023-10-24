package com.github.delirium25.shelter.controller;

import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.service.NewOwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/owners")
public class NewOwnerController {

    @Autowired
    private NewOwnerService newOwnerService;

    @PostMapping
    public ResponseEntity<Object> saveNewOwner(@RequestBody NewOwner newOwner) {
        NewOwner savedOwner = newOwnerService.save(newOwner);
        return ResponseEntity
                .status(CREATED)
                .header("location", "/owners/" + savedOwner.getId().toString())
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NewOwner> getNewOwners(@RequestParam Optional<UUID> animalId) {
        return animalId.map(id -> List.of(newOwnerService.getByAnimaId(id)))
                .orElseGet(() -> newOwnerService.getAll());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteOwner(@PathVariable UUID id) {
        newOwnerService.delete(id);
        return "Animal successfully deleted!";
    }
}