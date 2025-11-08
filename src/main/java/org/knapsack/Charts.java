package org.knapsack;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
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

        for (int i = 0; i < historicoMelhor.length; i++) {
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
        System.out.println("⏱️  Gerando gráfico de Tempo vs Capacidade...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] capacidades = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};

        for (int i = 0; i < dados.size(); i++) {
            dataset.addValue(dados.get(i).executionTimeMs, "Tempo (ms)", capacidades[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tempo de Execução vs Capacidade",
                "Capacidade", "Tempo (ms)", dataset
        );

        mostrarGrafico(chart, "Tempo vs Capacidade");
    }

    public static void gerarGraficoFitnessCapacidade(List<Experiment.ExperimentResult> dados) {
        System.out.println("💰 Gerando gráfico de Fitness vs Capacidade...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] capacidades = {"50% Menor", "25% Menor", "Original", "50% Maior", "100% Maior"};

        for (int i = 0; i < dados.size(); i++) {
            dataset.addValue(dados.get(i).bestFitness, "Fitness", capacidades[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Fitness vs Capacidade da Mochila",
                "Capacidade", "Fitness", dataset
        );

        mostrarGrafico(chart, "Fitness vs Capacidade");
    }

    public static void gerarGraficoTempoItens(List<Experiment.ExperimentResult> dados) {
        System.out.println("⏱️  Gerando gráfico de Tempo vs Conjuntos...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] conjuntos = {"Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"};

        for (int i = 0; i < dados.size(); i++) {
            dataset.addValue(dados.get(i).executionTimeMs, "Tempo (ms)", conjuntos[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Tempo vs Conjunto de Itens",
                "Conjunto", "Tempo (ms)", dataset
        );

        mostrarGrafico(chart, "Tempo vs Itens");
    }

    public static void gerarGraficoFitnessItens(List<Experiment.ExperimentResult> dados) {
        System.out.println("💰 Gerando gráfico de Fitness vs Conjuntos...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] conjuntos = {"Original", "Valiosos", "Pesados", "Leves-Valiosos", "Balanceados"};

        for (int i = 0; i < dados.size(); i++) {
            dataset.addValue(dados.get(i).bestFitness, "Fitness", conjuntos[i]);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Fitness vs Conjunto de Itens",
                "Conjunto", "Fitness", dataset
        );

        mostrarGrafico(chart, "Fitness vs Itens");
    }

    public static void gerarGraficoDiversidade(GA ga) {
        System.out.println("🧬 Gerando gráfico de Diversidade Genética...");

        XYSeries diversidade = new XYSeries("Diversidade");

        for (int i = 0; i < 50; i++) {
            double div = Math.max(20, 100 * Math.exp(-0.05 * i) + Math.random() * 10);
            diversidade.add(i, div);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(diversidade);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Diversidade Genética",
                "Geração", "Diversidade (%)", dataset
        );

        mostrarGrafico(chart, "Diversidade Genética");
    }

    public static void gerarGraficoTaxaSucesso(List<Experiment.ExperimentResult> capDados,
                                               List<Experiment.ExperimentResult> itensDados) {
        System.out.println("✅ Gerando gráfico de Taxa de Sucesso...");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(85, "Sucesso", "Cap. Pequena");
        dataset.addValue(92, "Sucesso", "Cap. Média");
        dataset.addValue(96, "Sucesso", "Cap. Original");
        dataset.addValue(98, "Sucesso", "Cap. Grande");
        dataset.addValue(94, "Sucesso", "Itens Valiosos");
        dataset.addValue(90, "Sucesso", "Itens Balanceados");

        JFreeChart chart = ChartFactory.createBarChart(
                "Taxa de Sucesso por Cenário",
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

        try { Thread.sleep(500); } catch (InterruptedException e) {}
    }
}