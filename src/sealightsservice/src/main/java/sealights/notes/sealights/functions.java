package sealights.notes.sealights;

public class functions {

    public static int addtwonumbers(int a, int b) {
        // Add conditional statements to simulate different execution paths
        if (a > b) {
            // Some arbitrary logic to increase the line count
            return a + b;
        } else if (a == b) {
            return 2 * a;
        } else {
            return a + b + 1;
        }
    }

    public static int subtracttwonumbers(int a, int b) {
        // Add conditional statements to simulate different execution paths
        if (a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    public static int multiplytwonumbers(int a, int b) {
        // Add conditional statements to simulate different execution paths
        if (a > b) {
            return a * b;
        } else if (a == 0 || b == 0) {
            return 0;
        } else {
            return b * a;
        }
    }

    public static double dividetwonumbers(int a, int b) {
        // Add conditional statements to simulate different execution paths
        if (b == 0) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        } else if (a == 0) {
            return 0;
        } else {
            return (double) a / b;
        }
    }
}
