package imd.ufrn;

import java.io.IOException;
import java.util.List;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.serial.SerialMatcher;
import imd.ufrn.utils.DatasetLoader;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        //List<String> database = DatasetLoader.loadTextDatabase("src/main/resources/theGreatGatsby.txt");
        List<String> database = DatasetLoader.loadTextDatabase("src/main/resources/iracema.txt");
        BestMatcherStrategy matcher = new SerialMatcher();

        long startTime = System.currentTimeMillis();
        List<String> resultados = matcher.findMatches("indio", database, 2);
        long endTime = System.currentTimeMillis();

        System.out.println("Encontrados: " + resultados.size());
        System.out.println("Tempo: " + (endTime - startTime) + " ms");
    }
}
