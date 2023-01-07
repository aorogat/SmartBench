package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.QuestionTypePrefixGenerator;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.CoordinatingConjunction;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PhraseRepresentationProcessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.EntityProcessing;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import benchmarkGenerator.subgraphShapeGenerator.model.TriplePattern;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.StarGraph;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import lexiconGenerator.predicateRepresentationExtractor.chunking.BasicNLP_FromPython;
import settings.Settings;

public class StarQuestion {

    StarGraph starGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String T;
    String FCs_AND;
    String FCs_AND_withGroupBy;
    String FCs_OR;
    String FCs_AND_NOT;
    String FCs_OR_NOT;
    String FCs_NOT_NOT;

    static String FCs_tagged = "";
    String T_tagged;
    String FCs_AND_tagged;
    String FCs_AND_withGroupBy_tagged;
    String FCs_OR_tagged;
    String FCs_AND_NOT_tagged;
    String FCs_OR_NOT_tagged;
    String FCs_NOT_NOT_tagged;

    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";

    String GPs_ASK; //for Graph Patterns

    public StarQuestion(StarGraph starGraph) throws Exception {
        this.starGraph = starGraph;
        String S_type = starGraph.getStar().get(0).getS_type();

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, starGraph.getStar().get(0).getSubject().getValueWithPrefix(), S_type);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        T = Settings.explorer.removePrefix(starGraph.getSeedType()).toLowerCase();

        Map<String, HashSet<String>> starPredicates = new HashMap<>();

        //Fill starPredicates map to make star as (p1, O1.1, O1.2,...), ..... (P2, O2.1,O2.2,...)
        for (TriplePattern triple : starGraph.getStar()) {
            String p = triple.getPredicate().getValue();
            String s = triple.getSubject().getValue();
            String o = null;
            if (triple.getO_type().equals(Settings.Number)
                    || triple.getO_type().equals(Settings.Date)
                    || triple.getO_type().equals(Settings.Literal)) {
                o = triple.getObject().getValueWithPrefix();
            } else {
                o = triple.getObject().getValue();
            }

            s = EntityProcessing.decide_quotes_only(s, S_type);
            o = EntityProcessing.decide_quotes_only(o, triple.getO_type());
            somethingElseWithoutPrefix = EntityProcessing.decide_quotes_only(somethingElseWithoutPrefix, S_type);

            if (!starPredicates.containsKey(p)) {
                HashSet<String> objects = new HashSet<>();
                objects.add(o);
                starPredicates.put(p, objects);
            } else {
                starPredicates.get(p).add(o);
            }
        }

