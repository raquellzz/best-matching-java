package imd.ufrn.jcstress;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

public class ListAdditionStressTest {
    // TESTE 1: A VERSÃO BÁSICA (COM CONDIÇÃO DE CORRIDA)
    @JCStressTest
    @Outcome(id = "1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Race Condition! Uma palavra foi perdida.")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Sucesso temporário (Sorte).")
    @State
    public static class UnsafeAddition {
        int contadorDePalavras = 0; // Simula o size() do ArrayList

        @Actor
        public void thread1() {
            contadorDePalavras++; // Simula thread 1 achando "morte" e dando add()
        }

        @Actor
        public void thread2() {
            contadorDePalavras++; // Simula thread 2 achando "norte" e dando add()
        }

        @Arbiter
        public void arbiter(I_Result r) {
            r.r1 = contadorDePalavras; // Verifica o estado final
        }
    }

    // TESTE 2: A VERSÃO MUTEX (SEGURA)
    @JCStressTest
    @Outcome(id = "1", expect = Expect.FORBIDDEN, desc = "Race Condition! Ocorreu um erro no cadeado.")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Thread-Safe! Nenhuma palavra perdida.")
    @State
    public static class SafeAddition {
        int contadorDePalavras = 0;

        @Actor
        public void thread1() {
            synchronized (this) {
                contadorDePalavras++;
            }
        }

        @Actor
        public void thread2() {
            synchronized (this) {
                contadorDePalavras++;
            }
        }

        @Arbiter
        public void arbiter(I_Result r) {
            r.r1 = contadorDePalavras;
        }
    }
}