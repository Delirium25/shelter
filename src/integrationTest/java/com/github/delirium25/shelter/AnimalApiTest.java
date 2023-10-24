package com.github.delirium25.shelter;

import com.github.delirium25.shelter.model.AnimalStatus;
import com.github.delirium25.shelter.repository.AnimalRepo;
import io.restassured.http.ContentType;
import com.github.delirium25.shelter.model.Animal;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AnimalApiTest extends IntegrationTest {

    private static final String ANIMAL_NAME = "LunaCica";
    private static final String ANIMAL_TYPE = "macska";
    private static final LocalDateTime CATCH_DATE = LocalDateTime.of(2023, 1, 1, 11, 00, 00);
    public static final String CATCH_DATE_STRING = "2023-01-01T11:00:00";
    private static final UUID ANIMAL_ID = UUID.fromString("beda0d89-0f81-4104-b349-4a35b349e088");

    @Autowired
    private AnimalRepo animalRepo;

    @Test
    public void createAnimal() {
        //given
        Response createResponse = with().body(Animal.builder()
                        .name(ANIMAL_NAME)
                        .animalType(ANIMAL_TYPE)
                        .catchDate(CATCH_DATE)
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/animals");

        //when //then
        createResponse
                .then()
                .statusCode(201);
        String location = createResponse.getHeader("location");
        String id = location.split("/")[2];

        verifyAnimalInDb(id);
    }

    @Test
    public void getAnimalWhenAnimalIsNotFound() {
        //given//when//then
        with()
                .when()
                .request("GET", "/animals/" + ANIMAL_ID)
                .then()
                .statusCode(404);
    }

    @Test
    public void getAnimal() {
        //given
        UUID id = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build()).getId();

        //when
        ValidatableResponse response = with()
                .when()
                .request("GET", "/animals/" + id)
                .then();

        //then
        response
                .statusCode(200).assertThat()
                .body("id", equalTo(id.toString()))
                .body("name", equalTo(ANIMAL_NAME))
                .body("animalType", equalTo(ANIMAL_TYPE))
                .body("catchDate", equalTo(CATCH_DATE_STRING));
    }


    @Test
    public void getAnimalsWhenAdoptable() {
        //given
        UUID adoptableAnimalId = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .status(AnimalStatus.ADOPTABLE)
                .build()).getId();

        UUID notAdoptableAnimalId = animalRepo.save(Animal.builder()
                .name("Not adoptable animal")
                .animalType(ANIMAL_TYPE)
                .catchDate(LocalDateTime.now())
                .status(AnimalStatus.ADOPTED)
                .build()).getId();

        //when
        ValidatableResponse response = with()
                .when()
                .request("GET", "/animals?adoptable=true")
                .then();

        //then
        response
                .statusCode(200)
                .assertThat()
                .body("size()", is(1))
                .body("[0].name", equalTo(ANIMAL_NAME))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].catchDate", equalTo(CATCH_DATE_STRING))
                .body("[0].status", equalTo(AnimalStatus.ADOPTABLE.toString()))
                .body("[0].id", equalTo(adoptableAnimalId.toString()));
    }


    @Test
    public void getAnimalsWhenNotAdoptable() {
        //given
        animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .status(AnimalStatus.ADOPTABLE)
                .build());

        Animal notAdoptableAnimal = animalRepo.save(Animal.builder()
                .name("Not adoptable animal")
                .animalType(ANIMAL_TYPE)
                .catchDate(LocalDateTime.of(2023, 9, 20, 15, 58))
                .status(AnimalStatus.ADOPTED)
                .build());

        //when
        ValidatableResponse response = with()
                .when()
                .request("GET", "/animals?adoptable=false")
                .then();

        //then
        response
                .statusCode(200)
                .assertThat()
                .body("size()", is(1))
                .body("[0].name", equalTo("Not adoptable animal"))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].catchDate", equalTo((notAdoptableAnimal.getCatchDate().format(DateTimeFormatter.ISO_DATE_TIME))))
                .body("[0].status", equalTo(AnimalStatus.ADOPTED.toString()))
                .body("[0].id", equalTo(notAdoptableAnimal.getId().toString()));
    }

    @Test
    public void getAnimalsWhenNotFilteredByAdoptionState() {
        //given
        Animal adoptableAnimal = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .status(AnimalStatus.ADOPTABLE)
                .build());

        Animal notAdoptableAnimal = animalRepo.save(Animal.builder()
                .name("Not adoptable animal")
                .animalType(ANIMAL_TYPE)
                .catchDate(LocalDateTime.of(2023, 9, 20, 15, 58))
                .status(AnimalStatus.ADOPTED)
                .build());

        //when
        ValidatableResponse response = with()
                .when()
                .request("GET", "/animals")
                .then();

        //then
        response
                .statusCode(200)
                .assertThat()
                .body("size()", is(2))
                .body("[1].name", equalTo("Not adoptable animal"))
                .body("[1].animalType", equalTo(ANIMAL_TYPE))
                .body("[1].catchDate", equalTo(notAdoptableAnimal.getCatchDate().format(DateTimeFormatter.ISO_DATE_TIME)))
                .body("[1].status", equalTo(AnimalStatus.ADOPTED.toString()))
                .body("[1].id", equalTo(notAdoptableAnimal.getId().toString()))
                .body("[0].name", equalTo(ANIMAL_NAME))
                .body("[0].animalType", equalTo(ANIMAL_TYPE))
                .body("[0].catchDate", equalTo(CATCH_DATE_STRING))
                .body("[0].status", equalTo(AnimalStatus.ADOPTABLE.toString()))
                .body("[0].id", equalTo(adoptableAnimal.getId().toString()));
    }

    @Test
    public void deleteAnimalWhenAnimalDoesNotExist() {
        //when//then
        with()
                .when()
                .request("DELETE", "/animals/" + ANIMAL_ID)
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteAnimal() {
        //given
        UUID adoptableAnimalId = animalRepo.save(Animal.builder()
                .name(ANIMAL_NAME)
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .build()).getId();

        Response response = with()
                .when()
                .request("DELETE", "/animals/" + adoptableAnimalId);

        //then
        response
                .then()
                .statusCode(204);
    }

    @Test
    public void updateAnimalWhenAnimalIsNotFound() {
        with()
                .when()
                .request("PUT", "/animal/" + ANIMAL_ID)
                .then()
                .statusCode(404);
    }

    @Test
    public void updateAnimal() {
        //given
        UUID animalId = UUID.randomUUID();
        Animal animal = Animal.builder()
                .name("OLD NAME")
                .animalType(ANIMAL_TYPE)
                .catchDate(CATCH_DATE)
                .id(animalId)
                .build();
        animalRepo.save(animal);

        animal.setName(ANIMAL_NAME);
        Response response = with()
                .body(animal)
                .contentType(ContentType.JSON)
                .when()
                .request("PUT", "/animals/" + animalId);

        //when
        response
                .then()
                .statusCode(200);

        //then
        verifyAnimalInDb(animalId.toString());
    }

    private void verifyAnimalInDb(String id) {
        Optional<Animal> animal = animalRepo.findById(UUID.fromString(id));

        assertTrue(animal.isPresent());
        assertEquals(ANIMAL_NAME, animal.get().getName());
        assertEquals(ANIMAL_TYPE, animal.get().getAnimalType());
        assertEquals(CATCH_DATE, animal.get().getCatchDate());
    }
}
