package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.QuestionTypePrefixGenerator;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PhraseRepresentationProcessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.PredicatePreprocessing;
import benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors.EntityProcessing;
import java.util.ArrayList;

import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.StarGraph;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class StarSetQuestion extends ShapeQuestion {

    StarGraph starGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String T;
    String T_withprefix;

    public StarSetQuestion(StarGraph starGraph, String T_prefix) throws Exception {
        this.starGraph = starGraph;
        this.T = Settings.explorer.removePrefix(T_prefix);
        this.T_withprefix = T_prefix;
        String P = starGraph.getStar().get(0).getPredicate().getValue();
        String P_withPrefix = starGraph.getStar().get(0).getPredicate().getValueWithPrefix();

        String S = starGraph.getStar().get(0).getSubject().getValue();
        String S_withPrefix = starGraph.getStar().get(0).getSubject().getValueWithPrefix();

        String S_Type = starGraph.getStar().get(0).getS_type_without_prefix();
        String S_Type_withPrefix = starGraph.getStar().get(0).getS_type();

        String O_Type = starGraph.getStar().get(0).getO_type_without_prefix();
        String O_Type_withPrefix = starGraph.getStar().get(0).getO_type();

        String O = null;
        if (O_Type.equals(Settings.Number) || O_Type.equals(Settings.Date) || O_Type.equals(Settings.Literal)) {
            O = starGraph.getStar().get(0).getObject().getValueWithPrefix();
        } else {
            O = starGraph.getStar().get(0).getObject().getValue();
        }

//        O = EntityProcessing.decide_quotes(O, O_Type_withPrefix);
        String O_withPrefix = starGraph.getStar().get(0).getObject().getValueWithPrefix();
        
        O_withPrefix = EntityProcessing.decide_quotes_Simple_question(O_withPrefix, O_Type_withPrefix);

        String compareEntityTop_withPrefix = Settings.knowledgeGraph.getTopEntity(T_withprefix, P_withPrefix, true);
        String compareEntityDown_withPrefix = Settings.knowledgeGraph.getTopEntity(T_withprefix, P_withPrefix, false);

        String compareEntityDown = Settings.explorer.removePrefix(compareEntityDown_withPrefix);
        String compareEntityTop = Settings.explorer.removePrefix(compareEntityTop_withPrefix);

        compareEntityDown = EntityProcessing.decide_quotes_Simple_question(compareEntityDown, T_withprefix);
        compareEntityTop = EntityProcessing.decide_quotes_Simple_question(compareEntityTop, T_withprefix);

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_Type_withPrefix, O_Type_withPrefix);
        if (predicateNL == null) {
            return;
        }
        String so_NP = PhraseRepresentationProcessing.NP_of_the_form_NP_only(predicateNL.getPredicate_s_O_NP());
        String so_VP = predicateNL.getPredicate_s_O_VP();
        String os_NP = PhraseRepresentationProcessing.NP_of_the_form_NP_only(predicateNL.getPredicate_o_s_NP());
        String os_VP = predicateNL.getPredicate_o_s_VP();

        String lessNL = "";
        String greaterNL = "";
        String equalNL = "";

        String question_tagged = "";

        if (O_Type_withPrefix.equals(Settings.Date)) {
            lessNL = "before";
            greaterNL = "after";
            equalNL = "as the same time as";
        } else if (O_Type_withPrefix.equals(Settings.Number)) {
            
            so_NP = PredicatePreprocessing.reorder_predicaeTokens_Numbers(so_NP);
            os_NP = PredicatePreprocessing.reorder_predicaeTokens_Numbers(os_NP);
            
            lessNL = "less than";
            greaterNL = "greater than";
            equalNL = "equals";
        }

        // < date
        String question = null;
        String query = null;
        if (so_VP != null) {
            question = "Which " + T + " " + so_VP + " " + lessNL + " " + compareEntityDown.trim() + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> <p>" + so_VP + "</p> <op>" + lessNL + "</op> <o>" + compareEntityDown.trim() + "</o>?";
        } else if (so_NP != null) {
            so_NP = so_NP.replaceAll("\\(.*\\)", "");
            question = "Which " + T + " has " + so_NP + " " + lessNL + " " + compareEntityDown + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> has <p>" + so_NP + "</p> <op>" + lessNL + "</op> <o>" + compareEntityDown + "</o>?";
        } else if (os_NP != null) {
            os_NP = os_NP.replaceAll("\\(.*\\)", "");
            question = "Which " + T + " has " + os_NP + " " + lessNL + " " + compareEntityDown + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> has <p>" + os_NP + "</p> <op>" + lessNL + "</op> <o>" + compareEntityDown + "</o>?";
        }
        query = "SELECT ?Seed WHERE \n"
                + "{\n"
                + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                + "     <" + compareEntityDown_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                + "     <" + compareEntityDown_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<?NN)"
                + "\n}";

        if (question != null && query != null) {
            if (!question.contains("null")) {
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET));

                String identifier = "";
                if (T.toLowerCase().startsWith("a")
                        || T.toLowerCase().startsWith("e")
                        || T.toLowerCase().startsWith("i")
                        || T.toLowerCase().startsWith("o")
                        || T.toLowerCase().startsWith("u")) {
                    identifier = "An";
                } else {
                    identifier = "A";
                }

                String question_pruned = question.replaceFirst("Which", identifier);
                String question_tagged_ = question_tagged.replaceFirst("<qt>Which</qt> ", identifier);
                
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question_pruned, question_tagged_, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET));

                String req = QuestionTypePrefixGenerator.getRequestPrefix();
                String quetion_request = question.replaceFirst("Which", req + " " + identifier.toLowerCase());
                String question_tagged_req = question_tagged.replaceFirst("<qt>Which</qt>", "<qt>" + req + "</qt>");
                
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), quetion_request, question_tagged_req, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET));
            }
        }

        //Modefied Question compare to constant
        question = null;
        query = null;
        if (so_VP != null) {
            question = "Which " + T + " " + so_VP + " " + lessNL + " " + O_withPrefix + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> <p>" + so_VP + "</p> <op>" + lessNL + "</op> <o>" + O_withPrefix + "</o>?";
        } else if (so_NP != null) {
            so_NP = so_NP.replaceAll("\\(.*\\)", "");
            question = "Which " + T + " has " + so_NP + " " + lessNL + " " + O_withPrefix + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> has <p>" + so_NP + "</p> <op>" + lessNL + "</op> <o>" + O_withPrefix + "</o>?";
        } else if (os_NP != null) {
            os_NP = os_NP.replaceAll("\\(\\?\\)", "");
            question = "Which " + T + " has " + os_NP + " " + lessNL + " " + O_withPrefix + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> has <p>" + os_NP + "</p> <op>" + lessNL + "</op> <o>" + O_withPrefix + "</o>?";
        }
        query = "SELECT ?Seed WHERE \n"
                + "{\n"
                + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<" + O_withPrefix + ")"
                + "\n}";

        if (question != null && query != null) {
            if (!question.contains("null")) {
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_STAR_MODIFIED));

                String identifier = "";
                if (T.toLowerCase().startsWith("a")
                        || T.toLowerCase().startsWith("e")
                        || T.toLowerCase().startsWith("i")
                        || T.toLowerCase().startsWith("o")
                        || T.toLowerCase().startsWith("u")) {
                    identifier = "An";
                } else {
                    identifier = "A";
                }

                String question_pruned = question.replaceFirst("Which", identifier);
                String question_tagged_ = question_tagged.replaceFirst("<qt>Which</qt> ", identifier);
                
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question_pruned, question_tagged_, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_STAR_MODIFIED));

                String req = QuestionTypePrefixGenerator.getRequestPrefix();
                String quetion_request = question.replaceFirst("Which", req + " " + identifier.toLowerCase());
                String question_tagged_req = question_tagged.replaceFirst("<qt>Which</qt>", "<qt>" + req + "</qt>");
                
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), quetion_request, question_tagged_req, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_STAR_MODIFIED));
            }
        }

        // > date
        question = null;
        query = null;
        if (so_VP != null) {
            question = "Which " + T + " " + so_VP + " " + greaterNL + " " + compareEntityTop.trim() + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> <p>" + so_VP + "</p> <op>" + greaterNL + "</op> <o>" + compareEntityTop.trim() + "</o>?";
        } else if (so_NP != null) {
            question = "Which " + T + " has " + so_NP + " " + greaterNL + " " + compareEntityTop + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> has <p>" + so_NP + "</p> <op>" + greaterNL + "</op> <o>" + compareEntityTop + "</o>?";
        } else if (os_NP != null) {
            question = "Which " + T + " has " + os_NP + " " + greaterNL + " " + compareEntityTop + "?";
            question_tagged = "<qt>Which</qt> <t>" + T + "</t> has <p>" + os_NP + "</p> <op>" + greaterNL + "</op> <o>" + compareEntityTop + "</o>?";
        }
        query = "SELECT ?Seed WHERE \n"
                + "{\n"
                + "     ?Seed rdf:type <" + T_withprefix + ">. "
                + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n>?NN)"
                + "\n}";

        if (question != null && query != null) {
            if (!question.contains("null")) {
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET));

                String identifier = "";
                if (T.toLowerCase().startsWith("a")
                        || T.toLowerCase().startsWith("e")
                        || T.toLowerCase().startsWith("i")
                        || T.toLowerCase().startsWith("o")
                        || T.toLowerCase().startsWith("u")) {
                    identifier = "An";
                } else {
                    identifier = "A";
                }

                String question_pruned = question.replaceFirst("Which", identifier);
                String question_tagged_ = question_tagged.replaceFirst("<qt>Which</qt> ", identifier);
                
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question_pruned, question_tagged_, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET));

                String req = QuestionTypePrefixGenerator.getRequestPrefix();
                String quetion_request = question.replaceFirst("Which", req + " " + identifier.toLowerCase());
                String question_tagged_req = question_tagged.replaceFirst("<qt>Which</qt>", "<qt>" + req + "</qt>");
                
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), quetion_request, question_tagged_req, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET));
            }
        }

        //Modefied Question - first after
        query = null;
        question = null;
        String offset_most = "has the most";

        if (O_Type_withPrefix.equals(Settings.Number)) {
            if (so_NP != null) {
                so_NP = so_NP.replaceAll("\\(.*\\)", "");

                if (so_NP.toLowerCase().contains("area")
                        || so_NP.toLowerCase().contains("size")) {
                    offset_most = "has the largest";
                }

                question = "Which " + T + " " + offset_most + " " + so_NP.trim() + " after " + compareEntityTop + "?";
                question_tagged = "<qt>Which</qt> <t>" + T + "</t>" + " <off>" + offset_most + "</off> <p>" + so_NP.trim() + "</p>" + " <op>after</op> <o>" + compareEntityTop + "<o>" + "?"; //off for offest
            } else if (os_NP != null) {
                if (os_NP.toLowerCase().contains("area")
                        || os_NP.toLowerCase().contains("size")) {
                    offset_most = "has the largest";
                }

                os_NP = os_NP.replaceAll("\\(\\?\\)", "");
                question = "Which " + T + " " + offset_most + " " + os_NP.trim() + " after " + compareEntityTop + "?";
                question_tagged = "<qt>Which</qt> <t>" + T + "</t>" + " <off>" + offset_most + "</off> <p>" + os_NP.trim() + "</p>" + " <op>after</op> <o>" + compareEntityTop + "</o>" + "?";
            }
            query = "SELECT ?Seed WHERE \n"
                    + "{\n"
                    + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                    + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<?NN)"
                    + "\n}" + " \nORDER BY DESC(?n)\n"
                    + "LIMIT 1\n"
                    + "OFFSET 0";
            if (question != null && query != null) {
                if (!question.contains("null")) {
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET_MODIFIED));

                    String identifier = "";
                    if (T.toLowerCase().startsWith("a")
                            || T.toLowerCase().startsWith("e")
                            || T.toLowerCase().startsWith("i")
                            || T.toLowerCase().startsWith("o")
                            || T.toLowerCase().startsWith("u")) {
                        identifier = "an";
                    } else {
                        identifier = "a";
                    }

                    question = question.replace(offset_most, "which " + offset_most + "").replace(T, identifier + " " + T);
                    question_tagged = question_tagged.replace(offset_most, "which " + offset_most);

                    String question_pruned = question.replaceFirst("Which ", "");
                    String question_tagged_ = question_tagged.replaceFirst("<qt>Which</qt> ", "");
                    
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question_pruned, question_tagged_, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET_MODIFIED));

                    String req = QuestionTypePrefixGenerator.getRequestPrefix();
                    String quetion_request = question.replaceFirst("Which", req);
                    String question_tagged_req = question_tagged.replaceFirst("<qt>Which</qt>", "<qt>" + req + "</qt>");
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), quetion_request, question_tagged_req, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET_MODIFIED));
                }
            }
        }

        //Modefied Question - second after
        query = null;
        question = null;
        String offset__second_most = "has the second most";
        if (O_Type_withPrefix.equals(Settings.Number)) {
            if (so_NP != null) {

                if (so_NP.toLowerCase().contains("area")
                        || so_NP.toLowerCase().contains("size")) {
                    offset__second_most = "has the second largest";
                }

                so_NP = so_NP.replaceAll("\\(.*\\)", "");
                question = "Which " + T + " after " + compareEntityTop + " " + offset__second_most + " " + so_NP.trim() + "?";
                question_tagged = "<qt>Which</qt> <t>" + T + "</t> <op>after</op> <o>" + compareEntityTop + "</o> <off>" + offset__second_most + "</off> <p>" + so_NP.trim() + "</p>?";
            } else if (os_NP != null) {

                if (os_NP.toLowerCase().contains("area")
                        || os_NP.toLowerCase().contains("size")) {
                    offset__second_most = "has the second largest";
                }

                os_NP = os_NP.replaceAll("\\(\\?\\)", "");
                question = "Which " + T + " after " + compareEntityTop + " " + offset__second_most + " " + os_NP.trim() + "?";
                question_tagged = "<qt>Which</qt> <t>" + T + "</t> <op>after</op> <o>" + compareEntityTop + "</o> <off>" + offset__second_most + "</off> <p>" + os_NP.trim() + "</p>?";
            }
            query = "SELECT ?Seed WHERE \n"
                    + "{\n"
                    + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                    + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<?NN)"
                    + "\n}" + " \nORDER BY DESC(?n)\n"
                    + "LIMIT 1\n"
                    + "OFFSET 1";
            if (question != null && query != null) {
                if (!question.contains("null")) {
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, question_tagged, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET_MODIFIED));

                    String identifier = "";
                    if (T.toLowerCase().startsWith("a")
                            || T.toLowerCase().startsWith("e")
                            || T.toLowerCase().startsWith("i")
                            || T.toLowerCase().startsWith("o")
                            || T.toLowerCase().startsWith("u")) {
                        identifier = "an";
                    } else {
                        identifier = "a";
                    }

                    question = question.replace(offset__second_most, "which " + offset__second_most).replace(T, identifier + " " + T);
                    question_tagged = question_tagged.replace(offset__second_most, "which " + offset__second_most);

                    String question_pruned = question.replaceFirst("Which ", "");
                    String question_tagged_ = question_tagged.replaceFirst("<qt>Which</qt> ", "");
                    
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question_pruned, question_tagged_, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET_MODIFIED));

                    String req = QuestionTypePrefixGenerator.getRequestPrefix();
                    String quetion_request = question.replaceFirst("Which", req);
                    String question_tagged_req = question_tagged.replaceFirst("<qt>Which</qt>", "<qt>" + req + "</qt>");
                    
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), quetion_request, question_tagged_req, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET_MODIFIED));
                }
            }
        }

        // = date
//        question = null;
//        query = null;
//        if (so_VP != null) {
//            question = "Which " + T + " " + so_VP + " " + equalNL + " " + compareEntityTop.trim() + "?";
//        } else if (so_NP != null) {
//            question = "Which " + T + " has " + so_NP + " " + equalNL + " " + compareEntityTop + "'s " + so_NP.replaceAll("\\(.*\\)", "").trim() + "?";
//        } else if (os_NP != null) {
//            question = "Which " + T + " has " + os_NP + " " + equalNL + " " + compareEntityTop + "'s " + os_NP.replaceAll("\\(.*\\)", "").trim() + "?";
//        }
//        query = "SELECT ?Seed WHERE \n"
//                + "{\n"
//                + "  {\n"
//                + "   ?Seed rdf:type <" + T_withprefix + ">. \n"
//                + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
//                + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
//                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n=?NN). \n"
//                + "  }\n"
//                + "  MINUS \n"
//                + "  { <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN } \n"
//                + "}";
//
//        if (question != null && query != null) {
//            if (!question.contains("null")) {
//                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET));
//                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET));
//                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", QuestionTypePrefixGenerator.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET));
//            }
//        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

    @Override
    public void generateAllPossibleQuestions() throws Exception {

    }
}
