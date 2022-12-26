/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.nl_generation.query;

import settings.Settings;

/**
 *
 * @author aorogat
 */
public class QueryGenerator {

    /**
     * Generates a SPARQL SELECT query to retrieve all values of the seed
     * subject in the triple pattern.
     *
     * @return a SPARQL SELECT query to retrieve all values of the seed subject
     * in the triple pattern
     */
    public static String generateSELECTQuery(String graph, String node, String nodeType, String variable) {
        return "SELECT DISTINCT ?Seed WHERE{\n\t" + graph_to_graphPattern(graph, node, nodeType, variable) + "\n}";
    }

    /**
     *
     * This method generates a SPARQL query to count the number of entities in
     * the graph that match the seed entity. The query replaces the seed entity
     * in the triple pattern with a variable, and counts the number of values
     * that the variable can take on.
     *
     * @return a String containing the SPARQL query to count the number of
     * entities in the graph that match the seed entity
     */
    public static String generateCountQuery(String graph, String node, String nodeType, String variable) {
        return "SELECT COUNT(?Seed) WHERE{\n\t" + graph_to_graphPattern(graph, node, nodeType, variable) + "\n}";
    }

    public static String generateAskQuery_Correct(String graph) {
        return "ASK WHERE{\n\t" + graph + "\n}";
    }
    
    public static String generateAskQuery_Wrong(String graph, String node, String nodeType, String variable) {
        return "ASK WHERE{\n\t" + graph_to_graphPattern(graph, node, nodeType, variable) + "\n}";
    }

    private static String graph_to_graphPattern(String graph, String node, String nodeType, String variable) {
        if (graph != null) {
            if (nodeType.equals(Settings.Number) || nodeType.equals(Settings.Date) || nodeType.equals(Settings.Literal)) {
                graph = graph
                        .replace("\"" + node + "\"^^xsd:dateTime ", variable)
                        .replace("\"" + node + "\"", variable)
                        .replace(" " + node + " ", variable)
                        + " .";
            } else {
                graph = graph.replace(node, variable) + " .";
            }

        }
        return graph;
    }
}
