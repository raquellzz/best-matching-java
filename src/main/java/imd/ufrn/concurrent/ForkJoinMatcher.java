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
        
        // Instancia o pool do Fork/Join. Por defeito, ele cria o mesmo número 
        // de threads que os núcleos do processador (Runtime.getRuntime().availableProcessors())
        try (ForkJoinPool pool = new ForkJoinPool()) {
            // Cria a tarefa raiz que engloba a totalidade do dataset (desde o índice 0 até ao tamanho total)
            LevenshteinTask rootTask = new LevenshteinTask(targetLower, textDatabase, maxDistance, 0, textDatabase.size());
            
            // O invoke bloqueia até que toda a árvore de tarefas recursivas termine e retorne o resultado
            return pool.invoke(rootTask);
        }
    }

    /**
     * Classe interna estática que representa a tarefa recursiva.
     * Estende RecursiveTask<List<String>> porque precisamos que a tarefa retorne dados.
     */
    private static class LevenshteinTask extends RecursiveTask<List<String>> {
        
        // Limiar Sequencial: O ponto em que deixamos de dividir e passamos a calcular.
        // Um dataset de 500.000 palavras / 10.000 = ~50 blocos, excelente para "Work-Stealing".
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
            // Caso Base: A quantidade de palavras neste bloco é menor ou igual ao limiar?
            if ((end - start) <= SEQUENTIAL_THRESHOLD) {
                // Se for pequeno o suficiente, resolve de forma sequencial (sem mais divisões)
                return computeSequentially();
            } else {
                // Caso Recursivo: O bloco ainda é grande, vamos dividi-lo ao meio!
                int mid = start + (end - start) / 2;

                // Cria as duas subtarefas (esquerda e direita)
                LevenshteinTask leftTask = new LevenshteinTask(target, textDatabase, maxDistance, start, mid);
                LevenshteinTask rightTask = new LevenshteinTask(target, textDatabase, maxDistance, mid, end);

                // 1. Faz o FORK da subtarefa da esquerda (coloca-a na fila para outra thread roubar/executar)
                leftTask.fork();

                // 2. Calcula a subtarefa da direita de forma síncrona na THREAD ATUAL 
                // (Isto é uma otimização fulcral do Fork/Join para não desperdiçar threads)
                List<String> rightResult = rightTask.compute();

                // 3. Faz o JOIN da subtarefa da esquerda (espera que a outra thread termine o trabalho)
                List<String> leftResult = leftTask.join();

                // 4. Combina os dois resultados parciais e devolve para cima na árvore
                leftResult.addAll(rightResult);
                return leftResult;
            }
        }

        /**
         * Método que faz o trabalho braçal da matemática, igual às versões anteriores,
         * mas estritamente isolado num estado local (Thread Confinement).
         */
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