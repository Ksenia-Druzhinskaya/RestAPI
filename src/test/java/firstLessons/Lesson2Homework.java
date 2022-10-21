package firstLessons;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.*;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Lesson2Homework
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

    @Test
    public void testTokens() throws InterruptedException
    {
        String methodPath = "https://playground.learnqa.ru/ajax/api/longtime_job";

        Response response = RestAssured
                .given()
                .get(methodPath)
                .andReturn();

        int statusCode = response.getStatusCode();
        if(statusCode != 200){
            System.out.println("Error. Status code of the first request is " + statusCode);
            return;
        }
        System.out.println("Success. Status code of the first request is " + statusCode);

        JsonPath jsonPath = response.jsonPath();
        Integer timeout = jsonPath.get("seconds");
        SECONDS.sleep(timeout);

        Map<String, String> params = new HashMap<>();
        params.put("token", jsonPath.get("token"));

        jsonPath = RestAssured
                .given()
                .queryParams(params)
                .get(methodPath)
                .jsonPath();

        String status = jsonPath.get("status");
        if(!status.equals("Job is ready")){
            System.out.println("Error. Status is '" + status + "'.");
        }else{
            System.out.println("Success. Status is '" + status + "'.");
        }

        String result = jsonPath.get("result");
        if(result == null){
            System.out.println("Error. Result is empty");
        }else{
            System.out.println("Success. Result is '" + result + "'.");
        }
    }
}


