package imd.ufrn.concurrent.PlatformThreadsExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class PTESemaphoreMatcher implements BestMatcherStrategy {
    private final Semaphore semaphore = new Semaphore(1);
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> sharedMatches = new ArrayList<>();
        String targetLower = target.toLowerCase();
        int numThreads = Runtime.getRuntime().availableProcessors();

        try (ExecutorService executor = Executors.newFixedThreadPool(numThreads)) {
            for (String word : textDatabase) {
                if (word == null || word.isEmpty()) continue;

                executor.submit(() -> {
                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        try {
                            semaphore.acquire();
                            sharedMatches.add(word);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace(System.err); 
                        }
                        finally {
                            semaphore.release();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace(System.err); 
        }
        return sharedMatches;
    }
}
