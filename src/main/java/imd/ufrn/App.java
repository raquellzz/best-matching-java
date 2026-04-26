package imd.ufrn;

import java.io.IOException;
import java.util.List;

import imd.ufrn.concurrent.PlatformThreadMutexMatcher;
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
        // BestMatcherStrategy matcher = new PlatformThreadBasicoMatcher();
        // BestMatcherStrategy matcher = new VirtualThreadBasicoMatcher();

        BestMatcherStrategy matcher = new PlatformThreadMutexMatcher();

        long startTime = System.currentTimeMillis();
        List<String> resultados = matcher.findMatches("morte", database, 2);
        long endTime = System.currentTimeMillis();

        System.out.println("Encontrados: " + resultados.size());
        System.out.println("Tempo: " + (endTime - startTime) + " ms");
    }
}
