
import io.restassured.RestAssured;
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
}


