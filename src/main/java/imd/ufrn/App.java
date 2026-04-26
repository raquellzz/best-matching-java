package imd.ufrn;

import java.io.IOException;
import java.util.List;

import imd.ufrn.concurrent.PlatformThreads.PTAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTReentrantMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTSemaphoreMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTVolatileMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTVolatileRLMatcher;
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
        // BestMatcherStrategy matcher = new VTBasicoMatcher();
        // BestMatcherStrategy matcher = new PTMutexMatcher();
        // BestMatcherStrategy matcher = new PTReentrantMatcher();
        // BestMatcherStrategy matcher = new PTAtomicMatcher();
        // BestMatcherStrategy matcher = new PTVolatileMatcher();
        // BestMatcherStrategy matcher = new PTVolatileRLMatcher();
        BestMatcherStrategy matcher = new PTSemaphoreMatcher();

        long startTime = System.currentTimeMillis();
        List<String> resultados = matcher.findMatches("morte", database, 2);
        long endTime = System.currentTimeMillis();

        System.out.println("Encontrados: " + resultados.size());
        System.out.println("Tempo: " + (endTime - startTime) + " ms");
    }
}
