package settings;

import knowledgeGraphs.MAKG;
import offLine.kg_explorer.explorer.MAKGExplorer;

/**
 *
 * @author aorogat
 */
public class MAKG_Settings extends Settings {
    
    //    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "MAKG_Smart_4";
//    
//    public static String databaseName = "makg";
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
//    public static String name = "MAKG";
//    public static String url = "https://makg.org/sparql";
//    public static String default_graph_uri = "";
//    public static MAKGExplorer explorer = new MAKGExplorer(url);
//    public static MAKG knowledgeGraph = new MAKG(url);
//    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
//
//    //Seed types
//    public static String Person = "<http://mag.graph/class/Author>";
//    public static String Place = "<http://mag.graph/class/ConferenceInstance>";
//    public static String Number = "Number";
//    public static String Date = "Date";
//    public static String Literal = "Literal";
//    
//    
//    public static String popularityFilter = "";//" ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
//    public static String popularityORDER = "";//" ORDER BY DESC(?len)\n ";  //used in SPARQL Class

    public static void intializeSetttings() {
        Query_SLICING_SIZE = 100;
        benchmarkName = "MAKG_5";

        //Database
        databaseName = "makg";
        databaseURL = "jdbc:postgresql://localhost:5432/";
        databaseUser = "postgres";
        databasePassword = "admin";

        requiredTypePrefix = null;
        unwantedTypes = null;

        maxAnswerCardinalityAllowed = 500;
        SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

        
        name = "MAKG";
        url = "https://makg.org/sparql";
        default_graph_uri = "";
        
        explorer = new MAKGExplorer(url);
        explorer = (MAKGExplorer) explorer;
        knowledgeGraph = new MAKG(url);
        knowledgeGraph = (MAKG) knowledgeGraph;
        
        Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
        //Seed types
        Person = "<http://mag.graph/class/Author>";
        Place = "<http://mag.graph/class/ConferenceInstance>";

        popularityFilter = "";  //used in SPARQL Class
        popularityORDER = "";  //used in SPARQL Class
        mu = 1.2;
    }
}
