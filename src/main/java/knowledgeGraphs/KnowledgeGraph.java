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
import offLine.kg_explorer.explorer.Explorer;
import static offLine.kg_explorer.explorer.Explorer.kg;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateContext;
import offLine.kg_explorer.model.PredicateTripleExample;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;
import online.kg_extractor.model.subgraph.Graph;
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
public abstract class KnowledgeGraph {

    protected String name = "";
    protected static KnowledgeGraph instance = null;
    protected String queryString;
    protected String endpoint;
    protected String[] unwantedProperties;
    
    public static ArrayList<VariableSet> predicatesVariableSet_entity = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_number = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_date = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_Literals = new ArrayList<>();

    public static ArrayList<VariableSet> predicatesTriplesVarSets;
    public static ArrayList<Graph> result = new ArrayList<>();
    protected ArrayList<Predicate> predicateList = new ArrayList<>();

    public String getNodeLabel(Explorer explorer, String node) {
        if(!node.contains("http"))
            return node;
//        if (Settings.name.toLowerCase().equals("makg")) {
//            return getNodeFOAFLabel(explorer, node);
//        }
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
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            String n = varSet.get(0).getVariables().get(0).toString();

            return n;
        } catch (Exception e) {
            return null;
        }
    }

//    public String getNodeFOAFLabel(Explorer explorer, String node) {
//        if (node.startsWith("<")) {
//            node = node.replace("<", "").replace(">", "");
//        }
//        String query = "";
//        //get labels
//        try {
//            query = "SELECT DISTINCT ?l WHERE { <" + node.trim() + "> <http://xmlns.com/foaf/0.1/name> ?l. "
//                    //                    + "FILTER(?p=<" + node.trim() + ">). "
//                    + "}";
//            ArrayList<VariableSet> varSet = Settings.knowledgeGraphrunQuery(query);
//            if (varSet == null || varSet.size() == 0) {
//                query = "SELECT DISTINCT ?l WHERE { <" + node.trim() + "> <http://purl.org/dc/terms/title> ?l. "
//                        //                    + "FILTER(?p=<" + node.trim() + ">). "
//                        + "}";
//                varSet = Settings.knowledgeGraphrunQuery(query);
//                String n = varSet.get(0).getVariables().get(0).toString();
//
//                return n;
//            }
//            String n = varSet.get(0).getVariables().get(0).toString();
//
//            return n;
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public String getTopEntity(String T, String P, boolean top) {

        String order = "";
        if (top) {
            order = "DESC";
        } else {
            order = "ASC";
        }

        if (T.startsWith("<")) {
            T = T.replace("<", "").replace(">", "");
        }
        String query = "";
        //get labels
        Random r = new Random();
        int offset = r.nextInt(Settings.SET_QUESTION_TOP_ENTITY) + 1;
        try {
            query = "select ?o ?n where\n"
                    + "{\n"
                    + "    ?o rdf:type <" + T + ">.\n"
                    + "    ?o <" + P + "> ?n\n"
                    + "} \n"
                    + "ORDER BY " + order + "(?n)\n"
                    + "LIMIT 1\n"
                    + "OFFSET " + offset;
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            String o = varSet.get(0).getVariables().get(0).toString();

            return o;
        } catch (Exception e) {
            return null;
        }
    }

    //Return an entitiy of the same type
    public String getSimilarEntity(Explorer explorer, String entity, String entityType) {
        String query = "";
        //get labels
        try {
            if (entityType.equals(Settings.Number)) {
                return "" + (Double.parseDouble(entity) + Double.parseDouble(entity)/2);
            } else if (entityType.equals(Settings.Date)) {
                String y = entity.substring(3,4);
                int yy = Integer.parseInt(y)-5;
                if(yy<1){
                    if(Integer.parseInt(y)!=1)
                        yy=1;
                    else
                        yy=2;
                }
                entity = entity.substring(0, 3) + yy + entity.substring(4);
                return entity;
            } else if (entityType.equals(Settings.Literal)) {
                return entity + entity.substring(0,entity.length()/2);
            } else {
                query = "SELECT ?similar WHERE { "
                        + "?similar rdf:type <" + entityType + ">. "
                        + "FILTER(?similar!=<" + entity.trim() + ">). "
                        + "} LIMIT 1";
            }
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            return varSet.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return null;
        }
    }

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
                    + "    FILTER EXISTS {\n"
                    + "      ?s_type rdfs:subClassOf ?superType1 .\n"
                    + "      <" + URI + "> rdf:type ?superType1 .\n"
                    + "    }.\n"
                    + "\n"
                    + "    FILTER strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))"
                    + "}";
