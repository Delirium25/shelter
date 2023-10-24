package com.github.delirium25.shelter.repository;

import com.github.delirium25.shelter.model.AdoptionDetails;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdoptionDetailsRepo extends CouchbaseRepository<AdoptionDetails, UUID> {

    @Query("#{#n1ql.selectEntity} WHERE ownerId = $1")
    List<AdoptionDetails> findByOwnerId(UUID ownerId);

    @Query("#{#n1ql.selectEntity} WHERE animalId = $1")
    List<AdoptionDetails> findByAnimalId(UUID animalId);

    @Query("#{#n1ql.selectEntity} WHERE animalId = $1 AND ownerId = $2")
    Optional<AdoptionDetails> findByAnimalAndOwnerId(UUID animalId, UUID ownerId);
}
