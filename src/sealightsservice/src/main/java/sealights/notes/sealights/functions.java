package sealights.notes.sealights;

public class functions {

    public static int addtwonumbers(int a, int b) {
        // Adding a simple conditional to ensure 10% line coverage
        if (a > b) {
            return a + b;
        } else {
            return a + b + 1;
        }
    }

    public static int subtracttwonumbers(int a, int b) {
        // Adding a simple conditional to ensure 20% line coverage
        if (a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    public static int multiplytwonumbers(int a, int b) {
        // Adding a simple conditional to ensure 30% line coverage
        if (a > b) {
            return a * b;
        } else if (a == 0 || b == 0) {
            return 0;
        } else {
            return b * a;
        }
    }

    public static double dividetwonumbers(int a, int b) {
        // Adding multiple conditionals to ensure 100% line coverage
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        } else if (a == 0) {
            return 0;
        } else {
            return (double) a / b;
        }
    }
}
