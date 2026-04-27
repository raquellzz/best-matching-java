# Best-Matching Algorithm (Java Implementation)

This repository contains the Java implementation of a "Best-Matching" search system using the Levenshtein distance algorithm. It was developed as part of the **DIM0124 - Concurrent Programming** course at the Federal University of Rio Grande do Norte (UFRN).

## Objective
The goal is to analyze the performance and behavior of different execution models and synchronization primitives in Java 21+, comparing:
* **Serial Execution**: Single-threaded baseline.
* **Platform Threads**: OS-level threads with various synchronization strategies (Mutex, Semaphores, Atomics).
* **Virtual Threads**: High-throughput lightweight threads introduced in Project Loom.
* **Garbage Collection**: Performance analysis across different GC implementations (G1, ZGC, Parallel).

## Requirements
* Java Development Kit (JDK) 21 or higher.
* Apache Maven.

## How to Run
### Building the Project
Navigate to the project root and run:
```bash
mvn clean package
```
### Running Benchmarks (JMH)
To get precise performance metrics, use the generated benchmark JAR:

```bash
java -jar target/benchmarks.jar -rf csv -rff resultados_benchmark.csv
```
### Running the Application
To run the main application logic:

```bash
java -cp target/best-matching-java-1.0-SNAPSHOT.jar imd.ufrn.App
```