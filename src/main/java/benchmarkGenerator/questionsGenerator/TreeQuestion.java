package benchmarkGenerator.questionsGenerator;

import java.util.ArrayList;
import benchmarkGenerator.kg_extractor.model.TriplePattern;
import benchmarkGenerator.kg_extractor.model.subgraph.StarGraph;
import benchmarkGenerator.kg_extractor.model.subgraph.TreeGraph;

/**
 *
 * @author aorogat
 */
public class TreeQuestion {

    TreeGraph treeGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public TreeQuestion(TreeGraph treeGraph) throws Exception {
        if (treeGraph.getTreeGraph().size() == 2) {
            this.treeGraph = treeGraph;
            StarGraph starGraph_0_0 = treeGraph.getTreeGraph().get(0); //the first star is the root star
            StarQuestion starQuestion_0_0 = new StarQuestion(starGraph_0_0, true);

            StarGraph starGraph_1_0 = treeGraph.getTreeGraph().get(1);
            StarQuestion starQuestion_1_0 = new StarQuestion(starGraph_1_0, true);

            String rootWhichQuestion = starQuestion_0_0.selectWhichQuestions(CoordinatingConjunction.AND);
            String rootWhichQuestion_tagged = starQuestion_0_0.selectWhichQuestions_tagged(CoordinatingConjunction.AND);
            
            //Query
            String rootWhichQuery = starQuestion_0_0.selectQuery(starGraph_0_0, CoordinatingConjunction.AND);

            String triple_1_Subject = starGraph_0_0.getStar().get(0).getSubject().getValueWithPrefix();
            String triple_1_Object = starGraph_0_0.getStar().get(0).getObject().getValueWithPrefix();
            String triple_1_Predicate = starGraph_0_0.getStar().get(0).getPredicate().getValueWithPrefix();

            String s = "<" + triple_1_Subject + ">\t<" + triple_1_Predicate + ">\t<" + triple_1_Object + ">";

            String triples = "";
            ArrayList<TriplePattern> star = starGraph_1_0.getStar();
            String T = starGraph_1_0.getSeedType();
            String star2Seed = "<" + starGraph_0_0.getStar().get(0).getObject().getValueWithPrefix() + ">";
            rootWhichQuery = rootWhichQuery.replace(star2Seed, "?Seed1");

            triples += "\n\t?Seed1 \t rdf:type \t <" + T + "> . ";

            for (TriplePattern triple : star) {
                triples += "\n\t" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed1")
                        .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed1")
                        .replace("\"" + triple.getSubject().getValueWithPrefix() + "\"", "?Seed1")
                        .replace(" " + triple.getSubject().getValueWithPrefix() + " ", "?Seed1")
                        ;
            }

            rootWhichQuery = rootWhichQuery.substring(0, rootWhichQuery.length() - 1) + triples + "\n}";

            String star_1_0_NL = starQuestion_1_0.getFCs_with_T_COO_is_AND();
            String star_1_0_NL_tagged = starQuestion_1_0.getFCs_with_T_COO_is_AND_taggString();
            
            if (star_1_0_NL == null || star_1_0_NL.contains("null")) {
                return;
            }
            String seed_1_0 = starGraph_1_0.getStar().get(0).getSubject().getValue();
            String seed_1_0_type = starGraph_1_0.getStar().get(0).getS_type_without_prefix();
            String seed_1_0_preprocessed = EntityProcessing.decide_quotes_only(seed_1_0, seed_1_0_type);

            if (star_1_0_NL.startsWith("a")
                    || star_1_0_NL.startsWith("e")
                    || star_1_0_NL.startsWith("i")
                    || star_1_0_NL.startsWith("o")
                    || star_1_0_NL.startsWith("u")) {
                star_1_0_NL = "an " + star_1_0_NL;
            } else {
                star_1_0_NL = "a " + star_1_0_NL;
            }
            
            
            String treeWhichQuestion = rootWhichQuestion.replace(seed_1_0_preprocessed, star_1_0_NL);
            String treeWhichQuestion_tagged = rootWhichQuestion_tagged.replace(seed_1_0_preprocessed, star_1_0_NL_tagged);

            //String question = rootWhichQuestion;
            allPossibleQuestions.add(new GeneratedQuestion(this.treeGraph.getSeed(), this.treeGraph.getSeedType(), treeWhichQuestion, treeWhichQuestion_tagged, rootWhichQuery, this.treeGraph.toString(), treeGraph.getSize(), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_TREE));

//            GeneratedQuestion generatedQuestion = new GeneratedQuestion(question, rootWhichQuery, treeGraph.toString());
//            allPossibleQuestions.add(generatedQuestion);
        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
