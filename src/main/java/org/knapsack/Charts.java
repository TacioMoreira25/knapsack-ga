package org.knapsack;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.List;

/**
 * Classe para gerar gráficos individuais
 */
public class Charts {

    public static void gerarGraficoEvolucaoFitness(GA ga) {
        System.out.println("📈 Gerando gráfico de Evolução do Fitness...");

        XYSeries melhor = new XYSeries("Melhor Fitness");
        XYSeries medio = new XYSeries("Fitness Médio");

        double[] historicoMelhor = ga.getBestFitnessHistory();
        double[] historicoMedio = ga.getAverageFitnessHistory();

        // Garante que só plote gerações que realmente rodaram
        int lastGen = ga.getConvergenceGeneration() > 0 ?
                ga.getConvergenceGeneration() : historicoMelhor.length;
        lastGen = Math.min(lastGen, historicoMelhor.length);


        for (int i = 0; i < lastGen; i++) {
            if (historicoMelhor[i] > 0) {
                melhor.add(i, historicoMelhor[i]);
                medio.add(i, historicoMedio[i]);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(melhor);
        dataset.addSeries(medio);

        JFreeChart chart = ChartFactory.createXYLineChart(
                String.format("Evolução do Fitness (%d pop, %d gen)",
                        Config.N_CROMOSSOMOS, Config.GERACOES),
                "Geração", "Fitness", dataset
        );

        mostrarGrafico(chart, "Evolução do Fitness");
    }

    public static void gerarGraficoTempoCapacidade(List<Experiment.ExperimentResult> dados) {
        System.out.println("⏱️  Gerando gráfico de Tempo (Médio) vs Capacidade...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] capacidades = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};

        // 'dados' agora contém os resultados médios (5 itens)
        for (int i = 0; i < dados.size() && i < capacidades.length; i++) {
            dataset.addValue(dados.get(i).executionTimeMs, "Tempo (ms)", capacidades[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tempo de Execução (Média) vs Capacidade",
                "Capacidade", "Tempo (ms)", dataset
        );

        mostrarGrafico(chart, "Tempo vs Capacidade");
    }

    public static void gerarGraficoFitnessCapacidade(List<Experiment.ExperimentResult> dados) {
        System.out.println("💰 Gerando gráfico de Fitness (Médio) vs Capacidade...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] capacidades = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};

        // 'dados' agora contém os resultados médios (5 itens)
        for (int i = 0; i < dados.size() && i < capacidades.length; i++) {
            dataset.addValue(dados.get(i).bestFitness, "Fitness", capacidades[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Fitness (Média) vs Capacidade da Mochila",
                "Capacidade", "Fitness", dataset
        );

        mostrarGrafico(chart, "Fitness vs Capacidade");
    }

    public static void gerarGraficoTempoItens(List<Experiment.ExperimentResult> dados) {
        System.out.println("⏱️  Gerando gráfico de Tempo (Médio) vs Conjuntos...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] conjuntos = {"Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"};

        for (int i = 0; i < dados.size() && i < conjuntos.length; i++) {
            dataset.addValue(dados.get(i).executionTimeMs, "Tempo (ms)", conjuntos[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tempo (Média) vs Conjunto de Itens",
                "Conjunto", "Tempo (ms)", dataset
        );

        mostrarGrafico(chart, "Tempo vs Itens");
    }

    public static void gerarGraficoFitnessItens(List<Experiment.ExperimentResult> dados) {
        System.out.println("💰 Gerando gráfico de Fitness (Médio) vs Conjuntos...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] conjuntos = {"Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"};

        for (int i = 0; i < dados.size() && i < conjuntos.length; i++) {
            dataset.addValue(dados.get(i).bestFitness, "Fitness", conjuntos[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Fitness (Média) vs Conjunto de Itens",
                "Conjunto", "Fitness", dataset
        );

        mostrarGrafico(chart, "Fitness vs Itens");
    }

    public static void gerarGraficoDiversidade(GA ga) {
        System.out.println("🧬 Gerando gráfico de Diversidade Genética...");

        XYSeries diversidade = new XYSeries("Diversidade");
        double[] historicoDiversidade = ga.getDiversityHistory(); // Pega dados reais

        // Garante que só plote gerações que realmente rodaram
        int lastGen = ga.getConvergenceGeneration() > 0 ?
                ga.getConvergenceGeneration() : historicoDiversidade.length;
        lastGen = Math.min(lastGen, historicoDiversidade.length);

        for (int i = 0; i < lastGen; i++) {
            if (historicoDiversidade[i] > 0) {
                diversidade.add(i, historicoDiversidade[i]);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(diversidade);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Diversidade Genética (Distância de Hamming Média)",
                "Geração", "Diversidade (%)", dataset
        );

        mostrarGrafico(chart, "Diversidade Genética");
    }

    public static void gerarGraficoTaxaSucesso(List<Experiment.ExperimentResult> capDados,
                                               List<Experiment.ExperimentResult> itensDados) {
        System.out.println("✅ Gerando gráfico de Taxa de Sucesso...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String[] capLabels = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};
        String[] itemLabels = {"Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"};

        // Pega a taxa de sucesso (que armazenamos no campo 'averageFitness')
        for (int i = 0; i < capDados.size() && i < capLabels.length; i++) {
            // Usa o rótulo "Cap." para diferenciar
            dataset.addValue(capDados.get(i).averageFitness, "Sucesso (%)", "Cap. " + capLabels[i]);
        }

        for (int i = 0; i < itensDados.size() && i < itemLabels.length; i++) {
            // Usa o rótulo "Itens." para diferenciar
            dataset.addValue(itensDados.get(i).averageFitness, "Sucesso (%)", "Itens " + itemLabels[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Taxa de Sucesso por Cenário (vs Ótimo de 805.0)",
                "Cenário", "Sucesso (%)", dataset
        );

        mostrarGrafico(chart, "Taxa de Sucesso");
    }

    public static void gerarTodosGraficos(GA ga, List<Experiment.ExperimentResult> capDados,
                                          List<Experiment.ExperimentResult> itensDados) {
        System.out.println("🎯 Gerando TODOS os gráficos...");

        gerarGraficoEvolucaoFitness(ga);
        gerarGraficoTempoCapacidade(capDados);
        gerarGraficoFitnessCapacidade(capDados);
        gerarGraficoTempoItens(itensDados);
        gerarGraficoFitnessItens(itensDados);
        gerarGraficoDiversidade(ga);
        gerarGraficoTaxaSucesso(capDados, itensDados);

        System.out.println("✅ Todos os gráficos gerados!");
    }

    private static void mostrarGrafico(JFreeChart chart, String titulo) {
        ChartFrame frame = new ChartFrame(titulo, chart);
        frame.pack();
        frame.setVisible(true);

        // Um pequeno delay para ajudar as janelas a se organizarem
        try { Thread.sleep(100); } catch (InterruptedException e) {}
    }
}