package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import lexiconGenerator.scrapping.model.PredicatesLexicon;
import benchmarkGenerator.kg_extractor.model.NodeType;
import benchmarkGenerator.kg_extractor.model.TriplePattern;
import benchmarkGenerator.kg_extractor.model.Variable;
import benchmarkGenerator.kg_extractor.model.subgraph.ChainGraph;
import benchmarkGenerator.kg_extractor.model.subgraph.CycleGraph;
import benchmarkGenerator.kg_extractor.model.subgraph.FlowerGraph;
import benchmarkGenerator.kg_extractor.model.subgraph.Graph;
import benchmarkGenerator.kg_extractor.model.subgraph.SingleEdgeGraph;
import benchmarkGenerator.kg_extractor.model.subgraph.StarGraph;
import benchmarkGenerator.kg_extractor.model.subgraph.TreeGraph;
import benchmarkGenerator.questionsGenerator.Benchmark;
import benchmarkGenerator.questionsGenerator.ChainQuestion;
import benchmarkGenerator.questionsGenerator.CycleGeneralQuestion;
import benchmarkGenerator.questionsGenerator.CycleQuestion;
import benchmarkGenerator.questionsGenerator.FlowerQuestion;
import benchmarkGenerator.questionsGenerator.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.SingleEdgeQuestion;
import benchmarkGenerator.questionsGenerator.StarQuestion;
import benchmarkGenerator.questionsGenerator.StarQuestionWithGroupBy;
import benchmarkGenerator.questionsGenerator.StarSetQuestion;
import benchmarkGenerator.questionsGenerator.TreeQuestion;
import settings.Configuration;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class ShapesGenerator {

    ArrayList<SingleEdgeGraph> singleEdgeGraphs = new ArrayList<>();
    static ArrayList<GeneratedQuestion> clearGeneratedQuestions = new ArrayList<>();

    static Random random = new Random();

    public static PredicatesLexicon lexicon = new PredicatesLexicon();

    static ChainGraph chainGraph = new ChainGraph();
    public static Benchmark benchmark = new Benchmark();

    static ArrayList<Branch> branchs;
    public static ArrayList<GeneratedQuestion> generatedQuestions = new ArrayList<>();

//    public static void main(String[] args) {
//        generateShapes();
//    }
    public static void generateShapes() throws Exception {
        System.out.println("\n\n\n\n=============================== SmartBench will generate the questions. ===============================");

        int benchmarkNumber = 37;

        for (int j = 480; j < 10000; j = j + 10) {

            clearGeneratedQuestions = new ArrayList<>();
            generatedQuestions = new ArrayList<>();

            RandomSeedGenerator.generateSeedList(j);
            branchs = RandomSeedGenerator.branchs;

            int currentSize = 0;
            int oldSize = 0;
            long numberOfGeneratedQuestions = 0;

            //Single-Edge Questions
            System.out.println("=========================== Generated Questions tell now: " + generatedQuestions.size() + " ===========================");
            benchmark.generatedBenchmark = generatedQuestions;
            int i = 0;
            for (Branch branch : branchs) {
//            if(generatedQuestions.size()>Settings.benchmarkSizeBeforePrune)
//                break;

                System.out.println("++++++++++++++++  Seed " + ++i + " of " + branchs.size() + " +++++++ Seed: " + branch.s);

//                Single-Edge
                if (Configuration.SH_Single_Edge) {
                    try {
                        generateSingleEdge(branch);

                    } catch (Exception e) {
                    }
                }
                if (Configuration.SH_Star) {
                    try {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 1);
//                    currentSize = generatedQuestions.size();
//                    if (MainBean.modified) {
//                        if (currentSize > oldSize) {
//                            oldSize = generatedQuestions.size();
                        generateStar(branch, 2);
                        if (Configuration.SH_Star_Having) {
                            generateStarWithGroupBy(branch, 2);
                        }
                        currentSize = generatedQuestions.size();
//                        }
//                    }
                        if (currentSize > oldSize) {
                            oldSize = generatedQuestions.size();
                            generateStar(branch, 3);
                            currentSize = generatedQuestions.size();
                        }
                        if (currentSize > oldSize) {
                            oldSize = generatedQuestions.size();
                            generateStar(branch, 4);
                            currentSize = generatedQuestions.size();
                        }
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 5);
//                    currentSize = generatedQuestions.size();
//                }
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 6);
//                    currentSize = generatedQuestions.size();
//                }

                    } catch (Exception e) {
                    }
                }

                if (Configuration.SH_Star_Set) {
                    try {
                        generateStarSet(branch, 1); //must be 1 for now

                    } catch (Exception e) {
                    }
                }
                if (Configuration.SH_Chain) {
                    try {
                        oldSize = generatedQuestions.size();
                        generateChain(branch, 2);
                        currentSize = generatedQuestions.size();
                        if (currentSize > oldSize) {
                            generateChain(branch, 3);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (Configuration.SH_Cycle) {
                    try {
                        generateCycle(branch);

                    } catch (Exception e) {
                    }
                }

                if (Configuration.SH_Cycle_General) {
                    try {
                        generateCycleGeneral(branch);

                    } catch (Exception e) {
                    }
                }

                if (Configuration.SH_Tree) {
                    try {
                        oldSize = generatedQuestions.size();
                        generateTree(branch, 2);
                        currentSize = generatedQuestions.size();
                        if (currentSize > oldSize) {
                            oldSize = generatedQuestions.size();
                            generateTree(branch, 3);
                            currentSize = generatedQuestions.size();
                        }
//                    if (currentSize > oldSize) {
//                        oldSize = generatedQuestions.size();
//                        testTree(branch, 4);
//                        currentSize = generatedQuestions.size();
//                    }

                    } catch (Exception e) {
                    }
                }

                if (Configuration.SH_Flower) {
                    try {
                        oldSize = generatedQuestions.size();
                        generateFlower(branch, 2);
                        currentSize = generatedQuestions.size();
                        if (currentSize > oldSize) {
                            oldSize = generatedQuestions.size();
                            generateFlower(branch, 3);
                            currentSize = generatedQuestions.size();
                        }
//                    if (currentSize > oldSize) {
//                        oldSize = generatedQuestions.size();
//                        testFlower(branch, 4);
//                        currentSize = generatedQuestions.size();
//                    }

                    } catch (Exception e) {
                    }
                }
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println("");

                System.out.println("============================== Benchmark =============================");
//        MainBean.output += "\n" + "================== Benchmark ===============\n";
                System.out.println("======================================================================");

                benchmark.generatedBenchmark = new ArrayList<>();
                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                    generatedQuestion.print();
                    if (generatedQuestion.getAnswerCardinality() > 0) {
                        benchmark.generatedBenchmark.add(generatedQuestion);
                    }
                }

                ArrayList<GeneratedQuestion> clearGeneratedQuestions = new ArrayList<>();
                for (GeneratedQuestion generatedQuestion : benchmark.generatedBenchmark) {
                    if (!(generatedQuestion.getQuestionType().equals(GeneratedQuestion.QT_HOW_MANY)
                            && generatedQuestion.getAnswers().get(0).equals("0"))) {
                        clearGeneratedQuestions.add(generatedQuestion);
                    }
                }

                benchmark.generatedBenchmark = clearGeneratedQuestions;
                BenchmarkJsonWritter.save(benchmark, Settings.benchmarkName + benchmarkNumber);
//            Pruner.prune(Settings.benchmarkName + ".json");
            }
            benchmarkNumber++;
        }
    }

    public static void addQuestions(ArrayList<GeneratedQuestion> gq) throws Exception {
        ArrayList<GeneratedQuestion> clearGeneratedQuestions = new ArrayList<>();
        for (GeneratedQuestion generatedQuestion : gq) {
            if (!(generatedQuestion.getQuestionType().equals(GeneratedQuestion.QT_HOW_MANY)
                    && generatedQuestion.getAnswers().get(0).equals("0"))
                    && generatedQuestion.getAnswerCardinality() > 0) {
                clearGeneratedQuestions.add(generatedQuestion);
            }
        }

        for (GeneratedQuestion generatedQuestion : clearGeneratedQuestions) {

            generatedQuestion.print();
        }

        generatedQuestions.addAll(clearGeneratedQuestions);

    }

    public static void generateSingleEdge(Branch branch) throws Exception {
        System.out.println("============================= Single-Edge Questions ==================================");

        //Numbers and dates are only supported in single-edge
        TriplePattern t0 = new TriplePattern(
                new Variable("s", branch.s, branch.s_type),
                new Variable("o", branch.o, branch.o_type),
                new Variable("l", branch.p, "_"), branch.s_type, branch.o_type);
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph(t0);

        String graphString = singleEdgeGraph.toString();

        if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
            System.out.println(graphString);
            SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, branch.s_type, branch.o_type);
            ArrayList<GeneratedQuestion> gq = singleEdgeQuestion.getAllPossibleQuestions();
            addQuestions(gq);
        } else {
        }
    }

    public static void generateChain(Branch branch, int n) throws Exception {
        System.out.println("============================= Chain (L=" + n + ") Questions ==================================");

        //Chain - Length 2
//            ArrayList<Graph> chainGraphs = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, true); //For one answer questions
        ArrayList<Graph> chainGraphs = new ArrayList<>();
        ArrayList<Graph> URIsEnd = chainGraph.generate(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, false);
        if (URIsEnd.size() > 5) {
            chainGraphs.add(URIsEnd.get(random.nextInt(URIsEnd.size()))); //For one or many answers questions
        } else if (URIsEnd.size() > 0) {
            chainGraphs.add(URIsEnd.get(0)); //For one or many answers questions
        }
        ArrayList<Graph> NumberEnd = chainGraph.generate(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.NUMBER, n, false);
        if (NumberEnd != null) {
            if (NumberEnd.size() > 5) {
                chainGraphs.add(NumberEnd.get(random.nextInt(NumberEnd.size()))); //For one or many answers questions
            } else if (NumberEnd.size() > 0) {
                chainGraphs.add(NumberEnd.get(0)); //For one or many answers questions
            }
        }

        ArrayList<Graph> DatesEnd = chainGraph.generate(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.DATE, n, false);
        if (DatesEnd != null) {
            if (DatesEnd.size() > 5) {
                chainGraphs.add(DatesEnd.get(random.nextInt(DatesEnd.size()))); //For one or many answers questions
            } else if (DatesEnd.size() > 0) {
                chainGraphs.add(DatesEnd.get(0)); //For one or many answers questions
            }
            int succededGraphs = 0;
            for (Graph chainGraph1 : chainGraphs) {

                chainGraph1 = (ChainGraph) chainGraph1;
                String graphString = chainGraph1.toString();
                if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                    System.out.println(graphString);
//                    MainBean.output += "\n" + graphString;
                    ChainQuestion chainQuestion = new ChainQuestion((ChainGraph) chainGraph1);
                    ArrayList<GeneratedQuestion> gq = chainQuestion.getAllPossibleQuestions();
                    addQuestions(gq);
//                    generatedQuestions.addAll(gq);
//                    for (GeneratedQuestion generatedQuestion : gq) {
//                        generatedQuestion.print();
//                    }
                    if (generatedQuestions.size() > 0) {
                        succededGraphs++;
                        if (succededGraphs > 0) {
                            return;
                        }
                    }

                } else {
                }

            }
        }
    }

    public static void generateStar(Branch branch, int n) throws Exception {
        System.out.println("============================= Star (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");

        StarGraph starGraph = new StarGraph();
//        int[] ends = new int[]{NodeType.LITERAL};
//        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase
        int[] ends = new int[]{NodeType.NUMBER};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1);//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.DATE};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.URI, NodeType.NUMBER, NodeType.DATE};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {

            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                if (graphString.contains("recorded in")) {
                    continue;
                }
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
//                MainBean.output += "\n" + graphString;
                StarQuestion starQuestion = new StarQuestion(currentStarGraph);
                ArrayList<GeneratedQuestion> gq = starQuestion.getAllPossibleQuestions();
                addQuestions(gq);
