package system.components;

import lexiconGenerator.nlPatternExtractor.NLPatternExtractor;
import lexiconGenerator.predicatesExtractor.Predicate_Extractor;
import database.Database;
import database.DatabaseIntializer;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.base.Sys;
import settings.*;

/**
 *
 * @author aorogat
 */
public class SmartBench {

    public static void main(String[] args) {

        lood_conf_file();

        //print welcome message
        welcome();

        switch (Configuration.kg_name) {
            case "DBPedia":
                DBpedia_Settings.intializeSetttings();
                break;
            case "MAKG":
                MAKG_Settings.intializeSetttings();
                break;
            case "NOBEL":
                Nobel_Settings.intializeSetttings();
                break;
            case "GEOData":
                GEO_Settings.intializeSetttings();
                break;
            case "DBTune":
                DBtune_Settings.intializeSetttings();
                break;
            case "DBLP":
                Dblp_Settings.intializeSetttings();
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
                NLPatternExtractor.extractNLPPatterns();
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

    private static void welcome() {
        //Print Maestro Logo in stars pattern
        System.out.println("Welcome to Maestro, a highly advanced benchmark generation system designed to support question answering over knowledge graphs.");
        System.out.println("Our software is a powerful and reliable tool that allows users to generate a new benchmark for any given knowledge graph, with the option to include a text corpus for added coverage.");
        System.out.println("With Maestro, you can be confident that your benchmark will include all relevant properties of natural language questions and queries, ensuring that your QA system is thoroughly tested.");
        System.out.println("In addition, Maestro generates high-quality natural language questions with various utterances, making it a top choice for evaluating the performance of QA systems.");
        System.out.println("We are thrilled to have you join us as we strive to enhance the functionality and effectiveness of Maestro.");
        System.out.println("Thank you for choosing our software and we look forward to supporting you in your endeavors.");
        System.out.println("*************************************************************************************************************************************************************************************");
        try {
            Thread.sleep(5000); // 5 seconds
        } catch (InterruptedException e) {
            // Do something here if you want to handle the exception
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

        Configuration.Database_Intializer = prop.getProperty("Database_Intializer").trim().equals("1");
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
        
        //Read number of branches for star
        String noOfBranches = prop.getProperty("noOfBranches");
        if (noOfBranches != null) {
            String[] n = noOfBranches.split(",");
            for (String i : n) {
                Configuration.noOfBranches.add(Byte.valueOf(i.trim()));
            }
        }
        
        //Read number of branches for Tree root star
        String rootNoOfBranchesTree = prop.getProperty("rootNoOfBranchesTree");
        if (rootNoOfBranchesTree != null) {
            String[] n = noOfBranches.split(",");
            for (String i : n) {
                Configuration.rootNoOfBranchesTree.add(Byte.valueOf(i.trim()));
            }
        }
        
        //Read number of branches for Flower root star
        String rootNoOfBranchesFlower = prop.getProperty("rootNoOfBranchesFlower");
        if (rootNoOfBranchesFlower != null) {
            String[] n = noOfBranches.split(",");
            for (String i : n) {
                Configuration.rootNoOfBranchesFlower.add(Byte.valueOf(i.trim()));
            }
        }
        
        
        
        //Read max length for chain
        Configuration.chainMaxLength = Byte.valueOf(prop.getProperty("chainMaxLength"));
    }

}
