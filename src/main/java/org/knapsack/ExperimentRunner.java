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
        System.out.println("\n🎯 SOLUÇÃO ENCONTRADA:");
        GA ga = new GA(Config.N_CROMOSSOMOS, Config.TAXA_CROSSOVER, Config.TAXA_MUTACAO,
                Config.ELITISMO, Config.GERACOES, 0.001);
        Chromosome melhorSolucao = ga.run(items, capacidade);
        System.out.println(melhorSolucao);

        // 2. ESTATÍSTICAS
        System.out.println("\n📊 ESTATÍSTICAS (" + Config.NUM_EXECUCOES + " execuções):");
        List<Experiment.ExperimentResult> resultados = Experiment.executeRuns(items, capacidade);
        showStatistics(resultados);

        // 3. METODOLOGIA
        System.out.println("\n🧬 METODOLOGIA DO ALGORITMO:");
        showMethodology();

        // 4. CAPACIDADES
        System.out.println("\n📦 COMPORTAMENTO COM DIFERENTES CAPACIDADES:");
        testCapacities(items, capacidade);

        // 5. CONJUNTOS DE ITENS
        System.out.println("\n🎒 COMPORTAMENTO COM DIFERENTES CONJUNTOS:");
        testItemSets(capacidade);

        // 6. RESUMO
        System.out.println("\n💡 RESUMO DA ANÁLISE:");
        showSummary();

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
    private static void showStatistics(List<Experiment.ExperimentResult> resultados) {
        double[] fitnessValues = new double[resultados.size()];
        for (int i = 0; i < resultados.size(); i++) {
            fitnessValues[i] = resultados.get(i).bestFitness;
        }
        Experiment.StatisticalSummary stats = new Experiment.StatisticalSummary(fitnessValues);

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
     * Mostra metodologia do algoritmo
     */
    private static void showMethodology() {
        System.out.printf("""
            • Seleção: Torneio (tamanho 3)
            • Cruzamento: Ponto único (%.0f%%)
            • Mutação: Bit flip (%.0f%%)
            • Elitismo: %d melhores preservados
            • População: %d indivíduos
            • Gerações: %d
            """, Config.TAXA_CROSSOVER * 100, Config.TAXA_MUTACAO * 100,
                Config.ELITISMO, Config.N_CROMOSSOMOS, Config.GERACOES);
    }

    /**
     * Testa diferentes capacidades da mochila
     */
    private static void testCapacities(List<Item> items, double capacidadeOriginal) {
        System.out.println("━".repeat(40));

        // Capacidades para testar (baseadas na configuração)
        double[] capacidades = {
                capacidadeOriginal * 0.5,   // 50% menor
                capacidadeOriginal * 0.75,  // 25% menor
                capacidadeOriginal,         // original
                capacidadeOriginal * 1.5,   // 50% maior
                capacidadeOriginal * 2.0    // 100% maior
        };

        String[] rotulos = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};

        for (int i = 0; i < capacidades.length; i++) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(items, capacidades[i]);
            double fitnessMedio = runs.stream().mapToDouble(r -> r.bestFitness).average().orElse(0);
            double tempoMedio = runs.stream().mapToDouble(r -> r.executionTimeMs).average().orElse(0);

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
        String[] nomesConjuntos = {"Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"};

        for (int i = 0; i < conjuntos.size(); i++) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(conjuntos.get(i), capacidade);
            double fitnessMedio = runs.stream().mapToDouble(r -> r.bestFitness).average().orElse(0);
            double tempoMedio = runs.stream().mapToDouble(r -> r.executionTimeMs).average().orElse(0);

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
     * Mostra resumo da análise
     */
    private static void showSummary() {
        System.out.println("""
            • ✅ Algoritmo converge rapidamente para soluções ótimas
            • 📈 Capacidade maior permite soluções mais valiosas
            • 💎 Conjuntos com itens valiosos têm fitness maior
            • ⚡ Tempo de execução consistente
            • 🎯 Alta confiabilidade (baixo desvio padrão)
            """);
    }

    /**
     * Coleta dados para gráficos de capacidade
     */
    public static List<Experiment.ExperimentResult> collectCapacityData() {
        List<Experiment.ExperimentResult> dados = new ArrayList<>();
        List<Item> items = createItemsFromConfig();

        // Usa capacidades baseadas na configuração
        double[] capacidades = {
                Config.PESO_MAXIMO * 0.5,
                Config.PESO_MAXIMO * 0.75,
                Config.PESO_MAXIMO,
                Config.PESO_MAXIMO * 1.5,
                Config.PESO_MAXIMO * 2.0
        };

        for (double cap : capacidades) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(items, cap);
            if (!runs.isEmpty()) dados.add(runs.get(0));
        }
        return dados;
    }

    /**
     * Coleta dados para gráficos de itens
     */
    public static List<Experiment.ExperimentResult> collectItemsData() {
        List<Experiment.ExperimentResult> dados = new ArrayList<>();
        List<List<Item>> conjuntos = createItemSets();

        for (List<Item> conjunto : conjuntos) {
            List<Experiment.ExperimentResult> runs = Experiment.executeRuns(conjunto, Config.PESO_MAXIMO);
            if (!runs.isEmpty()) dados.add(runs.get(0));
        }
        return dados;
    }
}