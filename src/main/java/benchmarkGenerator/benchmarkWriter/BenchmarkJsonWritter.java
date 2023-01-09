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

            // Create the "test benchmarks" folder if it does not exist
            File benchmarkFolder = new File("test benchmarks");
            if (!benchmarkFolder.exists()) {
                benchmarkFolder.mkdir();
            }

            // Create the file for the benchmark
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());                    
            writer.writeValue(new File("test benchmarks/" + benchmarkName + ".json"), benchmark.generatedBenchmark);
            
            System.out.println("Saved sucessfuly");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
