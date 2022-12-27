package settings;

import knowledgeGraphs.MAKG;
import lexiconGenerator.kg_explorer.explorer.GEOExplorer;

/**
 *
 * @author aorogat
 */
public class GEO_Settings extends Settings {
    
   //Geodata
//    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "Geo_Smart_1212";
//    
//    public static String databaseName = "geodata";
//    public static String databaseURL = "jdbc:postgresql://localhost:5432/";
//    public static String databaseUser = "postgres";
//    public static String databasePassword = "admin";
//    
//    
//    public static String requiredTypePrefix = null;//"dbo:";
//    public static String unwantedTypes = null;//"dbo:Agent, dbo:Settlement";
//    
//    
//    public static byte LABEL_NP_SO = 1;
//    public static byte LABEL_NP_OS = 2;
//    
//    public static int maxAnswerCardinalityAllowed = 500;
//    public static int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value
//
//    public static String name = "GEO";
//    public static String url = "http://linkedgeodata.org/sparql/";
//    public static String default_graph_uri = "";
//    public static GEOExplorer explorer = new GEOExplorer(url);
//    public static MAKG knowledgeGraph = new MAKG(url);
//    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
//
//    //Seed types
//    public static String Person = "<http://mag.graph/class/Author>";
//    public static String Place = "<http://linkedgeodata.org/ontology/Amenity>";
//    public static String Number = "Number";
//    public static String Date = "Date";
//    public static String Literal = "Literal";
//    
//    
//    public static String popularityFilter = "";//" ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
//    public static String popularityORDER = "";//" ORDER BY DESC(?len)\n ";  //used in SPARQL Class
//    
    
    public static void intializeSetttings() {
        Query_SLICING_SIZE = 100;
        benchmarkName = "GEO_5";

        //Database
        databaseName = "geodata";
        databaseURL = "jdbc:postgresql://localhost:5432/";
        databaseUser = "postgres";
        databasePassword = "admin";

        requiredTypePrefix = null;
        unwantedTypes = null;

        maxAnswerCardinalityAllowed = 500;
        SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

        
        name = "GEO";
        url = "http://linkedgeodata.org/sparql/";
        default_graph_uri = "";
        
        explorer = new GEOExplorer(url);
        explorer = (GEOExplorer) explorer;
        knowledgeGraph = new MAKG(url);
        knowledgeGraph = (MAKG) knowledgeGraph;
        
        Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
        //Seed types
        Person = "";
        Place = "<http://linkedgeodata.org/ontology/Amenity>";

        popularityFilter = "";  //used in SPARQL Class
        popularityORDER = "";  //used in SPARQL Class
        mu = 1.2;
    }
}
