import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


// Aplicação principal para simular a produção de veículos
public class SimulacaoProducaoVeiculos {

    private static final int NUM_ESTACOES_PRODUCAO = 4;
    private static final int NUM_ESTACOES_MONTAGEM = 10;
    private static final int TEMPO_SIMULACAO_SEGUNDOS = 60;

    public static void main(String[] args) {
        // Cria a fábrica
        Fabrica fabrica = new Fabrica();
        
        // Cria e inicia as estações de produção
        List<EstacaoProducao> estacoesProducao = new ArrayList<>();
        for (int i = 0; i < NUM_ESTACOES_PRODUCAO; i++) {
            EstacaoProducao estacao = new EstacaoProducao(i + 1, fabrica);
            estacoesProducao.add(estacao);
            estacao.iniciar();
        }
        
        // Número de estações de montagem
        int numEstacoes = NUM_ESTACOES_MONTAGEM;
        
        // Cria e inicia as estações de montagem
        ExecutorService executorService = Executors.newFixedThreadPool(numEstacoes);
        List<EstacaoMontagem> estacoesMontagem = new ArrayList<>();
        
        for (int i = 0; i < numEstacoes; i++) {
            EstacaoMontagem estacao = new EstacaoMontagem("Estacao-" + (i + 1), fabrica);
            estacoesMontagem.add(estacao);
            executorService.submit(estacao);
        }
        
        // Deixa a simulação rodar por algum tempo
        try {
            // Executa a simulação pelo tempo definido
            System.out.println("Simulação iniciada. Executando por " + TEMPO_SIMULACAO_SEGUNDOS + " segundos...");
            Thread.sleep(TEMPO_SIMULACAO_SEGUNDOS * 1000);
            
            // Desliga as estações de produção
            for (EstacaoProducao estacao : estacoesProducao) {
                estacao.parar();
            }
            
            // Desliga o executor das estações de montagem
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            
            // Exibe estatísticas da simulação
            System.out.println("\n===== RESULTADOS DA SIMULAÇÃO =====");
            System.out.println("Peças restantes no estoque: " + fabrica.getQuantidadePecas());
            
            int totalCarrosProduzidos = 0;
            for (int i = 0; i < estacoesProducao.size(); i++) {
                EstacaoProducao estacao = estacoesProducao.get(i);
                int carrosEstacao = estacao.getTotalCarrosProduzidos();
                totalCarrosProduzidos += carrosEstacao;
                System.out.println("Estação de produção " + estacao.getIdEstacao() + ": " + carrosEstacao + " carros produzidos");
                
                for (Funcionario funcionario : estacao.getFuncionarios()) {
                    System.out.println("  - Funcionário " + funcionario.getId() + ": " + funcionario.getCarrosProduzidos() + " carros");
                }
            }
            
            System.out.println("Total de carros produzidos: " + totalCarrosProduzidos);
            System.out.println("Simulação concluída.");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("A simulação foi interrompida.");
            
            // Desliga as estações de produção
            for (EstacaoProducao estacao : estacoesProducao) {
                estacao.parar();
            }
            
            // Desliga o executor imediatamente
            executorService.shutdownNow();
        }
    }
} 