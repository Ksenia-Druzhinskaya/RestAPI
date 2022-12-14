package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTests extends BaseTestCase
{
    int userIdOnAuth;

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.token = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("This test successfully authorizes user by email and password")
    @DisplayName("Test positive auth user")
    @Severity(SeverityLevel.CRITICAL)
    public void testAuthUser(){

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/api/user/auth",
                        this.token,
                        this.cookie);

        Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    // Old test that is rewritten below
    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    @Description("This test verifies authorization status without sending cookie or token")
    @DisplayName("Test negative auth user")
    @Severity(SeverityLevel.CRITICAL)
    public void testNegativeAuthUserOld(String condition){

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")){
            spec.cookie("auth_sid", this.cookie);
        }else if(condition.equals("headers")){
            spec.header("x-csrf-token", this.token);
        }else{
            throw new IllegalArgumentException("Condition value is not known: " + condition);
        }

        Response responseForCheck = spec.get().andReturn();
        Assertions.assertJsonByName(responseForCheck, "user_id", 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    @Description("This test verifies authorization status without sending cookie or token")
    @DisplayName("Test negative auth user")
    @Severity(SeverityLevel.CRITICAL)
    public void testNegativeAuthUser(String condition){

        if(condition.equals("cookie")){
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.cookie
            );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        }else if(condition.equals("headers")){
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.token
            );
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is not known: " + condition);
        }
    }
}
