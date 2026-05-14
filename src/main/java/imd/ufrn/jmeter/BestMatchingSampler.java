package imd.ufrn.jmeter;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import imd.ufrn.concurrent.HybridMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTLatchMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTReentrantMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTSemaphoreMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTAtomicMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTLatchMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTReentrantMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTSemaphoreMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTVolatileMatcher;
import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.serial.SerialMatcher;
import imd.ufrn.utils.DatasetLoader;


public class BestMatchingSampler extends AbstractJavaSamplerClient implements Serializable {
    private static List<String> textDatabase;
    private BestMatcherStrategy strategy;

    @Override
    public void setupTest(JavaSamplerContext context) {
        if (textDatabase == null) {
            try {
                textDatabase = DatasetLoader.loadTextDatabase("/home/raquel/git/prog-concorrente/BestMatching/best-matching-java/src/main/resources/Os-Miseraveis-clean.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String strategyName = context.getParameter("Estrategia");
        switch (strategyName) {
            case "Serial":
                strategy = new SerialMatcher();
                break;
            case "PTMutex":
                strategy = new PTMutexMatcher(); 
                break;
            case "PTAtomic":
                strategy = new PTAtomicMatcher();
                break;
            case "PTBasico":
                strategy = new PTBasicoMatcher();
                break;
            case "PTReentrant":
                strategy = new PTReentrantMatcher();
                break;
            case "PTSemaphore":
                strategy = new PTSemaphoreMatcher();
                break;
            case "PTLatch":
                strategy = new PTLatchMatcher();
                break;
            case "VTSemaphore":
                strategy = new VTSemaphoreMatcher();
                break;
            case "VTVolatile":
                strategy = new VTVolatileMatcher();
                break;
            case "VTAtomic":
                strategy = new VTAtomicMatcher();
                break;
            case "VTLatch":
                strategy = new VTLatchMatcher();
                break;
            case "VTMutex":
                strategy = new VTMutexMatcher();
                break;
            case "VTReentrant":
                strategy = new VTReentrantMatcher();
                break;
            case "Hybrid":
                String caminhoArquivo = "/home/raquel/git/prog-concorrente/BestMatching/best-matching-java/src/main/resources/Os-Miseraveis-clean.txt";
                strategy = new HybridMatcher(caminhoArquivo);
                break;
            default:
                strategy = new PTMutexMatcher();
        }
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("PalavraAlvo", "morte");
        defaultParameters.addArgument("DistanciaMax", "2");
        defaultParameters.addArgument("Estrategia", "PTBasico");
        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String targetWord = context.getParameter("PalavraAlvo");
        int maxDistance = context.getIntParameter("DistanciaMax", 2);

        SampleResult result = new SampleResult();
        result.sampleStart(); // Inicia o cronómetro do JMeter
        result.setSampleLabel("Teste " + context.getParameter("Estrategia"));

        try {
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