//                generatedQuestions.addAll(gq);
//                for (GeneratedQuestion generatedQuestion : gq) {
//                    generatedQuestion.print();
//                }
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 2) {
                        return;
                    }
                }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }
            } else {
            }
        }

    }

    public static void generateTree(Branch branch, int n) {
        System.out.println("============================= Tree (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");

        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1);//try 10 graphs because probability of failure increase

//        ends = new int[]{NodeType.DATE, NodeType.URI, NodeType.URI};
//        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
//
//        ends = new int[]{NodeType.NUMBER, NodeType.URI, NodeType.URI};
//        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
        int succededGraphs = 0;
        for (StarGraph currentStarGraph : rootStarGraphs) {

            try {
                ArrayList<StarGraph> tree_starGraphs = new ArrayList<>();
                tree_starGraphs.add(currentStarGraph);
                //add other
                int[] star2_ends = new int[]{NodeType.URI};
                StarGraph secondaryStarGraph = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, currentStarGraph.getStar().get(0).getObject().getValueWithPrefix(),
                        star2_ends, 1, 1, 2).get(0);
                tree_starGraphs.add(secondaryStarGraph);

                TreeGraph treeGraph = new TreeGraph(tree_starGraphs);

                String graphString = treeGraph.toString();
                if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                    TreeQuestion treeQuestion = new TreeQuestion(treeGraph);

                    ArrayList<GeneratedQuestion> gq = treeQuestion.getAllPossibleQuestions();
                    addQuestions(gq);
//                    generatedQuestions.addAll(gq);
//                    for (GeneratedQuestion generatedQuestion : gq) {
//                        generatedQuestion.print();
//                    }
                    if (generatedQuestions.size() > 0) {
                        succededGraphs++;
                        if (succededGraphs > 0) {
                            return;
                        }
                    }
                    System.out.println(treeGraph.getSeedType());
//                    MainBean.output += "\n" + graphString;
                    System.out.println(graphString);
//                    for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                        generatedQuestion.print();
//                    }
//
                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();
//                System.out.println("");
            }
        }

    }

    public static void generateCycle(Branch branch) throws Exception {
        System.out.println("============================= Cycle Questions ==================================");

        //Cycle 
        CycleGraph cycleGraph = new CycleGraph();
        ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
        int succededGraphs = 0;
        for (CycleGraph currecntCycleGraph : graphs) {

            String graphString = currecntCycleGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
//                MainBean.output += "\n" + graphString;
                CycleQuestion question = new CycleQuestion(currecntCycleGraph);
                question.generateQuestions();
                ArrayList<GeneratedQuestion> gq = question.getAllPossibleQuestions();
                addQuestions(gq);
//                generatedQuestions.addAll(gq);
//                for (GeneratedQuestion generatedQuestion : gq) {
//                    generatedQuestion.print();
//                }
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }

            } else {
            }

        }
    }

    public static void generateCycleGeneral(Branch branch) throws Exception {
        System.out.println("============================= Cycle General Questions ==================================");

        //Cycle 
        CycleGraph cycleGraph = new CycleGraph();
        ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
        int succededGraphs = 0;
        for (CycleGraph currecntCycleGraph : graphs) {

            String graphString = currecntCycleGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
//                MainBean.output += "\n" + graphString;
                CycleGeneralQuestion question = new CycleGeneralQuestion(currecntCycleGraph);
                question.generateQuestions();
                ArrayList<GeneratedQuestion> gq = question.getAllPossibleQuestions();
                addQuestions(gq);
//                generatedQuestions.addAll(gq);
//                for (GeneratedQuestion generatedQuestion : gq) {
//                    generatedQuestion.print();
//                }
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }

            } else {
            }

        }
    }

    public static void generateFlower(Branch branch, int n) {
        System.out.println("============================= Flower (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");

        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1);//try 10 graphs because probability of failure increase

