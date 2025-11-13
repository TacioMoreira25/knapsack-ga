package org.knapsack;

public class Config {

    public static final double[][] PESOS_E_VALORES = {
            {4, 30}, {8, 10}, {8, 30}, {25, 75},
            {2, 10}, {50, 100}, {6, 300}, {12, 50},
            {100, 400}, {8, 300}
    };

    public static final double PESO_MAXIMO = 100;

    public static final double KNOWN_OPTIMAL = 830.0;

    public static final int N_CROMOSSOMOS = 150;
    public static final int GERACOES = 100;
    public static final double TAXA_CROSSOVER = 0.85;
    public static final double TAXA_MUTACAO = 0.03;
    public static final int ELITISMO = 3;

    public static final int NUM_EXECUCOES = 30;
}