package benchmarkGenerator.questionsGenerator.questionBuilder.preprocessors;

import system.components.Predicate_Representation_Extractor;

/**
 *
 * @author aorogat
 */
public class PhraseRepresentationProcessing {
    public static String NP_without_verb(String NP)
    {
        if(NP==null)
            return NP;
        return NP.replaceAll("(\\bis/are\\b|\\bis\\b|\\bare\\b|\\bwas\\b|\\bwere\\b) ", "").trim();
    }
    
    public static String NP_of_the_form_NP_only(String NP)
    {
        if(NP==null)
            return NP;
        return NP_without_verb(NP).replaceAll("(\\ba\\b|\\ban\\b|\\bthe\\b)", "")
                .replaceAll("(" + Predicate_Representation_Extractor.getVerbPrepositionsConcatenated("\\b|\\b") + ")", "").trim();
    }
    
    public static String NP_of_the_form_VB_DT_NP(String NP)
    {
        if(NP==null)
            return NP;
        return NP.replaceAll("(" + Predicate_Representation_Extractor.getVerbPrepositionsConcatenated("\\b|\\b") + ")", "").trim();
    }
    
    public static String NP_of_the_form_DT_NP_IN(String NP)
    {
        if(NP==null)
            return NP;
        return NP.replaceFirst("(\\bis/are\\b|\\bis\\b|\\bare\\b|\\bwas\\b|\\bwere\\b) ", "").trim();
    }
    
    public static String NP_of_the_form_DT_NP(String NP)
    {
        if(NP==null)
            return NP;
        return NP_without_verb(NP).replaceFirst("(\\ba\\b|\\ban\\b|\\bthe\\b)", "")
                .replaceFirst("(" + Predicate_Representation_Extractor.getVerbPrepositionsConcatenated("\\b|\\b") + ")", "").trim();
    }

    public static String negatePhrase(String phrase) {
        String negatedPhrase = phrase.replaceAll("(?i)\\b(?:am|is|are|was|were|do|does|have|has|will|shall|should|may|might|must)\\b", "$0 not");
        if (negatedPhrase.equals(phrase)) negatedPhrase = "not " + phrase;
        return negatedPhrase;
    }

    public static String verbPhraseAddAuxiliary(String verbPhrase) {
        if ((verbPhrase.startsWith("was ") || verbPhrase.startsWith("were ") || verbPhrase.startsWith("is ") || verbPhrase.startsWith("are "))) {
            return verbPhrase;
        } else {
            return " were " + verbPhrase;
        }
    }

}
