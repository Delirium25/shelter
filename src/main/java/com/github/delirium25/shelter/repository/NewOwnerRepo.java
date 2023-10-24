package com.github.delirium25.shelter.repository;

import com.github.delirium25.shelter.model.NewOwner;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface NewOwnerRepo extends CouchbaseRepository<NewOwner, UUID> {

    @Query("#{#n1ql.selectEntity} WHERE ANY al IN adoptedAnimals SATISFIES al = $1 END")
    Optional<NewOwner> findByAdoptedAnimals(UUID animalId);

}
