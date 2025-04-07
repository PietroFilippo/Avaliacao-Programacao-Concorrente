import java.util.ArrayList;
import java.util.List;

// Aplicação principal para simular a produção de veículos
public class SimulacaoProducaoVeiculos {

    private static final int NUM_ESTACOES_PRODUCAO = 4;
    private static final int TEMPO_SIMULACAO_SEGUNDOS = 70;
    private static final int PORTA_SERVIDOR = 8080;

    private static List<EstacaoProducao> estacoesProducao = new ArrayList<>();

    public static void main(String[] args) {
        // Cria a fábrica
        Fabrica fabrica = new Fabrica();
        
        // Cria e inicia as estações de produção
        for (int i = 0; i < NUM_ESTACOES_PRODUCAO; i++) {
            EstacaoProducao estacao = new EstacaoProducao(i + 1, fabrica);
            estacoesProducao.add(estacao);
            estacao.iniciar();
        }
        
        // Inicia o servidor da fábrica para atender às lojas remotas
        ServidorFabrica servidor = new ServidorFabrica(PORTA_SERVIDOR, fabrica);
        Thread servidorThread = new Thread(servidor);
        servidorThread.start();
        System.out.println("Servidor da fábrica iniciado na porta " + PORTA_SERVIDOR);
        
        // Deixa a simulação rodar por algum tempo
        try {
            // Executa a simulação pelo tempo definido
            System.out.println("Simulação iniciada. Executando por " + TEMPO_SIMULACAO_SEGUNDOS + " segundos...");
            Thread.sleep(TEMPO_SIMULACAO_SEGUNDOS * 1000);
            
            // Desliga as estações de produção
            for (EstacaoProducao estacao : estacoesProducao) {
                estacao.parar();
            }
            
            // Para o servidor
            System.out.println("Encerrando servidor da fábrica...");
            servidor.parar();
            servidorThread.interrupt();
            servidorThread.join(5000);
            
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
            
            System.out.println("\nTotal de carros produzidos: " + totalCarrosProduzidos);
            System.out.println("Simulação concluída.");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("A simulação foi interrompida.");
            
            // Marca a fábrica como encerrada
            fabrica.encerrar();
            
            // Desliga as estações de produção
            for (EstacaoProducao estacao : estacoesProducao) {
                estacao.parar();
            }
            
            // Para o servidor
            System.out.println("Encerrando servidor da fábrica...");
            servidor.parar();
            servidorThread.interrupt();
        }
    }

    // Método para acessar as estações de produção de fora
    public static List<EstacaoProducao> getEstacoesProducao() {
        return estacoesProducao;
    }
} 