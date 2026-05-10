package imd.ufrn.concurrent.VirtualThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class VTVolatileMatcher implements BestMatcherStrategy {
    private volatile boolean exactMatchFound = false;
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        Queue<String> sharedMatches = new ConcurrentLinkedQueue<>();
        exactMatchFound = false;
        String targetLower = target.toLowerCase();

        int numChunks = Runtime.getRuntime().availableProcessors();
        int totalWords = textDatabase.size();
        int chunkSize = (int) Math.ceil((double) totalWords / numChunks);

        List<Thread> vThreads = new ArrayList<>();
        for (int i = 0; i < numChunks; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, totalWords);

            if (start >= end) break;

            Thread vt = Thread.ofVirtual().unstarted(() -> {
                for (int j = start; j < end; j++) {
                    String word = textDatabase.get(j);
                    if (word == null || word.isEmpty()) continue;
                    if (exactMatchFound) break;

                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        sharedMatches.add(word);
                        if (distance == 0) {
                            exactMatchFound = true; 
                        }
                    }
                }
            });

            vThreads.add(vt);
            vt.start();
        }

        for (Thread vt : vThreads) {
            try {
                vt.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        return new ArrayList<>(sharedMatches);
    }
}