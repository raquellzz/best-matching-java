package imd.ufrn.concurrent.VirtualThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class VTSemaphoreMatcher implements BestMatcherStrategy {
    private final Semaphore semaphore = new Semaphore(1);
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> sharedMatches = new ArrayList<>();
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

                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        try {
                            semaphore.acquire();
                            sharedMatches.add(word);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace(System.err); 
                        }
                        finally {
                            semaphore.release();
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

        return sharedMatches;
    }
}
