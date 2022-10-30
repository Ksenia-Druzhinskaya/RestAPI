package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTests extends BaseTestCase
{
    @Test
    @Description("This test verifies that user with existing email cannot be created")
    @Features({@Feature("Create user")})
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    @Description("This test verifies that user is created successfully")
    @Features({@Feature("Create user")})
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateUserSuccessfully(){

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Description("This test verifies that user with incorrect email format cannot be created")
    @Features({@Feature("Create user")})
    @Severity(SeverityLevel.MINOR)
    public void testCreateUserWithIncorrectEmail(){
        String email = "test_example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Test
    @Description("This test verifies that user with one letter first name cannot be created")
    @Features({@Feature("Create user")})
    @Severity(SeverityLevel.MINOR)
    public void testCreateUserWithShortName(){
        String username = DataGenerator.getRandomLetter();

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @ParameterizedTest
    @ValueSource(ints = {251, 1000000})
    @Description("This test verifies that user with long name cannot be created")
    @Features({@Feature("Create user")})
    @Severity(SeverityLevel.MINOR)
    public void testCreateUserWithLongName(int userNameLength){
        String username = DataGenerator.getRandomString(userNameLength);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Description("This test verifies that user without one field cannot be created")
    @Features({@Feature("Create user")})
    @Severity(SeverityLevel.NORMAL)
    public void testCreateUserWithoutField(String userField){
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(userField);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + userField);
    }
}
