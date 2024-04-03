package settings;

import knowledgeGraphs.DBLP;
import knowledgeGraphs.YAGO;
import lexiconGenerator.kg_explorer.explorer.DBLPExplorer;
import lexiconGenerator.kg_explorer.explorer.YagoExplorer;

public class Dblp_Settings extends Settings {
    public static void intializeSetttings() {
        Query_SLICING_SIZE = 1500;
        benchmarkName = "Smart_dblp_";

        //Database
        databaseName = "dblp";
        databaseURL = "jdbc:postgresql://localhost:5432/";
        databaseUser = "postgres";
        databasePassword = "admin";

        requiredTypePrefix = null;
        unwantedTypes = null;

        maxAnswerCardinalityAllowed = 500;
        SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value


        name = "dblp";
        url = "http://206.12.95.86:8894/sparql";
        default_graph_uri = "";

        explorer = new DBLPExplorer(url);
        explorer = (DBLPExplorer) explorer;
        knowledgeGraph = new DBLP(url);
        knowledgeGraph = (DBLP) knowledgeGraph;

        Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
        //Seed types
        Person = "<https://dblp.org/rdf/schema#Person>";
        Place = "";

        popularityFilter = "";  //used in SPARQL Class
        popularityORDER = "";  //used in SPARQL Class
        mu = 1.2;
    }

}
