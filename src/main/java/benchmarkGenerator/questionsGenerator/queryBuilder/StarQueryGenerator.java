package benchmarkGenerator.questionsGenerator.queryBuilder;

import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.CoordinatingConjunction;
import benchmarkGenerator.subgraphShapeGenerator.model.TriplePattern;
import benchmarkGenerator.subgraphShapeGenerator.subgraph.StarGraph;
import settings.Settings;

import java.util.ArrayList;

public class StarQueryGenerator {

    StarGraph starGraph;

    public StarQueryGenerator(StarGraph starGraph) {
        this.starGraph = starGraph;
    }

    public String selectQuery(String coordinatingConjunction) {
        String query = "";
        String triples = "";
        ArrayList<TriplePattern> star = starGraph.getStar();
        String T = starGraph.getSeedType();
        triples += "\n\t ?Seed \t rdf:type \t <" + T + "> . ";

        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
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
                break;
            case CoordinatingConjunction.OR:
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
                break;
            case CoordinatingConjunction.AND_NOT:
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
                break;
            case CoordinatingConjunction.NOT_NOT:
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
                break;
            case CoordinatingConjunction.OR_NOT:
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
                break;
        }
        return query;
    }

    public String askQuery_true_answer(String coordinatingConjunction) {
        return selectQuery(coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + starGraph.getStar().get(0).getSubject().getValueWithPrefix() + ">");
    }

    public String askQuery_false_answer(String coordinatingConjunction, String somethingElse) {
        return selectQuery(coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    public String countQuery(String coordinatingConjunction) {
        return selectQuery(coordinatingConjunction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }
}