//            if (Settings.name.equals("GEO") || Settings.name.equals("MAKG") || Settings.name.equals("NobelPrize")) {
//                query = "SELECT DISTINCT ?s_type WHERE { "
//                        + "    <" + URI + "> rdf:type ?s_type. "
//                        + "    FILTER NOT EXISTS {\n"
//                        + "      <" + URI + "> rdf:type ?type1 .\n"
//                        + "      ?type1 rdfs:subClassOf ?s_type.\n"
//                        + "      FILTER NOT EXISTS {\n"
//                        + "         ?type1 owl:equivalentClass ?s_type.\n"
//                        + "      }\n"
//                        + "    }.\n"
//                        + "}";
//            }
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            return varSet.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return "UNKONWN";
        }
    }

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
                + "<" + subjectURI + ">      rdf:type              ?s_type.\n"
                + "    FILTER NOT EXISTS {\n"
                + "      <" + subjectURI + "> rdf:type ?type1 .\n"
                + "      ?type1 rdfs:subClassOf ?s_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type1 owl:equivalentClass ?s_type.\n"
                + "      }\n"
                + "    }.\n"
                + "    FILTER EXISTS {\n"
                + "      ?s_type rdfs:subClassOf ?superType1 .\n"
                + "      <" + subjectURI + "> rdf:type ?superType1 .\n"
                + "    }.\n"
                + "\n"
                + "   <" + objectURI + ">      rdf:type              ?o_type.\n"
                + "    FILTER NOT EXISTS {\n"
                + "      <" + objectURI + "> rdf:type ?type2 .\n"
                + "      ?type2 rdfs:subClassOf ?o_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type2 owl:equivalentClass ?o_type.\n"
                + "      }\n"
                + "    }.\n"
                + "    FILTER EXISTS {\n"
                + "      ?o_type rdfs:subClassOf ?superType2 .\n"
                + "      <" + objectURI + "> rdf:type ?superType2 .\n"
                + "    }.\n";

