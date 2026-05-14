package imd.ufrn.jcstress;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

public class ListAdditionStressTest {

    // 1. A VERSÃO BÁSICA (COM CONDIÇÃO DE CORRIDA) - Vai falhar!
    @JCStressTest
    @Outcome(id = "1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Race Condition! Uma palavra foi perdida.")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Sucesso temporário (Sorte).")
    @State
    public static class UnsafeAddition {
        int contadorDePalavras = 0;

        @Actor
        public void thread1() { contadorDePalavras++; }

        @Actor
        public void thread2() { contadorDePalavras++; }

        @Arbiter
        public void arbiter(I_Result r) { r.r1 = contadorDePalavras; }
    }

    // 2. A VERSÃO MUTEX (Monitor Nativo) - Vai passar!
    @JCStressTest
    @Outcome(id = "1", expect = Expect.FORBIDDEN, desc = "Race Condition! Ocorreu um erro no cadeado.")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Thread-Safe! Nenhuma palavra perdida.")
    @State
    public static class SafeMutexAddition {
        int contadorDePalavras = 0;

        @Actor
        public void thread1() { synchronized (this) { contadorDePalavras++; } }

        @Actor
        public void thread2() { synchronized (this) { contadorDePalavras++; } }

        @Arbiter
        public void arbiter(I_Result r) { r.r1 = contadorDePalavras; }
    }

    // 3. A VERSÃO ATOMIC (Não-bloqueante) - Vai passar!
    @JCStressTest
    @Outcome(id = "1", expect = Expect.FORBIDDEN, desc = "Race Condition!")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Thread-Safe! Incremento atômico com sucesso.")
    @State
    public static class SafeAtomicAddition {
        AtomicInteger contador = new AtomicInteger(0);

        @Actor
        public void thread1() { contador.incrementAndGet(); }

        @Actor
        public void thread2() { contador.incrementAndGet(); }

        @Arbiter
        public void arbiter(I_Result r) { r.r1 = contador.get(); }
    }

    // 4. A VERSÃO REENTRANT LOCK - Vai passar!
    @JCStressTest
    @Outcome(id = "1", expect = Expect.FORBIDDEN, desc = "Race Condition!")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Thread-Safe! Lock explícito funcionou.")
    @State
    public static class SafeReentrantAddition {
        int contador = 0;
        Lock lock = new ReentrantLock();

        @Actor
        public void thread1() {
            lock.lock();
            try { contador++; } finally { lock.unlock(); }
        }

        @Actor
        public void thread2() {
            lock.lock();
            try { contador++; } finally { lock.unlock(); }
        }

        @Arbiter
        public void arbiter(I_Result r) { r.r1 = contador; }
    }

    // 5. A VERSÃO SEMAPHORE (Permissão = 1 funciona como Mutex) - Vai passar!
    @JCStressTest
    @Outcome(id = "1", expect = Expect.FORBIDDEN, desc = "Race Condition!")
    @Outcome(id = "2", expect = Expect.ACCEPTABLE, desc = "Thread-Safe! Semáforo binário funcionou.")
    @State
    public static class SafeSemaphoreAddition {
        int contador = 0;
        Semaphore semaphore = new Semaphore(1); // 1 permissão = Exclusão Mútua

        @Actor
        public void thread1() {
            try { semaphore.acquire(); contador++; } 
            catch (InterruptedException e) {} 
            finally { semaphore.release(); }
        }

        @Actor
        public void thread2() {
            try { semaphore.acquire(); contador++; } 
            catch (InterruptedException e) {} 
            finally { semaphore.release(); }
        }

        @Arbiter
        public void arbiter(I_Result r) { r.r1 = contador; }
    }
}