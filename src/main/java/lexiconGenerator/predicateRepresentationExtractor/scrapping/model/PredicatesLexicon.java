package lexiconGenerator.predicateRepresentationExtractor.scrapping.model;

import java.io.IOException;
import java.util.ArrayList;
import database.Database;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexiconGenerator.kg_explorer.explorer.Explorer;
import knowledgeGraphs.KnowledgeGraph;
import lexiconGenerator.kg_explorer.model.Predicate;
import lexiconGenerator.kg_explorer.model.PredicateContext;
import lexiconGenerator.kg_explorer.ontology.KGOntology;
import settings.Settings;
import system.components.Predicate_Representation_Extractor;

public class PredicatesLexicon {

    public static ArrayList<PredicateNLRepresentation> predicatesNL;
    public static PredicateNLRepresentation predicateNL;

    public PredicatesLexicon() {
        //predicatesNL = new ArrayList<>();
        predicatesNL = Database.getPredicatesNLRepresentationLexicon();
        //Baltic Sea___riverMouth___Bräkneån
        // S(sea) is the river mouth of O(river)  //NP
        // O(river) eventually flows into S(sea)  //VP
        // O(river) is the tributary of S(sea)   //NP
        // S(sea) destinate O(river)  //VP
//        predicatesNL.add(new PredicateNLRepresentation("riverMouth", "place", "river", 
//                    "river mouth", "eventually flows into",
//                    "tributary", "destinate"));
//        
//        predicatesNL.add(new PredicateNLRepresentation("mainRiver", "river", "country", 
//                    "main river", "run through",
//                    "", "country of"));
//        predicatesNL.add(new PredicateNLRepresentation("largestCountry", "country", "place", 
//                    "largest country", "located in",
//                    "", ""));
//        
//        predicatesNL.add(new PredicateNLRepresentation("enclosedBy", "place", "country", 
//                    "edge", "encloses",
//                    "", "enclosed by"));

        // S is the president of O
        // O leaded by S
        // O is the country of S(Obama)
        // S leaded O
//        predicatesNL.add(new PredicateNLRepresentation("lead", "person", "place", "president", "leaded by",
//        "leaded country","leaded"));
    }

