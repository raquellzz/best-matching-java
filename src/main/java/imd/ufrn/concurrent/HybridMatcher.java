package imd.ufrn.concurrent;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class HybridMatcher implements BestMatcherStrategy {
    private static final String POISON_PILL = "FIM_DO_ARQUIVO";
    private final String filePath;

    public HybridMatcher(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<String> findMatches(String target, List<String> ignoredDatabase ,int maxDistance) {
        String targetLower = target.toLowerCase();
        List<String> matches = new CopyOnWriteArrayList<>();

        BlockingQueue<String> workQueue = new java.util.concurrent.LinkedBlockingQueue<>();
        int numCores = Runtime.getRuntime().availableProcessors();
        CountDownLatch latch = new CountDownLatch(numCores);

        ExecutorService executorCPU = Executors.newFixedThreadPool(numCores);
        for (int i = 0; i < numCores; i++) {
            executorCPU.submit(() ->  {
                try {
                    while(true){
                        String word = workQueue.take();    
                        if(word.equals(POISON_PILL)){
                            workQueue.put(POISON_PILL);
                            break;
                        }
                        if(!word.isEmpty()){
                            int dist = LevenshteinAlgorithm.calculate(targetLower, word);
                            if(dist <= maxDistance) {
                                matches.add(word);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        Thread.ofVirtual().start(() -> {
            try(BufferedReader reader = Files.newBufferedReader(Paths.get(this.filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    workQueue.put(line);
                }
                workQueue.put(POISON_PILL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            latch.await();
            executorCPU.shutdown();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return new ArrayList<>(matches);
    }
}
