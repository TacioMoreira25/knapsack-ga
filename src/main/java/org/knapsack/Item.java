package org.knapsack;

/**
 * Representa um item que pode ser colocado na mochila
 */
public class Item
{
    private final String name;
    private final double weight;
    private final double value;

    public Item(String name, double weight, double value) {
        this.name = name;
        this.weight = weight;
        this.value = value;
    }

    // Getters
    public String getName() { return name; }
    public double getWeight() { return weight; }
    public double getValue() { return value; }

    @Override
    public String toString() {
        return String.format("%s (Peso: %.1f, Valor: %.1f)", name, weight, value);
    }
}

