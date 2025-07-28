
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class APITests {

    static int itemId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:3001";
    }

    @Test @Order(1)
    public void testLoginValid() {
        given().contentType("application/json")
                .body("{ \"username\": \"test\", \"password\": \"test123\" }")
        .when().post("/login")
        .then().statusCode(200);
    }

    @Test @Order(2)
    public void testLoginInvalid() {
        given().contentType("application/json")
                .body("{ \"username\": \"x\", \"password\": \"x\" }")
        .when().post("/login")
        .then().statusCode(401);
    }

    @Test @Order(3)
    public void createItem() {
        Response res = given().contentType("application/json")
                .body("{ \"name\": \"API Item\" }")
        .when().post("/items")
        .then().statusCode(201).extract().response();

        itemId = res.jsonPath().getInt("id");
    }

    @Test @Order(4)
    public void getItems() {
        when().get("/items").then().statusCode(200)
                .body("name", hasItem("API Item"));
    }

    @Test @Order(5)
    public void updateItem() {
        given().contentType("application/json")
                .body("{ \"name\": \"Updated API Item\" }")
        .when().put("/items/" + itemId)
        .then().statusCode(200).body("name", equalTo("Updated API Item"));
    }

    @Test @Order(6)
    public void deleteItem() {
        when().delete("/items/" + itemId)
        .then().statusCode(204);
    }
}
