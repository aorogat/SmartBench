package benchmarkGenerator.questionsGenerator.queryBuilder;

import benchmarkGenerator.subgraphShapeGenerator.subgraph.SingleEdgeGraph;

public class SingleEdgeQueryGenerator {

    SingleEdgeGraph singleEdgeGraph;

    public SingleEdgeQueryGenerator(SingleEdgeGraph singleEdgeGraph) {
        this.singleEdgeGraph = singleEdgeGraph;
    }

    /**
     * Generates a SPARQL SELECT query to retrieve all values of the seed
     * subject in the triple pattern.
     *
     * @return a SPARQL SELECT query to retrieve all values of the seed subject
     * in the triple pattern
     */
    public String generateSELECTQuery(String S_withPrefix, String S_type_withPrefix) {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        return QueryGenerator.generateSELECTQuery(triple, S_withPrefix, S_type_withPrefix, "?Seed");
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
    public String generateCountQuery(String S_withPrefix, String S_type_withPrefix) {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        return QueryGenerator.generateCountQuery(triple, S_withPrefix, S_type_withPrefix, "?Seed");
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
    public String generateAskQuery_Wrong(String S_withPrefix, String S_type_withPrefix, String somethingElse) {
        if (somethingElse == null) {
            return null;
        }
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        return QueryGenerator.generateAskQuery_Wrong(triple, S_withPrefix, S_type_withPrefix, somethingElse);
    }
}
