package benchmarkGenerator.questionsGenerator.questionBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.GeneratedQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.CoordinatingConjunction;
import java.util.ArrayList;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.FlowerGraph;

/**
 *
 * @author aorogat
 */
public class FlowerQuestion extends ShapeQuestion {
    FlowerGraph flowerGraph;
    StarQuestion starQuestion;
    CycleQuestion cycleQuestion;
    
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public FlowerQuestion(FlowerGraph flowerGraph) throws Exception {
        this.flowerGraph = flowerGraph;
        starQuestion = new StarQuestion(flowerGraph.getStar(), true);
        cycleQuestion = new CycleQuestion(flowerGraph.getCycle());
        
        String question = "";
        String question_tagged = "";
        
        String starQuestionString = starQuestion.selectWhichQuestions(CoordinatingConjunction.AND);
        String starQuestionString_tagged = starQuestion.selectWhichQuestions_tagged(CoordinatingConjunction.AND);
        
        starQuestionString = starQuestionString.substring(0, starQuestionString.length()-1) + " ";
        starQuestionString_tagged = starQuestionString_tagged.substring(0, starQuestionString_tagged.length()-1) + " ";
        
        if(starQuestionString==null || starQuestionString.contains("null"))
            return;
        
        question = starQuestionString;
        question_tagged = starQuestionString_tagged;
        
        cycleQuestion.direction = CycleQuestion.FORWARD;
        String cycleQuestionString = cycleQuestion.selectWh_Questions(CoordinatingConjunction.AND,  "NP");
        String cycleQuestionString_tagged = CycleQuestion.getQuestion_tagged();
        
        if(cycleQuestionString==null || cycleQuestionString.contains("null"))
        {
            cycleQuestionString = cycleQuestion.selectWh_Questions(CoordinatingConjunction.AND,  "VP");
            cycleQuestionString_tagged = CycleQuestion.getQuestion_tagged();
            if(cycleQuestionString==null || cycleQuestionString.contains("null"))
                return;
        }
        question += cycleQuestionString.replaceAll("\\b(What|Where)\\b", ", as well ").replaceAll("\\b(Who|Whose|Whom)\\b", ", as well he/she").replace(" ,", "");
        question_tagged += cycleQuestionString_tagged.replace("<qt>What</qt>", ", as well ")
                .replace("<qt>Where</qt>", ", as well ")
                .replace("<qt>Who</qt>", ", as well he/she")
                .replace("<qt>Whose</qt>", ", as well he/she")
                .replace("<qt>Whom</qt>", ", as well he/she")
                .replace(" ,", "");

//        String queryString = starQuestion.selectQuery(flowerGraph.getStar(), CoordinatingConjunction.AND);
        String queryString = starQuestion.starQueryGenerator.selectQuery(CoordinatingConjunction.AND);
        queryString = queryString.substring(0,queryString.length()-1);
        
        queryString += cycleQuestion.selectQuery(flowerGraph.getCycle(), CoordinatingConjunction.AND).replace("SELECT DISTINCT ?Seed WHERE{", "");
        
        String graphString = flowerGraph.toString();
        
        String QT = "";
        if(question.toLowerCase().startsWith("is")||
                question.toLowerCase().startsWith("was")||
                question.toLowerCase().startsWith("are")||
                question.toLowerCase().startsWith("were"))
            QT = GeneratedQuestion.QT_YES_NO_IS;
        else if(question.toLowerCase().startsWith("which"))
            QT = GeneratedQuestion.QT_WHICH;
        else if(question.toLowerCase().startsWith("how many"))
            QT = GeneratedQuestion.QT_HOW_MANY;
        else
            QT = GeneratedQuestion.QT_REQUEST;
        
        allPossibleQuestions.add(new GeneratedQuestion(flowerGraph.getStar().getStar().get(0).getSubject().getValueWithPrefix(), flowerGraph.getStar().getStar().get(0).getS_type(), question, question_tagged, queryString, flowerGraph.toString(), flowerGraph.getStar().getStar().size()+1+2, QT, GeneratedQuestion.SH_FLOWER));
//        GeneratedQuestion generatedQuestion = new GeneratedQuestion(question, queryString, graphString);
//        allPossibleQuestions.add(generatedQuestion);
    }


    @Override
    public void generateAllPossibleQuestions() throws Exception {

    }

    public FlowerGraph getFlowerGraph() {
        return flowerGraph;
    }

    public void setFlowerGraph(FlowerGraph flowerGraph) {
        this.flowerGraph = flowerGraph;
    }

    public StarQuestion getStarQuestion() {
        return starQuestion;
    }

    public void setStarQuestion(StarQuestion starQuestion) {
        this.starQuestion = starQuestion;
    }

    public CycleQuestion getCycleQuestion() {
        return cycleQuestion;
    }

    public void setCycleQuestion(CycleQuestion cycleQuestion) {
        this.cycleQuestion = cycleQuestion;
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }



}
