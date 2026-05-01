package imd.ufrn;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import imd.ufrn.concurrent.HybridMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTAtomicMatcher;
import imd.ufrn.concurrent.PlatformThreads.PTBasicoMatcher;
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
import imd.ufrn.concurrent.VirtualThreads.VTAtomicMatcher;
import imd.ufrn.concurrent.VirtualThreads.VTBasicoMatcher;
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
            System.out.println("3. Platform Threads (Pool de Threads Executor)");
            System.out.println("4. Virtual Threads");
            System.out.println("5. Híbrido - Virtual Threads + Platform Threads");
            System.out.print("Opção: ");
            int modelOption = scanner.nextInt();

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
                    System.out.println("\n--- Estratégias para Platform Threads (Pool de Threads Executor) ---");
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
                    HybridMatcher hybridMatcher =  new HybridMatcher("src/main/resources/Os-Miseraveis-clean.txt");
                    strategy = hybridMatcher;
                }
            }
            if (strategy == null) {
                System.out.println("Estratégia não definida ou não comentada no código.");
                continue;
            }
            
            try {
                executarMatcher(strategy, targetWord, textDatabase, maxDistance);

                System.out.println("Pressione ENTER para voltar ao menu principal...");
                scanner.nextLine();
                scanner.nextLine();
                continue;
            } catch (Exception e) {
                System.out.println("Ocorreu um erro durante a execução: " + e.getMessage());
                e.printStackTrace();
            } 
            scanner.close();
        }
    }
    private static void executarMatcher(BestMatcherStrategy strategy , String target, List<String> textDatabase, int maxDistance) {
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

    private static List<String> carregarBaseDeDados(int bookOption) {
        String caminho = "";
        switch (bookOption) {
            case 1:
                caminho = "Os-Miseraveis-clean.txt";
                break;
            case 2:
                caminho = "iracema_clean.txt";
                break;
            case 3:
                caminho = "theGreatGatsby.txt";
                break;
            default:
                System.out.println("Opção de livro inválida!");
                break;
        }
        try {
            List<String> database = DatasetLoader.loadTextDatabase("src/main/resources/" + caminho);
            return database;
        } catch (IOException e) {
            System.out.println("Ficheiro não encontrado: " + caminho);
            return null;
        }
    }
}
