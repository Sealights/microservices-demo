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
    public static int _80_lins_Complex(int a, int b) {
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

    public static int _150_lins_Complex(int a, int b) {
        int result = 0;
        int temp = 0;

        // Line 1
        for (int i = 0; i < 10; i++) {
            // Line 2
            result += i * a; // Line 3
            if (i % 2 == 0) {
                // Line 4
                result -= b; // Line 5
            } else {
                // Line 6
                result += b; // Line 7
            }
        }

        // Line 8
        if (result > 50) {
            // Line 9
            result /= 2; // Line 10
        } else {
            // Line 11
            result *= 2; // Line 12
        }

        // Line 13
        for (int i = 0; i < 5; i++) {
            // Line 14
            temp += i * result; // Line 15
            if (temp % 3 == 0) {
                // Line 16
                temp /= 3; // Line 17
            } else {
                // Line 18
                temp *= 3; // Line 19
            }
        }

        // Line 20
        while (temp > 100) {
            // Line 21
            temp -= 10; // Line 22
        }

        // Line 23
        switch (temp) {
            case 50:
                // Line 24
                temp += 10; // Line 25
                break; // Line 26
            case 75:
                // Line 27
                temp += 20; // Line 28
                break; // Line 29
            default:
                // Line 30
                temp += 30; // Line 31
                break; // Line 32
        }

        // Line 33
        result = temp; // Line 34
        if (result % 2 == 0) {
            // Line 35
            result /= 2; // Line 36
        } else {
            // Line 37
            result *= 2; // Line 38
        }

        // Line 39
        for (int i = 0; i < 10; i++) {
            // Line 40
            result += i * a; // Line 41
            if (result % 2 == 0) {
                // Line 42
                result -= b; // Line 43
            } else {
                // Line 44
                result += b; // Line 45
            }
        }

        // Line 46
        if (result < 0) {
            // Line 47
            result = Math.abs(result); // Line 48
        }

        // Line 49
        int sum = 0; // Line 50
        for (int i = 0; i < 10; i++) {
            // Line 51
            sum += i * result; // Line 52
        }

        // Line 53
        result += sum; // Line 54
        if (result > 100) {
            // Line 55
            result -= 50; // Line 56
        } else {
            // Line 57
            result += 50; // Line 58
        }

        // Line 59
        while (result > 200) {
            // Line 60
            result -= 10; // Line 61
        }

        // Line 62
        int[] arr = new int[10]; // Line 63
        for (int i = 0; i < arr.length; i++) {
            // Line 64
            arr[i] = i * result; // Line 65
        }

        // Line 66
        for (int i = 0; i < arr.length; i++) {
            // Line 67
            if (arr[i] % 3 == 0) {
                // Line 68
                result += arr[i]; // Line 69
            } else {
                // Line 70
                result -= arr[i]; // Line 71
            }
        }

        // Line 72
        if (result < 0) {
            // Line 73
            result = Math.abs(result); // Line 74
        }

        // Line 75
        int sum2 = 0; // Line 76
        for (int i = 0; i < 10; i++) {
            // Line 77
            sum2 += i * result; // Line 78
        }

        // Line 79
        result += sum2; // Line 80
        if (result > 1000) {
            // Line 81
            result -= 500; // Line 82
        } else {
            // Line 83
            result += 500; // Line 84
        }

        // Line 85
        while (result > 2000) {
            // Line 86
            result -= 100; // Line 87
        }

        // Line 88
        int[] arr2 = new int[20]; // Line 89
        for (int i = 0; i < arr2.length; i++) {
            // Line 90
            arr2[i] = i * result; // Line 91
        }

        // Line 92
        for (int i = 0; i < arr2.length; i++) {
            // Line 93
            if (arr2[i] % 5 == 0) {
                // Line 94
                result += arr2[i]; // Line 95
            } else {
                // Line 96
                result -= arr2[i]; // Line 97
            }
        }

        // Line 98
        if (result < 0) {
            // Line 99
            result = Math.abs(result); // Line 100
        }

        // Line 101
        int sum3 = 0; // Line 102
        for (int i = 0; i < 10; i++) {
            // Line 103
            sum3 += i * result; // Line 104
        }

        // Line 105
        result += sum3; // Line 106
        if (result > 5000) {
            // Line 107
            result -= 2500; // Line 108
        } else {
            // Line 109
            result += 2500; // Line 110
        }

        // Line 111
        while (result > 10000) {
            // Line 112
            result -= 500; // Line 113
        }

        // Line 114
        int[] arr3 = new int[30]; // Line 115
        for (int i = 0; i < arr3.length; i++) {
            // Line 116
            arr3[i] = i * result; // Line 117
        }

        // Line 118
        for (int i = 0; i < arr3.length; i++) {
            // Line 119
            if (arr3[i] % 7 == 0) {
                // Line 120
                result += arr3[i]; // Line 121
            } else {
                // Line 122
                result -= arr3[i]; // Line 123
            }
        }

        // Line 124
        if (result < 0) {
            // Line 125
            result = Math.abs(result); // Line 126
        }

        // Line 127
        int sum4 = 0; // Line 128
        for (int i = 0; i < 10; i++) {
            // Line 129
            sum4 += i * result; // Line 130
        }

        // Line 131
        result += sum4; // Line 132
        if (result > 20000) {
            // Line 133
            result -= 10000; // Line 134
        } else {
            // Line 135
            result += 10000; // Line 136
        }

        // Line 137
        while (result > 50000) {
            // Line 138
            result -= 2500; // Line 139
        }

        // Line 140
        int[] arr4 = new int[40]; // Line 141
        for (int i = 0; i < arr4.length; i++) {
            // Line 142
            arr4[i] = i * result; // Line 143
        }

        // Line 144
        for (int i = 0; i < arr4.length; i++) {
            // Line 145
            if (arr4[i] % 11 == 0) {
                // Line 146
                result += arr4[i]; // Line 147
            } else {
                // Line 148
                result -= arr4[i]; // Line 149
            }
        }

        return result; // Line 150
    }

    // 10 functions with 10 lines each for line coverage testing
    public static int func_10_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        }
        return result;
    }

    public static int func_20_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else {
            result = a - b;
        }
        return result;
    }

    public static int func_30_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        }
        return result;
    }

    public static int func_40_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else {
            result = a * b;
        }
        return result;
    }

    public static int func_50_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else if (a == b) {
            result = a * b;
        }
        return result;
    }

    public static int func_60_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else if (a == b) {
            result = a * b;
        } else {
            result = a / b;
        }
        return result;
    }

    public static int func_70_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else if (a == b) {
            result = a * b;
        } else if (a != b) {
            result = a / b;
        }
        return result;
    }

    public static int func_80_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else if (a == b) {
            result = a * b;
        } else if (a != b) {
            result = a / b;
        } else {
            result = a % b;
        }
        return result;
    }

    public static int func_90_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else if (a == b) {
            result = a * b;
        } else if (a != b) {
            result = a / b;
        } else if (a >= b) {
            result = a % b;
        }
        return result;
    }

    public static int func_100_lines(int a, int b) {
        int result = 0;
        if (a > b) {
            result = a + b;
        } else if (a < b) {
            result = a - b;
        } else if (a == b) {
            result = a * b;
        } else if (a != b) {
            result = a / b;
        } else if (a >= b) {
            result = a % b;
        } else {
            result = a & b;
        }
        return result;
    }
}
