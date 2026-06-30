package imd.ufrn.concurrent;

import java.util.List;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import imd.ufrn.core.BestMatcherStrategy;

public class SparkMatcher implements BestMatcherStrategy {

    @Override
    public List<String> findMatches(String target, List<String> textDatabase, int maxDistance) {
        String targetLower = target.toLowerCase();

        SparkSession spark = SparkSession.builder()
                .appName("BestMatchingSpark")
                .master("local[*]")
                .getOrCreate();

        try {
            Dataset<Row> dataset = spark.read().text("src/main/resources/Os-Miseraveis-clean.txt");

            Dataset<Row> matchesDataset = dataset.filter(
                functions.levenshtein(functions.lower(functions.col("value")), functions.lit(targetLower))
                         .leq(maxDistance)
            );

            return matchesDataset.as(org.apache.spark.sql.Encoders.STRING()).collectAsList();
            
        } finally {
            spark.stop();
        }
    }
}