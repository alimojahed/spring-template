package com.spring.template.springtemplate.util;


import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Ali Mojahed on 6/4/2021
 * @project spring-template
 **/

public class StringsUtility {

    private static Pattern englishPattern = Pattern.compile("[A-Za-z0-9_ ]+");

    public static boolean isNumeric(String s) {
        return StringUtils.isNumeric(s);
    }

    public static boolean emailIsValid(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        return email.matches(regex);
    }

    public static boolean isEnglish(String text) {
        if (text == null) {
            return false;
        }
        return englishPattern.matcher(text).matches();

    }

    public static boolean isUrlValid(String url) {
        try {

            URL obj = new URL(url);
            obj.toURI();

            return true;

        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static boolean isRegex(String input) {
        boolean isRegex;
        try {
            Pattern.compile(input);
            isRegex = true;
        } catch (PatternSyntaxException e) {
            isRegex = false;
        }

        return isRegex;
    }


    public static List<String> sentenceToWords(String filterValue) {
        String[] words = filterValue.split("\\s+");
        return Arrays.asList(words);
    }


    public static boolean isDigit(String input) {
        if (input == null) {
            return false;
        }
        boolean isDigit = true;
        for (char ch : input.toCharArray()) {
            if (!Character.isDigit(ch)) {
                isDigit = false;
                break;
            }
        }
        return isDigit;
    }

    public static String fixPersianNumbers(String input) {
        if (input != null) {
            StringBuilder result = new StringBuilder();
            char[] pNums = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹',
                    '٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};

            char[] enNums = {'0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

            for (int i = 0; i < input.length(); i++) {
                boolean added = false;
                for (int j = 0; j < pNums.length; j++) {
                    if (pNums[j] == input.charAt(i)) {
                        result.append(enNums[j]);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    result.append(input.charAt(i));
                }
            }
            return result.toString();
        } else {
            return null;
        }
    }

}
