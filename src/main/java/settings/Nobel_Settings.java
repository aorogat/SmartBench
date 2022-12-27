package settings;

import knowledgeGraphs.NobelPrizeKG;
import lexiconGenerator.kg_explorer.explorer.MAKGExplorer;

/**
 *
 * @author aorogat
 */
public class Nobel_Settings extends Settings {
    
   //    //Nobel Prize
//    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "Nobel_Smart_1212";
//    
//    public static String databaseName = "nobel";
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
//    public static String name = "NobelPrize";
//    public static String url = "https://data.nobelprize.org/store/sparql";
//    public static String default_graph_uri = "";
//    public static Explorer explorer = new MAKGExplorer(url);
//    public static KnowledgeGraph knowledgeGraph = new NobelPrizeKG(url);
//    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
//
//    //Seed types
//    public static String Person = "foaf:Person";
//    public static String Place = "foaf:Place";
//    public static String Number = "Number";
//    public static String Date = "Date";
//    public static String Literal = "Literal";
//    
//    
//    public static String popularityFilter = "";//" ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
//    public static String popularityORDER = "";//" ORDER BY DESC(?len)\n ";  //used in SPARQL Class
    
    public static void intializeSetttings() {
        Query_SLICING_SIZE = 100;
        benchmarkName = "Nobel_5";

        //Database
        databaseName = "nobel";
        databaseURL = "jdbc:postgresql://localhost:5432/";
        databaseUser = "postgres";
        databasePassword = "admin";

        requiredTypePrefix = null;
        unwantedTypes = null;

        maxAnswerCardinalityAllowed = 500;
        SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

        
        name = "NobelPrize";
        url = "https://data.nobelprize.org/store/sparql";
        default_graph_uri = "";
        
        explorer = new MAKGExplorer(url);
        explorer = (MAKGExplorer) explorer;
        knowledgeGraph = new NobelPrizeKG(url);
        knowledgeGraph = (NobelPrizeKG) knowledgeGraph;
        
        Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
        //Seed types
        Person = "foaf:Person";
        Place = "foaf:Place";

        popularityFilter = "";  //used in SPARQL Class
        popularityORDER = "";  //used in SPARQL Class
        mu = 1.2;
    }
}
