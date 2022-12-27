package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.Request;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.FactConstraint;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PhraseRepresentationProcessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.EntityProcessing;
import java.util.ArrayList;
import lexiconGenerator.kg_explorer.ontology.KGOntology;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.SingleEdgeGraph;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import benchmarkGenerator.questionsGenerator.queryBuilder.QueryGenerator;
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
        generate_questions_S_is_Seed(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix);
        generate_questions_O_is_Seed(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix);
    }

    private void fillPredicateRepresentations(boolean forward, String P_withPrefix, String S_type_withPrefix, String O_type_withPrefix) {
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_type_withPrefix, O_type_withPrefix);
        if (forward) {
            s_o_VP = predicateNL.getPredicate_s_O_VP();
            s_o_NP = predicateNL.getPredicate_s_O_NP();
            o_s_VP = predicateNL.getPredicate_o_s_VP();
            o_s_NP = predicateNL.getPredicate_o_s_NP();
        } else {
            o_s_VP = predicateNL.getPredicate_s_O_VP();
            o_s_NP = predicateNL.getPredicate_s_O_NP();
            s_o_VP = predicateNL.getPredicate_o_s_VP();
            s_o_NP = predicateNL.getPredicate_o_s_NP();
        }
        fillNounPhraseRepresentations(s_o_NP, o_s_NP);
    }

    private void fillNounPhraseRepresentations(String s_o_NP, String o_s_NP) {
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
    }

    void generate_questions_S_is_Seed(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) throws Exception {
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

        fillPredicateRepresentations(true, P_withPrefix, S_type_withPrefix, O_type_withPrefix);

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        S = EntityProcessing.decide_quotes_Simple_question(S, this.S_type_withPrefix);
        O = EntityProcessing.decide_quotes_Simple_question(O, this.O_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_Simple_question(somethingElseWithoutPrefix, this.S_type_withPrefix);

        generateAllPossibleSingleEdgeQuestions();
    }

    void generate_questions_O_is_Seed(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) throws Exception {
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

        fillPredicateRepresentations(false, P_withPrefix, S_type_withPrefix, O_type_withPrefix);

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        S = EntityProcessing.decide_quotes_Simple_question(S, this.S_type_withPrefix);
        O = EntityProcessing.decide_quotes_Simple_question(O, this.O_type_withPrefix);
        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_Simple_question(somethingElseWithoutPrefix, this.S_type_withPrefix);

        generateAllPossibleSingleEdgeQuestions();
    }

    /**
     * Generates all possible questions for a single edge graph where the
     * subject is the seed. The generated questions include ask correct, ask
     * wrong, and select questions of various types. The type of select
     * questions generated depends on the type of the subject. If the subject is
     * a person, a place, a date, or a number, then a corresponding select
     * question is generated. Otherwise, a generic select question is generated.
     *
     * @return a list of all the generated questions
     * @throws Exception if there is an error while generating the questions
     */
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

    /**
     * Generates a SPARQL SELECT query to retrieve all values of the seed
     * subject in the triple pattern.
     *
     * @return a SPARQL SELECT query to retrieve all values of the seed subject
     * in the triple pattern
     */
    public String generateSELECTQuery() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        return QueryGenerator.generateSELECTQuery(triple, "<" + S_withPrefix + ">", S_type_withPrefix, "?Seed");
    }

    /**
     *
     * This method generates a SPARQL query to count the number of entities in
     * the graph that match the seed entity. The queryreplaces the seed entity
     * in the triple pattern with a variable, and counts the number of values
     * that the variable can take on.
     *
     * @return a String containing the SPARQL query to count the number of
     * entities in the graph that match the seed entity
     */
    public String generateCountQuery() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        return QueryGenerator.generateCountQuery(triple, "<" + S_withPrefix + ">", S_type_withPrefix, "?Seed");
    }

    public String generateAskQuery_Correct() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern() + " .";
        return QueryGenerator.generateAskQuery_Correct(triple);
    }

    /**
     *
     * Generates an ASK query with a subject that is different from the original
     * triple pattern
     *
     * @return the generated ASK query, or null if somethingElse is null
     */
    public String generateAskQuery_Wrong() {
        if (somethingElse == null) {
            return null;
        }
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        return QueryGenerator.generateAskQuery_Wrong(triple, S_withPrefix, S_type_withPrefix, somethingElse);
    }

    /**
     * Generates select questions of the form "Who X?", "Whose Y?", or "Whom Y?"
     * for a single edge graph where the subject is a person and the object is
     * of any type. If a verb phrase (VP) and/or a noun phrase (NP)
     * representation of the predicate exists, then the method generates select
     * questions using those representations.
     *
     * @throws Exception if there is an error while generating the questions
     */
    private void generateQuestionSELECT_e_of_type_Person() throws Exception {
        //Generate Question
        if (s_o_VP != null) {
            generatQuestion("Who", O, s_o_VP, FactConstraint.S_O_VP, "", "", GeneratedQuestion.QT_WHO);
        }
        if (s_o_NP != null) {
            generatQuestion("Who", O, s_o_NP, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_WHO);
            generateRequestQuestion();
            generatePrunedQuestion();
        }
        if (o_s_VP != null) {
            generateWhomQuestion();
        }
        if (o_s_NP != null) {
            generatQuestion("Whose", O, o_s_NP_only, FactConstraint.O_S_NP, "is", "", GeneratedQuestion.QT_WHOSE);
        }
    }

    /**
     * Generates select questions of the form "What X?" or "Where Y?" for a
     * single edge graph where the subject is a place and the object is of any
     * type. If a noun phrase (NP) representation of the predicate exists, then
     * the method generates a select question of the form "What X?". If a verb
     * phrase (VP) representation of the predicate exists, then the method
     * generates a select question of the form "Where Y?".
     *
     * @throws Exception if there is an error while generating the questions
     */
    private void generateQuestionSELECT_e_of_type_Place() throws Exception {
        //Generate "What" questions
        if (s_o_NP != null) {
            generatQuestion("What", O, s_o_NP, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_WHAT);
            generateRequestQuestion();
            generatePrunedQuestion();
        }
        //Generate "Where" questions
        if (o_s_VP != null) {
            generatQuestion("Where", O, o_s_VP, FactConstraint.O_S_VP, "does", "", GeneratedQuestion.QT_WHERE);
        }
    }

    /**
     * Generates select questions of the form "What X?" for a single edge graph
     * where the subject and the object are both entities of any type. If a noun
     * phrase (NP) representation of the predicate exists, then the method
     * generates a select question of the form "What X?". If a verb phrase (VP)
     * representation of the predicate exists and the object is a date (Z), then
     * the method generates a select question of the form "What was Y on Z?". If
     * a verb phrase (VP) representation of the predicate exists and the object
     * is not a date, then the method generates a select question of the form
     * "What Y?".
     *
     * @throws Exception if there is an error while generating the questions
     */
    private void generateQuestionSELECT_e_of_type_Entity() throws Exception {
        //Generate Question
        if (s_o_NP != null) {
            generatQuestion("What", O, s_o_NP, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_WHAT);
            generateRequestQuestion();
            generatePrunedQuestion();
        }
        if (s_o_VP != null) {
            if (O_type_withPrefix.equals(Settings.Date)) {
                generatQuestion("What", O, s_o_VP, FactConstraint.S_O_VP, "was", "on", GeneratedQuestion.QT_WHAT);
            } else {
                generatQuestion("What", O, s_o_VP, FactConstraint.S_O_VP, "", "", GeneratedQuestion.QT_WHAT);
            }
        }
    }

    /**
     * Generates select questions of the form "What X?" or "How Y?" for a single
     * edge graph where the subject is a number and the object is of any type.
     * If a noun phrase (NP) representation of the predicate exists, then the
     * method generates a select question of the form "What X?". If a noun
     * phrase (NP) representation of the predicate exists, then the method
     * generates a select question of the form "How Y?".
     *
     * @throws Exception if there is an error while generating the questions
     */
    private void generateQuestionSELECT_e_of_type_Number() throws Exception {
        if (s_o_NP != null) {
            generatQuestion("What", O, s_o_NP, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_WHAT);
            generateRequestQuestion();
            generatePrunedQuestion();
        }
        if (o_s_NP != null) {
            generatQuestion("How", O, o_s_NP, FactConstraint.O_S_NP, "", "is", GeneratedQuestion.QT_HOW_ADJ);
        }
    }

    /**
     * Generates select questions of the form "When X?" or "What Y?" for a
     * single edge graph where the subject is a date and the object is of any
     * type. If a verb phrase (VP) representation of the predicate exists, then
     * the method generates a select question of the form "When X?". If a noun
     * phrase (NP) representation of the predicate exists, then the method
     * generates a select question of the form "What Y?".
     *
     * @throws Exception if there is an error while generating the questions
     */
    private void generateQuestionSELECT_e_of_type_Date() throws Exception {
        //Generate Question
        if (o_s_VP != null) {
            generatQuestion("When", O, o_s_VP, FactConstraint.O_S_VP, "was", "", GeneratedQuestion.QT_WHEN);
        }
        if (s_o_NP != null) {
            generatQuestion("What", O, s_o_NP, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_WHAT);
            generateRequestQuestion();
            generatePrunedQuestion();
        }
    }

    /**
     * Generates a select question of the form "qt X P_prefix Y P_postfix?" for
     * a single edge graph where X is the object and Y is a phrase
     * representation of the predicate. The method also creates a
     * GeneratedQuestion object with the question and its tagged version and
     * adds it to the list of all possible questions.
     *
     * @param qt the question type, e.g. "What", "Who", "Where", etc.
     * @param O the object of the triple pattern
     * @param P_phrase the phrase representation of the predicate
     * @param P_phrase_type the type of the phrase representation of the
     * predicate, e.g. FactConstraint.S_O_VP, FactConstraint.S_O_NP, etc.
     * @param P_prefix the prefix to be added before the phrase representation
     * of the predicate
     * @param P_postfix the postfix to be added after the phrase representation
     * of the predicate
     * @param qt_type the type of the question, e.g. GeneratedQuestion.QT_WHAT,
     * GeneratedQuestion.QT_WHEN, etc.
     * @throws Exception if there is an error while generating the question
     */
    private void generatQuestion(String qt, String O, String P_phrase, byte P_phrase_type, String P_prefix, String P_postfix, String qt_type) throws Exception {
        String tagged_qt = "<qt>" + qt + "</qt>";
        String fact_Constraint = FactConstraint.constructFactContraintNL(O, P_phrase, P_phrase_type, false, P_prefix, P_postfix);
        String tagged_fact_Constraint = FactConstraint.constructFactContraintNL(O, P_phrase, P_phrase_type, true, P_prefix, P_postfix);
        String question = qt + " " + fact_Constraint + "?";
        String tagged_question = tagged_qt.replace("<qt></qt>", "") + " " + tagged_fact_Constraint + "?";
        allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question.trim(), tagged_question.trim(), selectQuery, singleEdgeGraph.toString(), 1, qt_type, GeneratedQuestion.SH_SINGLE_EDGE));
    }

    /**
     *
     * Generates a request question for the given parameters.
     *
     * @throws Exception if an error occurs while generating the question
     */
    private void generateRequestQuestion() throws Exception {
        String req = Request.getRequestPrefix();
        generatQuestion(req, O, s_o_NP_without_verb, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_REQUEST);
    }

    /**
     *
     * Generates a pruned question by constructing a question with the form "O
     * Predicate_representation?" and adding it to the list of all possible
     * questions.
     *
     * @throws Exception if an error occurs while generating the question
     */
    private void generatePrunedQuestion() throws Exception {
        String s_o_NP_without_verb_capetalized = s_o_NP_without_verb.substring(0, 1).toUpperCase() + s_o_NP_without_verb.substring(1);
        generatQuestion("", O, s_o_NP_without_verb_capetalized, FactConstraint.S_O_NP, "", "", GeneratedQuestion.QT_TOPICAL_PRUNE);
    }

    private void generateWhomQuestion() throws Exception {
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

        generatQuestion(questionType, O, o_s_VP_Pronoun, FactConstraint.O_S_VP, "was", "", GeneratedQuestion.QT_WHOM);
    }

    private void generateQuestionAsk_Correct() throws Exception {
        String[] prefixes = {"Is", "Are", "Was", "Were"};
        String s = S;
        String askQuery = askQuery_correct;

        if (S == null || S.equals("")) {
            return;
        }

        generateQuestionAsk(s, prefixes, O, askQuery);
    }

    private void generateQuestionAsk_Wrong() throws Exception {
        String[] prefixes = {"Is", "Are", "Was", "Were"};
        String s = somethingElseWithoutPrefix;
        String askQuery = askQuery_wrong;

        if (somethingElseWithoutPrefix == null || somethingElseWithoutPrefix.equals("")) {
            return;
        }

        generateQuestionAsk(s, prefixes, O, askQuery);
    }

    private void generateQuestionAsk(String s, String[] prefixes, String o, String askQuery) throws Exception {
        for (String prefix : prefixes) {
            if (s_o_NP != null && s_o_NP.startsWith(prefix.toLowerCase() + " ")) {
                s_o_NP_without_verb = s_o_NP.replace(prefix.toLowerCase() + " ", "");
                String question = prefix + " " + s + " " + s_o_NP_without_verb + " " + o + "?";
                String tagged_question = "<qt>" + prefix + "</qt> <s>" + s + "</s> <p>" + s_o_NP_without_verb + "</p> <o>" + o + "</o>?";
                allPossibleQuestions.add(new GeneratedQuestion(S_withPrefix, S_type_withPrefix, question, tagged_question, askQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
                break;
            }
        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
