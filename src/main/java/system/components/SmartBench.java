package system.components;

import lexiconGenerator.nlPatternExtractor.NLP_Pattern_Extractor;
import lexiconGenerator.predicatesExtractor.Predicate_Extractor;
import database.Database;
import database.DatabaseIntializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import settings.Configuration;
import settings.DBpedia_Settings;
import settings.DBtune_Settings;
import settings.GEO_Settings;
import settings.MAKG_Settings;
import settings.Nobel_Settings;

/**
 *
 * @author aorogat
 */
public class SmartBench {

    public static void main(String[] args) {

        lood_conf_file();

        switch (Configuration.kg_name) {
            case "DBPedia":
                DBpedia_Settings.intializeSetttings();
                break;
            case "MAKG":
                MAKG_Settings.intializeSetttings();
                break;
            case "NOBEL":
                GEO_Settings.intializeSetttings();
                break;
            case "GEOData":
                Nobel_Settings.intializeSetttings();
                break;
            case "DBTune":
                DBtune_Settings.intializeSetttings();
                break;
        }

        Scanner in = new Scanner(System.in);

        try {
            String input = "";
            if (Configuration.Database_Intializer) {
                System.out.println("Are you sure? This will delete all previous data in database [Y/N]");
                input = in.next();
                if (input.toLowerCase().equals("y")) {
                    DatabaseIntializer.intialize();
                }
            }

            if (Configuration.Predicate_Extractor) {
                Predicate_Extractor extractor = new Predicate_Extractor();
                extractor.exploreAllPredicates();
            }

            if (Configuration.NLP_Pattern_Extractor_Text_Corpus) {
                NLP_Pattern_Extractor.extractNLPPatterns();
            }

            if (Configuration.Predicate_Representations_Labels) {
                Predicate_Representation_Extractor.fill_from_Labels_VP_and_NP_S_O();
                Predicate_Representation_Extractor.fill_from_Labels_VP_O_S();
                Database.populateLexicon();
            }

            if (Configuration.Predicate_Representations_Text_Corpus) {
                Predicate_Representation_Extractor.fill_from_text_corpus_VP();
                Predicate_Representation_Extractor.fill_from_text_corpus_NP();
                Database.populateLexicon();
            }

            //Online
            ShapesGenerator.generateShapes(); //generate and save them and prune and save again

        } catch (Exception ex) {
            Logger.getLogger(SmartBench.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void lood_conf_file() {
        Properties prop = new Properties();
        InputStream inputt = SmartBench.class.getClassLoader().getResourceAsStream("conf.properties");
        try {
            prop.load(inputt);
        } catch (IOException ex) {
            Logger.getLogger(SmartBench.class.getName()).log(Level.SEVERE, null, ex);
        }

        Configuration.kg_name = prop.getProperty("kg_name");

        Configuration.Predicate_Extractor = prop.getProperty("Predicate_Extractor").trim().equals("1");
        Configuration.NLP_Pattern_Extractor_Text_Corpus = prop.getProperty("NLP_Pattern_Extractor_Text_Corpus").trim().equals("1");
        Configuration.Predicate_Representations_Labels = prop.getProperty("Predicate_Representations_Labels").trim().equals("1");
        Configuration.Predicate_Representations_Text_Corpus = prop.getProperty("Predicate_Representations_Text_Corpus").trim().equals("1");

        Configuration.QT_What = prop.getProperty("What").trim().equals("1");
        Configuration.QT_Who = prop.getProperty("Who").trim().equals("1");
        Configuration.QT_Whose = prop.getProperty("Whose").trim().equals("1");
        Configuration.QT_Whom = prop.getProperty("Whom").trim().equals("1");
        Configuration.QT_When = prop.getProperty("When").trim().equals("1");
        Configuration.QT_Where = prop.getProperty("Where").trim().equals("1");
        Configuration.QT_Which = prop.getProperty("Which").trim().equals("1");
        Configuration.QT_How = prop.getProperty("How").trim().equals("1");
        Configuration.QT_Yes_No = prop.getProperty("Yes_No").trim().equals("1");
        Configuration.QT_Request = prop.getProperty("Request").trim().equals("1");
        Configuration.QT_Pruned = prop.getProperty("Pruned").trim().equals("1");

        Configuration.SH_Single_Edge = prop.getProperty("Single_Edge").trim().equals("1");
        Configuration.SH_Chain = prop.getProperty("Chain").trim().equals("1");
        Configuration.SH_Star = prop.getProperty("Star").trim().equals("1");
        Configuration.SH_Tree = prop.getProperty("Tree").trim().equals("1");
        Configuration.SH_Flower = prop.getProperty("Flower").trim().equals("1");
        Configuration.SH_Cycle = prop.getProperty("Cycle").trim().equals("1");
        Configuration.SH_Cycle_General = prop.getProperty("Cycle_General").trim().equals("1");
        Configuration.SH_Star_Set = prop.getProperty("Star_Set").trim().equals("1");
        Configuration.SH_Star_Having = prop.getProperty("Star_Having").trim().equals("1");
    }

}
