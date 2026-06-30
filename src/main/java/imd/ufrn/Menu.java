package imd.ufrn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import imd.ufrn.concurrent.ForkJoinMatcher;
import imd.ufrn.concurrent.HybridMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTCompletableFutureMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTExecRunnableMutexMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTExecutorCallableMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTLatchMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTMutexMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTReentrantMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTSemaphoreMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTVolatileMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEBasicoMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTELatchMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEMutexMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEReentrantMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTESemaphoreMatcher;
import imd.ufrn.concurrent.PlatformThreadsExecutor.PTEVolatileMatcher;
import imd.ufrn.concurrent.SparkMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTAtomicMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTBasicoMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTCompletableFutureMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTExecRunnableSemaphoreMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTExecutorCallableMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTLatchMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTMutexMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTReentrantMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTSemaphoreMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTVolatileMatcher;
import imd.ufrn.core.BestMatcherStrategy;
import imd.ufrn.serial.SerialMatcher;
import imd.ufrn.utils.DatasetLoader;

public class Menu {
    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("=================================================");
            System.out.println("                  BEST MATCHING                  ");
            System.out.println("=================================================");

            // Escolha do Livro / Dataset
            System.out.println("\n[1] Escolha o Dataset:");
            System.out.println("0. Sair do Programa");
            System.out.println("1. Os Miseráveis (Pt-br)");
            System.out.println("2. Iracema (Pt-Br)");
            System.out.println("3. The Great Gatsby (En-US)");
            System.out.print("Opção: ");
            int bookOption = scanner.nextInt();
            scanner.nextLine();

            if (bookOption == 0) { System.out.println("Encerrando..."); break; }
            List<String> textDatabase = carregarBaseDeDados(bookOption);
            if (textDatabase == null) {
                System.out.println("Pressione ENTER para tentar novamente...");
                scanner.nextLine();
                continue;
            }

            // Parâmetros de Busca
            String targetWord = "morte"; // default
            System.out.print("\n[2] Digite a palavra alvo (ex: morte): ");
            targetWord = scanner.nextLine();

            int maxDistance = 2; // default
            System.out.print("[3] Digite a distância máxima de Levenshtein (ex: 2): ");
            maxDistance = scanner.nextInt();

            // Escolha do Modelo de Execução
            System.out.println("\n[4] Escolha o Modelo de Concorrência:");
            System.out.println("0. <-- Voltar ao Menu Principal");
            System.out.println("1. Serial (Baseline de 1 Thread)");
            System.out.println("2. Platform Threads (SO)");
            System.out.println("3. Platform Threads (Pool de Threads Executor Antigo)");
            System.out.println("4. Virtual Threads");
            System.out.println("5. Híbrido - Virtual Threads + Platform Threads");
            System.out.println("6. Tecnologias da Unidade 3 (Executors, ForkJoin, CompletableFuture, Spark)");
            System.out.println("7. Executar TODAS as versões e Exportar para CSV (Benchmark Automático)");
            System.out.print("Opção: ");
            int modelOption = scanner.nextInt();

            if (modelOption == 7) {
                executarTodasEGerarCSV(targetWord, textDatabase, maxDistance);
                System.out.println("\nPressione ENTER para voltar ao menu principal...");
                scanner.nextLine();
                scanner.nextLine();
                continue;
            }

            BestMatcherStrategy strategy = null;
            int stratOption = 0;

