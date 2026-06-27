package imd.ufrn.concurrent.VirtualThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class VTExecutorCallableMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        String targetLower = target.toLowerCase();
        
        int numChunks = Runtime.getRuntime().availableProcessors();
        int totalWords = textDatabase.size();
        int chunkSize = (int) Math.ceil((double) totalWords / numChunks);

        List<Callable<List<String>>> tasks = new ArrayList<>();

        for (int i = 0; i < numChunks; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, totalWords);

            if (start >= end) break;

            tasks.add(() -> {
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
            });
        }

        List<String> finalMatches = new ArrayList<>();
        
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            List<Future<List<String>>> futures = executor.invokeAll(tasks);
            
            for (Future<List<String>> future : futures) {
                finalMatches.addAll(future.get());
            }
            
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace(System.err);
        }

        return finalMatches;
    }
}