    public static PredicateNLRepresentation getPredicateNL(String predicateString, String S_type, String O_type) {
        if (predicatesNL == null) {
            predicatesNL = Database.getPredicatesNLRepresentationLexicon();
        }
        for (PredicateNLRepresentation p : predicatesNL) {
            if (p.getPredicate() == null) {
                continue;
            }
            if (p.getPredicate().equals(predicateString)
                    && S_type.equals(p.getSubject_type())
                    && O_type.equals(p.getObject_type())) {
                return p;
            }
        }

        //try to get representation from label
        try {
            Predicate predicate = new Predicate(Settings.explorer);
            predicate.setLabel(Settings.explorer.removePrefix(predicateString));
            try {
                if (predicate.getLabel().endsWith(" of")) {
                    if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                        return new PredicateNLRepresentation(predicateString, S_type, O_type, predicate.getLabel(), null, null, null);
//                    Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    } else {
                        return new PredicateNLRepresentation(predicateString, S_type, O_type, null, null, predicate.getLabel(), null);
//                    Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //2- Labels of one word and only can be a verb
            ArrayList<String> tokens = new ArrayList<>();
            if (!predicate.getLabel().trim().contains(" ")) //only label of one word
            {
                try {
                    if (Predicate_Representation_Extractor.wordPOS(predicate.getLabel()).trim().contains("v") && !Predicate_Representation_Extractor.wordPOS(predicate.getLabel()).trim().contains("n")) {
                        return new PredicateNLRepresentation(predicateString, S_type, O_type, null, null, null, predicate.getLabel());
//                    Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    } else if (Predicate_Representation_Extractor.wordPOS(predicate.getLabel()).trim().contains("n")) {
                        if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                            return new PredicateNLRepresentation(predicateString, S_type, O_type, predicate.getLabel(), null, null, null);
//                        Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                        } else {
                            return new PredicateNLRepresentation(predicateString, S_type, O_type, null, null, predicate.getLabel(), null);
//                        Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 99, 1, 1, 1);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                //More than one word, if there are no verbs, add to NP_S_O
                try {

                    String in = null;
                    String label = predicate.getLabel().trim();
                    StringTokenizer tokenizer = new StringTokenizer(label);
                    boolean hasVerb = false;
                    while (tokenizer.hasMoreTokens()) {
                        tokens.add(tokenizer.nextToken());
                    }
                    for (int i = 0; i < tokens.size(); i++) {
                        if (Predicate_Representation_Extractor.wordPOS(tokens.get(i)).trim().contains("v")
                                || label.contains(" above")
                                || label.contains(" across")
                                || label.contains(" against")
                                || label.contains(" along")
                                || label.contains(" among")
                                || label.contains(" around")
                                || label.contains(" at")
                                || label.contains(" before")
                                || label.contains(" behind")
                                || label.contains(" below")
                                || label.contains(" beneath")
                                || label.contains(" beside")
                                || label.contains(" between")
                                || label.contains(" by")
                                || label.contains(" from")
                                || label.contains(" in")
                                || label.contains(" into")
                                || label.contains(" near")
                                || label.contains(" on")
                                || label.contains(" to")
                                || label.contains(" toward")
                                || label.contains(" under")
                                || label.contains(" upon")
                                || label.contains(" with")
                                || label.contains(" within")) {
                            hasVerb = true;
//                            break;
                        }
                        if (label.contains(" above ")) {
                            in = "above";
                        }
                        if (label.contains(" across ")) {
                            in = "across";
                        }
                        if (label.contains(" across ")) {
                            in = "across";
                        }
                        if (label.contains(" along ")) {
                            in = "along";
                        }
                        if (label.contains(" among ")) {
                            in = "among";
                        }
                        if (label.contains(" around ")) {
                            in = "around";
                        }
                        if (label.contains(" at ")) {
                            in = "at";
                        }
                        if (label.contains(" before ")) {
                            in = "before";
                        }
                        if (label.contains(" behind ")) {
                            in = "behind";
                        }
                        if (label.contains(" below ")) {
                            in = "below";
                        }
                        if (label.contains(" beneath ")) {
                            in = "beneath";
                        }
                        if (label.contains(" beside ")) {
                            in = "beside";
                        }
                        if (label.contains(" between ")) {
                            in = "between";
                        }
                        if (label.contains(" by ")) {
                            in = "by";
                        }
                        if (label.contains(" from ")) {
                            in = "from";
                        }
                        if (label.contains(" in ")) {
                            in = "in";
                        }
                        if (label.contains(" into ")) {
                            in = "into";
                        }
                        if (label.contains(" near ")) {
                            in = "near";
                        }
                        if (label.contains(" on ")) {
                            in = "on";
                        }
                        if (label.contains(" to ")) {
                            in = "to";
                        }
                        if (label.contains(" toward ")) {
                            in = "toward";
                        }
                        if (label.contains(" under ")) {
                            in = "under";
                        }
                        if (label.contains(" upon ")) {
                            in = "upon";
                        }
                        if (label.contains(" with ")) {
                            in = "with";
                        }
                        if (label.contains(" within ")) {
                            in = "within";
                        }
                        hasVerb = true;
//                        break;
                    }

                    if (in != null) {
                        String l = predicate.getLabel();
                        int i = l.indexOf(in) + in.length();
                        String p = l.substring(0, i);
                        return new PredicateNLRepresentation(predicateString, S_type, O_type, null, null, null, predicate.getLabel());
//                    Database.storePredicates_VP("VP_S_O", predicate, p, 98, 1, 1, 1);
                    } else {
//                        if (!hasVerb) {
                        if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                            return new PredicateNLRepresentation(predicateString, S_type, O_type, predicate.getLabel(), null, null, null);
//                        Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 98, 1, 1, 1);
                        } else {
                            return new PredicateNLRepresentation(predicateString, S_type, O_type, null, null, predicate.getLabel(), null);
//                        Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 98, 1, 1, 1);
                        }
//                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