        FCs_AND = factConstraints_toString(starGraph, CoordinatingConjunction.AND, starPredicates);
        FCs_AND_tagged = FCs_tagged;
        if (FCs_AND == null) {
            return;
        }
        FCs_OR = factConstraints_toString(starGraph, CoordinatingConjunction.OR, starPredicates);
        FCs_OR_tagged = FCs_tagged;
        FCs_AND_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.AND_NOT, starPredicates);
        FCs_AND_NOT_tagged = FCs_tagged;
        FCs_OR_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.OR_NOT, starPredicates);
        FCs_OR_NOT_tagged = FCs_tagged;
        FCs_NOT_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.NOT_NOT, starPredicates);
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
        String S_type = starGraph.getStar().get(0).getS_type();

        somethingElse = Settings.knowledgeGraph.getSimilarEntity(Settings.explorer, starGraph.getStar().get(0).getSubject().getValueWithPrefix(), S_type);
        somethingElseWithoutPrefix = Settings.explorer.removePrefix(somethingElse);

        T = Settings.explorer.removePrefix(starGraph.getSeedType()).toLowerCase();

        Map<String, HashSet<String>> starPredicates = new HashMap<>();

        //Fill starPredicates map to make star as (p1, O1.1, O1.2,...), ..... (P2, O2.1,O2.2,...)
        for (TriplePattern triple : starGraph.getStar()) {
            String p = triple.getPredicate().getValue();
            String s = triple.getSubject().getValue();
            String o = null;
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

        somethingElseWithoutPrefix = EntityProcessing.decide_quotes_only(somethingElseWithoutPrefix, S_type);

        FCs_AND = factConstraints_toString(starGraph, CoordinatingConjunction.AND, starPredicates);
        FCs_AND_tagged = FCs_tagged;
        if (FCs_AND == null) {
            return;
        }
        FCs_OR = factConstraints_toString(starGraph, CoordinatingConjunction.OR, starPredicates);
        FCs_OR_tagged = FCs_tagged;
        FCs_AND_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.AND_NOT, starPredicates);
        FCs_AND_NOT_tagged = FCs_tagged;
        FCs_OR_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.OR_NOT, starPredicates);
        FCs_OR_NOT_tagged = FCs_tagged;
        FCs_NOT_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.NOT_NOT, starPredicates);
        FCs_NOT_NOT_tagged = FCs_tagged;

    }

    public String askQuery_true_answer(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + starGraph.getStar().get(0).getSubject().getValueWithPrefix() + ">");
    }

    public String askQuery_false_answer(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    public String countQuery(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }

    public void countQuestions(String coordinatingConjunction) throws Exception {
        String FCs = "";
        String FCs_tag = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                FCs_tag = FCs_AND_tagged;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                FCs_tag = FCs_AND_NOT_tagged;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                FCs_tag = FCs_NOT_NOT_tagged;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                FCs_tag = FCs_OR_tagged;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                FCs_tag = FCs_OR_NOT_tagged;
                break;
            default:
        }
        if (FCs != null) {
            String howMany = QuestionTypePrefixGenerator.getHowManyPrefix();
            String countQuery = countQuery(starGraph, coordinatingConjunction);
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
            String askQuery = askQuery_true_answer(starGraph, coordinatingConjunction);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, askQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_STAR));
        }
    }

    public void askQuestions_false_answer(String coordinatingConjunction) throws Exception {
        String FCs = "";
        String FCs_tag = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                FCs_tag = FCs_AND_tagged;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                FCs_tag = FCs_AND_NOT_tagged;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                FCs_tag = FCs_NOT_NOT_tagged;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                FCs_tag = FCs_OR_tagged;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                FCs_tag = FCs_OR_NOT_tagged;
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
            String askQuery = askQuery_false_answer(starGraph, coordinatingConjunction);
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
            String whichQuestion = selectWhichQuestions(coordinatingConjunction);
            String whichQuestion_tagged = selectWhichQuestions_tagged(coordinatingConjunction);
            String question = whichQuestion;
            String question_tagged = whichQuestion_tagged;
            String selectQuery = selectQuery(starGraph, coordinatingConjunction);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_STAR));

            question = whichQuestion.replaceFirst("Which " + T.trim(), "What are the " + BasicNLP_FromPython.nounPlural(T) + " ");
            question = whichQuestion.replaceFirst("Which " + BasicNLP_FromPython.nounPlural(T).trim(), "What are the " + BasicNLP_FromPython.nounPlural(T) + " ");
            question_tagged = whichQuestion_tagged.replaceFirst("<qt>Which</qt> <t>" + T.trim() + "</t>", "<qt>What</qt> are the <t>" + BasicNLP_FromPython.nounPlural(T) + "</t> ");
            question_tagged = whichQuestion_tagged.replaceFirst("<qt>Which</qt> <t>" + BasicNLP_FromPython.nounPlural(T).trim() + "</t>", "<qt>What</qt> are the <t>" + BasicNLP_FromPython.nounPlural(T) + "</t> ");
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_STAR));

            String req = QuestionTypePrefixGenerator.getRequestPrefix().trim();
            question = whichQuestion.replaceFirst("Which ", req + " ");
            question_tagged = whichQuestion_tagged.replaceFirst("Which", req);
            
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, selectQuery, starGraph.toString(), starGraph.getStar().size() + 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_STAR));

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

    public String selectQuery(StarGraph starGraph, String coordinatingConjunction) {
        String query = "";
        String triples = "";
        ArrayList<TriplePattern> star = starGraph.getStar();
        String T = starGraph.getSeedType();
        triples += "\n\t ?Seed \t rdf:type \t <" + T + "> . ";

        if (coordinatingConjunction.equals(CoordinatingConjunction.AND)) {
            for (TriplePattern triple : star) {
                if (triple.getS_type().equals(Settings.Number) || triple.getS_type().equals(Settings.Date) || triple.getS_type().equals(Settings.Literal)) {
                    triples += "\n\t" + triple.toQueryTriplePattern()
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed")
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"", "?Seed")
                            .replace(" " + triple.getSubject().getValueWithPrefix() + " ", "?Seed")
                            + " . ";
                } else {
                    triples += "\n\t" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + " . ";
                }
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR)) {
            for (TriplePattern triple : star) {
//                triples += "\n\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} UNION ";
                if (triple.getS_type().equals(Settings.Number) || triple.getS_type().equals(Settings.Date) || triple.getS_type().equals(Settings.Literal)) {
                    triples += "\n\t{" + triple.toQueryTriplePattern()
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed")
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"", "?Seed")
                            .replace(" " + triple.getSubject().getValueWithPrefix() + " ", "?Seed")
                            + ".} UNION ";
                } else {
                    triples += "\n\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} UNION ";
                }
            }
            triples = triples.substring(0, triples.length() - "UNION ".length());
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.AND_NOT)) {
            for (TriplePattern triple : star) {
                if (triple.getS_type().equals(Settings.Number) || triple.getS_type().equals(Settings.Date) || triple.getS_type().equals(Settings.Literal)) {
                    triples += "\n\t{" + triple.toQueryTriplePattern()
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed")
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"", "?Seed")
                            .replace(" " + triple.getSubject().getValueWithPrefix() + " ", "?Seed")
                            + ".} MINUS ";
                } else {
                    triples += "\n\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} MINUS ";
                }
            }
            triples = triples.substring(0, triples.length() - "MINUS ".length());
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.NOT_NOT)) {
            triples += "\n\tMINUS{ ";
            for (TriplePattern triple : star) {
                if (triple.getS_type().equals(Settings.Number) || triple.getS_type().equals(Settings.Date) || triple.getS_type().equals(Settings.Literal)) {
                    triples += "\n\t\t{" + triple.toQueryTriplePattern()
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed")
                            .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"", "?Seed")
                            .replace(" " + triple.getSubject().getValueWithPrefix() + " ", "?Seed")
                            + ".} UNION ";
                } else {
                    triples += "\n\t\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} UNION ";
                }
            }
            triples = triples.substring(0, triples.length() - "UNION ".length());
            triples += "\n\t} ";
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR_NOT)) {
            triples += "\n\tMINUS{ ";
            triples += "\n\t\t {{ ?Seed \t rdf:type \t <" + T + "> .} MINUS{ " + star.get(0).toQueryTriplePattern()
                    .replace("<" + star.get(0).getSubject().getValueWithPrefix() + ">", "?Seed")
                    .replace("\"" + star.get(0).getSubject().getValueWithPrefix() + "\"", "?Seed")
                    .replace(" " + star.get(0).getSubject().getValueWithPrefix() + " ", "?Seed")
                    + "}} . ";
            int k = 0;
            for (TriplePattern triple : star) {
                k++;
                if (k == 1) {
                    continue;
                }
                triples += "\n\t\t" + triple.toQueryTriplePattern()
                        .replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed")
                        .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed")
                        .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"", "?Seed")
                        .replace(" " + triple.getSubject().getValueWithPrefix() + " ", "?Seed")
                        + ". ";
            }
            triples += "\n\t} ";
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        }
        return query;
    }

    public static String objectListToString(ArrayList<String> objects, boolean tagged) {
        String objectsList = "";
        if (tagged) {
            if (objects.size() == 1) {
                objectsList = "<o>" + objects.get(0) + "</o>";
            } else if (objects.size() == 2) {
                String o1 = "<o>" + objects.get(0) + "</o>";
                String o2 = "<o>" + objects.get(1) + "</o>";
                objectsList = "both " + o1 + " and " + o2;
            } else if (objects.size() > 2) {
                //represent objectsList
                objectsList += "<o>" + objects.get(0) + "</o>";
                for (int i = 1; i < objects.size() - 1; i++) {
                    objectsList += ", " + "<o>" + objects.get(i) + "</o>";
                }
                objectsList += " and " + "<o>" + objects.get(objects.size() - 1) + "</o>";
            }
        } else {
            if (objects.size() == 1) {
                objectsList = objects.get(0);
            } else if (objects.size() == 2) {
                String o1 = objects.get(0);
                String o2 = objects.get(1);
                objectsList = "both " + o1 + " and " + o2;
            } else if (objects.size() > 2) {
                //represent objectsList
                objectsList += objects.get(0);
                for (int i = 1; i < objects.size() - 1; i++) {
                    objectsList += ", " + objects.get(i);
                }
                objectsList += " and " + objects.get(objects.size() - 1);
            }
        }
        return objectsList;
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

    public static String factConstraints_toString(StarGraph starGraph, String coorinatingConjunction, Map<String, HashSet<String>> starPredicates) {
        ArrayList<String> FCs_Representation = new ArrayList<>();
        ArrayList<String> FCs_Representation_tagged = new ArrayList<>();
        ArrayList<TriplePattern> branches = starGraph.getStar();

        ArrayList<String> processedPredicates = new ArrayList<>();
        for (TriplePattern branch : branches) {
            String p_with_Prefix = branch.getPredicate().getValueWithPrefix();
            String p = branch.getPredicate().getValue();
            if (processedPredicates.contains(p)) {
                continue;
            }
            String s_type = branch.getS_type();
            String o_type = branch.getO_type();
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            String O = objectListToString(objects, false);
            String O_tagged = objectListToString(objects, true);
            processedPredicates.add(p);

            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_with_Prefix, s_type, o_type);

            if (predicateNL != null) {
                System.out.println("Predicate Representation for " + p_with_Prefix + " (" + s_type + " ," + o_type + ") is");
                predicateNL.print();

                if (predicateNL.getPredicate_s_O_NP() != null) {
                    String p_SO_NP = predicateNL.getPredicate_s_O_NP();
                    FCs_Representation.add(" " + p_SO_NP + " " + O);
                    FCs_Representation_tagged.add(" <p>" + p_SO_NP + "</p> " + O_tagged);
                } else if (predicateNL.getPredicate_s_O_VP() != null) {
                    String p_SO_VP = predicateNL.getPredicate_s_O_VP().trim();
                    if ((p_SO_VP.startsWith("was ") || p_SO_VP.startsWith("were ") || p_SO_VP.startsWith("is ") || p_SO_VP.startsWith("are "))) {
                        FCs_Representation.add(" " + p_SO_VP + " " + O);
                        FCs_Representation_tagged.add(" <p>" + p_SO_VP + "</p> " + O_tagged);
                    } else {
                        FCs_Representation.add(" were " + p_SO_VP + " " + O);
                        FCs_Representation_tagged.add(" were <p>" + p_SO_VP + "</p> " + O_tagged);
                    }
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
                        FCs_Representation.add(" whose " + p_OS_NP + " is " + O);
                        FCs_Representation_tagged.add(" whose <p>" + p_OS_NP + "</p> is " + O_tagged);
                    }
                } else {
                    return null;
                }
            } else {
                System.out.println("Predicate Representation for " + p_with_Prefix + " (" + s_type + " ," + o_type + ") is");
                System.out.println("NULL");
                return null;
            }
        }
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
                    FCs = FCs_Representation.get(0) + " but" + FCs_Representation.get(1)
                            .replaceAll("\\bis\\b", "is not")
                            .replaceAll("\\bare\\b", "are not")
                            .replaceAll("\\bwas\\b", "was not")
                            .replaceAll("\\bwere\\b", "were not");
                    FCs_tagged = FCs_Representation_tagged.get(0) + " <cc>but</cc>" + FCs_Representation_tagged.get(1)
                            .replaceAll("\\bis\\b", "is not")
                            .replaceAll("\\bare\\b", "are not")
                            .replaceAll("\\bwas\\b", "was not")
                            .replaceAll("\\bwere\\b", "were not");
                    if (!FCs.contains(" not")) {
                        return null;
                    }
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.OR_NOT)) {
                    FCs = FCs_Representation.get(0) + " or" + FCs_Representation.get(1)
                            .replaceAll("\\bis\\b", "is not")
                            .replaceAll("\\bare\\b", "are not")
                            .replaceAll("\\bwas\\b", "was not")
                            .replaceAll("\\bwere\\b", "were not");
                    FCs_tagged = FCs_Representation_tagged.get(0) + " or" + FCs_Representation_tagged.get(1)
                            .replaceAll("\\bis\\b", "is not")
                            .replaceAll("\\bare\\b", "are not")
                            .replaceAll("\\bwas\\b", "was not")
                            .replaceAll("\\bwere\\b", "were not");
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
                    FCs_tagged += "and" + FCs_Representation_tagged.get(FCs_Representation_tagged.size() - 1);
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

}
