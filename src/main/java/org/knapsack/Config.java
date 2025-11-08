package org.knapsack;

/**
 * Arquivo de configuração - Altere aqui os parâmetros facilmente
 */
public class Config {

    // Itens: [peso, valor]
    public static final double[][] PESOS_E_VALORES = {
            {4, 30}, {8, 10}, {8, 30}, {25, 75},
            {2, 10}, {50, 100}, {6, 300}, {12, 50},
            {100, 400}, {8, 300}
    };

    // Capacidade máxima da mochila
    public static final double PESO_MAXIMO = 100;

    // Parâmetros do algoritmo genético
    public static final int N_CROMOSSOMOS = 150;
    public static final int GERACOES = 10000;
    public static final double TAXA_CROSSOVER = 0.85;
    public static final double TAXA_MUTACAO = 0.03;
    public static final int ELITISMO = 3;

    // Número de execuções para estatísticas
    public static final int NUM_EXECUCOES = 30;
}