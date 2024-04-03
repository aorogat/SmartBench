package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import database.Database;
import java.util.Collections;
import lexiconGenerator.kg_explorer.model.Predicate;
import settings.Settings;

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
    static boolean orderedSeeds = false;

    private static void init(){
        branches_with_number = new ArrayList<>();
        branches_with_date = new ArrayList<>();
        branches_with_entity = new ArrayList<>();
        branches_with_Literal = new ArrayList<>();
        branchs = new ArrayList<>();
    }
    
    public static void generateSeedList(int offset) {

        init();

        //Get subject types avialable in Lexicon
        availablePredicates = Database.getAvailablePredicates(orderedSeeds);
        System.out.println("We have " + availablePredicates.size() + " types");
        System.out.println("==============================================");

        int typesSize = availablePredicates.size();

        for (int i = offset;
                i < (offset + 10);
                //                i<availablePredicates.size(); 
                i++) {
            if (i >= typesSize) {
                break;
            }
            Predicate p = availablePredicates.get(i);

            if (p.getPredicateURI().toLowerCase().contains("party")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("relation")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("child")) {
                continue;
            } else if (p.getPredicateURI().toLowerCase().contains("musiccomposer")) {
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

        addBranchesFromList(branches_with_entity);
        addBranchesFromList(branches_with_date);
        addBranchesFromList(branches_with_number);

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

    private static void addBranchesFromList(ArrayList<Predicate> branches) {
        Random random = new Random();
        int count = 0;

        double offset = orderedSeeds ? 1.2 : 1;

        int i = random.nextInt(10) + 1;
        while (i < branches.size() && count < (long) 500000000) {
            Predicate p = branches.get(i);
            addBranchs(p);
            count++;
            i = (int) (i * offset + 1);
        }
    }

    private static void addBranchs(Predicate p) {
        if (p == null) {
            return;
        }

        // Create a new random number generator
        Random random = new Random();

        // Generate a random starting index for the loop
        int startIndex = random.nextInt(2);

        // Determine the loop offset based on the value of orderedSeeds
        double loopOffset = orderedSeeds ? 1.2 : 1;

        // Loop through the list of branches
        for (int index = startIndex; index <= startIndex + 10; index = (int) ((index * loopOffset) + 1)) {
            try {
                // Get the next branch from the knowledge graph
                Branch branch = Settings.knowledgeGraph.getBranchOfType_SType_connectTo_OType(
                        Settings.explorer, p.getPredicateContext().getSubjectType(),
                        p.getPredicateContext().getObjectType(), p.getPredicateURI(), index);
                if (branch == null) {
                    return;
                }

                // Remove prefixes from the subject and object types
                String subjectType = Settings.explorer.removePrefix(p.getPredicateContext().getSubjectType());
                String objectType = Settings.explorer.removePrefix(p.getPredicateContext().getObjectType());

                // Print the branch details and add the branch to the list
                System.out.println(p.getPredicate() + "\t" + branch.s + "[" + subjectType + "]\t" + branch.o + "[" + objectType + "]");
                branchs.add(branch);
            } catch (Exception e) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        generateSeedList(0);
    }
}
