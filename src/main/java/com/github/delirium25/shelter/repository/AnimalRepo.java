package com.github.delirium25.shelter.repository;

import com.github.delirium25.shelter.model.Animal;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnimalRepo extends CouchbaseRepository<Animal, UUID> {
    @Query("#{#n1ql.selectEntity} WHERE adoptionDate IS NULL")
    List<Animal> findAdoptableAnimals();

    @Query("#{#n1ql.selectEntity} ORDER BY name")
    List<Animal> findAllOrderedByName();
}
