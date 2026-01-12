package com.lilly.url_shortener.utils;

import java.util.concurrent.ThreadLocalRandom;

public  class Base62 {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();
    // encode a number to a short code
    public static String encode(long value){
        StringBuilder str = new StringBuilder();
        while(value>0){
            str.append(ALPHABET.charAt((int) value%BASE));
            value /= BASE;
        }

        return str.reverse().toString();
    }

    // decode a shortcode to a number
    public static long decode(String shortCode){
        long res = 0;
        for(char c: shortCode.toCharArray()){
            res = res*BASE + shortCode.indexOf(c);
        }
        return res;
    }
    public static String createRandomShortCode(){
        long randomNum = ThreadLocalRandom.current().nextInt(1_000_001, Integer.MAX_VALUE);
        return encode(randomNum);
    }
}
