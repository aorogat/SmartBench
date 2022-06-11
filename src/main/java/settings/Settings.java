package settings;

import knowledgeGraphs.BabelNet;
import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Explorer;
import knowledgeGraphs.DBpedia;
import offLine.kg_explorer.explorer.GEOExplorer;
import knowledgeGraphs.KnowledgeGraph;
import knowledgeGraphs.MAKG;
import offLine.kg_explorer.explorer.MAKGExplorer;
//import offLine.kg_explorer.explorer.NobelPrizeExplorer;
import knowledgeGraphs.NobelPrizeKG;
import knowledgeGraphs.WikidataKG;
import knowledgeGraphs.dbtune;
import offLine.kg_explorer.explorer.BabelNetExplorer;
import offLine.kg_explorer.explorer.WikidataExplorer;
import offLine.kg_explorer.explorer.dbtuneExplorer;
//import offLine.kg_explorer.explorer.WikidataKG;

/**
 *
 * @author aorogat
 */
public class Settings {
    
    public static final int DBPEDIA_ = 1;
    public static final int MAKG_ = 2;
    public static final int NOBEL_ = 3;
    public static final int GEO_ = 4;
    public static final int DBTUNE_ = 5;
    
    

    public static byte LABEL_NP_SO = 1;
    public static byte LABEL_NP_OS = 2;
    
    
    
    public static String Number = "Number";
    public static String Date = "Date";
    public static String Literal = "Literal";
    
    
    
    public static int Query_SLICING_SIZE = 100;
    public static String benchmarkName = "GeneralBenchmark";

    public static String databaseName = "DBName";
    public static String databaseURL = "jdbc:postgresql://localhost:5432/";
    public static String databaseUser = "postgres";
    public static String databasePassword = "admin";

    public static String requiredTypePrefix = "";
    public static String unwantedTypes = "";


    public static int maxAnswerCardinalityAllowed = 500;
    public static int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

    public static String name = "";
    public static String url = "";
    public static String default_graph_uri = "";
    public static Explorer explorer;
    public static KnowledgeGraph knowledgeGraph;
    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
    
//    Seed types
    public static String Person = "";
    public static String Place = "";

    public static String popularityFilter = "";  //used in SPARQL Class
    public static String popularityORDER = "";  //used in SPARQL Class
    
    
    //Wikidata
//    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "Wikidata_Smart_1";
//    
//    public static String databaseName = "Wikidata";
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
//    public static String name = "Wikidata";
//    public static String url = "http://localhost:9999/C:/Program%20Files/Git/bigdata/namespace/wdq/sparql";
//    public static String default_graph_uri = "";
//    public static WikidataExplorer explorer = new WikidataExplorer(url);
//    public static KnowledgeGraph knowledgeGraph = new WikidataKG(url);
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
//    
    

    //DBpedia
//    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "Smart_100_single";
//
//    public static String databaseName = "dbpedia";
//    public static String databaseURL = "jdbc:postgresql://localhost:5432/";
//    public static String databaseUser = "postgres";
//    public static String databasePassword = "admin";
//
//    public static String requiredTypePrefix = "dbo:";
//    public static String unwantedTypes = "dbo:Agent, dbo:Settlement";
//
//
//    public static int maxAnswerCardinalityAllowed = 500;
//    public static int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value
//
//    public static String name = "DBpedia";
//    public static String url = "https://dbpedia.org/sparql";
//    public static String default_graph_uri = "http%3A%2F%2Fdbpedia.org";
//    public static DBpediaExplorer explorer = new DBpediaExplorer(url);
//    public static DBpedia knowledgeGraph = new DBpedia(url);
//    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
//    
////    Seed types
//    public static String Person = "dbo:Person";
//    public static String Place = "dbo:Place";
//
//    public static String popularityFilter = " ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
//    public static String popularityORDER = " ORDER BY DESC(?len)\n ";  //used in SPARQL Class

////    //MAKG
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
//    
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
//    
//    //    //BabelNet
//    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "BabelNet_Smart_1";
//    
//    public static String databaseName = "BabelNet";
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
//    public static String name = "BabeNet";
//    public static String url = "https://babelnet.org/sparql/";
//    public static String default_graph_uri = "";
//    public static BabelNetExplorer explorer = new BabelNetExplorer(url);
//    public static BabelNet knowledgeGraph = new BabelNet(url);
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
////    
    //    //dbtune
//    public static int Query_SLICING_SIZE = 100;
//    public static String benchmarkName = "dbtune_Smart_1";
//    
//    public static String databaseName = "dbtune";
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
//    public static String name = "BabeNet";
//    public static String url = "http://dbtune.org/bbc/peel/cliopatria/sparql";
//    public static String default_graph_uri = "";
//    public static dbtuneExplorer explorer = new dbtuneExplorer(url);
//    public static dbtune knowledgeGraph = new dbtune(url);
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
//    
}
