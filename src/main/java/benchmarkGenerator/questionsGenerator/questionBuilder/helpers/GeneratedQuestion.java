package benchmarkGenerator.questionsGenerator.questionBuilder.helpers;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.StringTokenizer;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import settings.Settings;

public class GeneratedQuestion {

    public static final String QT_WHAT = "What";
    public static final String QT_WHO = "Who";
    public static final String QT_WHOSE = "Whose";
    public static final String QT_WHOM = "Whom";
    public static final String QT_WHERE = "Where";
    public static final String QT_WHEN = "When";
    public static final String QT_WHICH = "Which";
    public static final String QT_HOW_MANY = "How-many";
    public static final String QT_HOW_ADJ = "How-adj";
    public static final String QT_YES_NO_IS = "Yes-No-Is";
    public static final String QT_YES_NO_DO = "Yes-No-Do";
    public static final String QT_REQUEST = "Requests";
    public static final String QT_TOPICAL_EMPH = "Topical-Emphasize";
    public static final String QT_TOPICAL_PRUNE = "Topical-Prune";

    public static final String SH_SINGLE_EDGE = "SINGLE_EDGE";
    public static final String SH_CHAIN = "CHAIN";
    public static final String SH_STAR = "STAR";
    public static final String SH_TREE = "TREE";
    public static final String SH_CYCLE = "CYCLE";
    public static final String SH_PETAL = "PETAL";
    public static final String SH_FLOWER = "FLOWER";
    public static final String SH_SET = "SET";
    public static final String SH_STAR_MODIFIED = "STAR_MODIFIED";
    public static final String SH_SET_MODIFIED = "SET_MODIFIED";
    public static final String SH_CYCLE_GENERAL = "CYCLE_GENERAL";

    private String seed_withPrefix;
    private String seedType_withPrefix;

    private String questionString;
    private String questionStringTagged;
    private String query;
    private String graphString;

    private int noOfTriples;
    private String QuestionType;
    private String ShapeType;
    private ArrayList<String> answers;
    private int answerCardinality;
    private int noOfTokens;
    private int keywords;
    private double questionComplexity;

