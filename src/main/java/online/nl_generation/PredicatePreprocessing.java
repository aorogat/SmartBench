/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.nl_generation;

import java.util.regex.Pattern;

/**
 *
 * @author aorogat
 */
public class PredicatePreprocessing {

    /**
     * Reorder the predicate tokens if the predicate is connected to a number.
     * For example, the predicate "area metro" must be reordered to be "metro
     * area" before using it in the question generation process.
     *
     * @param p predicate's label
     * @return the predicate suitable for question generation
     */
    public static String reorder_predicaeTokens_Numbers(String p) {
        try {
            String pNew = "";
            Pattern PATTERN = Pattern.compile("\\barea\\w");
            if (PATTERN.matcher(p).matches()) {
                pNew = p.replace("area", "").trim();
                pNew += " area";
            } else {
                pNew = p;
            }

            return pNew;
        } catch (Exception e) {
            return p;
        }
    }

}
