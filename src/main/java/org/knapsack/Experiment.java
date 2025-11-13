package org.knapsack;

import java.util.ArrayList;
import java.util.List;

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

            boolean foundOptimal = knownOptimal > 0 &&
                    (Math.abs(best.getFitness() - knownOptimal) < 0.01);

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
}