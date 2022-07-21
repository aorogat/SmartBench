package settings;

import knowledgeGraphs.DBpedia;
import offLine.kg_explorer.explorer.DBpediaExplorer;

/**
 *
 * @author aorogat
 */
public class DBpedia_Settings extends Settings {

    public static void intializeSetttings() {
        Query_SLICING_SIZE = 450;
        benchmarkName = "Smart_450_COMPARE";

        //Database
        databaseName = "dbpedia";
        databaseURL = "jdbc:postgresql://localhost:5432/";
        databaseUser = "postgres";
        databasePassword = "admin";

        requiredTypePrefix = "dbo:";
        unwantedTypes = "dbo:Agent, dbo:Settlement";

        maxAnswerCardinalityAllowed = 500;
        SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

        
        name = "DBpedia";
        url = "https://dbpedia.org/sparql";
        default_graph_uri = "http%3A%2F%2Fdbpedia.org";
        
        explorer = new DBpediaExplorer(url);
        explorer = (DBpediaExplorer) explorer;
        knowledgeGraph = new DBpedia(url);
        knowledgeGraph = (DBpedia) knowledgeGraph;
        
        Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
        //Seed types
        Person = "dbo:Person";
        Place = "dbo:Place";

        popularityFilter = " ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
        popularityORDER = " ORDER BY DESC(?len)\n ";  //used in SPARQL Class
    }
}
