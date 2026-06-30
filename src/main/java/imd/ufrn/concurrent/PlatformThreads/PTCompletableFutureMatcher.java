package imd.ufrn.concurrent.PlatformThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class PTCompletableFutureMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        String targetLower = target.toLowerCase();
        
        int numCores = Runtime.getRuntime().availableProcessors();
        int totalWords = textDatabase.size();
        int chunkSize = (int) Math.ceil((double) totalWords / numCores);

        List<String> finalMatches = new ArrayList<>();
        List<CompletableFuture<List<String>>> futures = new ArrayList<>();

        try (ExecutorService executor = Executors.newFixedThreadPool(numCores)) {
            
            for (int i = 0; i < numCores; i++) {
                final int start = i * chunkSize;
                final int end = Math.min(start + chunkSize, totalWords);

                if (start >= end) break;

                CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
                    List<String> localMatches = new ArrayList<>();
                    
                    for (int j = start; j < end; j++) {
                        String word = textDatabase.get(j);
                        if (word == null || word.isEmpty()) continue;

                        int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                        
                        if (distance <= maxDistance) {
                            localMatches.add(word);
                        }
                    }
                    return localMatches;
                }, executor);

                futures.add(future);
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(CompletableFuture[]::new)
            );

            allFutures.join();

            for (CompletableFuture<List<String>> future : futures) {
                finalMatches.addAll(future.join());
            }
        }

        return finalMatches;
    }
}