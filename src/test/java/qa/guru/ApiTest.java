package qa.guru;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApiTest extends TestBase{

    @Test
    void updatePutTest() {
        Instant timestamp = Instant.now();
        timestamp = timestamp.minusSeconds(5);

        String updateData = "{\"name\":\"morpheus\",\"job\":\"zion resident\"}";

        Response response = given()
                .body(updateData)
                .contentType(JSON)
                .when()
                .put("/api/users/2")
                .then()
                .extract()
                .response();

        Instant reqInstant = Instant.parse(response.path("updatedAt"));
        System.out.println(timestamp.toString());
        System.out.println(reqInstant.toString());
        System.out.println(timestamp.isBefore(reqInstant));

        assertTrue(timestamp.isBefore(reqInstant));



    }

    @Test
    void registerUnsuccessfulTest() {
        String registerData = "{\"email\":\"sydney@fife\"}";

        given()
                .body(registerData)
                .contentType(JSON)
                .when()
                .post("/api/register")
                .then()
                .statusCode(400)
                .body("error", is("Missing password"));

    }

    @Test
    void registerSuccessfulTest() {
        String registerData = "{\"email\": \"eve.holt@reqres.in\",\"password\": \"pistol\"}";

        given()
                .body(registerData)
                .contentType(JSON)
                .when()
                .post("/api/register")
                .then()
                .statusCode(200)
                .body("id", greaterThan(0))
                .body("token.length()", greaterThan(0));
    }

    @Test
    void listResourceUnknownTest() {

        Response response = get("/api/unknown")
                .then()
                .extract()
                .response();

        int resPage = response.path("per_page");

        ArrayList<Integer> idList = new ArrayList<>(response.path("data.id"));

        assertEquals(resPage, idList.size());
    }


    @Test
    void singleUserTest() {
        given()
                .when()
                .get("/api/users/2")
                .then()
                .body("data.id", is(2))
                .body("data.email", is("janet.weaver@reqres.in"))
                .body("data.first_name", is("Janet"))
                .body("data.last_name", is("Weaver"));
    }


}
