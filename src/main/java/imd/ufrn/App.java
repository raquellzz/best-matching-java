package imd.ufrn;

import java.io.IOException;
import java.util.List;

import imd.ufrn.concurrent.VirtualThreads.VTSemaphoreMatcher;
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
