package firstLessons;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static lib.Assertions.assertStringLength;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testGetHeaders(){
        Response response = RestAssured
                .given()
                .post("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = response.getHeaders();
        assertTrue(headers.size() > 0, "Headers is empty");

        headers.forEach(h -> {
            assertFalse(h.getName().isEmpty(), "Header name is empty.");
            assertFalse(h.getValue().isEmpty(), "Header value is empty.");
            System.out.println("Header name is '" + h.getName() + "', header value is '" + h.getValue() + "'");
        });
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void testCheckUserAgent(String userAgent, String expectedPlatform, String expectedBrowser, String expectedDevice){

        JsonPath response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();

        String actualPlatform = response.get("platform");
        assertEquals(expectedPlatform, actualPlatform, "Incorrect platform for User Agent '" + userAgent + "'");
        String actualBrowser = response.get("browser");
        assertEquals(expectedBrowser, actualBrowser, "Incorrect browser for User Agent '" + userAgent + "'");
        String actualDevice = response.get("device");
        assertEquals(expectedDevice, actualDevice, "Incorrect device for User Agent '" + userAgent + "'");
    }

    private static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile", "No", "Android"),
                Arguments.of("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile", "Chrome", "iOS"),
                Arguments.of("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot", "Unknown", "Unknown"),
                Arguments.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web", "Chrome", "No"),
                Arguments.of("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile", "No", "iPhone")
        );
    }
}


