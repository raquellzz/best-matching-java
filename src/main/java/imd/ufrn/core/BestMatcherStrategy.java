package imd.ufrn.core;

import java.util.List;

public interface BestMatcherStrategy {
    
    /**
     * Busca palavras similares no banco de texto.
     *
     * @param target A palavra que estamos procurando (ex: "gato")
     * @param textDatabase A lista completa contendo todas as palavras do arquivo de texto
     * @param maxDistance A distância máxima de Levenshtein permitida (ex: 2)
     * @return Uma lista com todas as palavras que deram "match"
     */
    List<String> findMatches(String target, List<String> textDatabase, int maxDistance);
}
