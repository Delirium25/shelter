package com.github.delirium25.shelter;

import com.github.delirium25.shelter.model.NewOwner;
import com.github.delirium25.shelter.repository.NewOwnerRepo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewOwnerApiTest extends IntegrationTest {

    private static final String NEW_OWNER_NAME = "Anakin";
    private static final String EMAIL = "anakin.skywalker@gmail.com";
    private static final UUID ADOPTED_ANIMAL_1 = UUID.randomUUID();
    private static final UUID ADOPTED_ANIMAL_2 = UUID.randomUUID();
    private static final UUID OWNER_ID = UUID.fromString("82c09c59-6a77-46a9-a854-83fdecf04221");

    private static final List<NewOwner> testOwners = List.of(NewOwner.builder()
            .name(NEW_OWNER_NAME)
            .email(EMAIL)
            .adoptedAnimals(List.of(ADOPTED_ANIMAL_1, ADOPTED_ANIMAL_2))
            .build());

    @Autowired
    private NewOwnerRepo newOwnerRepo;

    @Test
    public void createOwner() {
        //given
        Response createResponse = with()
                .body(NewOwner.builder()
                        .name(NEW_OWNER_NAME)
                        .email(EMAIL)
                        .adoptedAnimals(List.of(ADOPTED_ANIMAL_1, ADOPTED_ANIMAL_2))
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .request("POST", "/owners");

        //when//then
        createResponse.then().statusCode(201);
        String location = createResponse.getHeader("location");
        String id = location.split("/")[2];

        verifyOwnerInDb(id);
    }

    @Test
    public void getOwners() {
        //given
        newOwnerRepo.save(NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .adoptedAnimals(List.of(ADOPTED_ANIMAL_1, ADOPTED_ANIMAL_2))
                .id(OWNER_ID)
                .build());

        //when
        ValidatableResponse response = with()
                .when()
                .request("GET", "/owners")
                .then();

        //then
        response
                .statusCode(200)
                .assertThat()
                .body("[0].name", equalTo(NEW_OWNER_NAME));
    }

    @Test
    public void getOwnersWhenOwnersAreNotFound() {
        with()
                .when()
                .request("GET", "/owners/")
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteOwner() {
        //given
        NewOwner owner = NewOwner.builder()
                .name(NEW_OWNER_NAME)
                .email(EMAIL)
                .adoptedAnimals(List.of(ADOPTED_ANIMAL_1))
                .build();
        UUID id = newOwnerRepo.save(owner).getId();

        Response response = with()
                .when()
                .request("DELETE", "/owners/" + id);

        //then
        response
                .then()
                .statusCode(204);
    }

    @Test
    public void deleteOwnerWhenOwnerDoesNotExist() {
        with()
                .when()
                .request("DELETE", "/owners/" + OWNER_ID)
                .then()
                .statusCode(404);
    }

    private void verifyOwnerInDb(String id) {
        Optional<NewOwner> owner = newOwnerRepo.findById(UUID.fromString(id));

        assertTrue(owner.isPresent());
        assertEquals(NEW_OWNER_NAME, owner.get().getName());
        assertEquals(EMAIL, owner.get().getEmail());
        assertEquals(List.of(ADOPTED_ANIMAL_1, ADOPTED_ANIMAL_2), owner.get().getAdoptedAnimals());
    }
}
