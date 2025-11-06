package com.avsmart.keyboard.util;

public final class KeyUtils {
    private KeyUtils() {}
    public static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
}
