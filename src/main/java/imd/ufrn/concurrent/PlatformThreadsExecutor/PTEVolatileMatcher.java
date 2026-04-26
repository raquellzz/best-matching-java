package imd.ufrn.concurrent.PlatformThreadsExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import imd.ufrn.core.LevenshteinAlgorithm;
import imd.ufrn.core.BestMatcherStrategy;

public class PTEVolatileMatcher implements BestMatcherStrategy {
    private volatile boolean exactMatchFound = false;
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        Queue<String> sharedMatches = new ConcurrentLinkedQueue<>();
        String targetLower = target.toLowerCase();
        exactMatchFound = false;

        try (ExecutorService executor = Executors.newFixedThreadPool(10000)) {
            for (String word : textDatabase) {
                if (word == null || word.isEmpty()) continue;
                if (exactMatchFound) {
                        break; 
                    }

                executor.submit(() -> {
                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        // região crítica
                        sharedMatches.add(word); 
                        if (distance == 0) {
                            exactMatchFound = true; 
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(sharedMatches);
    }
}
