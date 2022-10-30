package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTests extends BaseTestCase
{
    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth(){

        Response responseUserData = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        responseUserData.prettyPrint();
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        responseGetAuth.prettyPrint();
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    public void testGetUserDetailsAuthAsOtherUser(){

        // Create the first user and get user id
        Map<String, String> firstUserData = DataGenerator.getRegistrationData();
        JsonPath jsonPathCreateAuth = apiCoreRequests
                .getJsonPathForPostRequest("https://playground.learnqa.ru/api/user", firstUserData);
        String userId = jsonPathCreateAuth.getString("id");

        // Create the second user
        Map<String, String> secondUserData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", secondUserData);
        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);

        // Login as the second user
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", secondUserData);
        Assertions.assertResponseCodeEquals(responseGetAuth, 200);

        // Get data of the first user
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/" + userId);

        responseUserData.prettyPrint();
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}
