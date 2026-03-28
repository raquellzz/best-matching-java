package imd.ufrn.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatasetLoader {

    /**
     * Lê um arquivo de texto e o transforma em uma lista de palavras na memória.
     *
     * @param filePath O caminho para o arquivo .txt
     * @return Uma lista contendo todas as palavras do texto, limpas e em minúsculo
     * @throws IOException Se houver erro na leitura do arquivo
     */
    public static List<String> loadTextDatabase(String filePath) throws IOException {
        List<String> wordsDatabase = new ArrayList<>();
        
        List<String> lines = Files.readAllLines(Path.of(filePath));


        if (lines.isEmpty()) {
            throw new IOException("O arquivo está vazio: " + filePath);
        }
        
        Pattern wordPattern = Pattern.compile("[a-zA-ZÀ-ÿ]+");
        
        
        for (String line : lines) {
            Matcher matcher = wordPattern.matcher(line);
            while (matcher.find()) {
                wordsDatabase.add(matcher.group().toLowerCase());
            }
        }
        return wordsDatabase;
    }
}