            switch (modelOption) {
                case 1 -> strategy = new SerialMatcher();
                case 2 -> {
                    System.out.println("\n--- Estratégias para Platform Threads (SO) ---");
                    System.out.println("0. <-- Voltar ao Menu Principal");
                    System.out.println("1. Básica (Com Race Condition)");
                    System.out.println("2. Mutex (synchronized)");
                    System.out.println("3. ReentrantLock");
                    System.out.println("4. Atomic (ConcurrentLinkedQueue)");
                    System.out.println("5. Volatile");
                    System.out.println("6. Semáforo");
                    System.out.println("7. CountDownLatch");
                    System.out.print("Opção: ");
                    stratOption = scanner.nextInt();
                    if (stratOption == 0) continue;
                    switch (stratOption) {
                        case 1 -> strategy = new PTBasicoMatcher();
                        case 2 -> strategy = new PTMutexMatcher();
                        case 3 -> strategy = new PTReentrantMatcher();
                        case 4 -> strategy = new PTAtomicMatcher();
                        case 5 -> strategy = new PTVolatileMatcher();
                        case 6 -> strategy = new PTSemaphoreMatcher();
                        case 7 -> strategy = new PTLatchMatcher();
                    }
                }
                case 3 -> {
                    System.out.println("\n--- Estratégias para Platform Threads (Pool de Threads Executor Antigo) ---");
                    System.out.println("0. <-- Voltar ao Menu Principal");
                    System.out.println("1. Básica (Com Race Condition)");
                    System.out.println("2. Mutex (synchronized)");
                    System.out.println("3. ReentrantLock");
                    System.out.println("4. Atomic (ConcurrentLinkedQueue)");
                    System.out.println("5. Volatile");
                    System.out.println("6. Semáforo");
                    System.out.println("7. CountDownLatch");
                    System.out.print("Opção: ");
                    stratOption = scanner.nextInt();
                    if (stratOption == 0) continue;
                    switch (stratOption) {
                        case 1 -> strategy = new PTEBasicoMatcher();
                        case 2 -> strategy = new PTEMutexMatcher();
                        case 3 -> strategy = new PTEReentrantMatcher();
                        case 4 -> strategy = new PTEAtomicMatcher();
                        case 5 -> strategy = new PTEVolatileMatcher();
                        case 6 -> strategy = new PTESemaphoreMatcher();
                        case 7 -> strategy = new PTELatchMatcher();
                    }
                }
                case 4 -> {
                    System.out.println("\n--- Estratégias para Virtual Threads ---");
                    System.out.println("0. <-- Voltar ao Menu Principal");
                    System.out.println("1. Básica (Com Race Condition)");
                    System.out.println("2. Mutex (synchronized) *Provável Pinning*");
                    System.out.println("3. ReentrantLock (Seguro contra Pinning)");
                    System.out.println("4. Atomic (ConcurrentLinkedQueue)");
                    System.out.println("5. Volatile");
                    System.out.println("6. Semáforo");
                    System.out.println("7. CountDownLatch");
                    System.out.print("Opção: ");
                    stratOption = scanner.nextInt();
                    if (stratOption == 0) continue;
                    switch (stratOption) {
                        case 1 -> strategy = new VTBasicoMatcher();
                        case 2 -> strategy = new VTMutexMatcher();
                        case 3 -> strategy = new VTReentrantMatcher();
                        case 4 -> strategy = new VTAtomicMatcher();
                        case 5 -> strategy = new VTVolatileMatcher();
                        case 6 -> strategy = new VTSemaphoreMatcher();
                        case 7 -> strategy = new VTLatchMatcher();
                    }
                }
                case 5 -> {
                    strategy = new HybridMatcher("src/main/resources/Os-Miseraveis-clean.txt");
                }
                case 6 -> {
                    System.out.println("\n--- Estratégias da Unidade 3 ---");
                    System.out.println("0. <-- Voltar ao Menu Principal");
                    System.out.println("1. PT Executor (Runnable)");
                    System.out.println("2. VT Executor (Runnable)");
                    System.out.println("3. PT Executor (Callable + Future)");
                    System.out.println("4. VT Executor (Callable + Future)");
                    System.out.println("5. Fork/Join Framework");
                    System.out.println("6. PT CompletableFuture");
                    System.out.println("7. VT CompletableFuture");
                    System.out.println("8. Apache Spark");
                    System.out.print("Opção: ");
                    stratOption = scanner.nextInt();
                    if (stratOption == 0) continue;
                    switch (stratOption) {
                        case 1 -> strategy = new PTExecRunnableMutexMatcher();
                        case 2 -> strategy = new VTExecRunnableSemaphoreMatcher();
                        case 3 -> strategy = new PTExecutorCallableMatcher();
                        case 4 -> strategy = new VTExecutorCallableMatcher();
                        case 5 -> strategy = new ForkJoinMatcher();
                        case 6 -> strategy = new PTCompletableFutureMatcher();
                        case 7 -> strategy = new VTCompletableFutureMatcher();
                        case 8 -> strategy = new SparkMatcher();
                    }
                }
            }

            if (strategy == null) {
                System.out.println("Estratégia não definida ou opção inválida.");
                continue;
            }
            
