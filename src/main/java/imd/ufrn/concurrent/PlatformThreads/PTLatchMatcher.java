package imd.ufrn.concurrent.PlatformThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class PTLatchMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        Queue<String> sharedMatches = new ConcurrentLinkedQueue<>();
        String targetLower = target.toLowerCase();

        int numThreads = Runtime.getRuntime().availableProcessors();
        int totalWords = textDatabase.size();
        int chunkSize = (int) Math.ceil((double) totalWords / numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = Math.min(start + chunkSize, totalWords);

            if (start >= end) break;

            new Thread(() -> {
                try{
                    for (int j = start; j < end; j++) {
                        String word = textDatabase.get(j);
                        if (word == null || word.isEmpty()) continue;

                        int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                        
                        if (distance <= maxDistance) {
                            sharedMatches.add(word); 
                        }
                    }
                } finally {
                    latch.countDown();
                }
                
            }).start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return new ArrayList<>(sharedMatches);
    }
}