package com.av.smartkeyboard.util;
public final class KeyUtils {
    private KeyUtils() {}
    public static boolean isLetter(char c) { return (c>='a' && c<='z') || (c>='A' && c<='Z'); }
    public static String toUpper(String s) { return s==null?"":s.toUpperCase(); }
}
