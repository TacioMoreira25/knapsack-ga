package org.knapsack;

import java.util.ArrayList;
import java.util.List;

public class ExperimentRunner {

    public static GA runAllExperiments() {
        System.out.println("\n EXECUTANDO EXPERIMENTOS...");
        System.out.println("━".repeat(50));

        List<Item> items = createItemsFromConfig();
        double capacidade = Config.PESO_MAXIMO;

        System.out.println("\n SOLUÇÃO ENCONTRADA:");
        GA ga = new GA(Config.N_CROMOSSOMOS, Config.TAXA_CROSSOVER, Config.TAXA_MUTACAO,
                Config.ELITISMO, Config.GERACOES, 0.001);
        Chromosome melhorSolucao = ga.run(items, capacidade);
        System.out.println(melhorSolucao);

        System.out.println("\n ESTATÍSTICAS (" + Config.NUM_EXECUCOES + " execuções):");
        List<Experiment.ExperimentResult> resultados = Experiment.executeRuns(items,
                capacidade, Config.KNOWN_OPTIMAL);
        showStatistics(resultados);

        System.out.println("\n COMPORTAMENTO COM DIFERENTES CAPACIDADES:");
        testCapacities(items, capacidade);

        System.out.println("\n COMPORTAMENTO COM DIFERENTES CONJUNTOS:");
        testItemSets(capacidade);

        return ga;
    }
    public static List<Item> createItemsFromConfig() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < Config.PESOS_E_VALORES.length; i++) {
            double peso = Config.PESOS_E_VALORES[i][0];
            double valor = Config.PESOS_E_VALORES[i][1];
            items.add(new Item("Item" + (i + 1), peso, valor));
        }
        return items;
    }

    private static void showStatistics(List<Experiment.ExperimentResult> resultados)
    {
        double[] fitnessValues = new double[resultados.size()];
        for (int i = 0; i < resultados.size(); i++) {
            fitnessValues[i] = resultados.get(i).bestFitness;
        }
        Experiment.StatisticalSummary stats = new Experiment.StatisticalSummary
                (fitnessValues);

        double tempoMedio = resultados.stream()
                .mapToDouble(r -> r.executionTimeMs)
                .average().orElse(0);

        System.out.printf("""
            • Média: %.2f
            • Desvio Padrão: %.2f
            • Mínimo: %.2f
            • Máximo: %.2f
            • Tempo médio: %.2f ms
            """, stats.mean, stats.standardDeviation, stats.min, stats.max, tempoMedio);
    }

    private static void testCapacities(List<Item> items, double capacidadeOriginal)
    {
        System.out.println("━".repeat(40));

        double[] capacidades = {
                capacidadeOriginal * 0.5,
                capacidadeOriginal * 0.75,
                capacidadeOriginal,
                capacidadeOriginal * 1.5,
                capacidadeOriginal * 2.0
        };

        String[] rotulos = {"50% Menor", "25% Menor", "Original", "50% Maior",
                "100% Maior"};

        for (int i = 0; i < capacidades.length; i++) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(items,
                    capacidades[i], Config.KNOWN_OPTIMAL);
            double fitnessMedio = runs.stream().mapToDouble(r ->
                    r.bestFitness).average().orElse(0);
            double tempoMedio = runs.stream().mapToDouble(r ->
                    r.executionTimeMs).average().orElse(0);

            System.out.printf("• %-12s: Fitness=%-6.1f Tempo=%-5.1fms%n",
                    rotulos[i], fitnessMedio, tempoMedio);
        }
    }

    private static void testItemSets(double capacidade) {
        System.out.println("━".repeat(40));

        List<List<Item>> conjuntos = createItemSets();
        String[] nomesConjuntos = {"Original", "Valiosos", "Pesados",
                "Leves-Valiosos", "Balanceados"};

        for (int i = 0; i < conjuntos.size(); i++)
        {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns
                    (conjuntos.get(i), capacidade, Config.KNOWN_OPTIMAL);
            double fitnessMedio = runs.stream().mapToDouble(r ->
                    r.bestFitness).average().orElse(0);
            double tempoMedio = runs.stream().mapToDouble(r ->
                    r.executionTimeMs).average().orElse(0);

            System.out.printf("• %-15s: Fitness=%-6.1f Tempo=%-5.1fms%n",
                    nomesConjuntos[i], fitnessMedio, tempoMedio);
        }
    }

    public static List<List<Item>> createItemSets() {
        List<Item> itensOriginais = createItemsFromConfig();
        List<List<Item>> conjuntos = new ArrayList<>();

        conjuntos.add(itensOriginais);

        List<Item> valiosos = new ArrayList<>(itensOriginais);
        valiosos.add(new Item("Diamante", 2, 500));
        valiosos.add(new Item("Ouro", 3, 400));
        conjuntos.add(valiosos);

        List<Item> pesados = new ArrayList<>(itensOriginais);
        pesados.add(new Item("Pedra", 80, 50));
        pesados.add(new Item("Ferro", 60, 80));
        conjuntos.add(pesados);

        List<Item> levesValiosos = new ArrayList<>(itensOriginais);
        levesValiosos.add(new Item("Joia Rara", 1, 600));
        levesValiosos.add(new Item("Pérola", 1, 450));
        conjuntos.add(levesValiosos);

        List<Item> balanceados = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            double peso = 5 + (i * 3);
            double valor = peso * (8 + (i % 5));
            balanceados.add(new Item("Bal" + i, peso, valor));
        }
        conjuntos.add(balanceados);

        return conjuntos;
    }

    public static List<Experiment.ExperimentResult> collectCapacityData() {
        List<Experiment.ExperimentResult> dadosResumidos = new ArrayList<>();
        List<Item> items = createItemsFromConfig();

        double[] capacidades = {
                Config.PESO_MAXIMO * 0.5,
                Config.PESO_MAXIMO * 0.75,
                Config.PESO_MAXIMO,
                Config.PESO_MAXIMO * 1.5,
                Config.PESO_MAXIMO * 2.0
        };

        for (double cap : capacidades) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns
                    (items, cap, Config.KNOWN_OPTIMAL);
            if (!runs.isEmpty()) {
                dadosResumidos.add(createSummaryResult(runs));
            }
        }
        return dadosResumidos;
    }

    public static List<Experiment.ExperimentResult> collectItemsData() {
        List<Experiment.ExperimentResult> dadosResumidos = new ArrayList<>();
        List<List<Item>> conjuntos = createItemSets();

        for (List<Item> conjunto : conjuntos) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(conjunto,
                    Config.PESO_MAXIMO, Config.KNOWN_OPTIMAL);
            if (!runs.isEmpty()) {
                dadosResumidos.add(createSummaryResult(runs));
            }
        }
        return dadosResumidos;
    }

    private static Experiment.ExperimentResult createSummaryResult(List<Experiment.
            ExperimentResult> runs)

    {
        double avgBestFitness = runs.stream()
                .mapToDouble(r -> r.bestFitness)
                .average().orElse(0);
        double avgWorstFitness = runs.stream()
                .mapToDouble(r -> r.worstFitness)
                .average().orElse(0);
        double avgTime = runs.stream()
                .mapToDouble(r -> r.executionTimeMs)
                .average().orElse(0);
        int avgConvGen = (int) runs.stream()
                .mapToInt(r -> r.convergenceGeneration)
                .average().orElse(0);

        long successCount = runs.stream().filter(r -> r.foundOptimal).count();
        double successRate = (double) successCount * 100.0 / runs.size();

        return new Experiment.ExperimentResult(
                avgBestFitness,
                successRate,
                avgWorstFitness,
                avgTime,
                avgConvGen,
                successCount > (runs.size() / 2)
        );
    }
}
