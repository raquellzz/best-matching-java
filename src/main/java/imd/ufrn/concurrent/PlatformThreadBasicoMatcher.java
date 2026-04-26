package imd.ufrn.concurrent;

import java.util.ArrayList;
import java.util.List;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class PlatformThreadBasicoMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> sharedMatches = new ArrayList<>();
        String targetLower = target.toLowerCase();

        int numThreads = Runtime.getRuntime().availableProcessors()/2; // Usar metade dos núcleos para evitar sobrecarga
        int totalWords = textDatabase.size();
        int chunkSize = (int) Math.ceil((double) totalWords / numThreads);

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, totalWords);

            if (start >= end) break;

            Thread t = new Thread(() -> {
                for (int j = start; j < end; j++) {
                    String word = textDatabase.get(j);
                    if (word == null || word.isEmpty()) continue;

                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        // região crítica
                        sharedMatches.add(word); 
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