package com.github.delirium25.shelter.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionDetails {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Field
    private UUID animalId;

    @Field
    private UUID ownerId;

    @Field
    private String ownerName;

    @Field
    private String animalName;

    @Field
    private String animalType;

    @Field
    private LocalDateTime adoptionDate;

}