//        if (Settings.name.equals("NobelPrize")) {
//            query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
//                    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
//                    + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
//                    + "PREFIX schema: <http://schema.org/> \n"
//                    + " \n"
//                    + "SELECT DISTINCT ?s_type  ?o_type  \n"
//                    + "WHERE{\n"
//                    + "    <" + subjectURI + ">      rdf:type              ?s_type.\n"
//                    + "    <" + objectURI + ">      rdf:type              ?o_type.\n"
//                    + "    }.\n";
//        }

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

    public long getPredicateWeight(String predicate, String sType, String oType) {
        String query = "";

        ArrayList<VariableSet> predicatesTriplesVarSets = new ArrayList<>();
        //get weights
        try {
            query = "SELECT (count(?p) as ?count) WHERE { ?s ?p ?o . "
                    + "?s rdf:type <" + sType + ">. "
                    + "?o rdf:type <" + oType + ">. "
                    + "FILTER(?p=<" + predicate.trim() + ">)."
                    + ""
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?s rdf:type ?type1 .\n"
                    + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
                    + "      ?s rdf:type ?superType1 .\n"
                    + "    }.\n"
                    + "\n"
                    + "   ?o      rdf:type              <" + oType + ">.\n"
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?o rdf:type ?type2 .\n"
                    + "      ?type2 rdfs:subClassOf <" + oType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + oType + "> rdfs:subClassOf ?superType2 .\n"
                    + "      ?o rdf:type ?superType2 .\n"
                    + "    }.\n";
            if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n"
                        + "  FILTER strstarts(str(<" + oType + "> ), str(" + Settings.requiredTypePrefix + ")).\n";

            }
            query += ""
                    + "}";
            predicatesTriplesVarSets = kg.runQuery(query);
            return Long.valueOf(predicatesTriplesVarSets.get(0).getVariables().get(0).toString());
        } catch (Exception e) {
            return -1;
        }
    }

    public ArrayList<VariableSet> runQuery(String queryString) {
        if (!queryString.toLowerCase().contains("limit ") && !queryString.toLowerCase().contains("ask ")) { 
            queryString = queryString + "\n LIMIT " + (Settings.maxAnswerCardinalityAllowed + 10);
        }
        ArrayList<VariableSet> queryResult = new ArrayList<>();
        try {
            ArrayList<String> answersList = new ArrayList<>();
            String url = endpoint
                    + "?default-graph-uri=" + Settings.default_graph_uri + "&"
                    + "query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString()) + "&"
                    + "format=application%2Fsparql-results%2Bjson&"
                    + "timeout=30000&"
                    + "debug=on";
            try {
                JSONObject json = readJsonFromUrl(url);
                try {

                    JSONArray vars = json.getJSONObject("head").getJSONArray("vars");
                    JSONArray bindings = json.getJSONObject("results").getJSONArray("bindings");

                    for (Object binding : bindings) {
                        VariableSet variableSet = new VariableSet();
                        for (Object var : vars) {
                            try {

                                String v = (String) var;
                                JSONObject b = (JSONObject) binding;

                                variableSet.getVariables().add(
                                        new Variable(v, b.getJSONObject(v).getString("value"),
                                                b.getJSONObject(v).getString("type")));

//                                answersList.add(b.getJSONObject(v).getString("value")
//                                        .replace("http://dbpedia.org/resource/", "")
//                                        .trim().replace('_', ' '));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        queryResult.add(variableSet);
                    }
                    if (queryResult.size() < 1) {
                        String q = queryString;
                        System.out.println("No query result, may be boolean.");
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                    try {
                        VariableSet variableSet = new VariableSet();
                        //Boolean
                        if (json.getBoolean("boolean")) {
                            variableSet.getVariables().add(new Variable("v", "true", "boolean"));
                        } else {
                            variableSet.getVariables().add(new Variable("v", "false", "boolean"));
                        }
                        queryResult.add(variableSet);
                    } catch (Exception et) {
                        et.printStackTrace();
                    }
                }
            } catch (Exception ee) {
                //ee.printStackTrace();
            }
            try {
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (UnsupportedEncodingException ex) {
            //ex.printStackTrace();
        }
        return queryResult;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    protected static JSONObject readJsonFromUrl(String urlString) throws IOException, JSONException {
        //InputStream is = new URL(urlString).openStream();

//        System.out.println(System.getProperty("javax.net.ssl.trustStore"));
        //dbpedia used https instead of http
        URL url = new URL(urlString);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        Set<String> visitedUrls = new HashSet<>();
        boolean doneRedirecting = false;
        while (!doneRedirecting) {
            switch (c.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    // Follow redirect if not already visisted
                    String newLocation = c.getHeaderField("Location");
                    if (visitedUrls.contains(newLocation)) {
                        throw new RuntimeException(MessageFormat.format(
                                "Infinite redirect loop detected for URL", ""));
                    }
                    visitedUrls.add(newLocation);

                    url = new URL(newLocation);
                    c = (HttpURLConnection) url.openConnection();
                    break;
                default:
                    doneRedirecting = true;
                    break;
            }
        }

        InputStream is = c.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //System.out.println(jsonText);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public abstract String[] getUnwantedProperties();

    public String getName() {
        return name;
    }

    public static KnowledgeGraph getInstance() {
        return instance;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getUnwantedPropertiesString() {
        String unwantedPropertiesString = "";
        for (String unwantedProperty : this.getUnwantedProperties()) {
            unwantedPropertiesString += "" + unwantedProperty + ", ";
        }
        unwantedPropertiesString = unwantedPropertiesString.substring(0, unwantedPropertiesString.length() - 2);
        return unwantedPropertiesString;
    }

    
    
    
    
    
    public ArrayList<VariableSet> getPredicateList_EntityObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        //get predicates where the object is entity
        String unwantedPropertiesString = getUnwantedPropertiesString();
        String query = "SELECT DISTINCT ?p WHERE { "
                + "\n\t?s ?p ?o. " + " \nFilter(isIRI(?o)). " //Get only if ?o is entity
                + " \n\tFILTER (?p NOT IN(" + unwantedPropertiesString + "))."
                + "\n} LIMIT " + length + " OFFSET " + from;
        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> getPredicateList_NumberObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        //This one word for DP
        String query = "select distinct ?p where {\n"
                + "  ?s ?p ?o. ?s ?l ?t.\n"
                + "  FILTER isNumeric(?o). \n"
                + "} LIMIT " + length + " OFFSET " + from;

        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> getPredicateList_DateObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        String query = "select distinct ?p where {\n"
                + "  ?s ?p ?o. ?s ?l ?t.\n"
                + "  FILTER ( datatype(?o) = xsd:dateTime ) \n"
                + "} LIMIT " + length + " OFFSET " + from;
        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> getPredicateList_Literals(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        String query = "select distinct ?p where {\n"
                + "  ?s ?p ?o. ?s ?l ?t.\n"
                + "  FILTER isLiteral(?o). \n"
                + "} LIMIT " + length + " OFFSET " + from;
        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    public String getPredicateLabel(String predicate) {
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?l WHERE { ?p rdfs:label ?l. "
                    + "FILTER(?p=<" + predicate.trim() + ">). "
                    + "FILTER langMatches( lang(?l), \"EN\" )."
                    + "}";
            predicatesTriplesVarSets = kg.runQuery(query);
            return predicatesTriplesVarSets.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return (predicate.trim());
        }
    }

//    public static long getPredicateWeight(String predicate, String sType, String oType) {
//        String query = "";
//        //get weights
//        try {
//            query = "SELECT (count(?p) as ?count) WHERE { ?s ?p ?o . "
//                    + "?s rdf:type <" + sType + ">. "
//                    + "?o rdf:type <" + oType + ">. "
//                    + "FILTER(?p=<" + predicate.trim() + ">)."
//                    + ""
//                                        + "    FILTER NOT EXISTS {\n"
//                                        + "      ?s rdf:type ?type1 .\n"
//                                        + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
//                                        + "      FILTER NOT EXISTS {\n"
//                                        + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
//                                        + "      }\n"
//                                        + "    }.\n"
//                                        + "    FILTER EXISTS {\n"
//                                        + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
//                                        + "      ?s rdf:type ?superType1 .\n"
//                                        + "    }.\n"
//                    + "\n"
//                    + "   ?o      rdf:type              <" + oType + ">.\n"
//                                        + "    FILTER NOT EXISTS {\n"
//                                        + "      ?o rdf:type ?type2 .\n"
//                                        + "      ?type2 rdfs:subClassOf <" + oType + ">.\n"
//                                        + "      FILTER NOT EXISTS {\n"
//                                        + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
//                                        + "      }\n"
//                                        + "    }.\n"
//                                        + "    FILTER EXISTS {\n"
//                                        + "      <" + oType + "> rdfs:subClassOf ?superType2 .\n"
//                                        + "      ?o rdf:type ?superType2 .\n"
//                    + "    }.\n";
//            if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
//                query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n"
//                        + "  FILTER strstarts(str(<" + oType + "> ), str(" + Settings.requiredTypePrefix + ")).\n";
//            }
//
//            query += ""
//                    + "}";
//            predicatesTriplesVarSets = kg.runQuery(query);
//            return Long.valueOf(predicatesTriplesVarSets.get(0).getVariables().get(0).toString());
//        } catch (Exception e) {
//            return -1;
//        }
//    }

    public ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples = predicateTriples = new ArrayList<>();
        try {
            if (oType.equals("Number")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
                        + "\n"
                                                + "    FILTER NOT EXISTS {\n"
                                                + "      ?s rdf:type ?type1 .\n"
                                                + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
                                                + "      FILTER NOT EXISTS {\n"
                                                + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                                                + "      }\n"
                                                + "    }.\n"
                                                + "    FILTER EXISTS {\n"
                                                + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
                                                + "      ?s rdf:type ?superType1 .\n"
                                                + "    }.\n"
                                                + "\n"
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
                        + "\n"
                                                + "    FILTER NOT EXISTS {\n"
                                                + "      ?s rdf:type ?type1 .\n"
                                                + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
                                                + "      FILTER NOT EXISTS {\n"
                                                + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                                                + "      }\n"
                                                + "    }.\n"
                                                + "    FILTER EXISTS {\n"
                                                + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
                                                + "      ?s rdf:type ?superType1 .\n"
                                                + "    }.\n"
                        + "\n"
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
                        + "?o rdf:type <" + oType + ">. \n" //                        + "\n"
                                                + "\n"
                                                + "    FILTER NOT EXISTS {\n"
                                                + "      ?s rdf:type ?type1 .\n"
                                                + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
                                                + "      FILTER NOT EXISTS {\n"
                                                + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                                                + "      }\n"
                                                + "    }.\n"
                                                + "    FILTER EXISTS {\n"
                                                + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
                                                + "      ?s rdf:type ?superType1 .\n"
                                                + "    }.\n"
                                                + "\n"
                                                + "\n"
                                                + "\n"
                                                + "    FILTER NOT EXISTS {\n"
                                                + "      ?o rdf:type ?type2 .\n"
                                                + "      ?type2 rdfs:subClassOf <" + oType + ">.\n"
                                                + "      FILTER NOT EXISTS {\n"
                                                + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
                                                + "      }\n"
                                                + "    }.\n"
                                                + "    FILTER EXISTS {\n"
                                                + "      <" + oType + "> rdfs:subClassOf ?superType2 .\n"
                                                + "      ?o rdf:type ?superType2 .\n"
                                                + "    }.\n"
                                                + "\n"
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

    public ArrayList<PredicateContext> getPredicatesContext_EntityObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  ?o_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                + "   ?o      rdf:type              ?o_type.\n"
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
                                + "    FILTER NOT EXISTS {\n"
                                + "      ?s rdf:type ?type1 .\n"
                                + "      ?type1 rdfs:subClassOf ?s_type.\n"
                                + "      FILTER NOT EXISTS {\n"
                                + "         ?type1 owl:equivalentClass ?s_type.\n"
                                + "      }\n"
                                + "    FILTER EXISTS {\n"
                                + "      ?s_type rdfs:subClassOf ?superType1 .\n"
                                + "      ?s rdf:type ?superType1 .\n"
                                + "    }.\n"
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
                + "?s      rdf:type              ?s_type.\n" //                + "    FILTER NOT EXISTS {\n"
                                + "      ?s rdf:type ?type1 .\n"
                                + "      ?type1 rdfs:subClassOf ?s_type.\n"
                                + "      FILTER NOT EXISTS {\n"
                                + "         ?type1 owl:equivalentClass ?s_type.\n"
                                + "      }\n"
                                + "    FILTER EXISTS {\n"
                                + "      ?s_type rdfs:subClassOf ?superType1 .\n"
                                + "      ?s rdf:type ?superType1 .\n"
                                + "    }.\n"
                                + "\n"
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
                + "?s      rdf:type              ?s_type.\n" //                + "    FILTER NOT EXISTS {\n"
                                + "      ?s rdf:type ?type1 .\n"
                                + "      ?type1 rdfs:subClassOf ?s_type.\n"
                                + "      FILTER NOT EXISTS {\n"
                                + "         ?type1 owl:equivalentClass ?s_type.\n"
                                + "      }\n"
                                + "    FILTER EXISTS {\n"
                                + "      ?s_type rdfs:subClassOf ?superType1 .\n"
                                + "      ?s rdf:type ?superType1 .\n"
                                + "    }.\n"
                                + "\n"
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

    public static ArrayList<PredicateContext> filterOutNoisyContexts(ArrayList<PredicateContext> contexts) {
        ArrayList<PredicateContext> newContexts = new ArrayList<>();
        double mean = 0;
        double sum = 0;

        for (PredicateContext context : contexts) {
            sum += context.getWeight();
        }
        mean = sum / (double) contexts.size();

        for (PredicateContext context : contexts) {
            if (context.getWeight() >= mean) {
                newContexts.add(context);
            }
        }
        return newContexts;
    }

    
    
    
    
    
    
    
    
    
}
