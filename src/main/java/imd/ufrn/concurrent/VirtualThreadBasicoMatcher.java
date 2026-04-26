package imd.ufrn.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class VirtualThreadBasicoMatcher implements BestMatcherStrategy {

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
                        // região crítica
                        sharedMatches.add(word); 
                    }
                });
            }
        } 

        return sharedMatches;
    }
}