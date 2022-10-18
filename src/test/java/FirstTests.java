
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class FirstTests
{
    @Test
    public void testHello(){

        Map<String, String> params = new HashMap<>();
        params.put("name", "John");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();

        response.print();

        JsonPath jsonPath = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = jsonPath.get("answer1");

        if(name == null){
            System.out.println("The key answer1 is absent");
        }
        else {
            System.out.println(name);
        }
    }

    @Test
    public void testCheckType(){

        Map<String, Object> body = new HashMap<>();
        body.put("name1", "John");
        body.put("name2", "Jason");

        Response response = RestAssured
                .given()
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testStatusCode(){

        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        int statusCode = response.statusCode();
        System.out.println(statusCode);
    }

    @Test
    public void testHeaders(){

        Map<String, Object> headers = new HashMap<>();
        headers.put("header1", "myValue1");
        headers.put("header2", "myValue2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .post("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        // Заголовки запроса
        response.prettyPrint();
        // Заголовки ответа
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
        // Выбрали один заголовок ответа ответа
        String responseHeader = response.getHeader("Content-Type");
        System.out.println("\nContent-Type: " + responseHeader);
    }

    @Test
    public void testGetCookie(){

        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();
        // Headers
        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
        // Cookies
        System.out.println("\nCookies:");
        Map<String, String> responseCookies = response.getCookies();
        System.out.println(responseCookies);
        // "auth_cookie" cookie
        System.out.println("\nauth_cookie:");
        String responseCookie = response.getCookie("auth_cookie");
        System.out.println(responseCookie);
    }

    @Test
    public void testCheckCookie(){

        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        // "auth_cookie" cookie
        System.out.println("\nauth_cookie:");
        String responseCookie = response.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if(responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }
}
