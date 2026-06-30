package imd.ufrn.benchmark;

import org.openjdk.jmh.annotations.*;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.serial.SerialMatcher;
import imd.ufrn.utils.DatasetLoader;

import imd.ufrn.concurrent.HybridMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTSemaphoreMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTBasicoMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTSemaphoreMatcher;


import imd.ufrn.concurrent.PlatformThreads.PTCompletableFutureMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTConcurrentColectMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTExecutorCallableMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTExecRunnableMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTCompletableFutureMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTConcurrentColectMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTExecutorCallableMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTExecRunnableSemaphoreMatcher;
import imd.ufrn.concurrent.ForkJoinMatcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class MatcherBenchmark {

    @Param({
        "01_PT_Concurrent_Collection",
        "02_VT_Concurrent_Collection",
        "03_PT_Executor_Runnable",
        "04_VT_Executor_Runnable",
        "05_PT_Executor_Callable(Confinamento)",
        "06_VT_Executor_Callable(Confinamento)",
        "07_ForkJoin(Work-Stealing)",
        "08_PT_CompletableFuture",
        "09_VT_CompletableFuture",
    })
    public String strategyType;

    private List<String> dataset;
    private BestMatcherStrategy strategy;
    private final String targetWord = "morte";
    private final int maxDistance = 2;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        dataset = DatasetLoader.loadTextDatabase("src/main/resources/Os-Miseraveis-clean.txt");
        
        switch (strategyType) {
            case "01_PT_Concurrent_Collection": 
                strategy = new PTConcurrentColectMatcher(); break;
            case "02_VT_Concurrent_Collection": 
                strategy = new VTConcurrentColectMatcher(); break;
            case "03_PT_Executor_Runnable": 
                strategy = new PTExecRunnableMutexMatcher(); break;
            case "04_VT_Executor_Runnable": 
                strategy = new VTExecRunnableSemaphoreMatcher(); break;
            case "05_PT_Executor_Callable(Confinamento)": 
                strategy = new PTExecutorCallableMatcher(); break;
            case "06_VT_Executor_Callable(Confinamento)": 
                strategy = new VTExecutorCallableMatcher(); break;
            case "07_ForkJoin(Work-Stealing)": 
                strategy = new ForkJoinMatcher(); break;
            case "08_PT_CompletableFuture": 
                strategy = new PTCompletableFutureMatcher(); break;
            case "09_VT_CompletableFuture": 
                strategy = new VTCompletableFutureMatcher(); break;
            default: 
                throw new IllegalArgumentException("Estratégia não mapeada: " + strategyType);
        }
    }

    @Benchmark
    public List<String> executeMatch() {
        return strategy.findMatches(targetWord, dataset, maxDistance);
    }
}