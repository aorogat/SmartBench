package utils;

public class StringUtils {

    //captalize the first letter of a string
    public static String captalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
