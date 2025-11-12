package org.knapsack;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe para executar experimentos e coletar estatísticas
 */
public class Experiment
{
    private static final int NUM_RUNS = Config.NUM_EXECUCOES;

    public static class ExperimentResult
    {
        public final double bestFitness;
        public final double averageFitness;
        public final double worstFitness;
        public final double executionTimeMs;
        public final int convergenceGeneration;
        public final boolean foundOptimal;

        public ExperimentResult(double bestFitness, double averageFitness,
                                double worstFitness, double executionTimeMs,
                                int convergenceGeneration, boolean foundOptimal) {
            this.bestFitness = bestFitness;
            this.averageFitness = averageFitness;
            this.worstFitness = worstFitness;
            this.executionTimeMs = executionTimeMs;
            this.convergenceGeneration = convergenceGeneration;
            this.foundOptimal = foundOptimal;
        }
    }

    public static class StatisticalSummary
    {
        public final double mean;
        public final double standardDeviation;
        public final double min;
        public final double max;
        public final double confidenceInterval95;

        public StatisticalSummary(double[] values)
        {
            this.mean = calculateMean(values);
            this.standardDeviation = calculateStandardDeviation(values, mean);
            this.min = calculateMin(values);
            this.max = calculateMax(values);
            this.confidenceInterval95 = 1.96 * standardDeviation / Math.sqrt(values.length);
        }

        private double calculateMean(double[] values) {
            double sum = 0;
            for (double value : values) {
                sum += value;
            }
            return sum / values.length;
        }

        private double calculateStandardDeviation(double[] values, double mean) {
            double sumSquaredDifferences = 0;
            for (double value : values) {
                sumSquaredDifferences += Math.pow(value - mean, 2);
            }
            return Math.sqrt(sumSquaredDifferences / values.length);
        }

        private double calculateMin(double[] values) {
            double min = Double.MAX_VALUE;
            for (double value : values) {
                if (value < min) min = value;
            }
            return min;
        }

        private double calculateMax(double[] values) {
            double max = Double.MIN_VALUE;
            for (double value : values) {
                if (value > max) max = value;
            }
            return max;
        }

        @Override
        public String toString() {
            return String.format("""
                    Média: %.2f
                    Desvio Padrão: %.2f
                    Mínimo: %.2f
                    Máximo: %.2f
                    Intervalo Confiança 95%%: ±%.2f
                    """, mean, standardDeviation, min, max, confidenceInterval95);
        }
    }

    /**
     * Executa múltiplas runs para um cenário específico
     */
    public static List<ExperimentResult> executeRuns(List<Item> items, double capacity)
    {
        return executeRuns(items, capacity, -1);
    }

    public static List<ExperimentResult> executeRuns(List<Item> items, double capacity,
                                                     double knownOptimal)
    {
        List<ExperimentResult> results = new ArrayList<>();

        for (int run = 0; run < NUM_RUNS; run++)
        {
            GA ga = new GA(
                    Config.N_CROMOSSOMOS,
                    Config.TAXA_CROSSOVER,
                    Config.TAXA_MUTACAO,
                    Config.ELITISMO,
                    Config.GERACOES,
                    0.001
            );

            long startTime = System.nanoTime();
            Chromosome best = ga.run(items, capacity);
            long endTime = System.nanoTime();

            double executionTimeMs = (endTime - startTime) / 1_000_000.0;

            // Compara o melhor fitness (arredondado) com o ótimo conhecido
            boolean foundOptimal = knownOptimal > 0 &&
                    (Math.abs(best.getFitness() - knownOptimal) < 0.01);

            // Encontrar a última geração com dados válidos
            int lastGen = ga.getConvergenceGeneration() > 0 ?
                    ga.getConvergenceGeneration() : ga.getMaxGenerations() - 1;
            lastGen = Math.min(lastGen, ga.getMaxGenerations() - 1);

            results.add(new ExperimentResult(
                    best.getFitness(),
                    ga.getAverageFitnessHistory()[lastGen],
                    ga.getWorstFitnessHistory()[lastGen],
                    executionTimeMs,
                    ga.getConvergenceGeneration(),
                    foundOptimal
            ));
        }

        return results;
    }

    /**
     * QUESTÃO 2: Experimento com diferentes tamanhos de mochila
     */
    public static void runCapacityExperiments(List<Item> items, double originalCapacity)
    {
        System.out.println("=== QUESTÃO 2: EXPERIMENTOS COM TAMANHO DA MOCHILA ===");

        double[] capacities = {
                originalCapacity * 0.5,   // 50% menor
                originalCapacity * 0.75,  // 25% menor
                originalCapacity,         // original
                originalCapacity * 1.5,   // 50% maior
                originalCapacity * 2.0    // 100% maior
        };

        String[] labels = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};

        for (int i = 0; i < capacities.length; i++) {
            System.out.println("\n--- Capacidade: " + labels[i] + " (" + capacities[i]
                    + ") ---");

            List<ExperimentResult> results = executeRuns(items, capacities[i]);

            // Estatísticas do fitness
            double[] fitnessValues = results.stream()
                    .mapToDouble(r -> r.bestFitness)
                    .toArray();
            StatisticalSummary fitnessStats = new StatisticalSummary(fitnessValues);

            // Estatísticas do tempo
            double[] timeValues = results.stream()
                    .mapToDouble(r -> r.executionTimeMs)
                    .toArray();
            StatisticalSummary timeStats = new StatisticalSummary(timeValues);

            System.out.println("FITNESS:");
            System.out.println(fitnessStats);

            System.out.println("TEMPO (ms):");
            System.out.println(timeStats);

            // Taxa de sucesso (soluções válidas)
            long validSolutions = results.stream()
                    .filter(r -> r.bestFitness > 0)
                    .count();
            System.out.printf("Taxa de soluções válidas: %.1f%%%n",
                    (validSolutions * 100.0 / NUM_RUNS));
        }
    }

    /**
     * QUESTÃO 3: Experimento com diferentes conjuntos de itens
     */
    public static void runItemsExperiments(List<Item> originalItems, double capacity)
    {
        System.out.println("\n=== QUESTÃO 3: EXPERIMENTOS COM CONJUNTOS DE ITENS ===");

        // Diferentes conjuntos de itens
        List<List<Item>> itemSets = ExperimentRunner.createItemSets();

        String[] setLabels = {
                "Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"
        };

        for (int i = 0; i < itemSets.size(); i++) {
            System.out.println("\n--- Conjunto: " + setLabels[i] + " (" +
                    itemSets.get(i).size() + " itens) ---");

            List<ExperimentResult> results = executeRuns(itemSets.get(i), capacity);

            // Estatísticas do fitness
            double[] fitnessValues = results.stream()
                    .mapToDouble(r -> r.bestFitness)
                    .toArray();
            StatisticalSummary fitnessStats = new StatisticalSummary(fitnessValues);

            // Estatísticas do tempo
            double[] timeValues = results.stream()
                    .mapToDouble(r -> r.executionTimeMs)
                    .toArray();
            StatisticalSummary timeStats = new StatisticalSummary(timeValues);

            System.out.println("FITNESS:");
            System.out.println(fitnessStats);

            System.out.println("TEMPO (ms):");
            System.out.println(timeStats);

            // Média de gerações para convergência
            double avgGenerations = results.stream()
                    .mapToInt(r -> r.convergenceGeneration > 0 ?
                            r.convergenceGeneration : Config.GERACOES)
                    .average()
                    .orElse(0);
            System.out.printf("Média de gerações para convergência: %.1f%n", avgGenerations);
        }
    }
}