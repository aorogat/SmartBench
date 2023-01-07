package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.queryBuilder.QueryGenerator;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.FactConstraint;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.QuestionTypePrefixGenerator;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PhraseRepresentationProcessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.EntityProcessing;

import java.util.ArrayList;

import lexiconGenerator.kg_explorer.ontology.KGOntology;
import benchmarkGenerator.subgraphShapeGenerator.model.TriplePattern;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.ChainGraph;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import settings.Settings;

public class ChainQuestion {

    //Forward direction    (?S0)__(P0)__(O0)___(P1)___(O1)___....__(O_Final)
    // s_o_PN_series = [p0_s_o][p1_s_o][p2_s_o] ... [pn_s_o]  (((Wh.. [s_o_PN_series] [O_Final]?)))
    // o_s_PN_series = [p0_o_s][p1_o_s][p2_o_s] ... [pn_o_s]  (((Wh.. [o_s_PN_series] [S0]?)))
    ChainGraph chainGraph;
    String S0_Seed; //seed
    String P0;
    String O0;
    String O_Final; //last object in the chain
    String O_Final_type_withPrefix; //last object in the chain
    String P1_to_n_SO_PN_series = "";
    String P1_to_n_OS_PN_series = "";
    String P1_to_n_SO_PN_series_Tagged = "";
    String P1_to_n_OS_PN_series_Tagged = "";
    ArrayList<String> predicatesRepresentationsSeries = new ArrayList<>();
    String P1_to_n_SO_PN_series_without_verb = "";
    String P1_to_n_SO_PN_series_without_verb_Tagged = "";
    PredicateNLRepresentation predicateNL_for_P0;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    private String S0_withPrefix;
    private String P0_withPrefix;
    private String O0_withPrefix;
    private String S0_type_withPrefix;
    private String O0_type_withPrefix;
    private String P0_SO_VP;
    private String P0_SO_NP;
    private String P0_OS_VP;
    private String P0_OS_NP;
    private String P0_SO_NP_without_verb = "";
    private String P0_OS_NP_without_verb = "";
    //Query
    private String selectQuery;
    private String countQuery;
    private String askQuery_correct;
    private String askQuery_wrong;
    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";
    private String query_GP_triples;
    private boolean missingPredicateRepresentation_s_o_NP = false;

    public ChainQuestion(ChainGraph chainGraph) throws Exception {
        this.chainGraph = chainGraph;

        initialize_forward_chain();
        initialize_backward_chain();

    }

    private void initialize_forward_chain() throws Exception {
        predicatesRepresentationsSeries = new ArrayList<>();
        missingPredicateRepresentation_s_o_NP = false;
        S0_Seed = chainGraph.getChain().get(0).getSubject().getValue(); //seed
        P0 = chainGraph.getChain().get(0).getPredicate().getValue();
        O0 = chainGraph.getChain().get(0).getObject().getValue();
        O_Final = chainGraph.getChain().get(chainGraph.getChain().size() - 1).getObject().getValue(); //last object in the chain

        S0_withPrefix = chainGraph.getChain().get(0).getSubject().getValueWithPrefix();
        P0_withPrefix = chainGraph.getChain().get(0).getPredicate().getValueWithPrefix();
        O0_withPrefix = chainGraph.getChain().get(0).getObject().getValueWithPrefix();

        S0_type_withPrefix = chainGraph.getChain().get(0).getS_type();
        O0_type_withPrefix = chainGraph.getChain().get(0).getO_type();
        O_Final_type_withPrefix = chainGraph.getChain().get(chainGraph.getChain().size() - 1).getO_type();

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, S0_withPrefix, this.S0_type_withPrefix);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        predicateNL_for_P0 = PredicatesLexicon.getPredicateNL(P0_withPrefix, S0_type_withPrefix, O0_type_withPrefix);

        P0_SO_NP = "";
        P0_SO_VP = "";

        P0_OS_NP = "";
        P0_OS_VP = "";

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        P0_SO_NP = predicateNL_for_P0.getPredicate_s_O_NP();
        P0_SO_VP = predicateNL_for_P0.getPredicate_s_O_VP();

