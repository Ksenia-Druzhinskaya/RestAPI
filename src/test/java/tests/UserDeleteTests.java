package tests;

import io.qameta.allure.Description;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTests extends BaseTestCase
{
    @Test
    @Description("This test deletes just created user")
    public void testDeleteJustCreatedUser(){

        // Create user and login
        Map<String, String> userData = this.createUserAndLogin();

        // Delete user
        apiCoreRequests.makeDeleteRequest(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        this.token,
                        this.cookie);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        this.token,
                        this.cookie);

        // Verify that user is not found
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Description("This test verifies that user cannot be deleted by another user")
    public void testDeleteJustCreatedUserByAnotherUser(){

        // Generate the first user and get user id
        Map<String, String> firstUserData = DataGenerator.getRegistrationData();
        JsonPath jsonPathCreateAuth = apiCoreRequests
                .getJsonPathForPostRequest("https://playground.learnqa.ru/api/user/", firstUserData);
        String firstUserId = jsonPathCreateAuth.getString("id");

        // Create the second user and login as the second user
        this.createUserAndLogin();

        // Try to delete the first user user
        apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + firstUserId,
                this.token,
                this.cookie);

        // Get the first user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + firstUserId,
                        this.token,
                        this.cookie);

        // Verify that the first user is not deleted
        Assertions.assertJsonByName(responseUserData, "username", firstUserData.get("username"));
    }

    @Test
    @Description("This test verifies that user with ID 2 cannot be deleted")
    public void testUniqueUserCannotBeDeleted(){

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // Login as vinkotov@example.com
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.token = this.getHeader(responseGetAuth, "x-csrf-token");
        int userId = this.getIntFromJson(responseGetAuth, "user_id");

        // Try to delete user
        apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.token,
                this.cookie);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        this.token,
                        this.cookie);

        // Verify that user is not deleted
        Assertions.assertJsonByName(responseUserData, "email", authData.get("email"));
    }
}
