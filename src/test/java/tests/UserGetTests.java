package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTests extends BaseTestCase
{
    @Test
    @Description("This test verifies that not authorized user cannot see all user fields")
    @Features({@Feature("Get user")})
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDataNotAuth(){

        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/2");

        responseUserData.prettyPrint();
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Test
    @Description("This test verifies that authorized user can see all user fields")
    @Features({@Feature("Get user")})
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDetailsAuthAsSameUser(){

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData);

        responseGetAuth.prettyPrint();
        String token = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/2",
                token,
                cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Description("This test verifies that authorized user cannot see all fields of another user")
    @Features({@Feature("Get user")})
    @Severity(SeverityLevel.NORMAL)
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
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        String token = this.getHeader(responseGetAuth, "x-csrf-token");

        // Get data of the first user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie);

        responseUserData.prettyPrint();
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}
