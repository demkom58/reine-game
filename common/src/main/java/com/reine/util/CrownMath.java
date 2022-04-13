package com.reine.util;

public final class CrownMath {
    private CrownMath() {
    }

    public static boolean isPowerOfTwo(int number) {
        return (number & (number - 1)) == 0;
    }

    public static int ceilToPowerOfTwo(int number) {
        number--;
        number |= number >> 1;
        number |= number >> 2;
        number |= number >> 4;
        number |= number >> 8;
        number |= number >> 16;
        number++;
        return number;
    }

    public static int floorToPowerOfTwo(int number) {
        return ceilToPowerOfTwo(number) >> 1;
    }

    public static int roundToPowerOfTwo(int number) {
        int ceil = number;

        ceil--;
        ceil |= ceil >> 1;
        ceil |= ceil >> 2;
        ceil |= ceil >> 4;
        ceil |= ceil >> 8;
        ceil |= ceil >> 16;
        ceil++;

        int floor = ceil >> 1;

        return (ceil - number) > (number - floor) ? floor : ceil;
    }
}
