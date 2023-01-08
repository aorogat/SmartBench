package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.queryBuilder.StarQueryGenerator;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.QuestionTypePrefixGenerator;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.CoordinatingConjunction;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PhraseRepresentationProcessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.EntityProcessing;

import java.util.*;

import benchmarkGenerator.subgraphShapeGenerator.model.TriplePattern;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.StarGraph;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import lexiconGenerator.predicateRepresentationExtractor.chunking.BasicNLP_FromPython;
import settings.Settings;

public class StarQuestion extends ShapeQuestion {

    static String FCs_tagged = "";
    public StarQueryGenerator starQueryGenerator;
    /**
     * Consider the following questions for "Which aircraft whose designer is DARPA?":
     * What are the aircraft for which DARPA is the designer?
     * Which aircraft have a designer that is DARPA?
     */

    StarGraph starGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String T;
    String FCs_AND;
    String FCs_OR;
    String FCs_AND_NOT;
    String FCs_OR_NOT;
    String FCs_NOT_NOT;
    String FCs_AND_tagged;
    String FCs_OR_tagged;
    String FCs_AND_NOT_tagged;
    String FCs_OR_NOT_tagged;
    String FCs_NOT_NOT_tagged;
    ArrayList<String> FCs_Representation = new ArrayList<>();
    ArrayList<String> FCs_Representation_tagged = new ArrayList<>();
    private String somethingElse;
    private String somethingElseWithoutPrefix;
    private Map<String, HashSet<String>> starPredicates;


