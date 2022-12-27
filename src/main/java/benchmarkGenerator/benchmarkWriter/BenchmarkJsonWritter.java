package benchmarkGenerator.benchmarkWriter;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import benchmarkGenerator.questionsGenerator.questionBuilder.helpers.Benchmark;

/**
 *
 * @author aorogat
 */
public class BenchmarkJsonWritter {

    public static void save(Benchmark benchmark, String benchmarkName) {

        ObjectMapper mapper = new ObjectMapper();
        try {

            // Writing to a file   
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());                    
            writer.writeValue(new File(benchmarkName + ".json"), benchmark.generatedBenchmark);
            
            System.out.println("Saved sucessfuly");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
