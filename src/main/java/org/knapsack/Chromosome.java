package org.knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Representa um cromossomo (solução) para o problema da mochila
 */
public class Chromosome
{
    private final boolean[] genes; // true = item incluído, false = excluído
    private final List<Item> items;
    private final double capacity;
    private double fitness;
    private double totalWeight;
    private boolean fitnessCalculated;

    public Chromosome(List<Item> items, double capacity) {
        this.items = new ArrayList<>(items);
        this.capacity = capacity;
        this.genes = new boolean[items.size()];
        this.fitnessCalculated = false;
    }

    public Chromosome(boolean[] genes, List<Item> items, double capacity) {
        this.genes = genes.clone();
        this.items = new ArrayList<>(items);
        this.capacity = capacity;
        this.fitnessCalculated = false;
    }

    // Inicializa aleatoriamente
    public void initialize(RandomGenerator random) {
        for (int i = 0; i < genes.length; i++) {
            genes[i] = random.nextBoolean();
        }
        calculateFitness();
    }

    public void calculateFitness() {
        double totalValue = 0;
        totalWeight = 0;

        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                totalValue += items.get(i).getValue();
                totalWeight += items.get(i).getWeight();
            }
        }

        // Penaliza soluções que excedem a capacidade
        if (totalWeight > capacity) {

            // --- ESTA É A MUDANÇA (OPÇÃO 1) ---
            double excess = totalWeight - capacity;
            // Penalidade 5x mais forte. Se exceder 20% (0.2), a penalidade já é 100% (0.2 * 5)
            double penalty = (excess / capacity) * 5.0;
            fitness = Math.max(0, totalValue * (1 - penalty));
            // --- FIM DA MUDANÇA ---

        } else {
            fitness = totalValue;
        }

        fitnessCalculated = true;
    }

    public double getFitness() {
        if (!fitnessCalculated) {
            calculateFitness();
        }
        return fitness;
    }

    public double getTotalWeight() {
        if (!fitnessCalculated) {
            calculateFitness();
        }
        return totalWeight;
    }

    public boolean[] getGenes() { return genes.clone(); }
    public int getSize() { return genes.length; }
    public boolean isValid() { return getTotalWeight() <= capacity; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Fitness: %.2f, Peso: %.2f/%.2f, Itens: ",
                fitness, totalWeight, capacity));

        List<String> selectedItems = new ArrayList<>();
        for (int i = 0; i < genes.length; i++) {
            if (genes[i]) {
                selectedItems.add(items.get(i).getName());
            }
        }
        sb.append(selectedItems);
        return sb.toString();
    }
}