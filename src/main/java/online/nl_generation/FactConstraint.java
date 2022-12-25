package online.nl_generation;

/**
 *
 * @author aorogat
 */
public class FactConstraint {

    public static final byte S_O_VP = 1;
    public static final byte S_O_NP = 2;
    public static final byte O_S_VP = 3;
    public static final byte O_S_NP = 4;

    public static String constructFactContraintNL(String O, String P, byte P_type, boolean tagged, String auxVerb, String preposition) {
        String fc = "";
        if (tagged) {
            switch (P_type) {
                case S_O_VP: 
                    fc = auxVerb  + " " + "<p>" + P + "</p>" + " " + preposition + " " +"<o>" + O + "</o>";
                    break;
                case S_O_NP:
                    fc = "<p>" + P + "</p>" + " " + "<o>" + O + "</o>";
                    break;
                case O_S_VP: ;
                    fc = auxVerb  + " " + "<o>" + O + "</o>" + " " + preposition + " " +"<p>" + P + "</p>";
                    break;
                case O_S_NP: 
                    fc = auxVerb  + " " + "<o>" + O + "</o>" + " " + preposition + " " +"<p>" + P + "</p>";
                    break;
            }
        }
        else{
            switch (P_type) {
                case S_O_VP:
                    fc = auxVerb  + " " + P + " " + preposition + " " + O;
                    break;
                case S_O_NP:
                    fc = P + " " + O;
                    break;
                case O_S_VP: ;
                    fc = auxVerb  + " " + O + " " + preposition + " " + P;
                    break;
                case O_S_NP: 
                    fc = P + " is " + O;
                    break;
            }
        }
            
        return fc;
    }
}
