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
import imd.ufrn.concurrent.PlatformThreads.PTCompletableFutureMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTConcurrentColectMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTExecutorCallableMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTExecRunnableMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTCompletableFutureMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTConcurrentColectMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTExecutorCallableMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTExecRunnableSemaphoreMatcher;
import imd.ufrn.concurrent.ForkJoinMatcher;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class MatcherU3Benchmark {
    private List<String> dataset;
    // private String targetWord;
    // private int maxDistance;
    private String targetWord = "morte";
    private int maxDistance = 2;

    private BestMatcherStrategy ptCompletableFutureStrategy;
    private BestMatcherStrategy ptConcurrentCollectStrategy;
    private BestMatcherStrategy ptExecutorCallableStrategy;
    private BestMatcherStrategy ptExecutorRunnableMutexStrategy;
    private BestMatcherStrategy vtCompletableFutureStrategy;
    private BestMatcherStrategy vtConcurrentCollectStrategy;
    private BestMatcherStrategy vtExecutorCallableStrategy;
    private BestMatcherStrategy vtExecutorRunnableSemaphoreStrategy;
    private BestMatcherStrategy forkJoinStrategy;
    private BestMatcherStrategy sparkStrategy;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        dataset = DatasetLoader.loadTextDatabase("src/main/resources/Os-Miseraveis-clean.txt");
        
        ptCompletableFutureStrategy = new PTCompletableFutureMatcher();
        ptConcurrentCollectStrategy = new PTConcurrentColectMatcher();
        ptExecutorCallableStrategy = new PTExecutorCallableMatcher();
        ptExecutorRunnableMutexStrategy = new PTExecRunnableMutexMatcher();
        vtCompletableFutureStrategy = new VTCompletableFutureMatcher();
        vtConcurrentCollectStrategy = new VTConcurrentColectMatcher();
        vtExecutorCallableStrategy = new VTExecutorCallableMatcher();
        vtExecutorRunnableSemaphoreStrategy = new VTExecRunnableSemaphoreMatcher();
        forkJoinStrategy = new ForkJoinMatcher();

    }

    @Benchmark
    public List<String> ptCompletableFutureMatcher() {
        return ptCompletableFutureStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> ptConcurrentCollectMatcher() {
        return ptConcurrentCollectStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> ptExecutorCallableMatcher() {
        return ptExecutorCallableStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> ptExecutorRunnableMutexMatcher() {
        return ptExecutorRunnableMutexStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> vtCompletableFutureMatcher() {
        return vtCompletableFutureStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> vtConcurrentCollectMatcher() {
        return vtConcurrentCollectStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> vtExecutorCallableMatcher() {
        return vtExecutorCallableStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> vtExecutorRunnableSemaphoreMatcher() {
        return vtExecutorRunnableSemaphoreStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> forkJoinMatcher() {
        return forkJoinStrategy.findMatches(targetWord, dataset, maxDistance);
    }


}