/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiconGenerator.kg_explorer.explorer;

import knowledgeGraphs.MAKG;
import database.Database;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class GEOExplorer extends Explorer {

    public GEOExplorer(String url) {
        super();
        kg = MAKG.getInstance(url);
        endpoint = kg.getEndpoint();
        Database.connect();
    }

    public static GEOExplorer getInstance(String url) {
        if (instance == null) {
            instance = new DBpediaExplorer(url);
        }
        return (GEOExplorer) instance;
    }

    public static String getPredicateLabel(String predicate) {
        return Settings.knowledgeGraph.getNodeLabel(GEOExplorer.getInstance(Settings.url), predicate);
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
        if(node.startsWith("http") || node.startsWith("<http"))
            s = Settings.knowledgeGraph.getNodeLabel(this, node);

        if (s == null || s.equals("")) {
            String last;
            if (node.startsWith("http")) {
                last = node.substring(node.lastIndexOf("/") + 1);
                last = last.replace("#", " ").replace("_", " ");
                //for predicates with the form XXX%3YYYY get only XXX
                int start;
                int end;
                if(last.contains("%")){
                    end = last.lastIndexOf("%");
                    last = last.substring(0, end);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = "";
                for (String string : r) {
                    s += string.trim() + " ";
                }
            }
            else{
                last = node.replace("#", " ").replace("_", " ");
                //for predicates with the form XXX%3YYYY get only XXX
                int start;
                int end;
                if(last.contains("%")){
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
