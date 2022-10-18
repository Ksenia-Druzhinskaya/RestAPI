
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;

public class Lesson2HomeworkTests
{
    @Test
    public void testGetJsonHomework(){

        JsonPath jsonPath = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        List<Map> messages = jsonPath.getList("messages");
        System.out.println(messages.get(1).get("message"));
    }

    @Test
    public void testLongRedirect(){

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String location = response.getHeader("Location");
        System.out.println(location);
    }

    @Test
    public void testLongRedirectCycle(){

        String location = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode, redirectCount = 0;

        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(location)
                    .andReturn();
            location = response.getHeader("Location");
            if(location != null){
                redirectCount++;
            }
            statusCode = response.getStatusCode();
        } while(statusCode != 200);

        System.out.println(redirectCount);
    }
}