        P0_OS_NP = predicateNL_for_P0.getPredicate_o_s_NP();
        P0_OS_VP = predicateNL_for_P0.getPredicate_o_s_VP();

        P1_to_n_SO_PN_series = "";
        P1_to_n_OS_PN_series = "";

        P1_to_n_SO_PN_series_Tagged = "";
        P1_to_n_OS_PN_series_Tagged = "";

        ArrayList<TriplePattern> chain = chainGraph.getChain();
        query_GP_triples = "";
        for (TriplePattern triple : chain) {
            query_GP_triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
        }

        for (int i = 1; i < chainGraph.getChain().size(); i++) {
            TriplePattern triple = chainGraph.getChain().get(i);
            String s = triple.getSubject().getValue();
            String p = triple.getPredicate().getValue();
            String o = triple.getObject().getValue();
            String s_type = triple.getS_type();
            String o_type = triple.getO_type();
            String p_withPrefix = triple.getPredicate().getValueWithPrefix();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_withPrefix, s_type, o_type);
            String s_o_NP = "";
            String o_s_NP = "";
            if (predicateNL != null) {
                s_o_NP = predicateNL.getPredicate_s_O_NP();
                o_s_NP = predicateNL.getPredicate_o_s_NP();

                System.out.println("predicate_s_O_NP: " + s_o_NP);
                System.out.println("predicate_O_S_NP: " + o_s_NP);


            }
            //NL representation of intermediate predicates
            if (s_o_NP != null && !s_o_NP.equals("")) {
                P1_to_n_SO_PN_series += " " + PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
                P1_to_n_SO_PN_series_Tagged += " <p>" + PhraseRepresentationProcessing.NP_without_verb(s_o_NP) + "</p>";
            } else {
                missingPredicateRepresentation_s_o_NP = true;
            }
            if (o_s_NP != null && !o_s_NP.equals("")) {
                P1_to_n_OS_PN_series += " " + PhraseRepresentationProcessing.NP_without_verb(o_s_NP);
                P1_to_n_OS_PN_series_Tagged += " <p>" + PhraseRepresentationProcessing.NP_without_verb(o_s_NP) + "</p>";
            } else {
                missingPredicateRepresentation_s_o_NP = true;
            }
        }

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        P0_SO_NP = predicateNL_for_P0.getPredicate_o_s_NP();
        P0_OS_NP = predicateNL_for_P0.getPredicate_s_O_NP();

        System.out.println("predicate_0_s_O_NP: " + P0_SO_NP);
        System.out.println("predicate_0_O_S_NP: " + P0_OS_NP);

        selectQuery = generateSELECTQuery_Chain();
        countQuery = generateCountQuery_Chain();
        askQuery_correct = generateAskQuery_Correct_Chain();
        askQuery_wrong = generateAskQuery_Wrong_Chain();

        S0_Seed = EntityProcessing.decide_quotes_with_type(S0_Seed, this.S0_type_withPrefix);
        O_Final = EntityProcessing.decide_quotes_with_type(O_Final, this.O_Final_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_with_type(somethingElseWithoutPrefix, this.S0_type_withPrefix);

        generateAllPossibleChainQuestions();
    }

    //Chain take the revers direction
    private void initialize_backward_chain() throws Exception {
        predicatesRepresentationsSeries = new ArrayList<>();
        missingPredicateRepresentation_s_o_NP = false;
        int length = chainGraph.getChain().size() - 1;
        S0_Seed = chainGraph.getChain().get(length).getObject().getValue(); //seed //Swap
        P0 = chainGraph.getChain().get(length).getPredicate().getValue();
        O0 = chainGraph.getChain().get(length).getSubject().getValue(); //Swap
        O_Final = chainGraph.getChain().get(0).getSubject().getValue(); //last object in the chain //Swap

        S0_withPrefix = chainGraph.getChain().get(length).getObject().getValueWithPrefix(); //Swap
        P0_withPrefix = chainGraph.getChain().get(length).getPredicate().getValueWithPrefix();
        O0_withPrefix = chainGraph.getChain().get(length).getSubject().getValueWithPrefix(); //Swap

        S0_type_withPrefix = chainGraph.getChain().get(length).getO_type(); //Swap
        O0_type_withPrefix = chainGraph.getChain().get(length).getS_type(); //Swap
        O_Final_type_withPrefix = chainGraph.getChain().get(0).getS_type(); //Swap

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, S0_withPrefix, this.S0_type_withPrefix);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        predicateNL_for_P0 = PredicatesLexicon.getPredicateNL(P0_withPrefix, O0_type_withPrefix, S0_type_withPrefix); //Swap

        P0_SO_NP = "";
        P0_SO_VP = "";

        P0_OS_NP = "";
        P0_OS_VP = "";

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        P0_SO_NP = predicateNL_for_P0.getPredicate_o_s_NP();
        P0_SO_VP = predicateNL_for_P0.getPredicate_o_s_VP();

        P0_OS_NP = predicateNL_for_P0.getPredicate_s_O_NP();
        P0_OS_VP = predicateNL_for_P0.getPredicate_s_O_VP();

        System.out.println("predicate_0_s_O_NP: " + P0_SO_NP);
        System.out.println("predicate_0_s_O_VP: " + P0_SO_VP);
        System.out.println("predicate_0_O_S_NP: " + P0_OS_NP);
        System.out.println("predicate_0_O_S_VP: " + P0_OS_VP);

        ArrayList<TriplePattern> chain = chainGraph.getChain();
        query_GP_triples = "";

        for (int i = length; i >= 0; i--) {
            TriplePattern triple = chain.get(i);
            query_GP_triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
        }

        P1_to_n_SO_PN_series = "";
        P1_to_n_SO_PN_series_Tagged = "";
        for (int i = length - 1; i >= 0; i--) {
            TriplePattern triple = chainGraph.getChain().get(i);
            String s = triple.getObject().getValue();
            String p = triple.getPredicate().getValue();
            String o = triple.getSubject().getValue();
            String s_type = triple.getO_type();
            String o_type = triple.getS_type();
            String p_withPrefix = triple.getPredicate().getValueWithPrefix();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_withPrefix, o_type, s_type);
            String s_o_NP = "";
            if (predicateNL != null) {
                s_o_NP = predicateNL.getPredicate_o_s_NP(); //////////////replaced
            }
            //NL representation of intermediate predicates
            if (s_o_NP != null && !s_o_NP.equals("")) {
                if (i < length) {
                    s_o_NP = PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
                }
                P1_to_n_SO_PN_series += " " + s_o_NP;
                P1_to_n_SO_PN_series_Tagged += " <p>" + s_o_NP + "</p>";
            } else {
                missingPredicateRepresentation_s_o_NP = true;
            }
        }
        P1_to_n_SO_PN_series = P1_to_n_SO_PN_series.trim();
        P1_to_n_SO_PN_series_without_verb = PhraseRepresentationProcessing.NP_without_verb___first(P1_to_n_SO_PN_series);
        P1_to_n_SO_PN_series_without_verb_Tagged = PhraseRepresentationProcessing.NP_without_verb___first(P1_to_n_SO_PN_series_Tagged);

        if (P0_SO_NP != null) {
            P0_SO_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb___first(P0_SO_NP);
        }
        if (P0_OS_NP != null) {
            P0_OS_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb___first(P0_OS_NP);
        }

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        selectQuery = generateSELECTQuery_Chain();
        countQuery = generateCountQuery_Chain();
        askQuery_correct = generateAskQuery_Correct_Chain();
        askQuery_wrong = generateAskQuery_Wrong_Chain();

        S0_Seed = EntityProcessing.decide_quotes_with_type(S0_Seed, this.S0_type_withPrefix);
        O_Final = EntityProcessing.decide_quotes_with_type(O_Final, this.O_Final_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_with_type(somethingElseWithoutPrefix, this.S0_type_withPrefix);

        generateAllPossibleChainQuestions();
    }

    public void generateAllPossibleChainQuestions() throws Exception {
        // generateCountQuery_Chain(); //Not possible here  (Require a type branch)
        if (KGOntology.isSubtypeOf(S0_type_withPrefix, Settings.Person)) {
            generateQuestionSELECT_e_of_type_Person();
        } else if (KGOntology.isSubtypeOf(S0_type_withPrefix, Settings.Place)) {
            generateQuestionSELECT_e_of_type_Place();
        } else if (KGOntology.isSubtypeOf(S0_type_withPrefix, Settings.Date)) {
            generateQuestionSELECT_e_of_type_Date();
        } else if (KGOntology.isSubtypeOf(S0_type_withPrefix, Settings.Number)) {
            generateQuestionSELECT_e_of_type_Number();
        } else {
            generateQuestionSELECT_e_of_type_Entity();
        }
    }

    public String generateSELECTQuery_Chain() {
        String triples = QueryGenerator.graph_to_graphPattern(query_GP_triples, S0_withPrefix, S0_type_withPrefix, "?Seed");
        triples = QueryGenerator.chainGraphToChainGraphPattern(triples, chainGraph);
        return "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
    }

    public String generateCountQuery_Chain() {
        if (missingPredicateRepresentation_s_o_NP) {
            return null;
        }
        String triples = query_GP_triples.replaceFirst("<" + chainGraph.getChain().get(0).getSubject().getValueWithPrefix() + ">", "?Seed");
        triples = QueryGenerator.chainGraphToChainGraphPattern(triples, chainGraph);
        return "SELECT COUNT(?Seed) WHERE{" + triples + "\n}";
    }

    public String generateAskQuery_Correct_Chain() {
        if (missingPredicateRepresentation_s_o_NP) {
            return null;
        }
        String triples = QueryGenerator.chainGraphToChainGraphPattern(query_GP_triples, chainGraph);
        return "ASK WHERE{" + triples + "\n}";
    }

    public String generateAskQuery_Wrong_Chain() {

        if (missingPredicateRepresentation_s_o_NP) {
            return null;
        }
        String triples = QueryGenerator.chainGraphToChainGraphPattern(query_GP_triples, chainGraph);
        if (triples == null) {
            return null;
        }
        return "ASK WHERE{" + triples.replace(S0_withPrefix, somethingElse) + "\n}";
    }

    /**
     * Generates a select question of the form "qt X P_prefix Y P_postfix?" for
     * a single edge graph where X is the object and Y is a phrase
     * representation of the predicate. The method also creates a
     * GeneratedQuestion object with the question and its tagged version and
     * adds it to the list of all possible questions.
     *
     * @param qt            the question type, e.g. "What", "Who", "Where", etc.
     * @param O             the object of the triple pattern
     * @param P_phrase      the phrase representation of the predicate
     * @param P_phrase_type the type of the phrase representation of the
     *                      predicate, e.g. FactConstraint.S_O_VP, FactConstraint.S_O_NP, etc.
     * @param P_prefix      the prefix to be added before the phrase representation
     *                      of the predicate
     * @param P_postfix     the postfix to be added after the phrase representation
     *                      of the predicate
     * @param qt_type       the type of the question, e.g. GeneratedQuestion.QT_WHAT,
     *                      GeneratedQuestion.QT_WHEN, etc.
     * @throws Exception if there is an error while generating the question
     */
    private void generatQuestion(String qt, String O, String P_phrase, byte P_phrase_type, String P_prefix, String P_postfix, String qt_type) throws Exception {
        if (qt.equals(""))
            P_phrase = P_phrase.substring(0, 1).toUpperCase() + P_phrase.substring(1);
        String tagged_qt = "<qt>" + qt + "</qt>";
        String fact_Constraint = FactConstraint.constructFactContraintNL(O, P_phrase, P_phrase_type, false, P_prefix, P_postfix);
        String tagged_fact_Constraint = FactConstraint.constructFactContraintNL(O, P_phrase, P_phrase_type, true, P_prefix, P_postfix);
        String question = qt + " " + fact_Constraint + "?";
        String tagged_question = tagged_qt.replace("<qt></qt>", "") + " " + tagged_fact_Constraint + "?";
        allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question.trim(), tagged_question.trim(), selectQuery, chainGraph.toString(), chainGraph.getChain().size(), qt_type, GeneratedQuestion.SH_CHAIN));
    }

    private void generateQuestionSELECT_e_of_type_Person() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
