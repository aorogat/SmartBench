package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.CoordinatingConjunction;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PhraseRepresentationProcessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.EntityProcessing;
import java.util.ArrayList;
import lexiconGenerator.kg_explorer.explorer.SPARQL;
import lexiconGenerator.kg_explorer.ontology.KGOntology;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.CycleGraph;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class CycleQuestion {

    CycleGraph cycleGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String FCs_AND;
    String FCs_OR;
    private static String question_tagged;
    private static String fcs_tagged = null;

    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";

    String GPs_ASK; //for Graph Patterns
    String seed_withPrefix;
    String seed_without_prefix;
    String seed_type_withPrefix;
    String seed_type_without_prefix;

    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    int direction = 1;

    public CycleQuestion(CycleGraph cycleGraph) {
        this.cycleGraph = cycleGraph;

        seed_withPrefix = cycleGraph.getPath_1().getSubject().getValueWithPrefix();
        seed_without_prefix = cycleGraph.getPath_1().getSubject().getValue();

        seed_type_withPrefix = cycleGraph.getPath_1().getS_type();

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, cycleGraph.getPath_1().getSubject().getValueWithPrefix(), cycleGraph.getPath_1().getS_type());
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);
        
        seed_without_prefix = EntityProcessing.decide_quotes_with_type(seed_without_prefix, seed_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_with_type(somethingElseWithoutPrefix,  seed_type_withPrefix);
    }

    public void generateQuestions() throws Exception {
        //Forward generate nonacceptable-grammatical-questions
//        direction = FORWARD;
//        selectQuestions(CoordinatingConjunction.AND);
//        selectQuestions(CoordinatingConjunction.OR);

        direction = BACKWARD;
        selectQuestions(CoordinatingConjunction.AND);
        selectQuestions(CoordinatingConjunction.OR);
    }

    public String selectQuery(CycleGraph cycleGraph, String coordinatingConjunction) {
        String query = "";
        String triples = "";
        String t1 = cycleGraph.getPath_1().toQueryTriplePattern();
        String t2 = cycleGraph.getPath_2().toQueryTriplePattern();

        if (coordinatingConjunction.equals(CoordinatingConjunction.AND)) {
            triples = "\n\t" + t1 + "."
                    + "\n\t" + t2 + ".";
            if (direction == FORWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            } else if (direction == BACKWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getObject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR)) {
            triples += "\n\t{" + t1 + "} UNION \n\t {" + t2 + "}";
            if (direction == FORWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            } else if (direction == BACKWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getObject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        }
        return query;
    }

    public String askQuery_true_answer(CycleGraph cycleGraph, String coordinatingConjunction) {
        return selectQuery(cycleGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">");
    }

    public String askQuery_false_answer(CycleGraph cycleGraph, String coordinatingConjunction) {
        return selectQuery(cycleGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    public String countQuery(CycleGraph cycleGraph, String coordinatingConjunction) {
        return selectQuery(cycleGraph, coordinatingConjunction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }

    public void selectQuestions(String coordinatingConjunction) throws Exception {

        ArrayList<String> questions = new ArrayList<>();
        question_tagged = "";
        String whQuestion = selectWh_Questions(coordinatingConjunction, "NP");
        String selectQuery = selectQuery(cycleGraph, coordinatingConjunction);

        String QT = "";
        if (whQuestion.toLowerCase().startsWith("what")) {
            QT = GeneratedQuestion.QT_WHAT;
        } else if (whQuestion.toLowerCase().contains("whom ")) {
            QT = GeneratedQuestion.QT_WHOM;
        } else if (whQuestion.toLowerCase().startsWith("who")) {
            QT = GeneratedQuestion.QT_WHO;
        } else if (whQuestion.toLowerCase().startsWith("where")) {
            QT = GeneratedQuestion.QT_WHERE;
        } else if (whQuestion.toLowerCase().startsWith("when")) {
            QT = GeneratedQuestion.QT_WHEN;
        }

        if (whQuestion != null && !whQuestion.contains("null")) {
            allPossibleQuestions.add(new GeneratedQuestion(cycleGraph.getPath_1().getSubject().getValueWithPrefix(), cycleGraph.getPath_1().getS_type(), whQuestion, question_tagged, selectQuery, cycleGraph.toString(), 2, QT, GeneratedQuestion.SH_CYCLE));
//            GeneratedQuestion generatedQuestion = new GeneratedQuestion(whQuestion, whQuestion, cycleGraph.toString());
        }

        whQuestion = selectWh_Questions(coordinatingConjunction, "VP");
        if (whQuestion != null && !whQuestion.contains("null")) {
            allPossibleQuestions.add(new GeneratedQuestion(cycleGraph.getPath_1().getSubject().getValueWithPrefix(), cycleGraph.getPath_1().getS_type(), whQuestion, question_tagged, selectQuery, cycleGraph.toString(), 2, QT, GeneratedQuestion.SH_CYCLE));
//            GeneratedQuestion generatedQuestion = new GeneratedQuestion(whQuestion, whQuestion, cycleGraph.toString());
        }
    }

    public String selectWh_Questions(String coordinatingConjunction, String phrase) {
        String FCs = "";
        if (direction == FORWARD) {
            if (KGOntology.isSubtypeOf(seed_type_withPrefix, Settings.Person)) {
                if (phrase.equals("VP")) {
                    FCs = factConstraints_toString_VP_forward(cycleGraph, coordinatingConjunction);
                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>Who</qt> " + fcs_tagged + "?";
                    return "Who " + FCs + "?";
                } else if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);
                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>Who</qt> " + fcs_tagged + "?";
                    return "Who " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(seed_type_withPrefix, Settings.Place)) {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);
                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {
                    FCs = factConstraints_toString_VP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>Where</qt> " + fcs_tagged + "?";
                    return "Where " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(seed_type_withPrefix, Settings.Date)) {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>When</qt> " + fcs_tagged + "?";
                    return "When " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(seed_type_withPrefix, Settings.Number)) {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                }
            } else {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {
                    FCs = factConstraints_toString_VP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                }
            }

        } else if (direction == BACKWARD) {
            String FCs_tagged;
            String O_type = Settings.knowledgeGraph.getType(Settings.explorer, cycleGraph.getPath_1().getObject().getValueWithPrefix());
            if (KGOntology.isSubtypeOf(O_type, Settings.Person)) {
                if (phrase.equals("VP")) {

                    FCs = factConstraints_toString_VP_reverse(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    
                    if(FCs.trim().endsWith(" with"))
                    {
                        question_tagged = "<qt>With whom</qt> " + fcs_tagged .replace(" with ", " ").replace(" with<", "<").trim() + "?";
                        return "With whom " + (FCs + " ").replace(" with ", "") + "?";
                    }
                    else if(FCs.trim().endsWith(" For"))
                    {
                        question_tagged = "<qt>For whom</qt> " + fcs_tagged .replace(" for ", " ").replace(" for<", "<").trim() + "?";
                        return "For whom " + (FCs + " ").replace(" for ", "") + "?";
                    }
                    else
                    {
                        question_tagged = "<qt>By whom</qt> " + fcs_tagged .replace(" by ", " ").replace(" by<", "<").trim() + "?";
                        return "By whom " + (FCs + " ").replace(" by ", " ").trim() + "?";
                    }
                } else if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>Who</qt> " + fcs_tagged + "?";
                    return "Who " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(O_type, Settings.Place)) {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {

                    FCs = factConstraints_toString_VP_reverse(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>Where</qt> " + fcs_tagged + "?";
                    return "Where " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(O_type, Settings.Date)) {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>When</qt> " + fcs_tagged + "?";
                    return "When " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(O_type, Settings.Number)) {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                }
            } else {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    question_tagged = "<qt>What</qt> " + fcs_tagged + "?";
                    return "What " + FCs + "?";
                }
            }

        }

        return null;
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

    public static String factConstraints_toString_VP_forward(CycleGraph cycleGraph, String coorinatingConjunction) {
        String fcs_ = null;
        fcs_tagged = null;
        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {
            String p1_SO_VP = predicateNL_Path1.getPredicate_s_O_VP();
            String p2_SO_VP = predicateNL_Path2.getPredicate_s_O_VP();

            String p1_OS_VP = predicateNL_Path1.getPredicate_o_s_VP();
            String p2_OS_VP = predicateNL_Path2.getPredicate_o_s_VP();

            String O = cycleGraph.getPath_1().getObject().getValue();
            String O_type = cycleGraph.getPath_1().getO_type();
            O = EntityProcessing.decide_quotes_with_type(O, O_type);
            
            if (p1_SO_VP != null && p2_SO_VP != null) {
                fcs_ = p1_SO_VP + " " + coorinatingConjunction + " " + p2_SO_VP + " " + O;
                fcs_tagged = "<p>" + p1_SO_VP + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + p2_SO_VP + "</p> <o>" + O + "</o>";
            } else if (p1_OS_VP != null && p2_OS_VP != null) {
                fcs_ = O + " " + p1_OS_VP + " " + coorinatingConjunction + " " + p2_OS_VP;
                fcs_tagged = "<o>" + O + "</o> <p>" + p1_OS_VP + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + p2_OS_VP + "</p>";
            }

        }
        return fcs_;
    }

    public static String factConstraints_toString_VP_reverse(CycleGraph cycleGraph, String coorinatingConjunction) {
        String fcs_ = null;
        fcs_tagged = null;
        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {

            String p1_OS_VP = predicateNL_Path1.getPredicate_o_s_VP();
            String p2_OS_VP = predicateNL_Path2.getPredicate_o_s_VP();

            String p1_SO_VP = predicateNL_Path1.getPredicate_s_O_VP();
            String p2_SO_VP = predicateNL_Path2.getPredicate_s_O_VP();
            
            String O = cycleGraph.getPath_1().getSubject().getValue();
            String O_type = cycleGraph.getPath_1().getS_type();
            O = EntityProcessing.decide_quotes_with_type(O, O_type);
            

            if (p1_OS_VP != null && p2_OS_VP != null) {
//                if(!(p1_OS_VP.startsWith("was ")||p1_OS_VP.startsWith("were ")))
//                    p1_OS_VP = "was " + p1_OS_VP;
//                if(!(p2_OS_VP.startsWith("was ")||p2_OS_VP.startsWith("were ")))
//                    p2_OS_VP = "was " + p2_OS_VP;
                fcs_ = p1_OS_VP + " " + coorinatingConjunction + " " + p2_OS_VP + " " + O;
                fcs_tagged = "<p>" + p1_OS_VP + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + p2_OS_VP + "</p> <o>" + O + "</o>";
            } else if (p1_SO_VP != null && p2_SO_VP != null) {
                fcs_ = "was " + O + " " + p1_SO_VP + " " + coorinatingConjunction + " " + p2_SO_VP;
                fcs_tagged = "was <o>" + O + "</o> <p>" + p1_SO_VP + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + p2_SO_VP + "</p>";
            }

        }
        return fcs_;
    }

    public static String factConstraints_toString_NP_forward(CycleGraph cycleGraph, String coorinatingConjunction) {
        String fcs_ = null;
        fcs_tagged = null;
        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {
            String p1_SO_NP = predicateNL_Path1.getPredicate_s_O_NP();
            String p2_SO_NP = predicateNL_Path2.getPredicate_s_O_NP();

            String p1_OS_NP = predicateNL_Path1.getPredicate_o_s_NP();
            String p2_OS_NP = predicateNL_Path2.getPredicate_o_s_NP();
            
            String O = cycleGraph.getPath_1().getObject().getValue();
            String O_type = cycleGraph.getPath_1().getO_type();
            O = EntityProcessing.decide_quotes_with_type(O, O_type);
            

            if (p1_SO_NP != null && p2_SO_NP != null) {
                fcs_ = PhraseRepresentationProcessing.NP_without_Preposition(p1_SO_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_without_verb___first(p2_SO_NP) + " " + O;
                fcs_tagged = "<p>" + PhraseRepresentationProcessing.NP_without_Preposition(p1_SO_NP) + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + PhraseRepresentationProcessing.NP_without_verb___first(p2_SO_NP) + "</p> <o>" + O + "</o>";
            } else if (p1_OS_NP != null && p2_OS_NP != null) {
                String stype = cycleGraph.getPath_1().getS_type();
                if (KGOntology.isSubtypeOf(stype, Settings.Place)) {
                    fcs_ = O + " is his/her " + PhraseRepresentationProcessing.NP_only(p1_OS_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_only(p2_OS_NP);
                    fcs_tagged = "<o>" + O + "</o> is his/her <p>" + PhraseRepresentationProcessing.NP_only(p1_OS_NP) + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + PhraseRepresentationProcessing.NP_only(p2_OS_NP)+"</p>";
                } else {
                    fcs_ = O + " is its " + PhraseRepresentationProcessing.NP_only(p1_OS_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_only(p2_OS_NP);
                    fcs_tagged = "<o>" +O + "</o> is its <p>" + PhraseRepresentationProcessing.NP_only(p1_OS_NP) + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + PhraseRepresentationProcessing.NP_only(p2_OS_NP) + "</p>";
                }
            }

        }
        return fcs_;
    }

    public static String factConstraints_toString_NP_revers(CycleGraph cycleGraph, String coorinatingConjunction) {
        String fcs_ = null;
        fcs_tagged = null;
        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {

            String p1_OS_NP = predicateNL_Path1.getPredicate_o_s_NP();
            String p2_OS_NP = predicateNL_Path2.getPredicate_o_s_NP();

            String p1_SO_NP = predicateNL_Path1.getPredicate_s_O_NP();
            String p2_SO_NP = predicateNL_Path2.getPredicate_s_O_NP();
            
            String O = cycleGraph.getPath_1().getSubject().getValue();
            String O_type = cycleGraph.getPath_1().getS_type();
            O = EntityProcessing.decide_quotes_with_type(O, O_type);

            if (p1_OS_NP != null && p2_OS_NP != null) {
                fcs_ = PhraseRepresentationProcessing.NP_without_Preposition(p1_OS_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_without_verb___first(p2_OS_NP) + " " + O;
                fcs_tagged = "<p>" + PhraseRepresentationProcessing.NP_without_Preposition(p1_OS_NP) + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + PhraseRepresentationProcessing.NP_without_verb___first(p2_OS_NP) + "</p> <o>" + O + "</o>";
            } else if (p1_SO_NP != null && p2_SO_NP != null) {
                String stype = cycleGraph.getPath_1().getS_type();
                if (KGOntology.isSubtypeOf(stype, Settings.Place)) {
                    fcs_ = O + " is his/her " + PhraseRepresentationProcessing.NP_only(p1_SO_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_only(p2_SO_NP);
                    fcs_tagged = "<o>" + O + "</o> is his/her <p>" + PhraseRepresentationProcessing.NP_only(p1_SO_NP) + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + PhraseRepresentationProcessing.NP_only(p2_SO_NP) + "</p>";
                } else {
                    fcs_ = O + " is its " + PhraseRepresentationProcessing.NP_only(p1_SO_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_only(p2_SO_NP);
                    fcs_tagged = "<o>" + O + "</o> is its <p>" + PhraseRepresentationProcessing.NP_only(p1_SO_NP) + "</p> <cc>" + coorinatingConjunction + "</cc> <p>" + PhraseRepresentationProcessing.NP_only(p2_SO_NP) + "</p>";
                }

            }
        }
        return fcs_;
    }

    public static String getQuestion_tagged() {
        return question_tagged;
    }

    public static void setQuestion_tagged(String question_tagged) {
        CycleQuestion.question_tagged = question_tagged;
    }

    
    
}
