package knowledgeGraphs;

import benchmarkGenerator.subgraphShapeGenerator.model.Variable;
import benchmarkGenerator.subgraphShapeGenerator.model.VariableSet;
import lexiconGenerator.kg_explorer.explorer.Explorer;
import lexiconGenerator.kg_explorer.model.PredicateContext;
import lexiconGenerator.kg_explorer.model.PredicateTripleExample;
import settings.Settings;
import system.components.Branch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static lexiconGenerator.kg_explorer.explorer.Explorer.kg;

public class DBLP extends KnowledgeGraph {
    public DBLP(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new DBLP(endpoint);
        }
        return (DBLP) instance;
    }

    @Override
    public String[] getUnwantedProperties() {
        unwantedProperties = new String[]{
                "http://www.w3.org/2000/01/rdf-schema#label",
                "http://www.w3.org/2000/01/rdf-schema#subPropertyOf",
                "http://www.w3.org/2000/01/rdf-schema#comment",
                "http://www.w3.org/2000/01/rdf-schema#label",
                "http://www.w3.org/2000/01/rdf-schema#seeAlso",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
                "http://www.w3.org/2000/01/rdf-schema#subClassOf",
                "http://www.w3.org/2000/01/rdf-schema#range",
                "http://www.w3.org/2000/01/rdf-schema#domain",

                "http://www.w3.org/2002/07/owl#inverseOf ",
                "http://www.w3.org/2000/01/rdf-schema#subPropertyOf",
                "http://www.w3.org/2002/07/owl#imports",

                "http://www.w3.org/2002/07/owl#sameAs",
                "http://www.w3.org/2002/07/owl#equivalentProperty",
                "http://www.w3.org/2002/07/owl#differentFrom",
                "http://www.w3.org/2002/07/owl#versionInfo",
                "http://www.w3.org/2002/07/owl#disjointWith",
                "http://www.w3.org/2002/07/owl#equivalentClass",
                "http://www.w3.org/2002/07/owl#complementOf",

                "http://xmlns.com/foaf/0.1/name",
                "http://xmlns.com/foaf/0.1/primaryTopic",

                "http://www.openlinksw.com/schemas/virtrdf#loadAs",
                "http://www.openlinksw.com/schemas/virtrdf#item",
                "http://www.openlinksw.com/schemas/virtrdf#isSpecialPredicate",
                "http://www.openlinksw.com/schemas/virtrdf#qmfValRange-rvrRestrictions",
                "http://www.openlinksw.com/schemas/virtrdf#qmfSuperFormats",
                "http://www.openlinksw.com/schemas/virtrdf#inheritFrom",
                "http://www.openlinksw.com/schemas/virtrdf#noInherit",
                "http://www.openlinksw.com/schemas/virtrdf#qmfSubFormatForRefs",
                "http://www.openlinksw.com/schemas/virtrdf#qmGraphMap",
                "http://www.openlinksw.com/schemas/virtrdf#qmSubjectMap",
                "http://www.openlinksw.com/schemas/virtrdf#qmPredicateMap",
                "http://www.openlinksw.com/schemas/virtrdf#qmObjectMap",
                "http://www.openlinksw.com/schemas/virtrdf#qsMatchingFlags",
                "http://www.openlinksw.com/schemas/virtrdf#qmvGeo",
                "http://www.openlinksw.com/schemas/virtrdf#qmvATables",
                "http://www.openlinksw.com/schemas/virtrdf#qmvFormat",
                "http://www.openlinksw.com/schemas/virtrdf#qsDefaultMap",
                "http://www.openlinksw.com/schemas/virtrdf#qmvFText",
                "http://www.openlinksw.com/schemas/virtrdf#qmvftConds",
                "http://www.openlinksw.com/schemas/virtrdf#qmMatchingFlags",
                "http://www.openlinksw.com/schemas/DAV#ownerUser",
                "http://www.openlinksw.com/schemas/virtrdf#qmvColumns",
                "http://www.openlinksw.com/schemas/virtrdf#qsUserMaps",


                "http://www.w3.org/1999/02/22-rdf-syntax-ns#_1",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#_2",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#_3",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#_4",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#_5",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#rest",
                "http://www.w3.org/1999/02/22-rdf-syntax-ns#first",
                " http://www.w3.org/ns/sparql-service-description#url",

                "http://purl.org/linguistics/gold/hypernym",
                "http://purl.org/dc/terms/title",
                "http://purl.org/dc/elements/1.1/title",
                "http://purl.org/ontology/mo/biography",

                "http://www.w3.org/2002/07/owl#inverseOf",
        };
        return unwantedProperties;
    }

    @Override
    public ArrayList<VariableSet> getPredicateList_EntityObjects(int from, int length) {
        String query = "SELECT DISTINCT ?p WHERE { "
                + "\n\t?s ?p ?o. " + " \nFilter(isIRI(?o)). " //Get only if ?o is entity
                + "\n} LIMIT " + length + " OFFSET " + from;
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>(kg.runQuery(query));
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        predicatesVariableSet = filterResult(predicatesVariableSet);
        return predicatesVariableSet;
    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_EntityObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  ?o_type  (count(?s) as ?count)\n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                + "   ?o      rdf:type              ?o_type.\n" //
                ;
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n"
                    + "  FILTER (strstarts(str(?o_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type  ?o_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = predicate.getVariables().get(1).getValueWithPrefix();
            String weightString = predicate.getVariables().get(2).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_NumberObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                + "\n";

        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Number";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_DateObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n";
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Date";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicatesContext_Literals(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n";
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER (strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + "))).\n";
        }
        query += "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Literal";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    @Override
    public ArrayList<PredicateContext> getPredicateContextFromTripleExample(String subjectURI, String predicateURI, String objectURI) {
        String unwantedPropertiesString = Settings.knowledgeGraph.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  ?o_type  \n"
                + "WHERE{\n"
                + "   <" + subjectURI + ">     rdf:type              ?s_type.\n"
                + "   <" + objectURI + ">      rdf:type              ?o_type.\n"
                + "    }.\n";

        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            query += "  FILTER strstarts(str(?s_type ), str(" + Settings.requiredTypePrefix + ")).\n"
                    + "  FILTER strstarts(str(?o_type ), str(" + Settings.requiredTypePrefix + ")).\n";
        }
        query += "}";
        ArrayList<VariableSet> predicatesTriplesVarSets = Settings.knowledgeGraph.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = predicate.getVariables().get(1).getValueWithPrefix();
            weight = 0;
            predicateContexts.add(new PredicateContext(stype, otype, weight));
        }
        return predicateContexts;

    }

    @Override
    public Branch getBranchOfType_SType_connectTo_OType(Explorer explorer, String S_type, String O_type, String predicateURI, int offset) {
        String query = "";
        //get labels
        try {
            if (O_type.equals("Number") || O_type.equals("Date") || O_type.equals(Settings.Literal)) {
                query = "SELECT DISTINCT ?s ?o WHERE {\n "
                        + "  ?s <" + predicateURI + "> ?o.  ?s rdf:type <" + S_type + ">." + Settings.popularityFilter
                        + "\n}\n "
                        + Settings.popularityORDER
                        + "OFFSET " + offset;
            } else {
                query = "SELECT DISTINCT ?s ?o WHERE {\n "
                        + "  ?s <" + predicateURI + "> ?o.  ?s rdf:type <" + S_type + ">.  ?o rdf:type <" + O_type + ">.  " + Settings.popularityFilter
                        + "\n}\n "
                        + Settings.popularityORDER
                        + "OFFSET " + offset;
            }
            Explorer.predicatesTriplesVarSets = Settings.knowledgeGraph.runQuery(query);

            String s = Explorer.predicatesTriplesVarSets.get(0).getVariables().get(0).toString();
            String o = Explorer.predicatesTriplesVarSets.get(0).getVariables().get(1).toString();

            return new Branch(s, o, predicateURI, S_type, O_type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples  = new ArrayList<>();
        try {
            if (oType.equals("Number")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
                        + "\n";
                //Get only dbpedia types
                if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                    query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n";
                }

                query += "FILTER isNumeric(?o)."
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            } else if (oType.equals("Date")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
                        + "\n";
                if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                    //Get only dbpedia types
                    query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n";
                }
                query += " FILTER (datatype(?o) = xsd:dateTime ). \n"
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            } else {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
                        + "?o rdf:type <" + oType + ">. \n"
                        + "\n";
                if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                    //Get only dbpedia types
                    query += "  FILTER strstarts(str(<" + sType + ">  ), str(" + Settings.requiredTypePrefix + ")).\n"
                            + "  FILTER strstarts(str(<" + oType + "> ), str(" + Settings.requiredTypePrefix + ")).\n";
                }
                query += "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            }
            predicatesTriplesVarSets = kg.runQuery(query);
            if (predicatesTriplesVarSets.size() > noOfExamples) {
                predicatesTriplesVarSets = new ArrayList<>(predicatesTriplesVarSets.subList(0, noOfExamples));
            }
            for (VariableSet predicate1 : predicatesTriplesVarSets) {
                String s = predicate1.getVariables().get(0).toString();
                String o = predicate1.getVariables().get(1).toString();
                PredicateTripleExample predicateTriple = new PredicateTripleExample("<" + s + ">", "<" + o + ">", Settings.explorer.removePrefix(s), Settings.explorer.removePrefix(o), lable, Settings.explorer);
                predicateTriples.add(predicateTriple);
                //To speed up the system. break after one VP.
//                if(predicateTriple.getNlsSuggestionsObjects().size()>=1)
//                    break;
            }
            return predicateTriples;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private ArrayList<VariableSet> filterResult( ArrayList<VariableSet> result) {
        List<String> list = Arrays.asList(this.getUnwantedProperties());
        ArrayList<VariableSet> newVariableSetList = new ArrayList<>();
        for (VariableSet set : result) {
            VariableSet newSet = new VariableSet();
            ArrayList<Variable> newVariables = new ArrayList<>();
            for (Variable v : set.getVariables()) {
                String value = v.getValueWithPrefix().replace(" ","" );
                if (! list.contains(value) && ! value.contains("/virtrdf")) {
                    newVariables.add(v);
                }
            }
            newSet.setVariables(newVariables);
            newVariableSetList.add(newSet);
        }
        return newVariableSetList;
    }
}
