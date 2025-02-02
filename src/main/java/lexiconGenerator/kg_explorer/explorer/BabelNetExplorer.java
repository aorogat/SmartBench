/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexiconGenerator.kg_explorer.explorer;

import database.Database;
import knowledgeGraphs.BabelNet;
import knowledgeGraphs.MAKG;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class BabelNetExplorer extends Explorer {
    public BabelNetExplorer(String url) {
        super();
        kg = BabelNet.getInstance(url);
        endpoint = kg.getEndpoint();
        Database.connect();
    }

    public static BabelNetExplorer getInstance(String url) {
        if (instance == null) {
            instance = new DBpediaExplorer(url);
        }
        return (BabelNetExplorer) instance;
    }
    
   public static String getPredicateLabel(String predicate) {
        return Settings.knowledgeGraph.getNodeLabel(BabelNetExplorer.getInstance(Settings.url), predicate);
    }
    
    @Override
    public String removePrefix(String node) {
        if (node == null) {
            return node;
        }

        if (node.equals("true") || node.equals("false") || node.equals(Settings.Number) || node.equals(Settings.Date) || node.equals(Settings.Literal)) {
            return node;
        }

        StringBuilder s = new StringBuilder();
        s = new StringBuilder(Settings.knowledgeGraph.getNodeLabel(this, node));

        if (s == null || s.toString().equals("")) {
            if (node.startsWith("http")) {
                String last = node.substring(node.lastIndexOf("/") + 1);
                if (last.contains("#")) {
                    last = node.substring(node.lastIndexOf("#") + 1);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = new StringBuilder();
                for (String string : r) {
                    s.append(string).append(" ");
                }
                s = new StringBuilder(s.toString().trim().toLowerCase());
                return s.toString();
            }
        }
        return s.toString();
    }
}
