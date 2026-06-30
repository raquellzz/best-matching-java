package imd.ufrn.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class ForkJoinMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        String targetLower = target.toLowerCase();
        
        try (ForkJoinPool pool = new ForkJoinPool()) {
            LevenshteinTask rootTask = new LevenshteinTask(targetLower, textDatabase, maxDistance, 0, textDatabase.size());
            
            return pool.invoke(rootTask);
        }
    }

    private static class LevenshteinTask extends RecursiveTask<List<String>> {
        private static final int SEQUENTIAL_THRESHOLD = 10000; 

        private final String target;
        private final List<String> textDatabase;
        private final int maxDistance;
        private final int start;
        private final int end;

        public LevenshteinTask(String target, List<String> textDatabase, int maxDistance, int start, int end) {
            this.target = target;
            this.textDatabase = textDatabase;
            this.maxDistance = maxDistance;
            this.start = start;
            this.end = end;
        }

        @Override
        protected List<String> compute() {
            if ((end - start) <= SEQUENTIAL_THRESHOLD) {
                return computeSequentially();
            } else {
                int mid = start + (end - start) / 2;

                LevenshteinTask leftTask = new LevenshteinTask(target, textDatabase, maxDistance, start, mid);
                LevenshteinTask rightTask = new LevenshteinTask(target, textDatabase, maxDistance, mid, end);

                leftTask.fork();

                List<String> rightResult = rightTask.compute();

                List<String> leftResult = leftTask.join();

                leftResult.addAll(rightResult);
                return leftResult;
            }
        }

        private List<String> computeSequentially() {
            List<String> localMatches = new ArrayList<>();
            for (int i = start; i < end; i++) {
                String word = textDatabase.get(i);
                if (word == null || word.isEmpty()) continue;

                int distance = LevenshteinAlgorithm.calculate(target, word.toLowerCase());
                if (distance <= maxDistance) {
                    localMatches.add(word);
                }
            }
            return localMatches;
        }
    }
}