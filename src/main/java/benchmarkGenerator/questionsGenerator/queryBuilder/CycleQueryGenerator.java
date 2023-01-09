package benchmarkGenerator.questionsGenerator.queryBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.CycleQuestion;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.CoordinatingConjunction;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.CycleGraph;

public class CycleQueryGenerator{

    CycleGraph cycleGraph;

    public CycleQueryGenerator(CycleGraph cycleGraph) {
        this.cycleGraph = cycleGraph;
    }

    public String selectQuery(String coordinatingConjunction, int direction) {
        String query = "";
        String triples = "";
        String t1 = cycleGraph.getPath_1().toQueryTriplePattern();
        String t2 = cycleGraph.getPath_2().toQueryTriplePattern();

        if (coordinatingConjunction.equals(CoordinatingConjunction.AND)) {
            triples = "\n\t" + t1 + "."
                    + "\n\t" + t2 + ".";
            if (direction == CycleQuestion.FORWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            } else if (direction == CycleQuestion.BACKWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getObject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR)) {
            triples += "\n\t{" + t1 + "} UNION \n\t {" + t2 + "}";
            if (direction == CycleQuestion.FORWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            } else if (direction == CycleQuestion.BACKWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + ">", "?Seed").replace(" " + cycleGraph.getPath_1().getObject().getValueWithPrefix() + " ", "?Seed")
                        .replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"", "?Seed").replace("\"" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + "\"^^xsd:dateTime ", "?Seed");
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        }
        return query;
    }

    public String askQuery_true_answer(String coordinatingConjunction, int direction) {
        return selectQuery(coordinatingConjunction, direction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">");
    }

    public String askQuery_false_answer(CycleGraph cycleGraph, String coordinatingConjunction, int direction, String somethingElse) {
        return selectQuery(coordinatingConjunction, direction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    public String countQuery(String coordinatingConjunction, int direction) {
        return selectQuery(coordinatingConjunction, direction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }
}
