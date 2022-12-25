/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

/**
 *
 * @author aorogat
 */
public class Configuration {
    
    public static String kg_name = "";
    
    //Would you like to intialize the database? [Y/N] \nIf this is the 1st time to run on this KG, select 1.
    public static boolean Database_Intializer = false;
    //Would you like to extract the predicates from KG? [Y/N]. If this is the 1st time to run on this KG, select 1.
    public static boolean Predicate_Extractor = false;
    //Would you like to extract the NL patterns from Text corpus? [Y/N]. If this is the 1st time to run on this KG, select 1.
    public static boolean NLP_Pattern_Extractor_Text_Corpus = false;
    //Would you like to extract predicate representations from Labels? [Y/N]. If this is the 1st time to run on this KG, select 1.
    public static boolean Predicate_Representations_Labels = false;
    //Would you like to extract predicate representations from Text_Corpus? [Y/N]. If this is the 1st time to run on this KG, select 1.
    public static boolean Predicate_Representations_Text_Corpus = false;
    
    
    public static boolean QT_What = false;
    public static boolean QT_Who = false;
    public static boolean QT_Whose = false;
    public static boolean QT_Whom = false;
    public static boolean QT_When = false;
    public static boolean QT_Where = false;
    public static boolean QT_Which = false;
    public static boolean QT_How = false;
    public static boolean QT_Yes_No = false;
    public static boolean QT_Request = false;
    public static boolean QT_Pruned = false;
    
    
    public static boolean SH_Single_Edge = false;
    public static boolean SH_Chain = false;
    public static boolean SH_Star = false;
    public static boolean SH_Tree = false;
    public static boolean SH_Flower = false;
    public static boolean SH_Cycle = false;
    public static boolean SH_Cycle_General = false;
    public static boolean SH_Star_Set = false;
    public static boolean SH_Star_Having = false;
    
    
    
    
}
