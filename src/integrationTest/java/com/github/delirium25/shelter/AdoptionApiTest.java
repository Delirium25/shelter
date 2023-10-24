package com.github.delirium25.shelter;

import com.github.delirium25.shelter.controller.CreateAdoptionRequest;
import com.github.delirium25.shelter.controller.DeleteAdoptionRequest;
import com.github.delirium25.shelter.model.AdoptionDetails;
import com.github.delirium25.shelter.model.Animal;
import com.github.delirium25.shelter.model.AnimalStatus;
import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.AdoptionDetailsRepo;
import com.github.delirium25.shelter.repository.AnimalRepo;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AdoptionApiTest extends IntegrationTest {

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

    @Autowired
    private AnimalRepo animalRepo;

    @Autowired
    private NewOwnerRepo newOwnerRepo;

    @Autowired
    private AdoptionDetailsRepo adoptionDetailsRepo;

    //TESTS RELATED TO CREATE - POST
    @Test
    public void createAdoption() {
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build());
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        with().body(CreateAdoptionRequest.builder()
                        .animalIds(List.of(animal.getId()))
                        .ownerId(owner.getId())
                        .adoptionDate(ADOPTION_DATE)
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/adoption")
                .then()
                .statusCode(201)
                .body("size()", is(1))
                .body("[0].animalId", equalTo(animal.getId().toString()))
                .body("[0].ownerId", equalTo(owner.getId().toString()))
                .body("[0].ownerName", equalTo(NEW_OWNER_NAME))
                .body("[0].animalName", equalTo(ANIMAL_NAME))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].adoptionDate", equalTo(ADOPTION_DATE.format(DateTimeFormatter.ISO_DATE_TIME)));

        Response response = with()
                .contentType(ContentType.JSON)
                .when()
                .request("GET", "/adoption?animalId=" + animal.getId());

        response.then().statusCode(200)
                .body("size()", is(1))
                .body("[0].animalId", equalTo(animal.getId().toString()))
                .body("[0].ownerId", equalTo(owner.getId().toString()))
                .body("[0].ownerName", equalTo(NEW_OWNER_NAME))
                .body("[0].animalName", equalTo(ANIMAL_NAME))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].adoptionDate", equalTo(ADOPTION_DATE.format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    public void createAdoptionWhenOwnerDoesNotExist() {
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build());

        Response createResponse = with().body(CreateAdoptionRequest.builder()
                        .animalIds(List.of(animal.getId()))
                        .ownerId(UUID.randomUUID())
                        .adoptionDate(ADOPTION_DATE)
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/adoption");

        createResponse.then().statusCode(404);
    }

    @Test
    public void createAdoptionWhenAnimalDoesNotExist() {
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        Response createResponse = with().body(CreateAdoptionRequest.builder()
                        .animalIds(List.of(UUID.randomUUID()))
                        .ownerId(owner.getId())
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/adoption");

        createResponse.then().statusCode(404);
    }

    @Test
    public void createAdoptionWhenAnimalAlreadyAdopted() {
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .status(AnimalStatus.ADOPTED)
                .build());
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        Response createResponse = with().body(CreateAdoptionRequest.builder()
                        .animalIds(List.of(animal.getId()))
                        .ownerId(owner.getId())
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/adoption");

        createResponse.then().statusCode(400);
    }


    //TEST RELATED TO GET ADOPTIONDETAILS THROUH THE ANIMAL OR THE OWNER ID
    @Test
    public void getAdoptionDetailsThroughOwner() {
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build());
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        AdoptionDetails adoption = AdoptionDetails.builder()
                .animalId(animal.getId())
                .ownerId(owner.getId())
                .ownerName(owner.getName())
                .animalName(animal.getName())
                .animalType(animal.getAnimalType())
                .adoptionDate(ADOPTION_DATE)
                .build();

        adoptionDetailsRepo.save(adoption);

        Response createResponse = with()
                .when()
                .request("GET", "/adoption?ownerId=" + owner.getId());

        createResponse.then().statusCode(200)
                .body("size()", is(1))
                .body("[0].animalId", equalTo(animal.getId().toString()))
                .body("[0].ownerId", equalTo(owner.getId().toString()))
                .body("[0].ownerName", equalTo(NEW_OWNER_NAME))
                .body("[0].animalName", equalTo(ANIMAL_NAME))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].adoptionDate", equalTo(ADOPTION_DATE.format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    public void getAdoptionDetailsThroughAnimal() {
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build());
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        AdoptionDetails adoption = AdoptionDetails.builder()
                .animalId(animal.getId())
                .ownerId(owner.getId())
                .ownerName(owner.getName())
                .animalName(animal.getName())
                .animalType(animal.getAnimalType())
                .adoptionDate(ADOPTION_DATE)
                .build();

        adoptionDetailsRepo.save(adoption);

        Response createResponse = with()
                .when()
                .request("GET", "/adoption?animalId=" + animal.getId());

        createResponse.then().statusCode(200)
                .body("size()", is(1))
                .body("[0].animalId", equalTo(animal.getId().toString()))
                .body("[0].ownerId", equalTo(owner.getId().toString()))
                .body("[0].ownerName", equalTo(NEW_OWNER_NAME))
                .body("[0].animalName", equalTo(ANIMAL_NAME))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].adoptionDate", equalTo(ADOPTION_DATE.format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    public void deleteAdoptionThroughOwner() {
        Animal animal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build());
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        AdoptionDetails adoption = adoptionDetailsRepo.save(AdoptionDetails.builder()
                .animalId(animal.getId())
                .ownerId(owner.getId())
                .ownerName(owner.getName())
                .animalName(animal.getName())
                .animalType(animal.getAnimalType())
                .adoptionDate(ADOPTION_DATE)
                .build());

        Response createResponse = with().body(DeleteAdoptionRequest.builder()
                        .animalId(animal.getId())
                        .ownerId(owner.getId())
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("DELETE", "/adoption");

        createResponse.then().statusCode(204);

        assertFalse(adoptionDetailsRepo.existsById(adoption.getId()));
        assertEquals(AnimalStatus.ADOPTABLE,animalRepo.findById(animal.getId()).get().getStatus());
    }

    @Test
    public void deleteAdoptionWhenAnimalNotFound() {
        NewOwner owner = newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .build());

        Response createResponse = with().body(DeleteAdoptionRequest.builder()
                        .animalId(UUID.randomUUID())
                        .ownerId(owner.getId())
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("DELETE", "/adoption");

        createResponse.then().statusCode(404);
    }
}
