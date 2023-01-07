/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knowledgeGraphs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lexiconGenerator.kg_explorer.explorer.Explorer;
import static lexiconGenerator.kg_explorer.explorer.Explorer.kg;
import lexiconGenerator.kg_explorer.model.PredicateContext;
import lexiconGenerator.kg_explorer.model.PredicateTripleExample;
import benchmarkGenerator.subgraphShapeGenerator.model.Variable;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import settings.Settings;
import system.components.Branch;

/**
 *
 * @author ayaab
 */
public class WikidataKG extends KnowledgeGraph {

    public WikidataKG(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new WikidataKG(endpoint);
        }
        return (WikidataKG) instance;
    }

    @Override
    public String getType(Explorer explorer, String URI) {
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?s_type WHERE { "
                    + "    <" + URI + "> ps:P31 ?s_type. "
                    + "    FILTER NOT EXISTS {\n"
                    + "      <" + URI + "> ps:P31 ?type1 .\n"
                    + "      ?type1 ps:P279 ?s_type.\n"
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
    public ArrayList<PredicateContext> getPredicateContextFromTripleExample(String subjectURI, String predicateURI, String objectURI) {
        String unwantedPropertiesString = getUnwantedPropertiesString();
        long weight = 0;
        String query = "SELECT DISTINCT ?s_type  ?o_type  \n"
                + "WHERE{\n"
                + "    <" + subjectURI + ">      ps:P31              ?s_type.\n"
                + "    <" + objectURI + ">      ps:P31              ?o_type.\n"
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
    public String[] getUnwantedProperties() {
        unwantedProperties = new String[]{
            "rdfs:label",
            "rdfs:subPropertyOf",
            "rdfs:comment",
            "rdfs:label",
            "rdfs:seeAlso",
            "ps:P31",
            "ps:P279",
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
    public String getNodeLabel(Explorer explorer, String node) {
        return getPredicateLabel(node);
//        if (node.startsWith("<")) {
//            node = node.replace("<", "").replace(">", "");
//        }
//        String query = "";
//        //get labels
//        try {
//            query = "SELECT ?wdLabel WHERE {\n"
//                    + "  VALUES (?wdt) {(<" + node.trim() + ">)}\n"
//                    + "   ?wd wikibase:directClaim ?wdt .\n"
//                    + "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }\n"
//                    + "}";
//            ArrayList<VariableSet> varSet = kg.runQuery(query);
//            String n = varSet.get(0).getVariables().get(0).toString();
//
//            return n;
//        } catch (Exception e) {
//            return null;
//        }
    }

    @Override
    public ArrayList<VariableSet> runQuery(String queryString) {

        if (!queryString.toLowerCase().contains("prefix ")) {
            queryString = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                    + "PREFIX schema: <http://schema.org/>\n"
                    + queryString;
        }

        if (!queryString.toLowerCase().contains("limit ")) {
            queryString = queryString + "\n LIMIT " + (Settings.maxAnswerCardinalityAllowed + 10);
        }
        ArrayList<VariableSet> queryResult = new ArrayList<>();

        ArrayList<String> answersList = new ArrayList<>();

        try {
            JSONObject json = postFromCURL(Settings.url, queryString);
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

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.out.println(queryString);
                        }
                    }
                    queryResult.add(variableSet);
                }

            } catch (Exception e) {
                //e.printStackTrace();
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
                    System.out.println(queryString);
                }
            }
        } catch (Exception ee) {
            //ee.printStackTrace();
        }
        try {
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return queryResult;
    }

    private JSONObject postRequest(String url, String queryString) {
        String responseJSON = "";
        JSONObject jobj = null;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        queryString = "Select * {?s ?p ?o} limit 10";

        String json = "{\"query\": \"" + queryString + "\", "
                + "\"format\": \"application/sparql-results+json\", "
                + "\"timeout\": \"30000\""
                + "}";

        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        // set your POST request headers to accept json contents
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        try {
            // your closeablehttp response
            CloseableHttpResponse response = client.execute(httpPost);

            // print your status code from the response
            System.out.println(response.getStatusLine().getStatusCode());

            // take the response body as a json formatted string 
            responseJSON = EntityUtils.toString(response.getEntity());

            // convert/parse the json formatted string to a json object
            jobj = new JSONObject(responseJSON);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jobj;
    }

    static JSONObject postFromCURL(String url, String queryString) throws IOException, JSONException {

        JSONObject json = null;

        //////////////////
//        String command = "curl \""+url+"\" "
//                + "  -H \"authority: data.nobelprize.org\" "
//                + "  -H \"sec-ch-ua: \\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"98\\\", \\\"Google Chrome\\\";v=\\\"98\\\"\" "
//                + "  -H \"accept: application/sparql-results+json,*/*;q=0.9\" "
//                + "  -H \"content-type: application/x-www-form-urlencoded\" "
//                + "  -H \"sec-ch-ua-mobile: ?0\" "
//                + "  -H \"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\" "
//                + "  -H \"sec-ch-ua-platform: \\\"Windows\\\"\" "
//                + "  -H \"origin: https://data.nobelprize.org\" "
//                + "  -H \"sec-fetch-site: same-origin\" "
//                + "  -H \"sec-fetch-mode: cors\" "
//                + "  -H \"sec-fetch-dest: empty\" "
//                + "  -H \"referer: https://data.nobelprize.org/sparql\" "
//                + "  -H \"accept-language: en-CA,en;q=0.9,ar-EG;q=0.8,ar;q=0.7,en-GB;q=0.6,en-US;q=0.5\" "
//                + "  -H \"cookie: _ga=GA1.2.1783696672.1646674344; _gid=GA1.2.239545374.1646674344; _hjSessionUser_1004842=eyJpZCI6IjkxZmViOTMyLTNmNzQtNTc0Mi05MTg0LWMzMTQ1MGRhYjdlZiIsImNyZWF0ZWQiOjE2NDY2NzQzNDQxNTQsImV4aXN0aW5nIjp0cnVlfQ==\" "
//                + "  --data-raw \"query="+URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString())
//                + "\"  ";
//                
//        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
//        Process process = processBuilder.start();
//        try {
//            process.waitFor(5, TimeUnit.SECONDS);  // let the process run for 5 seconds
//            process.destroy();                     // tell the process to stop
//            process.waitFor(10, TimeUnit.SECONDS); // give it a chance to stop
//            process.destroyForcibly();             // tell the OS to kill the process
//            process.waitFor();                     // the process is now dead
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//
//        InputStream inputStream = process.getInputStream();
//        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//        /////////////////
//
//        System.out.println(result);
        URL url2 = new URL(Settings.url);
        HttpURLConnection http = (HttpURLConnection) url2.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("authority", "data.nobelprize.org");
        http.setRequestProperty("accept", "application/sparql-results+json,*/*;q=0.9");
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        http.setRequestProperty("sec-ch-ua-mobile", "?0");
        http.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        http.setRequestProperty("origin", "https://data.nobelprize.org");
        http.setRequestProperty("sec-fetch-site", "same-origin");
        http.setRequestProperty("sec-fetch-mode", "cors");
        http.setRequestProperty("sec-fetch-dest", "empty");
        http.setRequestProperty("referer", "https://data.nobelprize.org/sparql");
        http.setRequestProperty("accept-language", "en-CA,en;q=0.9,ar-EG;q=0.8,ar;q=0.7,en-GB;q=0.6,en-US;q=0.5");
        http.setRequestProperty("cookie", "_ga=GA1.2.1783696672.1646674344; _gid=GA1.2.239545374.1646674344; _hjSessionUser_1004842=eyJpZCI6IjkxZmViOTMyLTNmNzQtNTc0Mi05MTg0LWMzMTQ1MGRhYjdlZiIsImNyZWF0ZWQiOjE2NDY2NzQzNDQxNTQsImV4aXN0aW5nIjp0cnVlfQ==");

        String data = "query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString());

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        BufferedReader br = null;
        if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(http.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
        }

        String result = br.lines().collect(Collectors.joining());

//        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
//        System.out.println(result);
        http.disconnect();

        try {
            json = new JSONObject(result);

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

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
                    + "    ?o ps:P31 <" + T + ">.\n"
                    + "    ?o <" + P + "> ?n\n"
                    + "} \n"
                    + "ORDER BY " + order + "(?n)\n"
                    + "LIMIT 1\n"
                    + "OFFSET " + offset;
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);

            return varSet.get(0).getVariables().get(0).toString();
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
                return "" + (Double.parseDouble(entity) + Double.parseDouble(entity) / 2);
            } else if (entityType.equals(Settings.Date)) {
                String y = entity.substring(3, 4);
                int yy = Integer.parseInt(y) - 5;
                if (yy < 1) {
                    if (Integer.parseInt(y) != 1) {
                        yy = 1;
                    } else {
                        yy = 2;
                    }
                }
                entity = entity.substring(0, 3) + yy + entity.substring(4);
                return entity;
            } else if (entityType.equals(Settings.Literal)) {
                return entity + entity.substring(0, entity.length() / 2);
            } else {
                query = "SELECT ?similar WHERE { "
                        + "?similar ps:P31 <" + entityType + ">. "
                        + "FILTER(?similar!=<" + entity.trim() + ">). "
                        + "} LIMIT 1";
            }
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            return varSet.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public Branch getBranchOfType_SType_connectTo_OType(Explorer explorer, String S_type, String O_type, String predicateURI, int offset) {
        String query = "";
        //get labels
        try {
            if (O_type.equals("Number") || O_type.equals("Date") || O_type.equals(Settings.Literal)) {
                query = "SELECT DISTINCT ?s ?o WHERE {\n "
                        + "  ?s <" + predicateURI + "> ?o.  ?s ps:P31 <" + S_type + ">." + Settings.popularityFilter
                        + "\n}\n "
                        + Settings.popularityORDER
                        + "OFFSET " + offset;
            } else {
                query = "SELECT DISTINCT ?s ?o WHERE {\n "
                        + "  ?s <" + predicateURI + "> ?o.  ?s ps:P31 <" + S_type + ">.  ?o ps:P31 <" + O_type + ">.  " + Settings.popularityFilter
                        + "\n}\n "
                        + Settings.popularityORDER
                        + "OFFSET " + offset;
            }
            Explorer.predicatesTriplesVarSets = Settings.knowledgeGraph.runQuery(query);

            String s = Explorer.predicatesTriplesVarSets.get(0).getVariables().get(0).toString();
            String o = Explorer.predicatesTriplesVarSets.get(0).getVariables().get(1).toString();

            return new Branch(s, o, predicateURI, S_type, O_type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isASubtypeOf(Explorer explorer, String child, String parent) {
        if (child.toLowerCase().equals(parent.toLowerCase())) {
            return true;
        }
        if (child.equals(Settings.Number) || child.equals(Settings.Date) || child.equals(Settings.Literal)) {
            return false;
        }
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
                    + "  " + child + " ps:P279* " + parent + ".\n"
                    + "}";
            ArrayList<VariableSet> varSet = Settings.knowledgeGraph.runQuery(query);
            String answer = varSet.get(0).getVariables().get(0).getValueWithPrefix();
            return answer.equals("true");
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
                    + "?s ps:P31 <" + sType + ">. "
                    + "?o ps:P31 <" + oType + ">. "
                    + "FILTER(?p=<" + predicate.trim() + ">)."
                    + ""
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?s ps:P31 ?type1 .\n"
                    + "      ?type1 ps:P279 <" + sType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + sType + "> ps:P279 ?superType1 .\n"
                    + "      ?s ps:P31 ?superType1 .\n"
                    + "    }.\n"
                    + "\n"
                    + "   ?o      ps:P31              <" + oType + ">.\n"
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?o ps:P31 ?type2 .\n"
                    + "      ?type2 ps:P279 <" + oType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + oType + "> ps:P279 ?superType2 .\n"
                    + "      ?o ps:P31 ?superType2 .\n"
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
            return new JSONObject(jsonText);
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

    @Override
    public ArrayList<VariableSet> getPredicateList_EntityObjects(int from, int length) {

        //get predicates where the object is entity
        String unwantedPropertiesString = getUnwantedPropertiesString();
        String query = "SELECT DISTINCT ?p WHERE { "
                + "\n\t?s ?p ?o. " + " \nFilter(isIRI(?o)). " //Get only if ?o is entity
                + "\n\tFILTER strstarts(str(?p), str(wdt:))."
                + " \n\tFILTER (?p NOT IN(" + unwantedPropertiesString + "))."
                + "\n} LIMIT " + length + " OFFSET " + from;
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    @Override
    public ArrayList<VariableSet> getPredicateList_NumberObjects(int from, int length) {

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        //This one word for DP
        String query = "select distinct ?p where {\n"
                + "  ?s ?p ?o. ?s ?l ?t.\n"
                + "\n\tFILTER strstarts(str(?p), str(wdt:))."
                + "  FILTER isNumeric(?o). \n"
                + "} LIMIT " + length + " OFFSET " + from;

        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    @Override
    public ArrayList<VariableSet> getPredicateList_DateObjects(int from, int length) {

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        String query = "select distinct ?p where {\n"
                + "  ?s ?p ?o. ?s ?l ?t.\n"
                + "\n\tFILTER strstarts(str(?p), str(wdt:))."
                + "  FILTER ( datatype(?o) = xsd:dateTime ) \n"
                + "} LIMIT " + length + " OFFSET " + from;
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    @Override
    public ArrayList<VariableSet> getPredicateList_Literals(int from, int length) {

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        String query = "select distinct ?p where {\n"
                + "  ?s ?p ?o. ?s ?l ?t.\n"
                + "\n\tFILTER strstarts(str(?p), str(wdt:))."
                + "  FILTER isLiteral(?o). \n"
                + "} LIMIT " + length + " OFFSET " + from;
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    @Override
    public String getPredicateLabel(String predicate) {
        String query = "";
        //get labels
        try {
            query = "SELECT ?wdLabel WHERE {\n"
                    + "  VALUES (?wdt) {(<" + predicate.trim() + ">)}\n"
                    + "   ?wd wikibase:directClaim ?wdt .\n"
                    + "  SERVICE wikibase:label { bd:serviceParam wikibase:language \"en\". }\n"
                    + "}";
            predicatesTriplesVarSets = kg.runQuery(query);
            return predicatesTriplesVarSets.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(predicate.trim()).get();
                Element element = doc.select("span.wikibase-title-label").first();
                return element.text();
            } catch (Exception ignored) {
            }
            return (predicate.trim());
        }
    }

//    public static long getPredicateWeight(String predicate, String sType, String oType) {
//        String query = "";
//        //get weights
//        try {
//            query = "SELECT (count(?p) as ?count) WHERE { ?s ?p ?o . "
//                    + "?s ps:P31 <" + sType + ">. "
//                    + "?o ps:P31 <" + oType + ">. "
//                    + "FILTER(?p=<" + predicate.trim() + ">)."
//                    + ""
//                                        + "    FILTER NOT EXISTS {\n"
//                                        + "      ?s ps:P31 ?type1 .\n"
//                                        + "      ?type1 ps:P279 <" + sType + ">.\n"
//                                        + "      FILTER NOT EXISTS {\n"
//                                        + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
//                                        + "      }\n"
//                                        + "    }.\n"
//                                        + "    FILTER EXISTS {\n"
//                                        + "      <" + sType + "> ps:P279 ?superType1 .\n"
//                                        + "      ?s ps:P31 ?superType1 .\n"
//                                        + "    }.\n"
//                    + "\n"
//                    + "   ?o      ps:P31              <" + oType + ">.\n"
//                                        + "    FILTER NOT EXISTS {\n"
//                                        + "      ?o ps:P31 ?type2 .\n"
//                                        + "      ?type2 ps:P279 <" + oType + ">.\n"
//                                        + "      FILTER NOT EXISTS {\n"
//                                        + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
//                                        + "      }\n"
//                                        + "    }.\n"
//                                        + "    FILTER EXISTS {\n"
//                                        + "      <" + oType + "> ps:P279 ?superType2 .\n"
//                                        + "      ?o ps:P31 ?superType2 .\n"
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
    @Override
    public ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples = predicateTriples = new ArrayList<>();
        try {
            if (oType.equals("Number")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s ps:P31 <" + sType + ">. \n"
                        + "\n"
                        + "    FILTER NOT EXISTS {\n"
                        + "      ?s ps:P31 ?type1 .\n"
                        + "      ?type1 ps:P279 <" + sType + ">.\n"
                        + "      FILTER NOT EXISTS {\n"
                        + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                        + "      }\n"
                        + "    }.\n"
                        + "    FILTER EXISTS {\n"
                        + "      <" + sType + "> ps:P279 ?superType1 .\n"
                        + "      ?s ps:P31 ?superType1 .\n"
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
                        + "?s ps:P31 <" + sType + ">. \n"
                        + "\n"
                        + "    FILTER NOT EXISTS {\n"
                        + "      ?s ps:P31 ?type1 .\n"
                        + "      ?type1 ps:P279 <" + sType + ">.\n"
                        + "      FILTER NOT EXISTS {\n"
                        + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                        + "      }\n"
                        + "    }.\n"
                        + "    FILTER EXISTS {\n"
                        + "      <" + sType + "> ps:P279 ?superType1 .\n"
                        + "      ?s ps:P31 ?superType1 .\n"
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
                        + "?s ps:P31 <" + sType + ">. \n"
                        + "?o ps:P31 <" + oType + ">. \n" //                        + "\n"
                        + "\n"
                        + "    FILTER NOT EXISTS {\n"
                        + "      ?s ps:P31 ?type1 .\n"
                        + "      ?type1 ps:P279 <" + sType + ">.\n"
                        + "      FILTER NOT EXISTS {\n"
                        + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                        + "      }\n"
                        + "    }.\n"
                        + "    FILTER EXISTS {\n"
                        + "      <" + sType + "> ps:P279 ?superType1 .\n"
                        + "      ?s ps:P31 ?superType1 .\n"
                        + "    }.\n"
                        + "\n"
                        + "\n"
                        + "\n"
                        + "    FILTER NOT EXISTS {\n"
                        + "      ?o ps:P31 ?type2 .\n"
                        + "      ?type2 ps:P279 <" + oType + ">.\n"
                        + "      FILTER NOT EXISTS {\n"
                        + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
                        + "      }\n"
                        + "    }.\n"
                        + "    FILTER EXISTS {\n"
                        + "      <" + oType + "> ps:P279 ?superType2 .\n"
                        + "      ?o ps:P31 ?superType2 .\n"
                        + "    }.\n"
                        + "\n"
                        + "\n";
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
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      ps:P31              ?s_type.\n"
                + "    FILTER NOT EXISTS {\n"
                + "      ?s ps:P31 ?type1 .\n"
                + "      ?type1 ps:P279 ?s_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type1 owl:equivalentClass ?s_type.\n"
                + "      }\n"
                + "    }.\n"
                + "    FILTER EXISTS {\n"
                + "      ?s_type ps:P279 ?superType1 .\n"
                + "      ?s ps:P31 ?superType1 .\n"
                + "    }.\n"
                + "\n"
                + "   ?o      ps:P31              ?o_type.\n" //                + "    FILTER NOT EXISTS {\n"
                + "      ?o ps:P31 ?type2 .\n"
                + "      ?type2 ps:P279 ?o_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type2 owl:equivalentClass ?o_type.\n"
                + "      }\n"
                + "    FILTER EXISTS {\n"
                + "      ?o_type ps:P279 ?superType2 .\n"
                + "      ?o ps:P31 ?superType2 .\n"
                + "    }.\n";
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
                + "?s      ps:P31              ?s_type.\n"
                + "    FILTER NOT EXISTS {\n"
                + "      ?s ps:P31 ?type1 .\n"
                + "      ?type1 ps:P279 ?s_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type1 owl:equivalentClass ?s_type.\n"
                + "      }\n"
                + "    }.\n"
                + "    FILTER EXISTS {\n"
                + "      ?s_type ps:P279 ?superType1 .\n"
                + "      ?s ps:P31 ?superType1 .\n"
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
                + "?s      ps:P31              ?s_type.\n" //                + "    FILTER NOT EXISTS {\n"
                + "      ?s ps:P31 ?type1 .\n"
                + "      ?type1 ps:P279 ?s_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type1 owl:equivalentClass ?s_type.\n"
                + "      }\n"
                + "    FILTER EXISTS {\n"
                + "      ?s_type ps:P279 ?superType1 .\n"
                + "      ?s ps:P31 ?superType1 .\n"
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
                + "?s      ps:P31              ?s_type.\n" //                + "    FILTER NOT EXISTS {\n"
                + "      ?s ps:P31 ?type1 .\n"
                + "      ?type1 ps:P279 ?s_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type1 owl:equivalentClass ?s_type.\n"
                + "      }\n"
                + "    FILTER EXISTS {\n"
                + "      ?s_type ps:P279 ?superType1 .\n"
                + "      ?s ps:P31 ?superType1 .\n"
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
