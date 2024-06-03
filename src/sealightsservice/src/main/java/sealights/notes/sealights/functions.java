package sealights.notes.sealights;

public class functions {
    public static int addtwonumbers(int a, int b) {
        return a + b;
    }

    public static int subtracttwonumbers(int a, int b) {
        return a - b;
    }

    public static int multiplytwonumbers(int a, int b) {
        return a * b;
    }

    public static double dividetwonumbers(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
        return (double) a / b;
    }
}