            try {
                executarMatcher(strategy, targetWord, textDatabase, maxDistance);
                System.out.println("Pressione ENTER para voltar ao menu principal...");
                scanner.nextLine();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Ocorreu um erro durante a execução: " + e.getMessage());
                e.printStackTrace(System.err);
            } 
        }
    }

    private static void executarMatcher(BestMatcherStrategy strategy, String target, List<String> textDatabase, int maxDistance) {
        System.out.println("\nCarregando base de dados...");
        System.out.println("Total de linhas carregadas: " + textDatabase.size());

        System.out.println("\nIniciando processamento...");
        long startTime = System.currentTimeMillis();
        
        List<String> matches = strategy.findMatches(target, textDatabase, maxDistance);
        
        long endTime = System.currentTimeMillis();

        System.out.println("\n=================================================");
        System.out.println("                   RESULTADOS                    ");
        System.out.println("=================================================");
        System.out.println("Palavras similares encontradas : " + matches.size());
        System.out.println("Tempo de Execução              : " + (endTime - startTime) + " ms");
        System.out.println("=================================================\n");
    }

    private static void executarTodasEGerarCSV(String target, List<String> textDatabase, int maxDistance) {
        System.out.println("\nIniciando benchmark automático de TODAS as estratégias...");
        
        // Usamos LinkedHashMap para garantir a ordem de inserção no CSV
        Map<String, BestMatcherStrategy> benchmarkList = new LinkedHashMap<>();
        
        // Baseline
        benchmarkList.put("Serial", new SerialMatcher());
        
        // Platform Threads (Principais representativas)
        benchmarkList.put("PT Básica (Race Condition)", new PTBasicoMatcher());
        benchmarkList.put("PT Mutex", new PTMutexMatcher());
        benchmarkList.put("PT Semaphore", new PTSemaphoreMatcher());
        
        // Virtual Threads (Principais representativas)
        benchmarkList.put("VT Básica (Race Condition)", new VTBasicoMatcher());
        benchmarkList.put("VT Semaphore", new VTSemaphoreMatcher());
        
        // Híbrida
        benchmarkList.put("Híbrida (Produtor-Consumidor)", new HybridMatcher("src/main/resources/Os-Miseraveis-clean.txt"));
        
        // Estruturas da Unidade 3
        benchmarkList.put("PT Executor (Runnable)", new PTExecRunnableMutexMatcher());
        benchmarkList.put("VT Executor (Runnable)", new VTExecRunnableSemaphoreMatcher());
        benchmarkList.put("PT Executor (Callable)", new PTExecutorCallableMatcher());
        benchmarkList.put("VT Executor (Callable)", new VTExecutorCallableMatcher());
        benchmarkList.put("ForkJoin Framework", new ForkJoinMatcher());
        benchmarkList.put("PT CompletableFuture", new PTCompletableFutureMatcher());
        benchmarkList.put("VT CompletableFuture", new VTCompletableFutureMatcher());
        benchmarkList.put("Apache Spark", new SparkMatcher());

        String csvFileName = "resultados_benchmark.csv";

        try (FileWriter writer = new FileWriter(csvFileName)) {
            writer.append("Estrategia,TempoExecucao_ms,MatchesEncontrados\n");

            for (Map.Entry<String, BestMatcherStrategy> entry : benchmarkList.entrySet()) {
                String nome = entry.getKey();
                BestMatcherStrategy strategy = entry.getValue();

                System.out.print("Testando [" + nome + "] ... ");

                try {
                    long startTime = System.currentTimeMillis();
                    List<String> matches = strategy.findMatches(target, textDatabase, maxDistance);
                    long endTime = System.currentTimeMillis();
                    
                    long tempoDecorrido = endTime - startTime;
                    writer.append(nome).append(",")
                          .append(String.valueOf(tempoDecorrido)).append(",")
                          .append(String.valueOf(matches.size())).append("\n");
                          
                    System.out.println("OK (" + tempoDecorrido + " ms | " + matches.size() + " matches)");
                } catch (Exception e) {
                    writer.append(nome).append(",ERRO,0\n");
                    System.out.println("FALHOU (" + e.getMessage() + ")");
                }

                // Invoca o GC manualmente entre os testes para tentar limpar a Heap e isolar as medições
                System.gc();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
            
            System.out.println("\n✅ Benchmark concluído com sucesso!");
            System.out.println("📊 Arquivo gerado: " + csvFileName);

        } catch (IOException e) {
            System.out.println("Erro crítico ao escrever o CSV: " + e.getMessage());
        }
    }

    private static List<String> carregarBaseDeDados(int bookOption) {
        String caminho = "";
        switch (bookOption) {
            case 1 -> caminho = "Os-Miseraveis-clean.txt";
            case 2 -> caminho = "iracema_clean.txt";
            case 3 -> caminho = "theGreatGatsby.txt";
            default -> {
                System.out.println("Opção de livro inválida!");
                return null;
            }
        }
        try {
            return DatasetLoader.loadTextDatabase("src/main/resources/" + caminho);
        } catch (IOException e) {
            System.out.println("Ficheiro não encontrado: " + caminho);
            return null;
        }
    }
}