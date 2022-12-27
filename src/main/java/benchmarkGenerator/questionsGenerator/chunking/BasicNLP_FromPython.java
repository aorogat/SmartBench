package benchmarkGenerator.questionsGenerator.chunking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicNLP_FromPython {

    
    public static double phraseSimilarity(String phrase1, String phrase2)
    {
        try {
            URL url = new URL("http://127.0.0.1:12311/phraseSimilarity?phrase1="+ URLEncoder.encode(phrase1, StandardCharsets.UTF_8.toString())+"&phrase2="+URLEncoder.encode(phrase2, StandardCharsets.UTF_8.toString()));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return Double.parseDouble(content.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(BasicNLP_FromPython.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicNLP_FromPython.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
    
    
    public static String nounPlural(String word)
    {
        try {
            URL url = new URL("http://127.0.0.1:12311/plural?word="+ URLEncoder.encode(word, StandardCharsets.UTF_8.toString()));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } catch (MalformedURLException ex) {
            Logger.getLogger(BasicNLP_FromPython.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicNLP_FromPython.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    
    public static String baseVerb(String w) throws MalformedURLException, ProtocolException, IOException {
        URL url = new URL("http://127.0.0.1:12311/base?word="+w);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }
}
