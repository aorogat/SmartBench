package benchmarkGenerator.subgraphShapeGenerator.model;

import java.util.ArrayList;

import lexiconGenerator.kg_explorer.model.PredicateContext;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicateNLRepresentation;
import lexiconGenerator.predicateRepresentationExtractor.scrapping.model.PredicatesLexicon;
import settings.Settings;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author aorogat
 */
public class TriplePattern {

    private Variable subject;
    private Variable object;
    private Variable predicate;

    String s_type;
    String o_type;

    String s_type_without_prefix;
    String o_type_without_prefix;

    public TriplePattern(Variable source, Variable destination, Variable label) {
        this.subject = source;
        this.object = destination;
        this.predicate = label;
        setContext();

    }


    private void setContext() {
        if (o_type != null) {
            if (o_type.equals(Settings.Number) || o_type.equals(Settings.Date) || o_type.equals(Settings.Literal)) {
                s_type = Settings.knowledgeGraph.getType(Settings.explorer, subject.getValueWithPrefix());
                s_type_without_prefix = Settings.explorer.removePrefix(s_type);
                o_type_without_prefix = Settings.explorer.removePrefix(o_type);

                return;
            }
        }

        ArrayList<PredicateContext> allPossibleContexts = Settings.knowledgeGraph.getPredicateContextFromTripleExample(subject.getValueWithPrefix(),
                predicate.getValueWithPrefix(),
                object.getValueWithPrefix());
        //select one exist in Lexicon
        if (allPossibleContexts != null) {
            if (allPossibleContexts.size() > 0) {
                for (PredicateContext possibleContext : allPossibleContexts) {
                    ArrayList<PredicateNLRepresentation> predicatesNL = PredicatesLexicon.predicatesNL;
                    for (PredicateNLRepresentation predicateNLRepresentation : predicatesNL) {
                        if (possibleContext.getSubjectType().equals(predicateNLRepresentation.getSubject_type())
                                && possibleContext.getObjectType().equals(predicateNLRepresentation.getObject_type())) {
                            if (this.s_type == null) {
                                this.s_type = possibleContext.getSubjectType();
                            }
                            if (this.o_type == null) {
                                this.o_type = possibleContext.getObjectType();
                            }
                            break;
                        }
                    }

                }
            }
        }

        if (s_type == null) {
            s_type = Settings.knowledgeGraph.getType(Settings.explorer, subject.getValueWithPrefix());
        }
        if (o_type == null) {
            o_type = Settings.knowledgeGraph.getType(Settings.explorer, object.getValueWithPrefix());
        }

        s_type_without_prefix = Settings.explorer.removePrefix(s_type);
        o_type_without_prefix = Settings.explorer.removePrefix(o_type);

//
//        if (o_type.equals(Settings.Number)) {
//            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
//            o_type_without_prefix = Settings.Number;
//        } else if (o_type.equals(Settings.Date)) {
//            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
//            o_type_without_prefix = Settings.Date;
//        } else if (o_type.equals(Settings.Literal)) {
//            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
//            o_type_without_prefix = Settings.Literal;
//        } else {
//            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
//            o_type_without_prefix = Settings.explorer.removePrefix(o_type);
//        }
    }

    public TriplePattern(Variable subject, Variable object, Variable predicate, String s_type, String o_type) {
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;
        this.s_type = s_type;
        this.o_type = o_type;
        if (o_type.equals(Settings.Number)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Number;
        } else if (o_type.equals(Settings.Date)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Date;
        } else if (o_type.equals(Settings.Literal)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Literal;
        } else {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.explorer.removePrefix(o_type);
        }
    }

    public Variable getSubject() {
        return subject;
    }

    public void setSubject(Variable source) {
        this.subject = source;
    }

    public Variable getObject() {
        return object;
    }

    public void setObject(Variable destination) {
        this.object = destination;
    }

    public Variable getPredicate() {
        return predicate;
    }

    public void setPredicate(Variable label) {
        this.predicate = label;
    }

    public String getS_type() {
        return s_type;
    }

    public void setS_type(String s_type) {
        this.s_type = s_type;
    }

    public String getO_type() {
        return o_type;
    }

    public void setO_type(String o_type) {
        this.o_type = o_type;
    }

    public String toString() {
        String subjectValue = subject.getValue();
        String subjectType = s_type_without_prefix;
        String predicateValue = predicate.getValue();
        String objectValue = object.getValue();
        String objectType = o_type_without_prefix;

        if (s_type == null || o_type == null) {
            setContext();
        }
        if (s_type == null || o_type == null || o_type_without_prefix == null || s_type_without_prefix == null) {
            return null;
        }

        String s;
        if (Settings.Number.equals(objectType) || Settings.Date.equals(objectType) || Settings.Literal.equals(objectType)) {
            s = subjectValue + "[" + subjectType + "]" + " ____" + predicateValue + "____ " + object.getValueWithPrefix() + "[" + objectType + "]";
        } else {
            s = subjectValue + "[" + subjectType + "]" + " ____" + predicateValue + "____ " + objectValue + "[" + objectType + "]";
        }
        return s;
    }

    public String toStringNotSubject() {
        if (s_type == null || o_type == null || o_type_without_prefix == null) {
            setContext();
        }
        if (s_type == null || o_type == null || o_type_without_prefix == null) {
            return null;
        }
        String s = " ____" + predicate.getValue() + "____ " + object.getValue() + "[" + o_type_without_prefix + "]";
        return s;
    }

    
    //before refactor, know that space is important
    public String toQueryTriplePattern() {
        if (s_type == null || o_type == null) {
            setContext();
        }
        String o = "";
        if (object.getValueWithPrefix().startsWith("http")) {
            o = "<" + object.getValueWithPrefix() + ">";
        } else {
            o = object + "";
        }
        String s = "<" + subject + "> \t <" + predicate + "> \t " + o + " ";
        if (o_type.equals(Settings.Literal) && !o.toLowerCase().equals("?seed")) {
            s = " <" + subject + "> \t <" + predicate + "> \t \"" + o + "\" ";
        }
        if (o_type.equals(Settings.Date) || isValidDate(o) && !o.toLowerCase().equals("?seed")) {
            s = "<" + subject + ">\t <" + predicate + ">\t \"" + o + "\"^^xsd:dateTime ";
        }

        if (o_type.equals(Settings.Number) || isNumeric(o)) {
            s = "<" + subject + "> \t <" + predicate + "> \t " + o + " ";
        }
        return s;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getS_type_without_prefix() {
        return s_type_without_prefix;
    }

    public void setS_type_without_prefix(String s_type_without_prefix) {
        this.s_type_without_prefix = s_type_without_prefix;
    }

    public String getO_type_without_prefix() {
        return o_type_without_prefix;
    }

    public void setO_type_without_prefix(String o_type_without_prefix) {
        this.o_type_without_prefix = o_type_without_prefix;
    }

}
