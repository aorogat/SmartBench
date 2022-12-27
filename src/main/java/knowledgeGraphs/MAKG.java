package knowledgeGraphs;

import java.util.ArrayList;
import knowledgeGraphs.KnowledgeGraph;
import lexiconGenerator.kg_explorer.explorer.Explorer;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import settings.Settings;

/**
 *
 * @author aorogat
 */
//Singleton Class
public class MAKG extends KnowledgeGraph {  //Update as BabelNet *******************************************************

    public MAKG(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new MAKG(endpoint);
            return (MAKG) instance;
        } else {
            return (MAKG) instance;
        }
    }

    @Override
    public String getNodeLabel(Explorer explorer, String node) {
        if (node.startsWith("<")) {
            node = node.replace("<", "").replace(">", "");
        }
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?l WHERE { <" + node.trim() + "> <http://xmlns.com/foaf/0.1/name> ?l. "
                    //                    + "FILTER(?p=<" + node.trim() + ">). "
                    + "}";
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            if (varSet == null || varSet.size() == 0) {
                query = "SELECT DISTINCT ?l WHERE { <" + node.trim() + "> <http://purl.org/dc/terms/title> ?l. "
                        //                    + "FILTER(?p=<" + node.trim() + ">). "
                        + "}";
                varSet = Settings.knowledgeGraph.runQuery(query);
                String n = varSet.get(0).getVariables().get(0).toString();

                return n;
            }
            String n = varSet.get(0).getVariables().get(0).toString();

            return n;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getType(Explorer explorer, String URI) {
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?s_type WHERE { "
                    + "    <" + URI + "> rdf:type ?s_type. "
                    + "    FILTER NOT EXISTS {\n"
                    + "      <" + URI + "> rdf:type ?type1 .\n"
                    + "      ?type1 rdfs:subClassOf ?s_type.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type1 owl:equivalentClass ?s_type.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "}";

            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            return varSet.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return "UNKONWN";
        }
    }

    @Override
    public String[] getUnwantedProperties() {
        unwantedProperties = new String[]{
            "rdfs:label",
            "rdfs:subPropertyOf",
            "rdfs:comment",
            "rdfs:label",
            "rdfs:seeAlso",
            "rdf:type",
            "rdfs:subClassOf",
            "rdfs:range",
            "rdfs:domain",
            "owl:sameAs",
            "owl:equivalentProperty",
            "owl:differentFrom",
            "owl:versionInfo",
            "owl:disjointWith",
            "owl:equivalentClass",
            "foaf:name",
            "foaf:primaryTopic",
            "<http://purl.org/linguistics/gold/hypernym>",
            "<http://xmlns.com/foaf/0.1/name>",
            "<http://purl.org/dc/terms/title>"

        };
        return unwantedProperties;
    }

}
