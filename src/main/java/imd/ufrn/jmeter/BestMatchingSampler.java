package imd.ufrn.jmeter;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTMutexMatcher;
import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.utils.DatasetLoader;


public class BestMatchingSampler extends AbstractJavaSamplerClient implements Serializable {
    private static List<String> textDatabase;
    private BestMatcherStrategy strategy;

    @Override
    public void setupTest(JavaSamplerContext context) {
        // Este método roda antes do teste começar. Ideal para carregar a base!
        if (textDatabase == null) {
            try {
                // Mude para o seu método real de carregar o txt
                textDatabase = DatasetLoader.loadTextDatabase("/home/raquel/git/prog-concorrente/BestMatching/best-matching-java/src/main/resources/Os-Miseraveis-clean.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Escolhe a estratégia baseada no parâmetro do JMeter
        String strategyName = context.getParameter("Estrategia");
        if (strategyName.equalsIgnoreCase("VTMutex")) {
            strategy = new VTMutexMatcher();
        } else {
            strategy = new PTMutexMatcher();
        }
    }

    // Define os campos que vão aparecer na tela gráfica do JMeter
    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("PalavraAlvo", "morte");
        defaultParameters.addArgument("DistanciaMax", "2");
        defaultParameters.addArgument("Estrategia", "VTEBasico");
        return defaultParameters;
    }

    // Onde a mágica (e a cronometragem) acontece!
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String targetWord = context.getParameter("PalavraAlvo");
        int maxDistance = context.getIntParameter("DistanciaMax", 2);

        SampleResult result = new SampleResult();
        result.sampleStart(); // Inicia o cronómetro do JMeter
        result.setSampleLabel("Teste " + context.getParameter("Estrategia"));

        try {
            // Executa o seu algoritmo
            List<String> matches = strategy.findMatches(targetWord, textDatabase, maxDistance);

            result.sampleEnd(); // Para o cronómetro
            result.setSuccessful(true);
            result.setResponseCode("200");
            result.setResponseMessage("Sucesso! Palavras encontradas: " + matches.size());
            
        } catch (Exception e) {
            result.sampleEnd();
            result.setSuccessful(false);
            result.setResponseCode("500");
            result.setResponseMessage("Erro interno: " + e.getMessage());
        }

        return result;
    }
}
