package com.github.delirium25.shelter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.List;
import java.util.UUID;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewOwner {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private String name;

    @Field
    private String email;

    @Field
    @Deprecated
    private List<UUID> adoptedAnimals;

    @JsonIgnore
    @Deprecated
    public void addAdoptedAnimal(List<UUID> animalId) {
        adoptedAnimals.addAll(animalId);
    }

    @JsonIgnore
    @Deprecated
    public void removeAdoptedAnimal(UUID animalId) {
        adoptedAnimals.remove(animalId);
    }
}
