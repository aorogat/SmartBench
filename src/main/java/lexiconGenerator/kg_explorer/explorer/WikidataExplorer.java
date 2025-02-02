/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiconGenerator.kg_explorer.explorer;

import database.Database;
import java.util.ArrayList;
import knowledgeGraphs.WikidataKG;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class WikidataExplorer extends Explorer {

    public WikidataExplorer(String url) {
        super();
        kg = WikidataKG.getInstance(url);
        endpoint = kg.getEndpoint();
        Database.connect();
    }

    public static WikidataExplorer getInstance(String url) {
        if (instance == null) {
            instance = new DBpediaExplorer(url);
        }
        return (WikidataExplorer) instance;
    }

    public static String getPredicateLabel(String node) {

        //get labels
        try {
            String query = "";
            if (node.startsWith("http")) {
                node = "<" + node + ">";
            }
            query = "SELECT ?wdLabel WHERE {\n"
                    + "  VALUES (?wdt) {(" + node + ")}\n"
                    + "   ?wd wikibase:directClaim ?wdt .\n"
                    + "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }\n"
                    + "}";
            ArrayList<VariableSet> varSet = kg.runQuery(query);

            return varSet.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getNodeLabel(String node) {
        if (node.startsWith("<")) {
            node = node.replace("<", "").replace(">", "");
        }
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?l WHERE { ?p rdfs:label ?l. "
                    + "FILTER(?p=<" + node.trim() + ">). "
                    + "FILTER langMatches( lang(?l), \"EN\" )."
                    + "}";
            ArrayList<VariableSet> varSet = kg.runQuery(query);

            return varSet.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String removePrefix(String node) {
        if (node == null) {
            return node;
        }

        if (node.equals("true") || node.equals("false") || node.equals(Settings.Number) || node.equals(Settings.Date) || node.equals(Settings.Literal)) {
            return node;
        }

        String s = "";
        if (node.startsWith("http") || node.startsWith("<http")) {
            s = getNodeLabel(node);
        }

        if (s == null || s.equals("")) {
            String last;
            if (node.startsWith("http")) {
                last = node.substring(node.lastIndexOf("/") + 1);
                last = last.replace("#", " ").replace("_", " ");
                //for predicates with the form XXX%3YYYY get only XXX
                int start;
                int end;
                if (last.contains("%")) {
                    end = last.lastIndexOf("%");
                    last = last.substring(0, end);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = "";
                for (String string : r) {
                    s += string.trim() + " ";
                }
            } else {
                last = node.replace("#", " ").replace("_", " ");
                //for predicates with the form XXX%3YYYY get only XXX
                int start;
                int end;
                if (last.contains("%")) {
                    end = last.lastIndexOf("%");
                    last = last.substring(0, end);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = "";
                for (String string : r) {
                    s += string.trim() + " ";
                }
            }
            s = s.trim().toLowerCase();
            return s;
        }
        return s;
    }
}
