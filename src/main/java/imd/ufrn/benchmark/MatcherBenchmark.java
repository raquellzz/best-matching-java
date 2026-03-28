package imd.ufrn.benchmark;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.serial.SerialMatcher;
import imd.ufrn.utils.DatasetLoader;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class MatcherBenchmark {

    private List<String> dataset;
    private BestMatcherStrategy serialStrategy;
    private String targetWord;
    private int maxDistance;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        dataset = DatasetLoader.loadTextDatabase("src/main/resources/iracema.txt");
        serialStrategy = new SerialMatcher();
        targetWord = "indio";
        maxDistance = 2;
    }

    @Benchmark
    public List<String> testSerialMatcher() {
        return serialStrategy.findMatches(targetWord, dataset, maxDistance);
    }
}