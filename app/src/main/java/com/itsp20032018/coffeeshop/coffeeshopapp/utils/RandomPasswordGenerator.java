package com.itsp20032018.coffeeshop.coffeeshopapp.utils;

import java.security.SecureRandom;

public final class RandomPasswordGenerator {

    private RandomPasswordGenerator(){}

    private static SecureRandom random = new SecureRandom();

    /** different dictionaries used */
    public static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMERIC = "0123456789";
    public static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";

    /**
     * Method will generate random string based on the parameters
     * @param len the length of the random string
     * @param dic the dictionary used to generate the password
     * @return the random password
     */
    public static String generatePassword(int len, String dic) {
        String result = "";
        for (int i = 0; i < len; i++) {
            int index = random.nextInt(dic.length());
            result += dic.charAt(index);
        }
        return result;
    }
}