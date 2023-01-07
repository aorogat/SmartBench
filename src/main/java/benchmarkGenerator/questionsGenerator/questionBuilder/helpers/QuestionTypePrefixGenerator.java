
package benchmarkGenerator.questionsGenerator.questionBuilder.helpers;

import java.util.Random;

public class QuestionTypePrefixGenerator {
    private static String[] requestPrefixes = {
    "Give me",
    "Can you provide",
    "Tell me",
    "Mention",
    "Find",
    "Return",
    };

    private static String[] howManyPrefixes = {
            "How many",
            "Tell me the number of",
            "Mention the count of",
            "Find the number of",
            "Return the number of",
            "What is the total number of",
            "Can you tell me how many",
            "How many are there",
            "How many are there in total",
            "How many are there in total of",
            "How many are there in total of the",
            "Can you provide the number of",

    };

    private static Random rand = new Random();
    
    public static String getRequestPrefix()
    {
        return requestPrefixes[rand.nextInt(requestPrefixes.length)];
    }

    public static String getHowManyPrefix()
    {
        return howManyPrefixes[rand.nextInt(howManyPrefixes.length)];
    }
}
