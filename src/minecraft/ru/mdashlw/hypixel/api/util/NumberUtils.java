package ru.mdashlw.hypixel.api.util;

import java.util.TreeMap;

public final class NumberUtils {

    private static final TreeMap ROMAN_NUMERALS = new TreeMap();

    public static String toRomanNumeral(int i) {
        int l = ((Integer) NumberUtils.ROMAN_NUMERALS.floorKey(Integer.valueOf(i))).intValue();

        return i == l ? (String) NumberUtils.ROMAN_NUMERALS.get(Integer.valueOf(i)) : (String) NumberUtils.ROMAN_NUMERALS.get(Integer.valueOf(l)) + toRomanNumeral(i - l);
    }

    public static float ratio(int a, int b) {
        return b == 0 ? (float) a : (float) a / (float) b;
    }

    public static String plural(long i, String s) {
        return i != 1L && i != -1L ? i + " " + s + 's' : i + " " + s;
    }

    static {
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(1000), "M");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(900), "CM");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(500), "D");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(400), "CD");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(100), "C");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(90), "XC");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(50), "L");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(40), "XL");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(10), "X");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(9), "IX");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(5), "V");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(4), "IV");
        NumberUtils.ROMAN_NUMERALS.put(Integer.valueOf(1), "I");
    }
}
