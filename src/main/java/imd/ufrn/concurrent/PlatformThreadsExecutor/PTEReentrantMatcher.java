package imd.ufrn.concurrent.PlatformThreadsExecutor;

import java.util.ArrayList;
import java.util.List;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class PTEReentrantMatcher implements BestMatcherStrategy {
    private final ReentrantLock rLock = new ReentrantLock();
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> sharedMatches = new ArrayList<>();
        String targetLower = target.toLowerCase();

        try (ExecutorService executor = Executors.newFixedThreadPool(10000)) {
            for (String word : textDatabase) {
                if (word == null || word.isEmpty()) continue;

                executor.submit(() -> {
                    int distance = LevenshteinAlgorithm.calculate(targetLower, word.toLowerCase());
                    
                    if (distance <= maxDistance) {
                        rLock.lock();
                        try{
                            sharedMatches.add(word); 
                        } finally {
                            rLock.unlock();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sharedMatches;
    }
}