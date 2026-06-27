package imd.ufrn.concurrent.VirtualThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class VTConcurrentColectMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        Queue<String> sharedMatches = new ConcurrentLinkedQueue<>();
        String targetLower = target.toLowerCase();
        final Object lock = new Object();

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

                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        synchronized (lock) {
                            sharedMatches.add(word); 
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
                e.printStackTrace(System.err); 
            }
        }

        return new ArrayList<> (sharedMatches);
    }
}