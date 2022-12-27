package knowledgeGraphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import knowledgeGraphs.KnowledgeGraph;
import static knowledgeGraphs.KnowledgeGraph.instance;
import lexiconGenerator.kg_explorer.explorer.Explorer;
import static lexiconGenerator.kg_explorer.explorer.Explorer.kg;
import lexiconGenerator.kg_explorer.model.PredicateContext;
import lexiconGenerator.kg_explorer.model.PredicateTripleExample;
import benchmarkGenerator.subgraphShapeGenerator.model.Variable;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import settings.Settings;
import system.components.Branch;

/**
 *
 * @author aorogat
 */
//Singleton Class
public class BabelNet extends KnowledgeGraph {

    public BabelNet(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new BabelNet(endpoint);
            return (BabelNet) instance;
        } else {
            return (BabelNet) instance;
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
    
    
    
    @Override
    public ArrayList<PredicateContext> getPredicateContextFromTripleExample(String subjectURI, String predicateURI, String objectURI) {
        String unwantedPropertiesString = Settings.knowledgeGraph.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  ?o_type  \n"
                + "WHERE{\n"
                + "   <" + subjectURI + ">     rdf:type              ?s_type.\n"
                + "   <" + objectURI + ">      rdf:type              ?o_type.\n"
                + "    }.\n";


        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + ")).\n"
                    + "  FILTER strstarts(str(?o_type ), str(" + Settings.requiredTypePrefix + ")).\n";
        }
        query += "}";
        ArrayList<VariableSet> predicatesTriplesVarSets = Settings.knowledgeGraph.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = predicate.getVariables().get(1).getValueWithPrefix();
            weight = 0;
            predicateContexts.add(new PredicateContext(stype, otype, weight));
        }
        return predicateContexts;

    }

    @Override
    public Branch getBranchOfType_SType_connectTo_OType(Explorer explorer, String S_type, String O_type, String predicateURI, int offset) {
        String query = "";
        //get labels
        try {
            if (O_type.equals("Number") || O_type.equals("Date") || O_type.equals(Settings.Literal)) {
                query = "SELECT DISTINCT ?s ?o WHERE {\n "
                        + "  ?s <" + predicateURI + "> ?o.  ?s rdf:type <" + S_type + ">." + Settings.popularityFilter
                        + "\n}\n "
                        + Settings.popularityORDER
                        + "OFFSET " + offset;
            } else {
                query = "SELECT DISTINCT ?s ?o WHERE {\n "
                        + "  ?s <" + predicateURI + "> ?o.  ?s rdf:type <" + S_type + ">.  ?o rdf:type <" + O_type + ">.  " + Settings.popularityFilter
                        + "\n}\n "
                        + Settings.popularityORDER
                        + "OFFSET " + offset;
            }
            explorer.predicatesTriplesVarSets = Settings.knowledgeGraph.runQuery(query);

            String s = explorer.predicatesTriplesVarSets.get(0).getVariables().get(0).toString();
            String o = explorer.predicatesTriplesVarSets.get(0).getVariables().get(1).toString();

            Branch branch = new Branch(s, o, predicateURI, S_type, O_type);

            return branch;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean isASubtypeOf(Explorer explorer, String child, String parent) {
        if(child.toLowerCase().equals(parent.toLowerCase()))
            return true;
        if(child.equals(Settings.Number) || child.equals(Settings.Date) || child.equals(Settings.Literal))
            return false;
        String query = "";
        //A better solution is to use property path expressions in SPARQL 1.1. This would be rewritten as
        if (child.startsWith("http")) {
            child = "<" + child + ">";
        }
        if (parent.startsWith("http")) {
            parent = "<" + parent + ">";
        }
        try {
            query = "ASK WHERE {\n"
                    + "  " + child + " rdfs:subClassOf* " + parent + ".\n"
                    + "}";
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            String answer = varSet.get(0).getVariables().get(0).getValueWithPrefix();
            if (answer.equals("true")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public long getPredicateWeight(String predicate, String sType, String oType) {
        String query = "";

        ArrayList<VariableSet> predicatesTriplesVarSets = new ArrayList<>();
        //get weights
        try {
            query = "SELECT (count(?p) as ?count) WHERE {\n "
                    + "\t?s ?p ?o . \n"
                    + "\t?s rdf:type <" + sType + ">. \n"
                    + "\t?o rdf:type <" + oType + ">. \n"
                    + "\tFILTER(?p=<" + predicate.trim() + ">).\n";
            if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                query += "\tFILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n"
                        + "\tFILTER strstarts(str(<" + oType + "> ), str(" + Settings.requiredTypePrefix + ")).\n";

            }
            query += ""
                    + "}";
            predicatesTriplesVarSets = kg.runQuery(query);
            return Long.valueOf(predicatesTriplesVarSets.get(0).getVariables().get(0).toString());
        } catch (Exception e) {
            return -1;
        }
    }

  
    @Override
    public ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples = predicateTriples = new ArrayList<>();
        try {
            if (oType.equals("Number")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"               
                        + "\n";
                //Get only dbpedia types
                if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                    query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n";
                }

                query += "FILTER isNumeric(?o)."
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            } else if (oType.equals("Date")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
                        + "\n";
                if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                    //Get only dbpedia types
                    query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n";
                }
                query += " FILTER (datatype(?o) = xsd:dateTime ). \n"
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            } else {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
                        + "?o rdf:type <" + oType + ">. \n"
                        + "\n"
                        ;
                if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                    //Get only dbpedia types
                    query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n"
                            + "  FILTER strstarts(str(<" + oType + "> ), str(" + Settings.requiredTypePrefix + ")).\n";
                }
                query += "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            }
            predicatesTriplesVarSets = kg.runQuery(query);
            if (predicatesTriplesVarSets.size() > noOfExamples) {
                predicatesTriplesVarSets = new ArrayList<>(predicatesTriplesVarSets.subList(0, noOfExamples));
            }
            for (VariableSet predicate1 : predicatesTriplesVarSets) {
                String s = predicate1.getVariables().get(0).toString();
                String o = predicate1.getVariables().get(1).toString();
                PredicateTripleExample predicateTriple = new PredicateTripleExample("<" + s + ">", "<" + o + ">", Settings.explorer.removePrefix(s), Settings.explorer.removePrefix(o), lable, Settings.explorer);
                predicateTriples.add(predicateTriple);
                //To speed up the system. break after one VP.
//                if(predicateTriple.getNlsSuggestionsObjects().size()>=1)
//                    break;
            }
            return predicateTriples;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_EntityObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  ?o_type  (count(?s) as ?count)\n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                + "   ?o      rdf:type              ?o_type.\n" //  
                ;
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n"
                    + "  FILTER (strstarts(str(?o_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type  ?o_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = predicate.getVariables().get(1).getValueWithPrefix();
            String weightString = predicate.getVariables().get(2).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_NumberObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                + "\n";

        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Number";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_DateObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                ;
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Date";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_Literals(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                ;
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Literal";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }



}
