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

public class UserEditTests extends BaseTestCase
{
    @Test
    @Description("This test edits just created user")
    public void testEditJustCreatedUser(){

        // Create user and login
        this.createUserAndLogin();

        // Edit user
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + this.userId,
                        editData,
                        this.token,
                        this.cookie);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + this.userId,
                        this.token,
                        this.cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Description("This test verifies that user cannot be edited without authorization")
    public void testEditJustCreatedUserWithoutAuthorization(){

        // Create user and login
        Map<String, String> userData = this.createUserAndLogin();

        // Edit user
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        editData);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie);

        // Verify that the first name is not changed
        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Description("This test verifies that user email cannot be changed to incorrect email")
    public void testEditEmailToIncorrectForJustCreatedUser(){

        // Create user and login
        Map<String, String> userData = this.createUserAndLogin();

        // Edit user
        String incorrectEmail = "email.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", incorrectEmail);
        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        editData,
                        token,
                        cookie);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie);

        // Verify that the email is not changed
        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Test
    @Description("This test verifies that user name cannot be changed to name with one letter")
    public void testEditFirstNameToIncorrectForJustCreatedUser(){

        // Create user and login
        Map<String, String> userData = this.createUserAndLogin();

        // Edit user
        String firstName = DataGenerator.getRandomLetter();
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstName);
        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                        editData,
                        token,
                        cookie);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + userId,
                        token,
                        cookie);

        // Verify that the firstName is not changed
        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Description("This test verifies that user cannot be edited by another user")
    public void testEditFirstNameForJustCreatedUserByAnotherUser(){

        // Generate the first user and get user id
        Map<String, String> firstUserData = DataGenerator.getRegistrationData();
        JsonPath jsonPathCreateAuth = apiCoreRequests
                .getJsonPathForPostRequest("https://playground.learnqa.ru/api/user/", firstUserData);
        String firstUserId = jsonPathCreateAuth.getString("id");

        // Create the second user and login
        this.createUserAndLogin();

        // Edit the first user
        String firstName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstName);
        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + firstUserId,
                        editData,
                        this.token,
                        this.cookie);

        // Get user
        Response responseUserData = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/" + firstUserId,
                        this.token,
                        this.cookie);

        // Verify that only username is available
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}
