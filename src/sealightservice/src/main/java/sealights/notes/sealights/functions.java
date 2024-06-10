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
    // A function with 80 lines for line coverage testing
    public static int complexFunction(int a, int b) {
        int result = 0;

        // Adding dummy operations to expand the function to 80 lines
        // Line 1
        if (a > b) {
            result = a + b; // Line 2
        } else {
            result = a - b; // Line 3
        }

        // Line 4
        for (int i = 0; i < 10; i++) {
            // Line 5
            result += i; // Line 6
            if (i % 2 == 0) {
                // Line 7
                result -= 1; // Line 8
            } else {
                // Line 9
                result += 1; // Line 10
            }
        }

        // Line 11
        int temp = result; // Line 12
        for (int i = 0; i < 5; i++) {
            // Line 13
            temp += i; // Line 14
        }

        // Line 15
        result = temp; // Line 16
        if (result > 100) {
            // Line 17
            result = 100; // Line 18
        }

        // Line 19
        switch (result) {
            case 10:
                result += 10; // Line 20
                break; // Line 21
            case 20:
                result += 20; // Line 22
                break; // Line 23
            default:
                result += 30; // Line 24
                break; // Line 25
        }

        // Line 26
        if (result < 50) {
            // Line 27
            result *= 2; // Line 28
        } else {
            // Line 29
            result /= 2; // Line 30
        }

        // Line 31
        while (result > 0) {
            // Line 32
            result--; // Line 33
        }

        // Adding more dummy operations and comments
        // Line 34
        int[] arr = new int[10]; // Line 35
        for (int i = 0; i < arr.length; i++) {
            // Line 36
            arr[i] = i * 2; // Line 37
        }

        // Line 38
        for (int i = 0; i < arr.length; i++) {
            // Line 39
            if (arr[i] % 3 == 0) {
                // Line 40
                result += arr[i]; // Line 41
            } else {
                // Line 42
                result -= arr[i]; // Line 43
            }
        }

        // Line 44
        if (result < 0) {
            // Line 45
            result = Math.abs(result); // Line 46
        }

        // Line 47
        int sum = 0; // Line 48
        for (int i = 0; i < 10; i++) {
            // Line 49
            sum += i; // Line 50
        }

        // Line 51
        result += sum; // Line 52

        // Line 53
        for (int i = 0; i < 5; i++) {
            // Line 54
            if (i % 2 == 0) {
                // Line 55
                result += i; // Line 56
            } else {
                // Line 57
                result -= i; // Line 58
            }
        }

        // Line 59
        result *= 2; // Line 60

        // Line 61
        if (result % 2 == 0) {
            // Line 62
            result /= 2; // Line 63
        } else {
            // Line 64
            result *= 3; // Line 65
        }

        // Line 66
        if (result > 100) {
            // Line 67
            result -= 100; // Line 68
        } else {
            // Line 69
            result += 100; // Line 70
        }

        // Line 71
        for (int i = 0; i < 10; i++) {
            // Line 72
            result += i * 2; // Line 73
        }

        // Line 74
        result = (result + 10) / 2; // Line 75

        // Line 76
        int finalResult = result * 3; // Line 77

        // Line 78
        finalResult -= 5; // Line 79

        return finalResult; // Line 80
    }
}
