/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.nl_generation;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class EntityProcessing {

    static boolean simple = false;

    public static String decide_quotes_Simple_question(String O, String type) {
        simple = true;
        String s = decide_quotes_with_type(O, type);
        simple = false;
        return s;
    }

    public static String decide_quotes_with_type(String entity, String type) {
        // Remove any leading or trailing whitespace from value
        entity = entity.trim();

        // Check if value already ends with a quotation mark
        if (entity.endsWith("\"")) {
            // If it does, remove type from value and return the result
            return entity.replace(" " + type, "").trim();
        }

        // Remove any prefix from type using the removePrefix method from Settings.explorer
        type = Settings.explorer.removePrefix(type);

        // Check if value is longer than 24 characters
        if (entity.length() > 24) {
            // If it is, return a string that includes "the", type, and value in quotation marks
            return "the " + type + " \"" + entity + "\"";
        }

        // Check if type is equal to "Number", "Date", or "Literal"
        if (type.equals(Settings.Number) || type.equals(Settings.Date) || type.equals(Settings.Literal)) {
            // If it is, return value as is
            return entity;
        }

        // Check if value is 2 characters or shorter
        if (entity.length() <= 2) {
            // If it is, return a string that includes "the" and type followed by value
            return "the " + type + " " + entity;
        }

        // Use a StringTokenizer to count the number of tokens in value
        StringTokenizer st = new StringTokenizer(entity);
        int numTokens = st.countTokens();

        // Check if there are 4 or more tokens in value
        if (numTokens >= 4) {
            // If there are, return a string that includes "the", type, and value in quotation marks
            return "the " + type + " \"" + entity + "\"";
        } else if (simple) {
            // If simple is true and there are fewer than 4 tokens, return a similar string
            return "the " + type + " \"" + entity + "\"";
        }

        // Check if there is only 1 token in value
        if (numTokens == 1) {
            // Use a regular expression to check if value contains any special characters
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(entity);
            boolean hasSpecialCharacter = m.find();

            // If value contains special characters, return a string that includes "the", type, and value in quotation marks
            if (hasSpecialCharacter) {
                return "the " + type + " \"" + entity + "\"";
            }
        }

        // If none of the above conditions are met, return value as is
        return entity;
    }

    public static String decide_quotes_only(String O, String type) {
        if (O.trim().endsWith("\"")) {
            return O;
        }
        type = Settings.explorer.removePrefix(type);
        String O_with_quetes;

        System.out.println("O:" + O);
        System.out.println("type:" + type);

        if (O.length() > 24) {
            O_with_quetes = "the " + " \"" + O.replace(" " + type, "").trim() + "\"";
            return O_with_quetes;
        }

        if (type.equals(Settings.Number) || type.equals(Settings.Date) || type.equals(Settings.Literal)) {
            return O.replace(" " + type, "").trim();
        }

        StringTokenizer st = new StringTokenizer(O);
        if (st.countTokens() >= 4) {
            O_with_quetes = "the " + " \"" + O.replace(" " + type, "").trim() + "\"";
            return O_with_quetes;
        } else {
            O_with_quetes = O.replace(" " + type, "").trim();
        }

        if (st.countTokens() == 1) {
            Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(O);
            boolean hasSpecialCharacter = m.find();

            if (hasSpecialCharacter) {
                O_with_quetes = "the " + " \"" + O.replace(" " + type, "").trim() + "\"";
                return O_with_quetes;
            }
        }

        return O_with_quetes;
    }

    private static Pattern NumberPattern = Pattern.compile("-?\\d+(\\.\\d+)?([eE][-\\+]?\\d+(\\.\\d+)?)?");

    private static Pattern DATE_PATTERN = Pattern.compile(
            "^((2000|2400|2800|(19|2[0-9])(0[48]|[2468][048]|[13579][26]))-02-29)$"
            + "|^(((19|2[0-9])[0-9]{2})-02-(0[1-9]|1[0-9]|2[0-8]))$"
            + "|^(((19|2[0-9])[0-9]{2})-(0[13578]|10|12)-(0[1-9]|[12][0-9]|3[01]))$"
            + "|^(((19|2[0-9])[0-9]{2})-(0[469]|11)-(0[1-9]|[12][0-9]|30))$");

    public static boolean isDate(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }

}