    public GeneratedQuestion() {
    }

//    public GeneratedQuestion(String questionString, String query, String graphString) {
//        this.questionString = questionString;
//        this.query = query;
//        this.graphString = graphString;
//    }
    public GeneratedQuestion(String seed_withPrefix, String seedType_withPrefix, String questionString, String questionStringTagged, String query, String graphString, int noOfTriples, String QuestionType, String ShapeType) throws Exception {
        if (questionString.contains("second collaboration between")) {
            return;
        }
        if (questionString.contains(" recorded in  ")
                || questionString.contains("also went to")) {
            return;
        }
        this.seed_withPrefix = seed_withPrefix;
        this.seedType_withPrefix = seedType_withPrefix;
        this.questionString = questionString.trim();
        this.questionString = questionString.replace("(", "").replace(")", "").replace("  ", " ").replace(" , ", ", ").replace(" ,", ", ")
                .replace(" s ", " ").replace(" by on ", " by ").replace(" \"?", "\"?").replace(" plays for on ", " plays for ");
        //capitalize the first letter
        this.questionString = this.questionString.substring(0, 1).toUpperCase() + this.questionString.substring(1);
        this.questionString = this.questionString.replace(" the a ", " the ").replace(" the an ", " the ").replace("released as on", "released in")
                .replace("released as", "released in").replace(" ?", "?");
        this.query = query.replace("\"?Seed\"", "?Seed").replace("\"<?Seed>\"", "?Seed").replace(". .", ". ");
        this.graphString = graphString;
        this.noOfTriples = noOfTriples;
        this.QuestionType = QuestionType;
        this.ShapeType = ShapeType;
        this.questionStringTagged = questionStringTagged.replace("(", "").replace(")", "").replace("  ", " ").replace(" , ", ", ")
                .replace(" ,", ", ").replace(" s ", " ");

        try {
            ArrayList<VariableSet> answersVar = Settings.knowledgeGraph.runQuery(query);
            if (answersVar.size() > Settings.maxAnswerCardinalityAllowed) {
                System.out.println("Very long cardinality");
                return;
            }
            answers = new ArrayList<>();
            for (VariableSet variableSet : answersVar) {
                String a = variableSet.getVariables().get(0).getValueWithPrefix();
                if (a == null) {
                    continue;
                }
                answers.add(a);
            }
            answerCardinality = answers.size();

            if (this.QuestionType.equals(QT_HOW_MANY) && answers.get(0).equals("0")) {
                questionString = null;
                return;
            }

            StringTokenizer st = new StringTokenizer(questionString, " ");
            noOfTokens = st.countTokens();

            keywords = getKLength();
            questionComplexity = round((noOfTokens * noOfTriples * keywords) / (double) (20 * 5 * 3), 3);

            if (questionComplexity > 1) {
                questionComplexity = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        print();
    }

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void print() {

        if (getQuestionString().contains(" null ")
                || getQuestionStringTagged().contains("<p>null</p>")
                || getQuestionStringTagged().contains("<p> </p>")
                || getQuestionStringTagged().contains("<p></p>")) {
            System.out.print("\u001B[31m");//Red Color
            System.out.println("XXXXX It seems that this question has a problem and will not be added to the final file. XXXXX");
        }

        if (answerCardinality <= 0) {
            System.out.println(query);
            System.out.println("NO ANSWER");
            return;
        }

        System.out.println("\t \033[30m Seed with prefix: \033[1;35m " + seed_withPrefix);
        System.out.println("\t \033[30m Seed type with prefix: \033[1;35m " + seedType_withPrefix);
        System.out.println("\t \033[30m Question Graph: \033[1;35m " + graphString.replace("\n", "\t||\t"));
        System.out.println("\t \033[30m Questing String: \033[1;35m " + questionString);
        System.out.println("\t \033[30m Questing Tagged String: \033[1;35m " + questionStringTagged);
        System.out.println("\t \033[30m Question Query: \033[1;35m " + query.replace("\n", " "));
        System.out.println("\t \033[30m Answer Cardinality: \033[1;35m " + answerCardinality);
        System.out.println("\t \033[30m Answer: \033[1;35m " + answers.toString());
        System.out.println("\t \033[30m #Tokens: \033[1;35m " + noOfTokens);
        System.out.println("\t \033[30m #Triple Patterns: \033[1;35m " + noOfTriples);
        System.out.println("\t \033[30m #Query Keywords: \033[1;35m " + keywords);
        System.out.println("\t \033[30m #Question Complexity: \033[1;35m " + questionComplexity);
        System.out.println("\t \033[30m QT: \033[1;35m " + QuestionType);
        System.out.println("\t \033[30m Shape: \033[1;35m " + ShapeType);
        System.out.println("");
    }

    public String getGraphString() {
        return graphString;
    }

    public void setGraphString(String graphString) {
        this.graphString = graphString;
    }

    public int getNoOfTriples() {
        return noOfTriples;
    }

    public void setNoOfTriples(int noOfTriples) {
        this.noOfTriples = noOfTriples;
    }

    public String getSeed_withPrefix() {
        return seed_withPrefix;
    }

    public void setSeed_withPrefix(String seed_withPrefix) {
        this.seed_withPrefix = seed_withPrefix;
    }

    public String getSeedType_withPrefix() {
        return seedType_withPrefix;
    }

    public void setSeedType_withPrefix(String seedType_withPrefix) {
        this.seedType_withPrefix = seedType_withPrefix;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public void setQuestionType(String QuestionType) {
        this.QuestionType = QuestionType;
    }

    public String getShapeType() {
        return ShapeType;
    }

    public void setShapeType(String ShapeType) {
        this.ShapeType = ShapeType;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public int getAnswerCardinality() {
        return answerCardinality;
    }

    public void setAnswerCardinality(int answerCardinality) {
        this.answerCardinality = answerCardinality;
    }

    public int getNoOfTokens() {
        return noOfTokens;
    }

    public void setNoOfTokens(int noOfTokens) {
        this.noOfTokens = noOfTokens;
    }

    public int getKeywords() {
        return keywords;
    }

    public void setKeywords(int keywords) {
        this.keywords = keywords;
    }

    public double getQustionComplexity() {
        return questionComplexity;
    }

    public void setQustionComplexity(double questionComplexity) {
        this.questionComplexity = questionComplexity;
    }

    public String getQuestionStringTagged() {
        return questionStringTagged;
    }

    public void setQuestionStringTagged(String questionStringTagged) {
        this.questionStringTagged = questionStringTagged;
    }

    private int getKLength() {
        int k = 0;
        String q = query.toLowerCase().replace("\n", "").replace("\r", "").replaceAll(" ", "");

        // Check for SPARQL keywords
        String[] keywords = {"select", "ask", "where", "distinct", "limit", "offset", "orderby", "asc(", "desc(",
            "filter", "union{", "notexists{", "exists{", "minus{", "groupby", "having", "count(", "min(", "max(", "sum(", "avg("};
        for (String keyword : keywords) {
            if (q.contains(keyword)) {
                k++;
            }
        }

        return k;
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GeneratedQuestion) {
            GeneratedQuestion temp = (GeneratedQuestion) obj;
            return this.questionString.equals(temp.questionString);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.questionString.hashCode());
    }

    public boolean equals(GeneratedQuestion generatedQuestion) {
        String q1 = this.questionString;
        String q2 = generatedQuestion.questionString;
        return q1.equals(q2);
    }
}
