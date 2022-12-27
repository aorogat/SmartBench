package lexiconGenerator.predicateRepresentationExtractor.scrapping.model;

public class PredicateNLRepresentation {

    private String predicate;
    //Context
    private String subject_type;
    private String object_type;

    private String predicate_s_O_NP; // representing predicate as NP between S and O. ex (S is a citizen of O) 
    private String predicate_o_s_VP; // representing predicate as VP between O and S. ex (O protect S) 
    private String predicate_s_O_VP;
    private String predicate_o_s_NP;

    public PredicateNLRepresentation(String predicate, String subject_type, String object_type,
            String predicate_s_O_NP, String predicate_o_s_VP,
            String predicate_o_s_NP, String predicate_s_O_VP) {
        this.predicate = predicate;
        this.subject_type = subject_type;
        this.object_type = object_type;
        // S is the ... of O
        this.predicate_s_O_NP = predicate_s_O_NP;
        if (this.predicate_s_O_NP != null) {
            this.predicate_s_O_NP = this.predicate_s_O_NP.replaceAll("\\(.*\\)", "");
        }
        // O .... S
        this.predicate_o_s_VP = predicate_o_s_VP;
        if (this.predicate_o_s_VP != null) {
            this.predicate_o_s_VP = this.predicate_o_s_VP.replaceAll("\\(.*\\)", "");
        }
        // S .... O
        this.predicate_s_O_VP = predicate_s_O_VP;
        if (this.predicate_s_O_VP != null) {
            this.predicate_s_O_VP = this.predicate_s_O_VP.replaceAll("\\(.*\\)", "");
        }
        // O is the .... of S
        this.predicate_o_s_NP = predicate_o_s_NP;
        if (this.predicate_o_s_NP != null) {
            this.predicate_o_s_NP = this.predicate_o_s_NP.replaceAll("\\(.*\\)", "");
        }

    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getSubject_type() {
        return subject_type;
    }

    public void setSubject_type(String subject_type) {
        this.subject_type = subject_type;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }

    public void setPredicate_s_O_NP(String predicate_s_O_NP) {
        this.predicate_s_O_NP = predicate_s_O_NP;
    }

    public String getPredicate_o_s_VP() {
        return predicate_o_s_VP;
    }

    public void setPredicate_o_s_VP(String predicate_o_s_VP) {
        this.predicate_o_s_VP = predicate_o_s_VP;
    }

    public String getPredicate_s_O_VP() {
        return predicate_s_O_VP;
    }

    public void setPredicate_s_O_VP(String predicate_s_O_VP) {
        this.predicate_s_O_VP = predicate_s_O_VP;
    }

    public String getPredicate_s_O_NP() {
        if (predicate_s_O_NP == null) {
            return predicate_s_O_NP;
        }

        String predicate = "is the " + predicate_s_O_NP;

        // Check if the predicate starts with a verb
        if (predicate_s_O_NP.toLowerCase().matches("^(is|are|was|were) .*")) {
            predicate = predicate_s_O_NP;
        }

        // Check if the predicate ends with a preposition
        if (predicate_s_O_NP.toLowerCase().matches(".* (above|across|about|of|for|against|along|among|around|at|before|behind|below|beneath|beside|between|in|into|near|on|to|toward|under|upon|with|within)$")) {
            return predicate;
        } else {
            return predicate + " of";
        }
    }

    public String getPredicate_o_s_NP() {
        if (predicate_o_s_NP == null) {
            return predicate_o_s_NP;
        }

        String predicate = "is the " + predicate_o_s_NP;

        // Check if the predicate starts with a verb
        if (predicate_o_s_NP.toLowerCase().matches("^(is|are|was|were) .*")) {
            predicate = predicate_o_s_NP;
        }

        // Check if the predicate ends with a preposition
        if (predicate_o_s_NP.toLowerCase().matches(".* (above|across|about|of|for|against|along|among|around|at|before|behind|below|beneath|beside|between|in|into|near|on|to|toward|under|upon|with|within)$")) {
            return predicate;
        } else {
            return predicate + " of";
        }
    }

 

    public void setPredicate_o_s_NP(String predicate_o_s_NP) {
        this.predicate_o_s_NP = predicate_o_s_NP;
    }

    public void print() {
        System.out.println(predicate + " (" + subject_type + ", " + object_type + ") ----> SO_VP=" + predicate_s_O_VP + ", "
                + "SO_NP=" + predicate_s_O_NP + ", "
                + "OS_VP=" + predicate_o_s_VP + ", "
                + "OS_NP=" + predicate_o_s_NP + "");
    }

}
