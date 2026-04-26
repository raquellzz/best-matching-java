package imd.ufrn.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasetSanitizer {
    public static void main(String[] args) throws IOException {
        String content = Files.readString(Path.of("src/main/resources/Os-Miseraveis.txt"));
        
        content = Normalizer.normalize(content, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        
        content = content.toLowerCase();

        Pattern pattern = Pattern.compile("[a-z]+");
        Matcher matcher = pattern.matcher(content);
        
        StringBuilder cleanDataset = new StringBuilder();
        while (matcher.find()) {
            cleanDataset.append(matcher.group()).append("\n");
        }

        Files.writeString(Path.of("src/main/resources/Os-Miseraveis-clean.txt"), cleanDataset.toString());
        System.out.println("Dataset limpo gerado com sucesso!");
    }
}