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
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class MatcherBenchmark {
    private List<String> dataset;
    // private String targetWord;
    // private int maxDistance;
    private String targetWord = "morte";
    private int maxDistance = 2;

    private BestMatcherStrategy serialStrategy;
    private BestMatcherStrategy ptBasicoStrategy;
    private BestMatcherStrategy ptAtomicStrategy;
    private BestMatcherStrategy ptMutexStrategy;
    private BestMatcherStrategy ptReentrantStrategy;
    private BestMatcherStrategy ptSemaphoreStrategy;
    private BestMatcherStrategy ptVolatileStrategy;
    private BestMatcherStrategy ptLatchStrategy;

    private BestMatcherStrategy ptEBasicoStrategy;
    private BestMatcherStrategy ptEMutexStrategy;
    private BestMatcherStrategy ptEReentrantStrategy;
    private BestMatcherStrategy ptESemaphoreStrategy;
    private BestMatcherStrategy ptEVolatileStrategy;
    private BestMatcherStrategy ptELatchStrategy;
    private BestMatcherStrategy ptEAtomicStrategy;

    private BestMatcherStrategy vtBasicoStrategy;
    private BestMatcherStrategy vtAtomicStrategy;
    private BestMatcherStrategy vtMutexStrategy;
    private BestMatcherStrategy vtReentrantStrategy;
    private BestMatcherStrategy vtSemaphoreStrategy;
    private BestMatcherStrategy vtVolatileStrategy;
    private BestMatcherStrategy vtLatchStrategy;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        dataset = DatasetLoader.loadTextDatabase("src/main/resources/Os-Miseraveis-clean.txt");
        
        serialStrategy = new SerialMatcher();

        ptBasicoStrategy = new imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher();
        ptAtomicStrategy = new imd.ufrn.concurrent.PlatformThreads.PTAtomicMatcher();
        ptMutexStrategy = new imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher();
        ptReentrantStrategy = new imd.ufrn.concurrent.PlatformThreads.PTReentrantMatcher();
        ptSemaphoreStrategy = new imd.ufrn.concurrent.PlatformThreads.PTSemaphoreMatcher();
        ptVolatileStrategy = new imd.ufrn.concurrent.PlatformThreads.PTVolatileMatcher();
        ptLatchStrategy = new imd.ufrn.concurrent.PlatformThreads.PTLatchMatcher();

        ptEBasicoStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTEBasicoMatcher();
        ptEMutexStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTEMutexMatcher();
        ptEReentrantStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTEReentrantMatcher();
        ptESemaphoreStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTESemaphoreMatcher();
        ptEVolatileStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTEVolatileMatcher();
        ptELatchStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTELatchMatcher();
        ptEAtomicStrategy = new imd.ufrn.concurrent.PlatformThreadsExecutor.PTEAtomicMatcher();

        vtBasicoStrategy = new imd.ufrn.concurrent.VirtualThreads.VTBasicoMatcher();
        vtAtomicStrategy = new imd.ufrn.concurrent.VirtualThreads.VTAtomicMatcher();
        vtMutexStrategy = new imd.ufrn.concurrent.VirtualThreads.VTMutexMatcher();
        vtReentrantStrategy = new imd.ufrn.concurrent.VirtualThreads.VTReentrantMatcher();
        vtSemaphoreStrategy = new imd.ufrn.concurrent.VirtualThreads.VTSemaphoreMatcher();
        vtVolatileStrategy = new imd.ufrn.concurrent.VirtualThreads.VTVolatileMatcher();
        vtLatchStrategy = new imd.ufrn.concurrent.VirtualThreads.VTLatchMatcher();
    }

    @Benchmark
    public List<String> testSerialMatcher() {
        return serialStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTBasicoMatcher() {
        return ptBasicoStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTAtomicMatcher() {
        return ptAtomicStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTMutexMatcher() {
        return ptMutexStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTReentrantMatcher() {
        return ptReentrantStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTSemaphoreMatcher() {
        return ptSemaphoreStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTVolatileMatcher() {
        return ptVolatileStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTLatchMatcher() {
        return ptLatchStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTEBasicoMatcher() {
        return ptEBasicoStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTEMutexMatcher() {
        return ptEMutexStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTEReentrantMatcher() {
        return ptEReentrantStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTESemaphoreMatcher() {
        return ptESemaphoreStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTEVolatileMatcher() {
        return ptEVolatileStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTELatchMatcher() {
        return ptELatchStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testPTEAtomicMatcher() {
        return ptEAtomicStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTBasicoMatcher() {
        return vtBasicoStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTAtomicMatcher() {
        return vtAtomicStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTMutexMatcher() {
        return vtMutexStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTReentrantMatcher() {
        return vtReentrantStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTSemaphoreMatcher() {
        return vtSemaphoreStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTVolatileMatcher() {
        return vtVolatileStrategy.findMatches(targetWord, dataset, maxDistance);
    }

    @Benchmark
    public List<String> testVTLatchMatcher() {
        return vtLatchStrategy.findMatches(targetWord, dataset, maxDistance);
    }
}