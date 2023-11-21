package com.example.findaseat;

public class TestUtils {
    public static boolean isRunningTest() {
        try {
            Class.forName("androidx.test.espresso.Espresso");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
