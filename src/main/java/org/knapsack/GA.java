package org.knapsack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Implementação do Algoritmo Genético para o Problema da Mochila
 */
public class GA
{
    private int populationSize;
    private double crossoverRate;
    private double mutationRate;
    private int elitismCount;
    private int maxGenerations;
    private double convergenceThreshold;

    private List<Chromosome> population;
    private List<Item> items;
    private double capacity;
    private RandomGenerator random;

    // Estatísticas
    private double[] bestFitnessHistory;
    private double[] averageFitnessHistory;
    private double[] worstFitnessHistory;
    private double[] diversityHistory; // <-- NOVO: Histórico de diversidade
    private int convergenceGeneration;

    public GA(int populationSize, double crossoverRate, double mutationRate,
              int elitismCount, int maxGenerations, double convergenceThreshold) {
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.elitismCount = elitismCount;
        this.maxGenerations = maxGenerations;
        this.convergenceThreshold = convergenceThreshold;
        this.random = RandomGenerator.getDefault();
    }

    /**
     * Executa o algoritmo genético
     */
    public Chromosome run(List<Item> items, double capacity)
    {
        this.items = new ArrayList<>(items);
        this.capacity = capacity;
        this.bestFitnessHistory = new double[maxGenerations];
        this.averageFitnessHistory = new double[maxGenerations];
        this.worstFitnessHistory = new double[maxGenerations];
        this.diversityHistory = new double[maxGenerations]; // <-- NOVO: Inicializa array
        this.convergenceGeneration = -1;

        initializePopulation();

        // Evolução
        for (int generation = 0; generation < maxGenerations; generation++) {
            // Avalia população
            evaluatePopulation(generation);

            // Verifica convergência
            if (checkConvergence(generation)) {
                convergenceGeneration = generation;
                break;
            }

            // Cria nova população
            List<Chromosome> newPopulation = new ArrayList<>();

            // Elitismo: mantém os melhores indivíduos
            applyElitism(newPopulation);

            // Preenche o restante da população
            while (newPopulation.size() < populationSize) {
                // Seleção por torneio
                Chromosome parent1 = tournamentSelection(3);
                Chromosome parent2 = tournamentSelection(3);

                Chromosome[] offspring;
                if (random.nextDouble() < crossoverRate) {
                    offspring = crossover(parent1, parent2);
                } else {
                    offspring = new Chromosome[]{parent1, parent2};
                }

                // Mutação
                for (Chromosome child : offspring) {
                    mutate(child);
                    child.calculateFitness();
                    if (newPopulation.size() < populationSize) {
                        newPopulation.add(child);
                    }
                }
            }

            population = newPopulation;
        }

        // Retorna a melhor solução encontrada
        return getBestChromosome();
    }

    /**
     * QUESTÃO 1: Metodologia de seleção - Implementação por Torneio
     */
    private Chromosome tournamentSelection(int tournamentSize) {
        Chromosome best = null;

        for (int i = 0; i < tournamentSize; i++) {
            Chromosome candidate = population.get(random.nextInt(populationSize));
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }

        return best;
    }

    /**
     * Operador de crossover - ponto único
     */
    private Chromosome[] crossover(Chromosome parent1, Chromosome parent2)
    {
        boolean[] genes1 = parent1.getGenes();
        boolean[] genes2 = parent2.getGenes();
        boolean[] child1 = new boolean[genes1.length];
        boolean[] child2 = new boolean[genes2.length];

        int crossoverPoint = random.nextInt(genes1.length);

        for (int i = 0; i < genes1.length; i++) {
            if (i < crossoverPoint) {
                child1[i] = genes1[i];
                child2[i] = genes2[i];
            } else {
                child1[i] = genes2[i];
                child2[i] = genes1[i];
            }
        }

        return new Chromosome[]{
                new Chromosome(child1, items, capacity),
                new Chromosome(child2, items, capacity)
        };
    }

    /**
     * Operador de mutação - bit flip
     */
    private void mutate(Chromosome chromosome) {
        boolean[] genes = chromosome.getGenes();

        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationRate) {
                genes[i] = !genes[i];
            }
        }
    }

    /**
     * Aplica elitismo - mantém os melhores indivíduos
     */
    private void applyElitism(List<Chromosome> newPopulation) {
        population.sort(Comparator.comparingDouble(Chromosome::getFitness).reversed());

        for (int i = 0; i < elitismCount && i < populationSize; i++) {
            newPopulation.add(new Chromosome(population.get(i).getGenes(), items, capacity));
        }
    }

    private void initializePopulation() {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Chromosome(items, capacity);
            chromosome.initialize(random);
            population.add(chromosome);
        }
    }

    private void evaluatePopulation(int generation) {
        double bestFitness = Double.MIN_VALUE;
        double worstFitness = Double.MAX_VALUE;
        double totalFitness = 0;

        for (Chromosome chrom : population) {
            double fitness = chrom.getFitness();
            bestFitness = Math.max(bestFitness, fitness);
            worstFitness = Math.min(worstFitness, fitness);
            totalFitness += fitness;
        }

        bestFitnessHistory[generation] = bestFitness;
        averageFitnessHistory[generation] = totalFitness / populationSize;
        worstFitnessHistory[generation] = worstFitness;
        diversityHistory[generation] = calculateDiversity();
    }

    /**
     * Calcula a diversidade genética da população (Distância de Hamming média)
     */
    private double calculateDiversity() {
        double totalDistance = 0;
        int comparisons = 0;

        // Compara cada cromossomo com todos os outros
        for (int i = 0; i < population.size(); i++) {
            for (int j = i + 1; j < population.size(); j++) {
                totalDistance += hammingDistance(population.get(i), population.get(j));
                comparisons++;
            }
        }

        if (comparisons == 0) return 0;

        // Retorna a distância média percentual
        double averageDistance = totalDistance / comparisons;
        return (averageDistance / items.size()) * 100.0;
    }

    /**
     * Calcula a Distância de Hamming entre dois cromossomos
     */
    private double hammingDistance(Chromosome c1, Chromosome c2) {
        double distance = 0;
        boolean[] genes1 = c1.getGenes();
        boolean[] genes2 = c2.getGenes();
        for (int i = 0; i < genes1.length; i++) {
            if (genes1[i] != genes2[i]) {
                distance++;
            }
        }
        return distance;
    }


    private boolean checkConvergence(int generation) {
        if (generation < 10) return false;

        // Verifica se houve pouca melhoria nas últimas 10 gerações
        double improvement = bestFitnessHistory[generation] - bestFitnessHistory[generation - 10];
        return improvement < convergenceThreshold;
    }

    private Chromosome getBestChromosome() {
        return population.stream()
                .max(Comparator.comparingDouble(Chromosome::getFitness))
                .orElse(population.get(0));
    }

    // Getters para estatísticas
    public double[] getBestFitnessHistory() { return bestFitnessHistory; }
    public double[] getAverageFitnessHistory() { return averageFitnessHistory; }
    public double[] getWorstFitnessHistory() { return worstFitnessHistory; }
    public double[] getDiversityHistory() { return diversityHistory; } // <-- NOVO: Getter
    public int getConvergenceGeneration() { return convergenceGeneration; }
    public int getMaxGenerations() { return maxGenerations; }
}