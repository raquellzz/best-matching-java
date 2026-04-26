package imd.ufrn.concurrent.VirtualThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class VTSemaphoreMatcher implements BestMatcherStrategy {
    private final Semaphore semaphore = new Semaphore(1);
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> sharedMatches = new ArrayList<>();
        String targetLower = target.toLowerCase();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
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
                            e.printStackTrace();
                        }
                        finally {
                            semaphore.release();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sharedMatches;
    }
}
