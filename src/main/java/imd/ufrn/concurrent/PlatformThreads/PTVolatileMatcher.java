package imd.ufrn.concurrent.PlatformThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import imd.ufrn.core.LevenshteinAlgorithm;
import imd.ufrn.core.BestMatcherStrategy;

public class PTVolatileMatcher implements BestMatcherStrategy {
    private volatile boolean exactMatchFound = false;
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> sharedMatches = new ArrayList<>();
        String targetLower = target.toLowerCase();

        exactMatchFound = false;

        int numThreads = Runtime.getRuntime().availableProcessors();
        int totalWords = textDatabase.size();
        int chunkSize = (int) Math.ceil((double) totalWords / numThreads);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, totalWords);


            Thread t = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    if (exactMatchFound) {
                        break; 
                    }
                    
                    String word = textDatabase.get(j);
                    if (word == null || word.isEmpty()) continue;

                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        // região crítica
                        sharedMatches.add(word); 
                        if (distance == 0) {
                            exactMatchFound = true; 
                        }
                    }
                }
            });

            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        return sharedMatches;
    }
}
