package org.knapsack;

import java.util.Scanner;
import java.util.List;

public class Main
{
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        // Mostrar cabeçalho
        showHeader();

        // Executar todos os experimentos
        GA ga = ExperimentRunner.runAllExperiments();

        // Mostrar menu de gráficos
        showGraphicsMenu(ga);

        scanner.close();
    }

    private static void showHeader() {
        System.out.println("\n" + "⭐".repeat(60));
        System.out.println("           PROBLEMA DA MOCHILA - ALGORITMO GENÉTICO");
        System.out.println("⭐".repeat(60));

        // Mostra configurações atuais
        System.out.println("\n⚙️  CONFIGURAÇÃO ATUAL:");
        System.out.println("• Itens: " + Config.PESOS_E_VALORES.length + " itens");
        System.out.println("• Peso máximo: " + Config.PESO_MAXIMO);
        System.out.println("• População: " + Config.N_CROMOSSOMOS + " cromossomos");
        System.out.println("• Gerações: " + Config.GERACOES);
    }

    private static void showGraphicsMenu(GA ga) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📊 MENU DE GRÁFICOS");
        System.out.println("=".repeat(60));

        // Coletar dados para gráficos
        List<Experiment.ExperimentResult> dadosCapacidade =
                ExperimentRunner.collectCapacityData();
        List<Experiment.ExperimentResult> dadosItens =
                ExperimentRunner.collectItemsData();

        int opcao;
        do {
            System.out.println("\n🎯 ESCOLHA O GRÁFICO:");
            System.out.println("━".repeat(40));
            System.out.println("1. 📈 Evolução do Fitness");
            System.out.println("2. ⏱️  Tempo vs Capacidade");
            System.out.println("3. 💰 Fitness vs Capacidade");
            System.out.println("4. ⏱️  Tempo vs Conjuntos");
            System.out.println("5. 💰 Fitness vs Conjuntos");
            System.out.println("6. 🧬 Diversidade Genética");
            System.out.println("7. ✅ Taxa de Sucesso");
            System.out.println("8. 🎯 TODOS os Gráficos");
            System.out.println("0. 🚪 Finalizar");

            System.out.print("\nDigite sua escolha (0-8): ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1 -> Charts.gerarGraficoEvolucaoFitness(ga);
                case 2 -> Charts.gerarGraficoTempoCapacidade(dadosCapacidade);
                case 3 -> Charts.gerarGraficoFitnessCapacidade(dadosCapacidade);
                case 4 -> Charts.gerarGraficoTempoItens(dadosItens);
                case 5 -> Charts.gerarGraficoFitnessItens(dadosItens);
                case 6 -> Charts.gerarGraficoDiversidade(ga);
                case 7 -> Charts.gerarGraficoTaxaSucesso(dadosCapacidade, dadosItens);
                case 8 -> Charts.gerarTodosGraficos(ga, dadosCapacidade, dadosItens);
                case 0 -> System.out.println("\n✅ Análise concluída!");
                default -> System.out.println("❌ Opção inválida!");
            }
        } while (opcao != 0);
    }
}