//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
//        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
//
//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
//        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
//
//        ends = new int[]{NodeType.URI, NodeType.NUMBER, NodeType.DATE};
//        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 1));//try 10 graphs because probability of failure increase
        int succededGraphs = 0;
        for (StarGraph currentStarGraph : rootStarGraphs) {

            try {
                String starPredicates = "";
                for (TriplePattern tp : currentStarGraph.getStar()) {
                    starPredicates += "<" + tp.getPredicate().getValueWithPrefix() + ">, ";
                }

                CycleGraph cycleGraph = new CycleGraph();
                cycleGraph.setUnwantedPropertiesString(starPredicates);

                ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions

                for (CycleGraph currecntCycleGraph : graphs) {

                    FlowerGraph flowerGraph = new FlowerGraph(currentStarGraph, currecntCycleGraph);
                    String graphString = flowerGraph.toString();
                    if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                        System.out.println(graphString);
//                        MainBean.output += "\n" + graphString;
                        FlowerQuestion question = new FlowerQuestion(flowerGraph);
                        ArrayList<GeneratedQuestion> gq = question.getAllPossibleQuestions();
                        addQuestions(gq);
//                        generatedQuestions.addAll(gq);
//                        for (GeneratedQuestion generatedQuestion : gq) {
//                            generatedQuestion.print();
//                        }
                        if (generatedQuestions.size() > 0) {
                            succededGraphs++;
                            if (succededGraphs > 0) {
                                return;
                            }
                        }
                    } else {
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(".");
            }
        }

    }

    public static void generateStarSet(Branch branch, int n) throws Exception {
        System.out.println("============================= Star Set (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");

        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.NUMBER};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 2);//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.DATE};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {

            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
//                MainBean.output += "\n" + graphString;
                StarSetQuestion starQuestion = new StarSetQuestion(currentStarGraph, currentStarGraph.getSeedType());
                ArrayList<GeneratedQuestion> gq = starQuestion.getAllPossibleQuestions();
                addQuestions(gq);
//                generatedQuestions.addAll(gq);
//                for (GeneratedQuestion generatedQuestion : gq) {
//                    generatedQuestion.print();
//                }
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) { // the number must cover all types size
                        return;
                    }
                }
            } else {
            }

        }
    }

    public static void generateStarWithGroupBy(Branch branch, int n) throws Exception {
        System.out.println("============================= Star With Group By (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");

        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY_All_predicates_are_the_same(Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {

            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
//                MainBean.output += "\n" + graphString;
                StarQuestionWithGroupBy starQuestion = new StarQuestionWithGroupBy(currentStarGraph);
                ArrayList<GeneratedQuestion> gq = starQuestion.getAllPossibleQuestions();
                addQuestions(gq);
//                generatedQuestions.addAll(gq);
//                for (GeneratedQuestion generatedQuestion : gq) {
//                    generatedQuestion.print();
//                }
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
            } else {
            }
        }

    }

}
