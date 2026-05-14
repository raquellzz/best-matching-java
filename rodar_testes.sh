#!/bin/bash

# Lista de todas as suas estratégias
ESTRATEGIAS=("Serial" "PTBasico" "PTMutex" "PTAtomic" "PTReentrant" "PTSemaphore" "PTLatch" "VTBasico" "VTMutex" "VTAtomic" "VTReentrant" "VTSemaphore" "VTLatch" "VTVolatile" "Hybrid")

# Lista de GCs
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
        
        # Monta as variaveis de ambiente e roda o JMeter usando o CAMINHO ABSOLUTO
        GC_ALGO="-XX:+$GC" JVM_ARGS="-XX:StartFlightRecording=duration=60s,filename=$JFR_FILE" $JMETER_EXEC -n -t $JMX_FILE -l $JTL_FILE -Jestrategia=$STRATEGY
        
        echo " Teste $STRATEGY com $GC_NAME finalizado!"
        sleep 2 # Pausa para a JVM respirar antes do proximo teste
    done
done

echo "TODOS OS TESTES FORAM FINALIZADOS COM SUCESSO!"