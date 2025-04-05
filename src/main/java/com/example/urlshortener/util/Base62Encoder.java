package com.example.urlshortener.util;

public class Base62Encoder {

    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long id) {
        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % 62);
            sb.append(CHARSET.charAt(remainder));
            id = id / 62;
        }
        return sb.reverse().toString();
    }

    // Optional: decode if you ever want to reverse it
    public static long decode(String shortCode) {
        long id = 0;
        for (int i = 0; i < shortCode.length(); i++) {
            id = id * 62 + CHARSET.indexOf(shortCode.charAt(i));
        }
        return id;
    }
}