//        if (P0_SO_VP != null) {
//            generatQuestion("Who", O_Final, P0_SO_VP + " " + P1_to_n_SO_PN_series, FactConstraint.S_O_VP, "", "", GeneratedQuestion.QT_WHO);
//        }
        if (P0_SO_VP != null) {
            String question = "Who " + P0_SO_VP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            String questionTagged = "<qt>Who</qt> <p>" + P0_SO_VP + "</p> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_CHAIN));
        }

//        if (P0_SO_NP != null) {
//            generatQuestion("Who", O_Final, P0_SO_NP + " " + P1_to_n_SO_PN_series, FactConstraint.S_O_VP, "", "", GeneratedQuestion.QT_WHO);
//            generatQuestion(QuestionTypePrefixGenerator.getRequestPrefix(), O_Final, P0_SO_NP + " " + P1_to_n_SO_PN_series, FactConstraint.S_O_VP, "", "", GeneratedQuestion.QT_REQUEST);
//            generatQuestion("", O_Final, P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_TOPICAL_PRUNE);
//        }
        if (P0_SO_NP != null) {
            String question = "Who " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            String questionTagged = "<qt>Who</qt> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_CHAIN));

            String req = QuestionTypePrefixGenerator.getRequestPrefix();
            question = req + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<qt>" + req + "</qt> <p>" + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));

            question = P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<p>" + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_CHAIN));

        }
        if (P0_OS_VP != null) {
            String question = "Whom " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
            String questionTagged = "<qt>Whom</qt> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_VP + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHOM, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_NP != null) {
            String question = "Whose " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_NP + "?";
            String questionTagged = "<qt>Whose</qt> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHOSE, GeneratedQuestion.SH_CHAIN));
        }

    }

    private void generateQuestionSELECT_e_of_type_Place() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            String questionTagged = "<qt>What</qt> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));

            String req = QuestionTypePrefixGenerator.getRequestPrefix();
            question = req + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<qt>" + req + "</qt> <p>" + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));

            question = P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<p>" + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_CHAIN));

        }
        if (P0_OS_VP != null) {
            String question = "Where " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
            String questionTagged = "<qt>Where</qt> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_VP + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHERE, GeneratedQuestion.SH_CHAIN));
        }

    }

    private void generateQuestionSELECT_e_of_type_Entity() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P0_OS_VP != null) {
            String question = "What " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
            String questionTagged = "<qt>What</qt> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_VP + "</p>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
        }
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            String questionTagged = "<qt>What</qt> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));

            String req = QuestionTypePrefixGenerator.getRequestPrefix();
            question = req + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<qt>" + req + "</qt> <p> " + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));

            question = P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<p>" + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_SO_VP != null) {
            String question = "What " + P0_SO_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>What</qt> <p>" + P0_SO_VP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
        }

    }

    private void generateQuestionSELECT_e_of_type_Number() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            String questionTagged = "<qt>What</qt> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));

            String req = QuestionTypePrefixGenerator.getRequestPrefix();
            question = req + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<qt>" + req + "</qt> <p> " + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));

            question = P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<p>" + P0_SO_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_NP != null) {
            String question = "How " + P0_OS_NP + " is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>How</qt> <p>" + P0_OS_NP + "</p> is " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_HOW_ADJ, GeneratedQuestion.SH_CHAIN));
        }
    }

    private void generateQuestionSELECT_e_of_type_Date() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P0_OS_VP != null) {
            String question = "When did " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
        }
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            String questionTagged = "<qt>What</qt> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));

            String req = QuestionTypePrefixGenerator.getRequestPrefix();
            question = req + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<qt>" + req + "</qt> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));

            question = P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            questionTagged = "<p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";

            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_CHAIN));

        }
    }

    private void generateQuestionAsk_Correct() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is/are ")) {
            String question = "Is " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Is</qt> <s>" + S0_Seed + "</s> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is ")) {
            String question = "Is " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Is</qt> <s>" + S0_Seed + "</s> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("are ")) {
            String question = "Are " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Are</qt> <s>" + S0_Seed + "</s> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("was ")) {
            String question = "Was " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Was</qt> <s>" + S0_Seed + "</s> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("were ")) {
            String question = "Were " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Were</qt> <s>" + S0_Seed + "</s> <p>" + P0_SO_NP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_OS_NP != null && P0_OS_NP.startsWith("is/are ")) {
            P0_OS_NP = P0_OS_NP.replace("is/are ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            String questionTagged = "<qt>Is</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + S0_Seed + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("is ")) {
            P0_OS_NP = P0_OS_NP.replace("is ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            String questionTagged = "<qt>Is</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + S0_Seed + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("are ")) {
            P0_OS_NP = P0_OS_NP.replace("are ", "");
            String question = "Are " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            String questionTagged = "<qt>Are</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + S0_Seed + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("was ")) {
            P0_OS_NP = P0_OS_NP.replace("was ", "");
            String question = "Was " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            String questionTagged = "<qt>Was</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + S0_Seed + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("were ")) {
            P0_OS_NP = P0_OS_NP.replace("were ", "");
            String question = "Were " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            String questionTagged = "<qt>Were</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + S0_Seed + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_SO_VP != null) {
            String question = "Was " + S0_Seed + " " + P0_SO_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Does</qt> <s>" + S0_Seed + "</s> <p>" + P0_SO_VP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_VP != null) {
            String question = "Was " + O_Final + " " + P0_OS_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Does</qt> <o>" + O_Final + "</o> <p>" + P0_OS_VP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
    }

    private void generateQuestionAsk_Wrong() throws Exception {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        String s_o_NP_Auxiliary_Verb = "";

        if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is/are ")) {
            String question = "Is " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Is</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + P0_OS_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is ")) {
            String question = "Is " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Is</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + P0_OS_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("are ")) {
            String question = "Are " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Are</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + P0_OS_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("was ")) {
            String question = "Was " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Was</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + P0_OS_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("were ")) {
            String question = "Were " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Were</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + P0_OS_NP_without_verb + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_OS_NP != null && P0_OS_NP.startsWith("is/are ")) {
            P0_OS_NP = P0_OS_NP.replace("is/are ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            String questionTagged = "<qt>Is</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + somethingElseWithoutPrefix + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("is ")) {
            P0_OS_NP = P0_OS_NP.replace("is ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            String questionTagged = "<qt>Is</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + somethingElseWithoutPrefix + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("are ")) {
            P0_OS_NP = P0_OS_NP.replace("are ", "");
            String question = "Are " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            String questionTagged = "<qt>Are</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + somethingElseWithoutPrefix + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("was ")) {
            P0_OS_NP = P0_OS_NP.replace("was ", "");
            String question = "Was " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            String questionTagged = "<qt>Was</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + somethingElseWithoutPrefix + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("were ")) {
            P0_OS_NP = P0_OS_NP.replace("were ", "");
            String question = "Were " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            String questionTagged = "<qt>Were</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_OS_NP + "</p> <s>" + somethingElseWithoutPrefix + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_SO_VP != null) {
            String question = "Was " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_SO_VP + " " + somethingElseWithoutPrefix + "?";
            String questionTagged = "<qt>Does</qt> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o> <p>" + P0_SO_VP + "</p> <s>" + somethingElseWithoutPrefix + "</s>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_VP != null) {
            String question = "Was " + somethingElseWithoutPrefix + " " + P0_OS_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            String questionTagged = "<qt>Does</qt> <s>" + somethingElseWithoutPrefix + "</s> <p>" + P0_OS_VP + "</p> " + P1_to_n_SO_PN_series_without_verb_Tagged + " <o>" + O_Final + "</o>?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, questionTagged, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
