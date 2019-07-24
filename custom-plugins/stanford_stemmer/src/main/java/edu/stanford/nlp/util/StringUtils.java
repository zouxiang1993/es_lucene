package edu.stanford.nlp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /**
     * Returns whether a String is either null or empty.
     * (Copies the Guava method for this.)
     *
     * @param str The String to test
     * @return Whether the String is either null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }


    /**
     * Return a String of length a minimum of totalChars characters by
     * padding the input String str at the right end with spaces.
     * If str is already longer
     * than totalChars, it is returned unchanged.
     */
    public static String pad(String str, int totalChars) {
        return pad(str, totalChars, ' ');
    }

    /**
     * Return a String of length a minimum of totalChars characters by
     * padding the input String str at the right end with spaces.
     * If str is already longer
     * than totalChars, it is returned unchanged.
     */
    public static String pad(String str, int totalChars, char pad) {
        if (str == null) {
            str = "null";
        }
        int slen = str.length();
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < totalChars - slen; i++) {
            sb.append(pad);
        }
        return sb.toString();
    }

    /**
     * Pads the toString value of the given Object.
     */
    public static String pad(Object obj, int totalChars) {
        return pad(obj.toString(), totalChars);
    }


    /**
     * Pad or trim so as to produce a string of exactly a certain length.
     *
     * @param str The String to be padded or truncated
     * @param num The desired length
     */
    public static String padOrTrim(String str, int num) {
        if (str == null) {
            str = "null";
        }
        int leng = str.length();
        if (leng < num) {
            StringBuilder sb = new StringBuilder(str);
            for (int i = 0; i < num - leng; i++) {
                sb.append(' ');
            }
            return sb.toString();
        } else if (leng > num) {
            return str.substring(0, num);
        } else {
            return str;
        }
    }

    /**
     * Pad or trim so as to produce a string of exactly a certain length.
     *
     * @param str The String to be padded or truncated
     * @param num The desired length
     */
    public static String padLeftOrTrim(String str, int num) {
        if (str == null) {
            str = "null";
        }
        int leng = str.length();
        if (leng < num) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < num - leng; i++) {
                sb.append(' ');
            }
            sb.append(str);
            return sb.toString();
        } else if (leng > num) {
            return str.substring(str.length() - num);
        } else {
            return str;
        }
    }

    /**
     * Pad or trim the toString value of the given Object.
     */
    public static String padOrTrim(Object obj, int totalChars) {
        return padOrTrim(obj.toString(), totalChars);
    }


    /**
     * Pads the given String to the left with the given character ch to ensure that
     * it's at least totalChars long.
     */
    public static String padLeft(String str, int totalChars, char ch) {
        if (str == null) {
            str = "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, num = totalChars - str.length(); i < num; i++) {
            sb.append(ch);
        }
        sb.append(str);
        return sb.toString();
    }


    /**
     * Pads the given String to the left with spaces to ensure that it's
     * at least totalChars long.
     */
    public static String padLeft(String str, int totalChars) {
        return padLeft(str, totalChars, ' ');
    }


    public static String padLeft(Object obj, int totalChars) {
        return padLeft(obj.toString(), totalChars);
    }

    public static String padLeft(int i, int totalChars) {
        return padLeft(Integer.valueOf(i), totalChars);
    }

    public static String padLeft(double d, int totalChars) {
        return padLeft(new Double(d), totalChars);
    }

    public static List<String> valueSplit(String str, String valueRegex, String separatorRegex) {
        Pattern vPat = Pattern.compile(valueRegex);
        Pattern sPat = Pattern.compile(separatorRegex);
        List<String> ret = new ArrayList<>();
        while ( ! str.isEmpty()) {
            Matcher vm = vPat.matcher(str);
            if (vm.lookingAt()) {
                ret.add(vm.group());
                str = str.substring(vm.end());
                // String got = vm.group();
                // log.info("vmatched " + got + "; now str is " + str);
            } else {
                throw new IllegalArgumentException("valueSplit: " + valueRegex + " doesn't match " + str);
            }
            if ( ! str.isEmpty()) {
                Matcher sm = sPat.matcher(str);
                if (sm.lookingAt()) {
                    str = str.substring(sm.end());
                    // String got = sm.group();
                    // log.info("smatched " + got + "; now str is " + str);
                } else {
                    throw new IllegalArgumentException("valueSplit: " + separatorRegex + " doesn't match " + str);
                }
            }
        } // end while
        return ret;
    }

    public static String getShortClassName(Object o) {
        if (o == null) {
            return "null";
        }
        String name = o.getClass().getName();
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            name = name.substring(index + 1);
        }
        return name;
    }

    /**
     * Joins each element in the given array with the given glue. For example,
     * given an array of Integers, you can create a comma-separated list by calling
     * {@code join(numbers, ", ")}.
     */
    public static String join(String[] items, String glue) {
        return join(Arrays.asList(items), glue);
    }

    public static <X> String join(Iterable<X> l, String glue) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (X o : l) {
            if ( ! first) {
                sb.append(glue);
            } else {
                first = false;
            }
            sb.append(o);
        }
        return sb.toString();
    }

}
