#!/bin/bash

# Apenas as estratégias da Terceira Unidade (Removi o Spark, explico abaixo)
ESTRATEGIAS=("PTExecRunnableMutex" "VTExecrRunnableSemaphore" "PTExecutorCallable" "VTExecutorCallable" "ForkJoin" "PTCompletableFuture" "VTCompletableFuture" "PTConcurrentColect" "VTConcurrentColect")

GCS=("UseG1GC" "UseParallelGC" "UseZGC")

JMX_FILE="/home/raquel/git/prog-concorrente/BestMatching/best-matching-requests.jmx"
JMETER_EXEC="/home/raquel/Tools/apache-jmeter-5.6.3/bin/jmeter"

for STRATEGY in "${ESTRATEGIAS[@]}"; do
    for GC in "${GCS[@]}"; do
        
        # Nome limpo do GC (ex: tira o 'Use' e o 'GC')
        GC_NAME=${GC#Use}
        GC_NAME=${GC_NAME%GC}
        if [ "$GC" == "UseG1GC" ]; then GC_NAME="G1"; fi
        
        echo "=========================================================="
        echo " INICIANDO TESTE: Estrategia = $STRATEGY | GC = $GC_NAME"
        echo "=========================================================="
        
        # Nomes dos arquivos de saida
        JTL_FILE="resultado_${STRATEGY}_${GC_NAME}.jtl"
        JFR_FILE="profile_${STRATEGY}_${GC_NAME}.jfr"
        
        # Apaga os arquivos antigos para nao dar erro
        rm -f $JTL_FILE $JFR_FILE
        
        export GC_ALGO="-XX:+$GC"
        export JVM_ARGS="-XX:StartFlightRecording=duration=60s,filename=$JFR_FILE"
        
        $JMETER_EXEC -n -t $JMX_FILE -l $JTL_FILE -Jestrategia=$STRATEGY
        
        echo " Teste $STRATEGY com $GC_NAME finalizado!"
        sleep 5 # Pausa de 5s para o SO garantir o fechamento e a gravação limpa do JFR
    done
done

echo "TODOS OS TESTES FORAM FINALIZADOS COM SUCESSO!"