package org.knapsack;

import java.util.Scanner;
import java.util.List;

public class Main
{
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args)
    {
        showHeader();
        GA ga = ExperimentRunner.runAllExperiments();
        showGraphicsMenu(ga);
        scanner.close();
    }

    private static void showHeader() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║     PROBLEMA DA MOCHILA - ALGORITMO GENÉTICO         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        System.out.println("\n┌─ Configuração");
        System.out.println("├─ Itens........: " + Config.PESOS_E_VALORES.length);
        System.out.println("├─ Capacidade...: " + Config.PESO_MAXIMO + "kg");
        System.out.println("├─ População....: " + Config.N_CROMOSSOMOS);
        System.out.println("├─ Gerações.....: " + Config.GERACOES);
        System.out.println("├─ Crossover....: " + (Config.TAXA_CROSSOVER * 100) + "%");
        System.out.println("├─ Mutação......: " + (Config.TAXA_MUTACAO * 100) + "%");
        System.out.println("└─ Elitismo.....: " + Config.ELITISMO);
    }

    private static void showGraphicsMenu(GA ga) {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                MENU DE GRÁFICOS                      ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        List<Experiment.ExperimentResult> dadosCapacidade =
                ExperimentRunner.collectCapacityData();
        List<Experiment.ExperimentResult> dadosItens =
                ExperimentRunner.collectItemsData();

        int opcao;
        do {
            System.out.println("\n┌─ Escolha uma opção:");
            System.out.println("│");
            System.out.println("├─ [1] Evolução do Fitness");
            System.out.println("├─ [2] Tempo vs Capacidade");
            System.out.println("├─ [3] Fitness vs Capacidade");
            System.out.println("├─ [4] Tempo vs Conjuntos de Itens");
            System.out.println("├─ [5] Fitness vs Conjuntos de Itens");
            System.out.println("├─ [6] Diversidade Genética");
            System.out.println("├─ [7] Taxa de Sucesso");
            System.out.println("├─ [8] TODOS os Gráficos");
            System.out.println("│");
            System.out.println("└─ [0] Sair");

            System.out.print("\n→ Opção: ");
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
                case 0 -> System.out.println("\n✓ Finalizado!");
                default -> System.out.println("\n✗ Opção inválida!");
            }
        } while (opcao != 0);
    }
}
