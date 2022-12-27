package lexiconGenerator.nlPatternExtractor;

import lexiconGenerator.predicatesExtractor.Predicate_Extractor;
import java.sql.SQLException;
import java.util.ArrayList;
import database.Database;
import lexiconGenerator.kg_explorer.model.Predicate;
import lexiconGenerator.kg_explorer.model.PredicateTripleExample;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class NLPatternExtractor {
    
    
    
    
    public static void main(String[] args) throws SQLException {
        extractNLPPatterns();
    }
    
    public static void extractNLPPatterns(){
        Predicate_Extractor extractor = new Predicate_Extractor();
        ArrayList<Predicate> predicates = Database.getPredicates();
        int i = 0;
        for (Predicate predicate : predicates) {
            predicate.setTripleExamples(Settings.knowledgeGraph.getOneTripleExample(predicate.getPredicateURI().trim(),
                        predicate.getPredicateContext().getSubjectType(), 
                        predicate.getPredicateContext().getObjectType(), 
                        predicate.getLabel(), 
                        20));
            try {
                
                System.out.print(i++ + "\t");
                predicate.print();
                Database.storePredicates_NLP_Representation(predicate, (ArrayList<PredicateTripleExample>) predicate.getTripleExamples());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
}
