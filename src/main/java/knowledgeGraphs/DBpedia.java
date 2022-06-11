package knowledgeGraphs;

/**
 *
 * @author aorogat
 */
//Singleton Class
public class DBpedia extends KnowledgeGraph {

    public DBpedia(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new DBpedia(endpoint);
            return (DBpedia) instance;
        } else {
            return (DBpedia) instance;
        }
    }

    @Override
    public String[] getUnwantedProperties() {
        unwantedProperties = new String[]{
            "dbo:deathPlace",
            "dbr:deathPlace",
            "dbp:deathPlace",
            "dbo:birthPlace",
            "dbr:birthPlace",
            "dbp:birthPlace",
            
            "rdfs:label",
            "rdfs:subPropertyOf",
            "rdfs:comment",
            "rdfs:label",
            "rdfs:seeAlso",
            "rdf:type",
            "rdfs:subClassOf",
            "rdfs:range",
            "rdfs:domain",
            
            "dbo:abstract",
            "dbo:wikiPageWikiLink",
            "dbo:wikiPageExternalLink",
            "dbo:wikiPageID",
            "dbo:wikiPageLength",
            "dbo:wikiPageRevisionID",
            "dbo:wikiPageRedirects",
            "dbo:wikiPageDisambiguates",
            "dbo:thumbnail",
            
            "dbo:population_as_of",
            
            "<http://dbpedia.org/ontology/wikiPageRevisionID>",
            
            "dbo:wikiPageWikiLink",
            "<http://dbpedia.org/ontology/wikiPageWikiLink>",
            
            
            
            "dbp:image",
            "dbp:wikiPageUsesTemplate",
            "dbp:image",
            "dbp:name",
            
            "owl:sameAs",
            "owl:equivalentProperty",
            "owl:differentFrom",
            "owl:versionInfo",
            "owl:disjointWith",
            "owl:equivalentClass",
            
            "foaf:name",
            "foaf:primaryTopic",
        
            "<http://purl.org/linguistics/gold/hypernym>",
//            "<http://www.w3.org/ns/prov#wasDerivedFrom>"
        
        
        
        };
        return unwantedProperties;
    }

    
}
