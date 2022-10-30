package lib;

import io.qameta.allure.Step;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase
{
    protected final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    protected String cookie;
    protected String token;
    protected String userId;

    protected String getHeader(Response response, String name){
        Headers headers = response.getHeaders();

        assertTrue(headers.hasHeaderWithName(name), "Response does not have header with name " + name);
        return headers.getValue(name);
    }

    protected String getCookie(Response response, String name){
        Map<String, String> cookies = response.getCookies();

        assertTrue(cookies.containsKey(name), "Response does not have cookie with name " + name);
        return cookies.get(name);
    }

    protected int getIntFromJson(Response response, String name){
        response.then().assertThat().body("$", hasKey(name));
        return response.jsonPath().getInt(name);
    }

    @Step("Create a user, login as the created user")
    protected Map<String, String> createUserAndLogin()
    {
        // Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath jsonPathCreateAuth = apiCoreRequests
                .getJsonPathForPostRequest("https://playground.learnqa.ru/api/user/", userData);

        // Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.token = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.userId = jsonPathCreateAuth.getString("id");

        // Return user data
        return userData;
    }
}
