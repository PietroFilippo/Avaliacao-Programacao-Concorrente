import java.util.ArrayList;
import java.util.List;

public class SimulacaoProducaoVeiculos {

    private static final int NUM_ESTACOES_PRODUCAO = 4;
    private static final int TEMPO_SIMULACAO_SEGUNDOS = 70;
    private static final int PORTA_SERVIDOR = 8080;

    private static List<EstacaoProducao> estacoesProducao = new ArrayList<>();

    public static void main(String[] args) {
        Fabrica fabrica = new Fabrica();
        
        for (int i = 0; i < NUM_ESTACOES_PRODUCAO; i++) {
            EstacaoProducao estacao = new EstacaoProducao(i + 1, fabrica);
            estacoesProducao.add(estacao);
            estacao.iniciar();
        }
        
        ServidorFabrica servidor = new ServidorFabrica(PORTA_SERVIDOR, fabrica);
        Thread servidorThread = new Thread(servidor);
        servidorThread.start();
        System.out.println("Servidor da fábrica iniciado na porta " + PORTA_SERVIDOR);
        
        try {
            System.out.println("Simulação iniciada. Executando por " + TEMPO_SIMULACAO_SEGUNDOS + " segundos...");
            Thread.sleep(TEMPO_SIMULACAO_SEGUNDOS * 1000);
            
            for (EstacaoProducao estacao : estacoesProducao) {
                estacao.parar();
            }
            
            System.out.println("Encerrando servidor da fábrica...");
            servidor.parar();
            servidorThread.interrupt();
            servidorThread.join(5000);
            
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
            
            fabrica.encerrar();
            
            for (EstacaoProducao estacao : estacoesProducao) {
                estacao.parar();
            }
            
            System.out.println("Encerrando servidor da fábrica...");
            servidor.parar();
            servidorThread.interrupt();
        }
    }

    public static List<EstacaoProducao> getEstacoesProducao() {
        return estacoesProducao;
    }
} 