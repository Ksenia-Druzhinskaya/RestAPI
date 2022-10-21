package firstLessons;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static lib.Assertions.assertStringLength;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Lesson3Homework
{
    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Жил был у бабушки серенький козлик"})
    public void testStringLength(String string){
        assertStringLength(string, 15);
    }

    @Test
    public void testGetCookie(){
        Response response = RestAssured
                .given()
                .post("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookies = response.getCookies();
        assertTrue(cookies.size() > 0, "Cookie list is empty");

        cookies.forEach((k,v) -> {
            assertFalse(k.isEmpty(), "Cookie name is empty.");
            assertFalse(v.isEmpty(), "Cookie value is empty.");
            System.out.println("Cookie name is '" + k + "', cookie value is '" + v + "'");
        });
    }


}