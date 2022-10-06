package online.nl_generation;

import edu.stanford.nlp.process.Tokenizer;
import java.util.ArrayList;
import java.util.StringTokenizer;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import online.nl_generation.chunking.BasicNLP_FromPython;
import settings.Settings;

public class SingleEdgeQuestion {

    private SingleEdgeGraph singleEdgeGraph;

    private String S;
    private String P;
    private String O;

    private String S_withPrefix;
    private String P_withPrefix;
    private String O_withPrefix;

    private String S_type_withPrefix;
    private String O_type_withPrefix;

    private String s_o_VP;
    private String s_o_NP;
    private String o_s_VP;
    private String o_s_NP;
    private String s_o_NP_without_verb;
    private String o_s_NP_without_verb;

    private String s_o_NP_only;
    private String o_s_NP_only;

    private String selectQuery;
    private String countQuery;
    private String askQuery_correct;
    private String askQuery_wrong;
    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";

    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public SingleEdgeQuestion(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) throws Exception {

        intialize_Seed_is_S(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix);
        intialize_Seed_is_O(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix);

    }

    void intialize_Seed_is_S(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) throws Exception {
        this.singleEdgeGraph = singleEdgeGraph;
        this.S_type_withPrefix = S_type_withPrefix;
        this.O_type_withPrefix = O_type_withPrefix;
        S = singleEdgeGraph.getTriplePattern().getSubject().getValue();
        P = singleEdgeGraph.getTriplePattern().getPredicate().getValue();

        S_withPrefix = singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix();
        P_withPrefix = singleEdgeGraph.getTriplePattern().getPredicate().getValueWithPrefix();
        O_withPrefix = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();

        if (singleEdgeGraph.getTriplePattern().getO_type().equals(Settings.Number)
                || singleEdgeGraph.getTriplePattern().getO_type().equals(Settings.Date)
                || singleEdgeGraph.getTriplePattern().getO_type().equals(Settings.Literal)) {
            O = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();
        } else {
            O = singleEdgeGraph.getTriplePattern().getObject().getValue();
        }
        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, S_withPrefix, this.S_type_withPrefix);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_type_withPrefix, O_type_withPrefix);
        s_o_VP = predicateNL.getPredicate_s_O_VP();
        s_o_NP = predicateNL.getPredicate_s_O_NP();
        o_s_VP = predicateNL.getPredicate_o_s_VP();
        o_s_NP = predicateNL.getPredicate_o_s_NP();

        if (s_o_NP != null) {
            s_o_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
            s_o_NP_only = PhraseRepresentationProcessing.NP_only(s_o_NP);
        }

        if (o_s_NP != null) {
            o_s_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(o_s_NP);
            o_s_NP_only = PhraseRepresentationProcessing.NP_only(o_s_NP);
        }

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        S = EntityProcessing.decide_quotes_Simple_question(S, this.S_type_withPrefix);
        O = EntityProcessing.decide_quotes_Simple_question(O, this.O_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_Simple_question(somethingElseWithoutPrefix, this.S_type_withPrefix);

        generateAllPossibleSingleEdgeQuestions();
    }

    void intialize_Seed_is_O(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) throws Exception {
        //Relace each s by o and o by s ////////////////////////////////////////////////////////
        this.singleEdgeGraph = singleEdgeGraph;
        this.O_type_withPrefix = S_type_withPrefix;
        this.S_type_withPrefix = O_type_withPrefix;
        O = singleEdgeGraph.getTriplePattern().getSubject().getValue();
        P = singleEdgeGraph.getTriplePattern().getPredicate().getValue();

        O_withPrefix = singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix();
        P_withPrefix = singleEdgeGraph.getTriplePattern().getPredicate().getValueWithPrefix();
        S_withPrefix = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();

        if (singleEdgeGraph.getTriplePattern().getO_type().equals(Settings.Number)
                || singleEdgeGraph.getTriplePattern().getO_type().equals(Settings.Date)
                || singleEdgeGraph.getTriplePattern().getO_type().equals(Settings.Literal)) {
            S = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();

        } else {
            S = singleEdgeGraph.getTriplePattern().getObject().getValue();
        }

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, S_withPrefix, this.S_type_withPrefix);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_type_withPrefix, O_type_withPrefix); //except this one

        o_s_VP = predicateNL.getPredicate_s_O_VP();
        o_s_NP = predicateNL.getPredicate_s_O_NP();
        s_o_VP = predicateNL.getPredicate_o_s_VP();
        s_o_NP = predicateNL.getPredicate_o_s_NP();
        ///////////////////////////////////////////////////////////////////////////////////////////

        System.out.println("predicate_s_O_NP: " + s_o_NP);
        System.out.println("predicate_s_O_VP: " + s_o_VP);
        System.out.println("predicate_O_S_NP: " + o_s_NP);
        System.out.println("predicate_O_S_VP: " + o_s_VP);

        s_o_NP_without_verb = null;
        o_s_NP_without_verb = null;
        s_o_NP_only = null;
        o_s_NP_only = null;

        if (s_o_NP != null) {
            s_o_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
            s_o_NP_only = PhraseRepresentationProcessing.NP_only(s_o_NP);
        }

        if (o_s_NP != null) {
            o_s_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(o_s_NP);
            o_s_NP_only = PhraseRepresentationProcessing.NP_only(o_s_NP);
        }

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        S = EntityProcessing.decide_quotes_Simple_question(S, this.S_type_withPrefix);
        O = EntityProcessing.decide_quotes_Simple_question(O, this.O_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_Simple_question(somethingElseWithoutPrefix, this.S_type_withPrefix);

        generateAllPossibleSingleEdgeQuestions();
    }

    public ArrayList<GeneratedQuestion> generateAllPossibleSingleEdgeQuestions() throws Exception {
        generateQuestionAsk_Correct();
        generateQuestionAsk_Wrong();

        if (KGOntology.isSubtypeOf(S_type_withPrefix, Settings.Person)) {
            generateQuestionSELECT_e_of_type_Person();
        } else if (KGOntology.isSubtypeOf(S_type_withPrefix, Settings.Place)) {
            generateQuestionSELECT_e_of_type_Place();
        } else if (KGOntology.isSubtypeOf(S_type_withPrefix, Settings.Date)) {
            generateQuestionSELECT_e_of_type_Date();
        } else if (KGOntology.isSubtypeOf(S_type_withPrefix, Settings.Number)) {
            generateQuestionSELECT_e_of_type_Number();
        } else {
            generateQuestionSELECT_e_of_type_Entity();
        }
        return allPossibleQuestions;
    }

    public String generateSELECTQuery() {
        String triple = "";
        if (S_type_withPrefix.equals(Settings.Number) || S_type_withPrefix.equals(Settings.Date) || S_type_withPrefix.equals(Settings.Literal)) {
            triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern()
                    .replace("\"" + S_withPrefix + "\"^^xsd:dateTime ", "?Seed")
                    .replace("\"" + S_withPrefix + "\"", "?Seed")
                    .replace(" " + S_withPrefix + " ", "?Seed")
                    + " .";
        } else {
            triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace("<" + S_withPrefix + ">", "?Seed") + " .";
        }
        return "SELECT DISTINCT ?Seed WHERE{\n\t" + triple + "\n}";
    }

    public String generateCountQuery() {
        String triple = "";
        if (S_type_withPrefix.equals(Settings.Number) || S_type_withPrefix.equals(Settings.Date) || S_type_withPrefix.equals(Settings.Literal)) {
            triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern()
                    .replace("\"" + S_withPrefix + "\"^^xsd:dateTime ", "?Seed")
                    .replace("\"" + S_withPrefix + "\"", "?Seed")
                    .replace(" " + S_withPrefix + " ", "?Seed")
                    + " .";
        } else {
            triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace("<" + S_withPrefix + ">", "?Seed") + " .";
        }
        return "SELECT COUNT(?Seed) WHERE{\n\t" + triple + "\n}";
    }

    public String generateAskQuery_Correct() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern() + " .";
        return "ASK WHERE{\n\t" + triple + "\n}";
    }

    public String generateAskQuery_Wrong() {
        if (somethingElse == null) {
            return null;
        }
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        if (triple != null) {
            if (S_type_withPrefix.equals(Settings.Number) || S_type_withPrefix.equals(Settings.Date) || S_type_withPrefix.equals(Settings.Literal)) {
                triple = triple
                        .replace("\"" + S_withPrefix + "\"^^xsd:dateTime ", somethingElse)
                        .replace("\"" + S_withPrefix + "\"", somethingElse)
                        .replace(" " + S_withPrefix + " ", somethingElse)
                        + " .";
            } else {
                triple = triple.replace(S_withPrefix, somethingElse) + " .";
            }
            triple = triple.replace(S_withPrefix, somethingElse);
            return "ASK WHERE{\n\t" + triple + "\n}";
        }
        return null;
    }

    private void generateQuestionSELECT_e_of_type_Person() throws Exception {
        //Generate Question
        if (s_o_VP != null) {
            String question = "Who " + s_o_VP + " " + O + "?";
            String tagged_question = "<qt>Who</qt> <p>" + s_o_VP + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (s_o_NP != null) {
            String question = "Who " + s_o_NP + " " + O + "?";
            String tagged_question = "<qt>Who</qt> <p>" + s_o_NP + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_SINGLE_EDGE));
            String req = Request.getRequestPrefix();
            question = req + " " + s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<qt>" + req + "</qt> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
            question = s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (o_s_VP != null) {
            String questionType = "Whom";
            String o_s_VP_Pronoun = o_s_VP;
            String proposition = "";
            if (o_s_VP_Pronoun.endsWith(" by")) {
                proposition = "By";
            } else if (o_s_VP_Pronoun.endsWith(" for")) {
                proposition = "For";
            } else if (o_s_VP_Pronoun.endsWith(" with")) {
                proposition = "With";
            }
            if (!proposition.equals("")) {
                questionType = proposition + " whom";
                o_s_VP_Pronoun = o_s_VP_Pronoun.replace(" " + proposition.toLowerCase(), "");
            }
            String question = questionType + " was " + O + " " + o_s_VP_Pronoun + "?";
            String tagged_question = "<qt>" + questionType + "</qt> was <o>" + O + "</o> <p>" + o_s_VP_Pronoun + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHOM, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (o_s_NP != null) {
            String question = "Whose is " + O + " " + o_s_NP_only + "?";
            String tagged_question = "<qt>Whose</qt> is <o>" + O + "</o> <p>" + o_s_NP_only + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHOSE, GeneratedQuestion.SH_SINGLE_EDGE));
        }
    }

    private void generateQuestionSELECT_e_of_type_Place() throws Exception {
        //Generate Question
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            String tagged_question = "<qt>What</qt> <p>" + s_o_NP + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
            //question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
            String req = Request.getRequestPrefix();
            question = req + " " + s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<qt>" + req + "</qt> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
            question = s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (o_s_VP != null) {
            String question = "Where does " + O + " " + o_s_VP + "?";
            String tagged_question = "<qt>Where</qt> does <o>" + O + "</o> <p>" + o_s_VP + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHERE, GeneratedQuestion.SH_SINGLE_EDGE));

        }
    }

    private void generateQuestionSELECT_e_of_type_Entity() throws Exception {
        //Generate Question
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            String tagged_question = "<qt>What</qt> <p>" + s_o_NP + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
            String req = Request.getRequestPrefix();
            question = req + " " + s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<qt>" + req + "</qt> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
            question = s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (s_o_VP != null) {
            if (O_type_withPrefix.equals(Settings.Date)) {
                String question = "What was " + s_o_VP + " on " + O + "?";
                String tagged_question = "<qt>What was</qt> <p>" + s_o_VP + " on</p> <o>" + O + "</o>?";
                allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));

            } else {
                String question = "What " + s_o_VP + " " + O + "?";
                String tagged_question = "<qt>What</qt> <p>" + s_o_VP + "</p> <o>" + O + "</o>?";
                allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
            }
        }
    }

    private void generateQuestionSELECT_e_of_type_Number() throws Exception {
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            String tagged_question = "<qt>What</qt> <p>" + s_o_NP + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
            String req = Request.getRequestPrefix();
            question = req + " " + s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<qt>" + req + "</qt> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
            question = s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (o_s_NP != null) {
            String question = "How " + o_s_NP + " is " + O + "?";
            String tagged_question = "<qt>How</qt> <p>" + o_s_NP + "</p> is <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_HOW_ADJ, GeneratedQuestion.SH_SINGLE_EDGE));
        }
    }

    private void generateQuestionSELECT_e_of_type_Date() throws Exception {
        //Generate Question
        if (o_s_VP != null) {
            String question = "When was " + O + " " + o_s_VP + "?";
            String tagged_question = "<qt>When</qt> was <o>" + O + "</o> <p>" + o_s_VP + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHEN, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            String tagged_question = "<qt>What</qt> <p>" + s_o_NP + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
            String req = Request.getRequestPrefix();
            question = req + " " + s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<qt>" + req + "</qt> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
            question = s_o_NP_without_verb + " " + O + "?";
            tagged_question = "<p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SINGLE_EDGE));
        }
    }

    private void generateQuestionAsk_Correct() throws Exception {
        //Generate Question
        if (s_o_NP != null && s_o_NP.startsWith("is/are ")) {
            s_o_NP_without_verb = s_o_NP.replace("is/are ", "");
            String question = "Is " + S + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Is</qt> <s>" + S + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("is ")) {
            s_o_NP_without_verb = s_o_NP.replace("is ", "");
            String question = "Is " + S + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Is</qt> <s>" + S + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("are ")) {
            s_o_NP_without_verb = s_o_NP.replace("are ", "");
            String question = "Are " + S + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Are</qt> <s>" + S + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("was ")) {
            s_o_NP_without_verb = s_o_NP.replace("was ", "");
            String question = "Was " + S + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Was</qt> <s>" + S + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("were ")) {
            s_o_NP_without_verb = s_o_NP.replace("were ", "");
            String question = "Were " + S + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Were</qt> <s>" + S + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        }

        if (s_o_VP != null) {
            String question = "";
            String tagged_question = "";
            String s_o_VP_baseForm = s_o_VP;
            try {
                s_o_VP_baseForm = BasicNLP_FromPython.baseVerb(s_o_VP);
            } catch (Exception e) {
            }
//            if (O_type_withPrefix.equals(Settings.Date)) {
            question = "Was " + S + " " + s_o_VP + " on " + O + "?";
            tagged_question = "<qt>Was</qt> <s>" + S + "</s> <p>" + s_o_VP + " on</p> <o>" + O + "</o>?";
//            } else {
//                question = "Does " + S + " " + s_o_VP + " " + O + "?";
//                tagged_question = "<qt>Does</qt> <s>" + S + "</s> <p>" + s_o_VP + "</p> <o>" + O + "</o>?";
//            }
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_SINGLE_EDGE));
        }
    }

    private void generateQuestionAsk_Wrong() throws Exception {
        //Generate Question
        String s_o_NP_Auxiliary_Verb = "";

        if (somethingElseWithoutPrefix == null || somethingElseWithoutPrefix.equals("")) {
            return;
        }

        if (s_o_NP != null && s_o_NP.startsWith("is/are ")) {
            s_o_NP_without_verb = s_o_NP.replace("is/are ", "");
            String question = "Is " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Is</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("is ")) {
            s_o_NP_without_verb = s_o_NP.replace("is ", "");
            String question = "Is " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Is</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("are ")) {
            s_o_NP_without_verb = s_o_NP.replace("are ", "");
            String question = "Are " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Are</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("was ")) {
            s_o_NP_without_verb = s_o_NP.replace("was ", "");
            String question = "Was " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Was</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        } else if (s_o_NP != null && s_o_NP.startsWith("were ")) {
            s_o_NP_without_verb = s_o_NP.replace("were ", "");
            String question = "Were " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            String tagged_question = "<qt>Were</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + O + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
        }
        if (s_o_VP != null) {
            String question = "";
            String tagged_question = "";
            String s_o_VP_baseForm = s_o_VP;
            try {
                s_o_VP_baseForm = BasicNLP_FromPython.baseVerb(s_o_VP);
            } catch (Exception e) {
            }

//            if (O_type_withPrefix.equals(Settings.Date)) {
            question = "Was " + somethingElseWithoutPrefix + " " + s_o_VP + " on " + O + "?";
            tagged_question = "<qt>Was</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_VP + " on</p> <o>" + O + "</o>?";
//            } else {
//                question = "Does " + somethingElseWithoutPrefix + " " + s_o_VP + " " + O + "?";
//                tagged_question = "<qt>Does</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + s_o_VP + "</p> <o>" + O + "</o>?";
//            }
            allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_SINGLE_EDGE));
        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
