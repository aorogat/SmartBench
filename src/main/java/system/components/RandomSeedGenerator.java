package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import database.Database;
import java.util.Collections;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.model.Predicate;
import settings.Settings;
//import static system.components.ShapesGenerator.branchs;

/**
 *
 * @author aorogat
 */
public class RandomSeedGenerator {

    static ArrayList<Branch> branchs = new ArrayList<>();
    static ArrayList<Predicate> availablePredicates = new ArrayList<>();

    static ArrayList<Predicate> branches_with_number = new ArrayList<>();
    static ArrayList<Predicate> branches_with_date = new ArrayList<>();
    static ArrayList<Predicate> branches_with_entity = new ArrayList<>();
    static ArrayList<Predicate> branches_with_Literal = new ArrayList<>();

    public static void generateSeedList() {

        branches_with_number = new ArrayList<>();
        branches_with_date = new ArrayList<>();
        branches_with_entity = new ArrayList<>();
        branches_with_Literal = new ArrayList<>();
        branchs = new ArrayList<>();

        //Get subject types avialable in Lexicon
        availablePredicates = Database.getAvailablePredicates();
        System.out.println("We have " + availablePredicates.size() + " types");
        System.out.println("==============================================");


        int typesSize = availablePredicates.size();

        for (Predicate p : availablePredicates) {
            if (p.getPredicateURI().toLowerCase().contains("party")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("relation")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("child")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("musiccomposer")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("album")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("subsequentwork")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("populationasof")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("clubs")) {
                continue;
            }

            if (p.getPredicateContext().getObjectType().equals(Settings.Number)) {
                branches_with_number.add(p);
            } else if (p.getPredicateContext().getObjectType().equals(Settings.Date)) {
                branches_with_date.add(p);
            } else if (p.getPredicateContext().getObjectType().equals(Settings.Literal)) {
                branches_with_Literal.add(p);
            } else {
                branches_with_entity.add(p);
            }
        }

        
        addBranchesFromList(branches_with_entity, 5);
        addBranchesFromList(branches_with_date, 2);
        addBranchesFromList(branches_with_number, 2);
        
        


        Predicate pp = null;
        for (Predicate p : availablePredicates) {
            if (p.getPredicateURI().toLowerCase().contains("album")) {
                pp = p;
                break;
            }
        }

        if(pp!=null)
            addBranchs(pp);

        branches_with_entity = new ArrayList<>(new HashSet<>(branches_with_entity));
        branches_with_number = new ArrayList<>(new HashSet<>(branches_with_number));
        branches_with_date = new ArrayList<>(new HashSet<>(branches_with_date));
        branches_with_Literal = new ArrayList<>(new HashSet<>(branches_with_Literal));

        System.out.println("Numbers type list size: " + branches_with_number.size());
        System.out.println("Dates type list size: " + branches_with_date.size());
        System.out.println("URIs type list size: " + branches_with_entity.size());
        System.out.println("Literals type list size: " + branches_with_Literal.size());

        Collections.reverse(branchs);
    }
    
    private static void addBranchesFromList(ArrayList<Predicate> branches, int end){
        Random random = new Random();
        int count = 0;

        for (int //                i = 0;
                i = random.nextInt(3) + 1;
                i < branches.size();
//                i = i + 1 
                i = (int) (i * 1.2 + 1)
                ) { //make it 1.2
           
            Predicate p = branches.get(i);
            count += 1;
            if (count >= end) {
                break;
            }
            addBranchs(p);
        }
    }

    private static void addBranchs(Predicate p) {
        if (p == null) {
            return;
        }
        Random random = new Random();
        int c = random.nextInt(10);
        int count = 0;
        for (int j = c;
                j <= c + 2; 
//                j=j+1
                j=(int) ((j * 3.2) + 1)
                ) { 
            Branch branch = Settings.knowledgeGraph.getBranchOfType_SType_connectTo_OType(Settings.explorer, p.getPredicateContext().getSubjectType(),
                    p.getPredicateContext().getObjectType(), p.getPredicateURI(), j);
            if (branch == null) {
                return;
            }
            String st = Settings.explorer.removePrefix(p.getPredicateContext().getSubjectType());
            String ot = Settings.explorer.removePrefix(p.getPredicateContext().getObjectType());
            System.out.println(p.getPredicate() + "\t"
                    + branch.s + "[" + st + "]\t"
                    + branch.o + "[" + ot + "]");
            branchs.add(branch);
            count += 1;
            if (count >= 1) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        generateSeedList();
    }
}