    public StarQuestion(StarGraph starGraph) throws Exception {
        this.starGraph = starGraph;
        starQueryGenerator = new StarQueryGenerator(starGraph);
        starPredicates = starGraphToMapOfPredicates(starGraph);
        String S_type = starGraph.getStar().get(0).getS_type();
        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, starGraph.getStar().get(0).getSubject().getValueWithPrefix(), S_type);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);
        T = Settings.explorer.removePrefix(starGraph.getSeedType()).toLowerCase();
        FCs_AND = factConstraints_toString(starGraph, CoordinatingConjunction.AND);
        FCs_AND_tagged = FCs_tagged;
        if (FCs_AND == null) {
            return;
        }
        FCs_OR = factConstraints_toString(starGraph, CoordinatingConjunction.OR);
        FCs_OR_tagged = FCs_tagged;
        FCs_AND_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.AND_NOT);
        FCs_AND_NOT_tagged = FCs_tagged;
        FCs_OR_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.OR_NOT);
        FCs_OR_NOT_tagged = FCs_tagged;
        FCs_NOT_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.NOT_NOT);
        FCs_NOT_NOT_tagged = FCs_tagged;

        if (starGraph.getStar().size() == 1) {
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
        } else if (starGraph.getStar().size() == 2 && starGraph.getStar().size() == starPredicates.size()) { //no repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            selectQuestions(CoordinatingConjunction.OR);
            selectQuestions(CoordinatingConjunction.AND_NOT);
            selectQuestions(CoordinatingConjunction.OR_NOT);
            selectQuestions(CoordinatingConjunction.NOT_NOT);
            countQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.OR);
            countQuestions(CoordinatingConjunction.AND_NOT);
            countQuestions(CoordinatingConjunction.OR_NOT);
            countQuestions(CoordinatingConjunction.NOT_NOT);
        } else if (starGraph.getStar().size() == 2 && starGraph.getStar().size() != starPredicates.size()) { //repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
        } else if (starGraph.getStar().size() > 2 && starGraph.getStar().size() == starPredicates.size()) { //no repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            selectQuestions(CoordinatingConjunction.OR);
            countQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.OR);
        } else if (starGraph.getStar().size() > 2 && starGraph.getStar().size() != starPredicates.size()) { //repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
        }
    }

    public StarQuestion(StarGraph starGraph, boolean isTree) {
        this.starGraph = starGraph;
        starQueryGenerator = new StarQueryGenerator(starGraph);
        starPredicates = starGraphToMapOfPredicates(starGraph);
        String S_type = starGraph.getStar().get(0).getS_type();

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, starGraph.getStar().get(0).getSubject().getValueWithPrefix(), S_type);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        T = Settings.explorer.removePrefix(starGraph.getSeedType()).toLowerCase();

        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_only(somethingElseWithoutPrefix, S_type);

        FCs_AND = factConstraints_toString(starGraph, CoordinatingConjunction.AND);
        FCs_AND_tagged = FCs_tagged;
        if (FCs_AND == null) {
            return;
        }
        FCs_OR = factConstraints_toString(starGraph, CoordinatingConjunction.OR);
        FCs_OR_tagged = FCs_tagged;
        FCs_AND_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.AND_NOT);
        FCs_AND_NOT_tagged = FCs_tagged;
        FCs_OR_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.OR_NOT);
        FCs_OR_NOT_tagged = FCs_tagged;
        FCs_NOT_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.NOT_NOT);
        FCs_NOT_NOT_tagged = FCs_tagged;
    }

    /**
     * Converts a list of objects into a formatted string.
     *
     * @param objects  the list of objects to be converted
     * @param tagged  a boolean value indicating whether the objects should be wrapped in tags
     * @return a formatted string representing the list of objects
     *
     * Example:
     *
     * Given the following list of objects:
     *
     * ["o1.1", "o1.2", "o1.3"]
     *
     * and the value `true` for the `tagged` parameter, the method will return the following string:
     *
     * "&lt;o&gt;o1.1&lt;/o&gt;, &lt;o&gt;o1.2&lt;/o&gt;, and &lt;o&gt;o1.3&lt;/o&gt;"
     *
     * If the value of the `tagged` parameter is `false`, the method will return the following string:
     *
     * "o1.1, o1.2, and o1.3"
     *
     */
    public static String objectListToString(ArrayList<String> objects, boolean tagged) {
        String objectsList = "";
        ArrayList<String> objectsListCopy = new ArrayList<>();

        if (tagged) {
            for (String object : objects) {
                objectsListCopy.add("<o>" + object + "</o>");
            }
        } else
            objectsListCopy = new ArrayList(objects);


        switch (objectsListCopy.size()) {
            case 1:
                objectsList = objectsListCopy.get(0);
                break;
            case 2:
                objectsList = "both " + objectsListCopy.get(0) + " and " + objectsListCopy.get(1);
                break;
            default:
                objectsList += objectsListCopy.get(0);
                for (int i = 1; i < objectsListCopy.size() - 1; i++) {
                    objectsList += ", " + objectsListCopy.get(i);
                }
                objectsList += " and " + objectsListCopy.get(objectsListCopy.size() - 1);
                break;
        }

        return objectsList;
    }

    /**
     * Converts a StarGraph object into a map of predicates to a set of objects.
     *
     * @param starGraph  the StarGraph object to be converted
     * @return a map where the keys are predicates and the values are sets of objects
     *
     * Example:
     *
     * Given the following StarGraph object:
     *
     * StarGraph {
     *   star=[
     *     TriplePattern { subject=VarNode { value='x' }, predicate=URINode { value='p1' }, object=LiteralNode { value='o1.1', type='xsd:string' } },
     *     TriplePattern { subject=VarNode { value='x' }, predicate=URINode { value='p1' }, object=LiteralNode { value='o1.2', type='xsd:string' } },
     *     TriplePattern { subject=VarNode { value='x' }, predicate=URINode { value='p2' }, object=LiteralNode { value='o2.1', type='xsd:string' } },
     *     TriplePattern { subject=VarNode { value='x' }, predicate=URINode { value='p2' }, object=LiteralNode { value='o2.2', type='xsd:string' } }
     *   ]
     * }
     *
     * the method will return the following map:
     *
     * {
     *   p1=["o1.1", "o1.2"],
     *   p2=["o2.1", "o2.2"]
     * }
     *
     */
    private static Map<String, HashSet<String>> starGraphToMapOfPredicates(StarGraph starGraph) {
        //Fill starPredicates map to make star as (p1, O1.1, O1.2,...), ..... (P2, O2.1,O2.2,...)
        Map<String, HashSet<String>> starPredicates = new HashMap<>();
        for (TriplePattern triple : starGraph.getStar()) {
            String p = triple.getPredicate().getValue();
            String o;
            if (triple.getO_type().equals(Settings.Number)
                    || triple.getO_type().equals(Settings.Date)
                    || triple.getO_type().equals(Settings.Literal)) {
                o = triple.getObject().getValueWithPrefix();
            } else {
                o = triple.getObject().getValue();
            }

            o = EntityProcessing.decide_quotes_only(o, triple.getO_type());

            if (!starPredicates.containsKey(p)) {
                HashSet<String> objects = new HashSet<>();
                objects.add(o);
                starPredicates.put(p, objects);
            } else {
                starPredicates.get(p).add(o);
            }
        }
        return starPredicates;
    }


    public void countQuestions(String coordinatingConjunction) throws Exception {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            String howMany = QuestionTypePrefixGenerator.getHowManyPrefix();
            String countQuery = starQueryGenerator.countQuery(coordinatingConjunction);
            String question = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", howMany);
            String question_tagged = selectWhichQuestions_tagged(coordinatingConjunction).replaceFirst("Which", howMany);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, countQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_HOW_MANY, GeneratedQuestion.SH_STAR));
        }
    }

    public void askQuestions_true_answer(String coordinatingConjunction) throws Exception {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            String subject = starGraph.getStar().get(0).getSubject().getValue();
            subject = EntityProcessing.decide_quotes_only(subject, starGraph.getStar().get(0).getS_type());
            String question = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "");
            String question_tagged = selectWhichQuestions_tagged(coordinatingConjunction).replaceFirst("<qt>Which</qt>", "");
            if (subject.contains("\"")) {
                question = ("Is " + subject + " " + question.replace("whose", "its").replace("their", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
                question_tagged = ("<qt>Is</qt> <s>" + subject + "</s> " + question_tagged.replace("whose", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
            } else {
                question = ("Is " + subject.replace(T, "") + " " + question.replace("whose", "its").replace("their", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
                question_tagged = ("<qt>Is</qt> <s>" + subject.replace(T, "") + "</s> " + question_tagged.replace("whose", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
            }
            String askQuery = starQueryGenerator.askQuery_true_answer(coordinatingConjunction);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, askQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_STAR));
        }
    }

    public void askQuestions_false_answer(String coordinatingConjunction) throws Exception {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            String question = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "");
            String question_tagged = selectWhichQuestions_tagged(coordinatingConjunction).replaceFirst("<qt>Which</qt>", "");

            if (somethingElseWithoutPrefix.contains("\"")) {
                question = ("Is " + somethingElseWithoutPrefix + " " + question.replace("whose", "its").replace("their", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
                question_tagged = ("<qt>Is</qt> <s>" + somethingElseWithoutPrefix + "</s> " + question_tagged.replace("whose", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
            } else {
                question = ("Is " + somethingElseWithoutPrefix.replace(T, "") + " " + question.replace("whose", "its").replace("their", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
                question_tagged = ("<qt>Is</qt> <s>" + somethingElseWithoutPrefix.replace(T, "") + "</s> " + question_tagged.replace("whose", "its")).replaceFirst(" its ", "'s ").replace(" and's ", " its ").replace(" or's ", " its ").replaceFirst(BasicNLP_FromPython.nounPlural(T), T).replaceFirst(" is", "");
            }
            String askQuery = starQueryGenerator.askQuery_false_answer(coordinatingConjunction, somethingElse);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, askQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_STAR));

//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery, starGraph.toString()));
        }
    }

    public void selectQuestions(String coordinatingConjunction) throws Exception {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            //Generate QT_WHICH
            String whichQuestion = selectWhichQuestions(coordinatingConjunction);
            String whichQuestion_tagged = selectWhichQuestions_tagged(coordinatingConjunction);
            String question = whichQuestion;
            String question_tagged = whichQuestion_tagged;
            String selectQuery = starQueryGenerator.selectQuery(coordinatingConjunction);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_STAR));

            //Generate QT_WHAT
            question = whichQuestion.replaceFirst("Which " + BasicNLP_FromPython.nounPlural(T).trim(), "What are the " + BasicNLP_FromPython.nounPlural(T) + " ");
            question_tagged = whichQuestion_tagged.replaceFirst("<qt>Which</qt> <t>" + BasicNLP_FromPython.nounPlural(T).trim() + "</t>", "<qt>What</qt> are the <t>" + BasicNLP_FromPython.nounPlural(T) + "</t> ");
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_STAR));

            //Generate QT_REQUEST
            String req = QuestionTypePrefixGenerator.getRequestPrefix().trim();
            question = whichQuestion.replaceFirst("Which ", req + " ");
            question_tagged = whichQuestion_tagged.replaceFirst("Which", req);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_STAR));

            //Generate QT_TOPICAL_PRUNE
            question = whichQuestion.replaceFirst("Which ", "");
            question_tagged = whichQuestion_tagged.replaceFirst("<qt>Which</qt> ", "");
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_STAR));
        }
    }

    public String selectWhichQuestions(String coordinatingConjunction) {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }

        if (FCs != null) {
            return "Which " + BasicNLP_FromPython.nounPlural(T) + FCs + "?";
        }
        return null;
    }

    public String selectWhichQuestions_tagged(String coordinatingConjunction) {
        String FCs_tag = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs_tag = FCs_AND_tagged;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs_tag = FCs_AND_NOT_tagged;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs_tag = FCs_NOT_NOT_tagged;
                break;
            case CoordinatingConjunction.OR:
                FCs_tag = FCs_OR_tagged;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs_tag = FCs_OR_NOT_tagged;
                break;
            default:
        }

        if (FCs_tag != null) {
            return "<qt>Which</qt> <t>" + BasicNLP_FromPython.nounPlural(T) + "</t> " + FCs_tag + "?";
        }
        return null;
    }



    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }



    /**
     * Populates the `FCs_Representation` and `FCs_Representation_tagged` lists with natural language
     * representations of the fact constraints in the given {@code starGraph}.
     *
     * <p>For example, given a {@code starGraph} where the seed is a person and the fact constraints are:
     * <ul>
     *   <li>hasFriend: Alice</li>
     *   <li>hasFriend: Bob</li>
     *   <li>hasAge: 30</li>
     * </ul>
     * the resulting `FCs_Representation` and `FCs_Representation_tagged` lists might be:
     * <ul>
     *   <li>FCs_Representation: ["has Alice as a friend", "has Bob as a friend", "is 30 years old"]</li>
     *   <li>FCs_Representation_tagged: ["has &lt;o&gt;Alice&lt;/o&gt; as a &lt;p&gt;friend/&lt;p&gt;", "has &lt;o&gt;Bob&lt;/o&gt; as a &lt;p&gt;friend&lt;/p&gt;", "&lt;p&gt;is&lt;/p&gt; &lt;o&gt;30&lt;/o&gt; years old"]</li>
     * </ul>
     *
     * @param starGraph the {@link StarGraph} containing the fact constraints to be represented
     */
    private void fillFCs_Representation(StarGraph starGraph) {
        FCs_Representation = new ArrayList<>();
        FCs_Representation_tagged = new ArrayList<>();
        ArrayList<String> processedPredicates = new ArrayList<>();
        for (TriplePattern starTriple : starGraph.getStar()) {
            String p_with_Prefix = starTriple.getPredicate().getValueWithPrefix();
            String p = starTriple.getPredicate().getValue();
            if (processedPredicates.contains(p)) {
                continue;
            }
            String s_type = starTriple.getS_type();
            String o_type = starTriple.getO_type();
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            String O = objectListToString(objects, false);
            String O_tagged = objectListToString(objects, true);
            processedPredicates.add(p);

            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_with_Prefix, s_type, o_type);

            if (predicateNL != null) {
                if (predicateNL.getPredicate_s_O_NP() != null) {
                    String p_SO_NP = predicateNL.getPredicate_s_O_NP();
                    FCs_Representation.add(" " + p_SO_NP + " " + O);
                    FCs_Representation_tagged.add(" <p>" + p_SO_NP + "</p> " + O_tagged);
                } else if (predicateNL.getPredicate_s_O_VP() != null) {
                    String p_SO_VP = predicateNL.getPredicate_s_O_VP().trim();
                    p_SO_VP = PhraseRepresentationProcessing.verbPhraseAddAuxiliary(p_SO_VP);
                    FCs_Representation.add(" " + p_SO_VP + " " + O);
                    FCs_Representation_tagged.add(" <p>" + p_SO_VP + "</p> " + O_tagged);
                } else if (predicateNL.getPredicate_o_s_VP() != null) {
                    String p_OS_VP = predicateNL.getPredicate_o_s_VP();
                    FCs_Representation.add(" " + O + " " + p_OS_VP);
                    FCs_Representation_tagged.add(" " + O_tagged + " <p>" + p_OS_VP + "</p>");
                } else if (predicateNL.getPredicate_o_s_NP() != null) {
                    String p_OS_NP = PhraseRepresentationProcessing.NP_only(predicateNL.getPredicate_o_s_NP());
                    if (Settings.knowledgeGraph.isASubtypeOf(Settings.explorer, starGraph.getSeedType(), Settings.Person)) {
                        FCs_Representation.add(" their " + p_OS_NP + " is " + O);
                        FCs_Representation_tagged.add(" their <p>" + p_OS_NP + "</p> is " + O_tagged);
                    } else {
                        Random random = new Random();
                        switch (random.nextInt(2)) {
                            case 0:
                                FCs_Representation.add(" whose " + p_OS_NP + " is " + O);
                                FCs_Representation_tagged.add(" whose <p>" + p_OS_NP + "</p> is " + O_tagged);
                                break;
                            case 1:
                                FCs_Representation.add(" for which " + O + " is the " + p_OS_NP);
                                FCs_Representation_tagged.add(" for which " + O_tagged + " is the " + "<p>" + p_OS_NP + "</p>");
                                break;
                        }
                    }
                }
            }
        }
    }


    /**
     * Removes repeated connections in the FCs_Representation and FCs_Representation_tagged lists.
     *
     * This method iterates through the FCs_Representation list and looks for the "for which" and "whose" phrases. If they are found in an index other than the first, they are replaced with an empty string. The same operation is performed on the FCs_Representation_tagged list.
     */
    private void removeRepeatedConnectionsFCs_Representation() {
        for(int i=0; i<FCs_Representation.size(); i++){
            if(i==0 && FCs_Representation.get(i).contains("for which")){
                continue;
            }
            else if(i>0 && FCs_Representation.get(i).contains("for which")){
                FCs_Representation.set(i, FCs_Representation.get(i).replace("for which ", " "));
                FCs_Representation_tagged.set(i, FCs_Representation_tagged.get(i).replace("for which ", " "));
            }
            else {
                break;
            }
        }

        for(int i=0; i<FCs_Representation.size(); i++){
            if(i==0 && FCs_Representation.get(i).contains("whose")){
                continue;
            }
            else if(i>0 && FCs_Representation.get(i).contains("whose")){
                FCs_Representation.set(i, FCs_Representation.get(i).replace("whose ", " "));
                FCs_Representation_tagged.set(i, FCs_Representation_tagged.get(i).replace("whose ", " "));
            }
            else {
                break;
            }
        }
    }

    /**
     * Joins the elements of the FCs_Representation and FCs_Representation_tagged lists with a coordinating conjunction.
     *
     * @param starGraph the StarGraph object being processed
     * @param coorinatingConjunction the coordinating conjunction to be used for joining the elements of the lists
     * @return a string representing the joined elements of the lists, or null if the conjunction is "and not" or "or not" and the FCs do not contain "not"
     *
     * @example
     * List<String> FCs_Representation = new ArrayList<>(Arrays.asList("I am hungry", "I am thirsty"));
     * List<String> FCs_Representation_tagged = new ArrayList<>(Arrays.asList("I <vb>am</vb> hungry", "I <vb>am</vb> thirsty"));
     *
     * StarGraph starGraph = new StarGraph();
     *
     * String joinedFCs = joinFCs_RepresentationByCoordinatingConjunctions(starGraph, CoordinatingConjunction.AND);
     *
     * // joinedFCs will be "I am hungry and I am thirsty"
     * // FCs_tagged will be "I <vb>am</vb> hungry <cc>and</cc> I <vb>am</vb> thirsty"
     */
    private String joinFCs_RepresentationByCoordinatingConjunctions(StarGraph starGraph, String coorinatingConjunction) {
        String FCs = "";
        FCs_tagged = "";
        switch (FCs_Representation.size()) {
            case 0:
                return null;
            case 1:
                FCs = FCs_Representation.get(0);
                FCs_tagged = FCs_Representation_tagged.get(0);
                break;
            case 2:
                if (coorinatingConjunction.equals(CoordinatingConjunction.AND)) {
                    FCs = FCs_Representation.get(0) + " and" + FCs_Representation.get(1);
                    FCs_tagged = FCs_Representation_tagged.get(0) + " <cc>and</cc>" + FCs_Representation_tagged.get(1);
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.OR)) {
                    FCs = " either" + FCs_Representation.get(0) + " or" + FCs_Representation.get(1);
                    FCs_tagged = " either" + FCs_Representation_tagged.get(0) + " <cc>or</cc>" + FCs_Representation_tagged.get(1);
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.AND_NOT)) {
                    FCs = FCs_Representation.get(0) + " but" + PhraseRepresentationProcessing.negatePhrase(FCs_Representation.get(1));
                    FCs_tagged = FCs_Representation_tagged.get(0) + " <cc>but not</cc>" + FCs_Representation_tagged.get(1);
                    if (!FCs.contains(" not")) {
                        return null;
                    }
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.OR_NOT)) {
                    FCs = FCs_Representation.get(0) + " or" + PhraseRepresentationProcessing.negatePhrase(FCs_Representation.get(1));
                    FCs_tagged = FCs_Representation_tagged.get(0) + " <cc>or not</cc>" + FCs_Representation_tagged.get(1);
                    if (!FCs.contains(" not")) {
                        return null;
                    }
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.NOT_NOT)) {
                    FCs = " neither" + FCs_Representation.get(0) + " nor" + FCs_Representation.get(1);
                    FCs_tagged = " neither" + FCs_Representation_tagged.get(0) + " <cc>nor</cc>" + FCs_Representation_tagged.get(1);
                }
                break;
            default:
                if (coorinatingConjunction.equals("and")) {
                    for (int i = 0; i < FCs_Representation.size() - 1; i++) {
                        FCs += FCs_Representation.get(i) + " ,";
                        FCs_tagged += FCs_Representation_tagged.get(i) + " ,";
                    }
                    FCs += "and" + FCs_Representation.get(FCs_Representation.size() - 1);
                    FCs_tagged += "<cc>and</cc>" + FCs_Representation_tagged.get(FCs_Representation_tagged.size() - 1);
                } else if (coorinatingConjunction.equals("or")) {
                    for (int i = 0; i < FCs_Representation.size() - 1; i++) {
                        FCs += FCs_Representation.get(i) + " ,";
                        FCs_tagged += FCs_Representation_tagged.get(i) + " ,";
                    }
                    FCs += "or" + FCs_Representation.get(FCs_Representation.size() - 1);
                    FCs_tagged += "<cc>or</cc>" + FCs_Representation_tagged.get(FCs_Representation_tagged.size() - 1);
                }
                break;
        }
        return FCs;
    }


    /**
     * Generates a string representation of the fact constraints in the given StarGraph object, using the given coordinating conjunction.
     *
     * @param starGraph the StarGraph object to process
     * @param coorinatingConjunction the coordinating conjunction to use when joining the fact constraints
     * @return a string representation of the fact constraints, or null if the conjunction is "and not" or "or not" and the FCs do not contain "not"
     */
    public String factConstraints_toString(StarGraph starGraph, String coorinatingConjunction) {
        fillFCs_Representation(starGraph);
        removeRepeatedConnectionsFCs_Representation();
        return joinFCs_RepresentationByCoordinatingConjunctions(starGraph, coorinatingConjunction);
    }

    public String getFCs_with_T_COO_is_AND() {
        return T + " " + FCs_AND;
    }

    public String getFCs_with_T_COO_is_AND_taggString() {
        return "<t>" + T + "</t> " + FCs_AND_tagged;
    }

    public String getFCs_with_T_COO_is_OR() {
        return T + FCs_OR;
    }

    public String getFCs_with_T_COO_is_AND_NOT() {
        return T + FCs_AND_NOT;
    }

    public String getFCs_with_T_COO_is_OR_NOT() {
        return T + FCs_OR_NOT;
    }

    public String getFCs_with_T_COO_is_NOT_NOT() {
        return T + FCs_NOT_NOT;
    }

    @Override
    public void generateAllPossibleQuestions() throws Exception {

    }
}
