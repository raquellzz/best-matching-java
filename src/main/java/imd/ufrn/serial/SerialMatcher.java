package imd.ufrn.serial;

import java.util.ArrayList;
import java.util.List;

import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.core.LevenshteinAlgorithm;

public class SerialMatcher implements BestMatcherStrategy {
    
    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        List<String> matches = new ArrayList<>();
        String targetLower = target.toLowerCase();
        for (String word : textDatabase) {
            if(word == null || word.isEmpty()) continue;
            int distance = LevenshteinAlgorithm.calculate(target, word.toLowerCase());
            if (distance <= maxDistance) {
                matches.add(word);
            }
        }
        return matches;
    }
    
}
