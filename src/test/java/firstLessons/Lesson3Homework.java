package firstLessons;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static lib.Assertions.assertStringLength;

public class Lesson3Homework
{
    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Жил был у бабушки серенький козлик"})
    public void testStringLength(String string){
        assertStringLength(string, 15);
    }
}
