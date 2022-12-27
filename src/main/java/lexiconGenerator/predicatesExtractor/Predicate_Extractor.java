package lexiconGenerator.predicatesExtractor;

import java.util.ArrayList;
import database.Database;
import lexiconGenerator.kg_explorer.model.ListOfPredicates;
import lexiconGenerator.kg_explorer.model.Predicate;
import lexiconGenerator.kg_explorer.model.PredicateContext;
import knowledgeGraphs.KnowledgeGraph;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.Graph;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class Predicate_Extractor {

    private static int numberOfNLExamples = 100;
    private static int minContextWeight = 20;
    int counter = 0;
    public static KnowledgeGraph kg;
    public static String endpoint;
    public static ArrayList<VariableSet> predicatesVariableSet_entity = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_number = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_date = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_Literals = new ArrayList<>();

    public static ArrayList<VariableSet> predicatesTriplesVarSets;
    public static ArrayList<Graph> result = new ArrayList<>();
    protected ArrayList<Predicate> predicateList = new ArrayList<>();

    public Predicate_Extractor() {
        kg = Settings.knowledgeGraph;
    }

    public static void main(String[] args) {
        Predicate_Extractor extractor = new Predicate_Extractor();
        extractor.exploreAllPredicates();
    }

    public void exploreAllPredicates() {
        ListOfPredicates predicats;
        int offset = Settings.Query_SLICING_SIZE;
        int from = 0;
        boolean firstIteration = true;

        predicats = getLexiconLHS(from, offset);
//        predicats.printHeader();
//        predicats.print();
    }

    public ListOfPredicates getLexiconLHS(int from, int length) {

        predicatesVariableSet_entity = fillPredicatesURI_EntityObjects(from, length);
        predicatesVariableSet_number = fillPredicatesURI_NumberObjects(from, length);
        predicatesVariableSet_date = fillPredicatesURI_DateObjects(from, length);
        predicatesVariableSet_Literals = fillPredicatesURI_Literals(from, length);

        //get predicates LHS (e.g., uri, label, contexts)
        int i = 0;
        Predicate predicateObject = new Predicate(Settings.explorer);
        ArrayList<PredicateContext> contexts;
        ListOfPredicates predicates = new ListOfPredicates(predicateList);

        for (VariableSet predicate : predicatesVariableSet_entity) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = Settings.knowledgeGraph.getPredicateLabel(uri);
            try {
                contexts = Settings.knowledgeGraph.getPredicatesContext_EntityObjects("<" + uri + ">");

                for (PredicateContext context : contexts) {
                    predicateObject = new Predicate(Settings.explorer);
                    predicateObject.setPredicateURI(uri);
                    predicateObject.setPredicate(predi);
                    predicateObject.setLabel(lab);

                    predicateObject.setPredicateContext(context);
                    predicateObject.print();
                    predicates.getPredicates().add(predicateObject);
                    try {
                        Database.storePredicates(predicateObject);
                    } catch (Exception e) {
                        System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //numbers
        for (VariableSet predicate : predicatesVariableSet_number) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = Settings.knowledgeGraph.getPredicateLabel(uri);
            contexts = Settings.knowledgeGraph.getPredicatesContext_NumberObjects("<" + uri + ">");

            for (PredicateContext context : contexts) {
                predicateObject = new Predicate(Settings.explorer);
                predicateObject.setPredicateURI(uri);
                predicateObject.setPredicate(predi);
                predicateObject.setLabel(lab);

                predicateObject.setPredicateContext(context);
                predicateObject.print();
                predicates.getPredicates().add(predicateObject);
                try {
                    Database.storePredicates(predicateObject);
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                }
            }

        }

        //dates
        for (VariableSet predicate : predicatesVariableSet_date) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = Settings.knowledgeGraph.getPredicateLabel(uri);
            contexts = Settings.knowledgeGraph.getPredicatesContext_DateObjects("<" + uri + ">");

            for (PredicateContext context : contexts) {
                predicateObject = new Predicate(Settings.explorer);
                predicateObject.setPredicateURI(uri);
                predicateObject.setPredicate(predi);
                predicateObject.setLabel(lab);

                predicateObject.setPredicateContext(context);
                predicateObject.print();
                predicates.getPredicates().add(predicateObject);
                try {
                    Database.storePredicates(predicateObject);
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                }
            }

        }

        //Literals
        for (VariableSet predicate : predicatesVariableSet_Literals) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = Settings.knowledgeGraph.getPredicateLabel(uri);
            contexts = Settings.knowledgeGraph.getPredicatesContext_Literals("<" + uri + ">");

            for (PredicateContext context : contexts) {
                predicateObject = new Predicate(Settings.explorer);
                predicateObject.setPredicateURI(uri);
                predicateObject.setPredicate(predi);
                predicateObject.setLabel(lab);

                predicateObject.setPredicateContext(context);
                predicateObject.print();
                predicates.getPredicates().add(predicateObject);
                try {
                    Database.storePredicates(predicateObject);
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                }
            }

        }

        predicates.setPredicates(predicateList);
        return predicates;
    }

    public ArrayList<VariableSet> fillPredicatesURI_EntityObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(Settings.knowledgeGraph.getPredicateList_EntityObjects(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> fillPredicatesURI_NumberObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(Settings.knowledgeGraph.getPredicateList_NumberObjects(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> fillPredicatesURI_DateObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(Settings.knowledgeGraph.getPredicateList_DateObjects(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> fillPredicatesURI_Literals(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(Settings.knowledgeGraph.getPredicateList_Literals(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

}
