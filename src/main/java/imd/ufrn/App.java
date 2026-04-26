package imd.ufrn;

import java.io.IOException;
import java.util.List;

import imd.ufrn.concurrent.PlatformThreads.PTAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTReentrantMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTSemaphoreMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTVolatileMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTLatchMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTReentrantMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTSemaphoreMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTAtomicMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTVolatileMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTLatchMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTELatchMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEMutexMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEReentrantMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTESemaphoreMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEVolatileMatcher;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.utils.DatasetLoader;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
        //List<String> database = DatasetLoader.loadTextDatabase("src/main/resources/theGreatGatsby.txt");
        // List<String> database = DatasetLoader.loadTextDatabase("src/main/resources/iracema.txt");
        List<String> database = DatasetLoader.loadTextDatabase("src/main/resources/Os-Miseraveis-clean.txt");

        // BestMatcherStrategy matcher = new SerialMatcher();
        // BestMatcherStrategy matcher = new PTBasicoMatcher();
        // BestMatcherStrategy matcher = new PTMutexMatcher();
        // BestMatcherStrategy matcher = new PTReentrantMatcher();
        // BestMatcherStrategy matcher = new PTAtomicMatcher();
        // BestMatcherStrategy matcher = new PTVolatileMatcher();
        // BestMatcherStrategy matcher = new PTVolatileRLMatcher();
        // BestMatcherStrategy matcher = new PTSemaphoreMatcher();
        // BestMatcherStrategy matcher = new PTLatchMatcher();
        // BestMatcherStrategy matcher = new VTBasicoMatcher();
        // BestMatcherStrategy matcher = new VTMutexMatcher();
        // BestMatcherStrategy matcher = new VTReentrantMatcher();
        // BestMatcherStrategy matcher = new VTVolatileMatcher();
        // BestMatcherStrategy matcher = new VTVolatileRTMatcher();
        // BestMatcherStrategy matcher = new VTLatchMatcher();
        BestMatcherStrategy matcher = new VTSemaphoreMatcher();
        // BestMatcherStrategy matcher = new PTEBasicoMatcher();
        // BestMatcherStrategy matcher = new PTEMutexMatcher();
        // BestMatcherStrategy matcher = new PTEReentrantMatcher();
        // BestMatcherStrategy matcher = new PTESemaphoreMatcher();
        // BestMatcherStrategy matcher = new PTEAtomicMatcher();
        // BestMatcherStrategy matcher = new PTELatchMatcher();
        // BestMatcherStrategy matcher = new PTEVolatileMatcher();

        long startTime = System.currentTimeMillis();
        List<String> resultados = matcher.findMatches("morte", database, 2);
        long endTime = System.currentTimeMillis();

        System.out.println("Encontrados: " + resultados.size());
        System.out.println("Tempo: " + (endTime - startTime) + " ms");
    }
}
