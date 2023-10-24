package com.github.delirium25.shelter;

import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.AdoptionDetailsRepo;
import com.github.delirium25.shelter.repository.AnimalRepo;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static io.restassured.RestAssured.with;

public class AdminTest {

    @Autowired
    private AnimalRepo animalRepo;

    @Autowired
    private NewOwnerRepo newOwnerRepo;

    @Autowired
    private AdoptionDetailsRepo adoptionDetailsRepo;
    //OWNER
    private static final String NEW_OWNER_NAME = "Anakin";
    private static final String EMAIL = "anakin.skywalker@gmail.com";
    private static final UUID OWNER_ID = UUID.fromString("82c09c59-6a77-46a9-a854-83fdecf04221");

    //ANIMAL
    private static final String ANIMAL_NAME = "LunaCica";
    private static final String ANIMAL_TYPE = "macska";
    private static final LocalDateTime CATCH_DATE = LocalDateTime.of(2023, 1, 1, 11, 00, 00);
    public static final LocalDateTime ADOPTION_DATE = LocalDateTime.of(2023, 11, 10, 10, 00, 00);
    private static final UUID ANIMAL_ID = UUID.fromString("beda0d89-0f81-4104-b349-4a35b349e088");


    @Test
    @Disabled
    public void migration() {
        //given
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .adoptionDate(ADOPTION_DATE)
                .build());
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .adoptedAnimals(List.of(animal.getId()))
                .build());
        //when
        with()
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/generateAdoptionDocuments")
                .then()
                .statusCode(201);

        //then
        //TODO: finnish tests
    }
}
