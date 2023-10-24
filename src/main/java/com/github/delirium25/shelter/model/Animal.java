package com.github.delirium25.shelter.model;

import com.couchbase.client.core.deps.com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    private UUID id;

    @Getter
    @Field
    private String name;

    @Field
    private String animalType;

    @Field
    private LocalDateTime catchDate;

    @Field
    @Deprecated
    private LocalDateTime adoptionDate;

    @Field
    private AnimalStatus status;

    @Version
    private long version;

    @JsonIgnore
    public boolean isAdopted() {
        return AnimalStatus.ADOPTED.equals(status);
    }

    public void setName(String name) {
        this.name = name;
    }
}
