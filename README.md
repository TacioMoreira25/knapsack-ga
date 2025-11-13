# Algoritmo Genético Aplicado ao Problema da Mochila

Projeto acadêmico desenvolvido para a disciplina de **PROJETO E ANÁLISE DE ALGORITMOS** do curso de Engenharia da Computação da FAINOR.

## Sobre o Projeto

Este repositório contém a implementação de um Algoritmo Genético (AG) em Java, projetado para encontrar uma solução otimizada para o Problema da Mochila (Knapsack Problem 0/1).

O foco principal do projeto é a **análise de desempenho** do algoritmo. Para isso, o software executa uma análise estatística robusta e testa o comportamento do AG sob diferentes cenários e parâmetros, apresentando os resultados em gráficos.

## Principais Funcionalidades

  * **Núcleo do AG:** Implementa os operadores genéticos clássicos: Seleção por Torneio (`GA.java`), Crossover de ponto único, Mutação bit-flip e Elitismo.
  * **Função de Penalidade:** Soluções que excedem a capacidade da mochila são penalizadas (em `Chromosome.java`), em vez de serem descartadas, permitindo uma exploração mais robusta do espaço de busca.
  * **Análise Estatística:** O algoritmo é executado `NUM_EXECUCOES` (ex: 30) vezes para garantir a validade estatística dos resultados, calculando média, desvio padrão, mínimo e máximo (`Experiment.java`).
  * **Validação de Ótimo:** A taxa de sucesso é medida comparando o melhor *fitness* encontrado com um valor ótimo pré-definido (`KNOWN_OPTIMAL`).
  * **Análise de Cenários:** O `ExperimentRunner.java` testa o AG sob 5 variações de capacidade da mochila e 5 variações de conjuntos de itens.
  * **Visualização de Dados:** Um menu interativo (`Main.java`) permite a geração de 7 gráficos diferentes (em `Charts.java`), incluindo Evolução do Fitness, Diversidade Genética e Taxa de Sucesso.

## Tecnologias Utilizadas

  * **Java:** (Configurado para versão 25 no `pom.xml`, mas compatível com 17+).
  * **Apache Maven:** Gerenciamento de dependências e build do projeto.
  * **JFreeChart:** Biblioteca Java para a criação e exibição dos gráficos.

## Estrutura do Projeto

O código-fonte está localizado em `src/main/java/org/knapsack/`:

  * `Config.java`: Arquivo central para configurar **todos** os parâmetros.
  * `Main.java`: Ponto de entrada da aplicação e menu interativo.
  * `Item.java`: Classe de modelo para os itens.
  * `Chromosome.java`: Representação de uma solução (indivíduo) e função de fitness.
  * `GA.java`: Lógica central do Algoritmo Genético.
  * `Experiment.java`: Define a estrutura da análise estatística.
  * `ExperimentRunner.java`: Orquestra e executa todos os testes e cenários.
  * `Charts.java`: Gera e exibe todos os gráficos.

## Como Executar

### Pré-requisitos

  * JDK (Java Development Kit) (versão 17 ou superior).
  * Apache Maven (opcional, pois o projeto inclui o Maven Wrapper).

### Instruções

O projeto utiliza um Maven Wrapper (`mvnw`), que baixa e utiliza a versão correta do Maven automaticamente.

1.  Clone o repositório:

    ```sh
    git clone https://github.com/TacioMoreira25/knapsack-ga
    cd knapsack-ga
    ```

2.  Compile e execute o projeto:

    *No Linux/macOS:*

    ```sh
    ./mvnw clean compile exec:java -Dexec.mainClass=org.knapsack.Main
    ```

    *No Windows (CMD/PowerShell):*

    ```sh
    .\mvnw.cmd clean compile exec:java
    ```

3.  O programa executará toda a análise estatística no console e, em seguida, apresentará o menu interativo para a geração dos gráficos.
