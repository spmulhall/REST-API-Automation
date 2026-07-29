package com.dhmtesting.tests;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrameworkSmokeTest {

    @Test
    void shouldRunAJUniTest(){
        int actualResult = 2 + 2;

        assertEquals(4, actualResult);
    }
}
