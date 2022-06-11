package system.components;

import database.Database;
import database.DatabaseIntializer;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import settings.DBpedia_Settings;
import settings.DBtune_Settings;
import settings.GEO_Settings;
import settings.MAKG_Settings;
import settings.Nobel_Settings;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class SmartBench {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        System.out.println("Which KG you will use?");
        System.out.println("1- DBPEDIA  2-MAKG  3-NOBEL 4-GEOData   5-DBTUNE [Enter the number (e.g., 1 for DBpedia]");
        int kgSelected = in.nextInt();

        switch (kgSelected) {
            case Settings.DBPEDIA_:
                DBpedia_Settings.intializeSetttings();
                break;
            case Settings.MAKG_:
                MAKG_Settings.intializeSetttings();
                break;
            case Settings.GEO_:
                GEO_Settings.intializeSetttings();
                break;
            case Settings.NOBEL_:
                Nobel_Settings.intializeSetttings();
                break;
            case Settings.DBTUNE_:
                DBtune_Settings.intializeSetttings();
                break;
        }

        try {
            System.out.println("Would you like to intialize the database? [Y/N] \nIf this is the 1st time to run on this KG, select Y.");
            System.out.println("Make sure that PostgreSQL is running and you created an empty Database.");
            String input = "";
            input = in.next();
            if (input.toLowerCase().equals("y")) {
                System.out.println("Are you sure? This will delete all previous data in database [Y/N]");
                input = in.next();
                if (input.toLowerCase().equals("y")) {
                    DatabaseIntializer.intialize();
                }
            }

            //OFFLINE
            //Step 1
            System.out.println("Would you like to extract the predicates from KG? [Y/N] \nIf this is the 1st time to run on this KG, select Y.");
            input = in.next();
            if (input.toLowerCase().equals("y")) {
                Predicate_Extractor extractor = new Predicate_Extractor();
                extractor.exploreAllPredicates();
            }
            //Step 2
            System.out.println("Would you like to extract the NL patterns from Text corpus? [Y/N] \nIf this is the 1st time to run on this KG, select Y.");
            input = in.next();
            if (input.toLowerCase().equals("y")) {
                NLP_Pattern_Extractor.extractNLPPatterns();
            }
            //Step 3
            System.out.println("Would you like to extract predicate representations from Labels? [Y/N] \nIf this is the 1st time to run on this KG, select Y.");
            input = in.next();
            if (input.toLowerCase().equals("y")) {
                Predicate_Representation_Extractor.fill_from_Labels_VP_and_NP_S_O();
                Predicate_Representation_Extractor.fill_from_Labels_VP_O_S();
                Database.populateLexicon();
            }
            System.out.println("Would you like to extract predicate representations from Test Corpus? [Y/N] \nIf this is the 1st time to run on this KG, select Y.");
            input = in.next();
            if (input.toLowerCase().equals("y")) {
                Predicate_Representation_Extractor.fill_from_text_corpus_VP();
                Predicate_Representation_Extractor.fill_from_text_corpus_NP();
                Database.populateLexicon();
            }

            //Online
            System.out.println("\n\n\n\n=============================== SmartBench will generate the questions. ===============================");
            ShapesGenerator.generateShapes(); //generate and save them and prune and save again

        } catch (Exception ex) {
            Logger.getLogger(SmartBench.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
