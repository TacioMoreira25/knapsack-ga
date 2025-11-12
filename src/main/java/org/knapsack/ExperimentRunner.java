package org.knapsack;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por executar todos os experimentos
 */
public class ExperimentRunner {

    public static GA runAllExperiments() {
        System.out.println("\n🔍 EXECUTANDO EXPERIMENTOS...");
        System.out.println("━".repeat(50));

        // Carrega configurações
        List<Item> items = createItemsFromConfig();
        double capacidade = Config.PESO_MAXIMO;

        // 1. SOLUÇÃO INICIAL
        System.out.println("\n SOLUÇÃO ENCONTRADA:");
        GA ga = new GA(Config.N_CROMOSSOMOS, Config.TAXA_CROSSOVER, Config.TAXA_MUTACAO,
                Config.ELITISMO, Config.GERACOES, 0.001);
        Chromosome melhorSolucao = ga.run(items, capacidade);
        System.out.println(melhorSolucao);

        // 2. ESTATÍSTICAS
        System.out.println("\n ESTATÍSTICAS (" + Config.NUM_EXECUCOES + " execuções):");
        // --- MUDANÇA 1 ---
        List<Experiment.ExperimentResult> resultados = Experiment.executeRuns(items,
                capacidade, Config.KNOWN_OPTIMAL);
        showStatistics(resultados);

        // 4. CAPACIDADES
        System.out.println("\n COMPORTAMENTO COM DIFERENTES CAPACIDADES:");
        testCapacities(items, capacidade);

        // 5. CONJUNTOS DE ITENS
        System.out.println("\n COMPORTAMENTO COM DIFERENTES CONJUNTOS:");
        testItemSets(capacidade);

        return ga;
    }

    /**
     * Cria itens a partir da configuração
     */
    public static List<Item> createItemsFromConfig() {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < Config.PESOS_E_VALORES.length; i++) {
            double peso = Config.PESOS_E_VALORES[i][0];
            double valor = Config.PESOS_E_VALORES[i][1];
            items.add(new Item("Item" + (i + 1), peso, valor));
        }
        return items;
    }

    /**
     * Mostra estatísticas dos resultados
     */
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

    /**
     * Testa diferentes capacidades da mochila
     */
    private static void testCapacities(List<Item> items, double capacidadeOriginal)
    {
        System.out.println("━".repeat(40));

        // Capacidades para testar (baseadas na configuração)
        double[] capacidades = {
                capacidadeOriginal * 0.5,   // 50% menor
                capacidadeOriginal * 0.75,  // 25% menor
                capacidadeOriginal,         // original
                capacidadeOriginal * 1.5,   // 50% maior
                capacidadeOriginal * 2.0    // 100% maior
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

    /**
     * Testa diferentes conjuntos de itens
     */
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

    /**
     * Cria diferentes conjuntos de itens para teste
     */
    public static List<List<Item>> createItemSets() {
        List<Item> itensOriginais = createItemsFromConfig();
        List<List<Item>> conjuntos = new ArrayList<>();

        // 1. Original (da configuração)
        conjuntos.add(itensOriginais);

        // 2. Valiosos (adiciona itens valiosos)
        List<Item> valiosos = new ArrayList<>(itensOriginais);
        valiosos.add(new Item("Diamante", 2, 500));
        valiosos.add(new Item("Ouro", 3, 400));
        conjuntos.add(valiosos);

        // 3. Pesados (adiciona itens pesados)
        List<Item> pesados = new ArrayList<>(itensOriginais);
        pesados.add(new Item("Pedra", 80, 50));
        pesados.add(new Item("Ferro", 60, 80));
        conjuntos.add(pesados);

        // 4. Leves e Valiosos
        List<Item> levesValiosos = new ArrayList<>(itensOriginais);
        levesValiosos.add(new Item("Joia Rara", 1, 600));
        levesValiosos.add(new Item("Pérola", 1, 450));
        conjuntos.add(levesValiosos);

        // 5. Balanceados (novo conjunto balanceado)
        List<Item> balanceados = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            double peso = 5 + (i * 3);
            double valor = peso * (8 + (i % 5));
            balanceados.add(new Item("Bal" + i, peso, valor));
        }
        conjuntos.add(balanceados);

        return conjuntos;
    }

    /**
     * MUDANÇA: Coleta dados para gráficos de capacidade (calcula médias)
     */
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
            // Passa o KNOWN_OPTIMAL (mesmo que só seja válido para um cenário)
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns
                    (items, cap, Config.KNOWN_OPTIMAL);
            if (!runs.isEmpty()) {
                // Calcula as médias e taxas para este cenário
                dadosResumidos.add(createSummaryResult(runs));
            }
        }
        return dadosResumidos;
    }

    /**
     * Coleta dados para gráficos de itens (calcula médias)
     */
    public static List<Experiment.ExperimentResult> collectItemsData() {
        List<Experiment.ExperimentResult> dadosResumidos = new ArrayList<>();
        List<List<Item>> conjuntos = createItemSets();

        for (List<Item> conjunto : conjuntos) {
            // Passa o KNOWN_OPTIMAL (mesmo que só seja válido para um cenário)
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(conjunto,
                    Config.PESO_MAXIMO, Config.KNOWN_OPTIMAL);
            if (!runs.isEmpty()) {
                // Calcula as médias e taxas para este cenário
                dadosResumidos.add(createSummaryResult(runs));
            }
        }
        return dadosResumidos;
    }

    /**
     * NOVO: Método auxiliar para criar um resultado resumido (com médias)
     */
    private static Experiment.ExperimentResult createSummaryResult(List<Experiment.
            ExperimentResult> runs)

    {
        double avgBestFitness = runs.stream()
                .mapToDouble(r -> r.bestFitness)
                .average().orElse(0);
        double avgAvgFitness = runs.stream()
                .mapToDouble(r -> r.averageFitness)
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

        // Calcula a taxa de sucesso (0-100)
        long successCount = runs.stream().filter(r -> r.foundOptimal).count();
        double successRate = (double) successCount * 100.0 / runs.size();

        // Hack: Armazenamos a taxa de sucesso (0-100) no campo 'averageFitness'
        // e o 'bestFitness' e 'executionTimeMs' como as médias.
        return new Experiment.ExperimentResult(
                avgBestFitness,
                successRate, // Armazena a taxa de sucesso aqui
                avgWorstFitness,
                avgTime,
                avgConvGen,
                successCount > (runs.size() / 2)
        );
    }
}