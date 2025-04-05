import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Simulador principal que coordena todas as lojas e clientes
public class SimulacaoClientes {
    private static final int NUM_CLIENTES = 20;
    private static final int NUM_LOJAS = 3;
    private static final int TEMPO_SIMULACAO_SEGUNDOS = 90;
    private static final String ENDERECO_FABRICA = "localhost";
    private static final int PORTA_FABRICA = 8080;
    
    public static void main(String[] args) {
        // Cria e inicia as lojas
        List<Loja> lojas = new ArrayList<>();
        List<Thread> threadsLojas = new ArrayList<>();
        
        for (int i = 1; i <= NUM_LOJAS; i++) {
            Loja loja = SimulacaoLoja.criarLoja(String.valueOf(i), ENDERECO_FABRICA, PORTA_FABRICA);
            lojas.add(loja);
            
            Thread lojaThread = SimulacaoLoja.iniciarLoja(loja);
            threadsLojas.add(lojaThread);
        }
        
        // Aguarda um pouco para que as lojas se conectem
        try {
            System.out.println("Aguardando conexão das lojas...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Simulação interrompida durante inicialização");
            return;
        }
        
        // Cria e inicia os clientes
        List<Cliente> clientes = new ArrayList<>();
        ExecutorService clienteExecutor = Executors.newFixedThreadPool(NUM_CLIENTES);
        
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            Cliente cliente = new Cliente(i, lojas);
            clientes.add(cliente);
            clienteExecutor.submit(cliente);
            System.out.println("Cliente " + i + " iniciado");
        }
        
        System.out.println("Todos os clientes iniciados. Simulação em andamento...");
        
        // Executa a simulação pelo tempo determinado
        try {
            Thread.sleep(TEMPO_SIMULACAO_SEGUNDOS * 1000);
            
            System.out.println("\nFinalizando simulação após " + TEMPO_SIMULACAO_SEGUNDOS + " segundos");
            
            // Para todos os clientes
            System.out.println("Parando clientes...");
            for (Cliente cliente : clientes) {
                cliente.parar();
            }
            
            clienteExecutor.shutdown();
            if (!clienteExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Alguns clientes não terminaram a tempo, forçando encerramento...");
                clienteExecutor.shutdownNow();
            }
            
            // Para todas as lojas
            System.out.println("Parando lojas...");
            for (int i = 0; i < lojas.size(); i++) {
                SimulacaoLoja.pararLoja(lojas.get(i), threadsLojas.get(i), 5000);
            }
            
            // Exibe estatísticas finais
            exibirEstatisticas(clientes, lojas);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Simulação interrompida durante execução");
            
            // Para todos os clientes
            for (Cliente cliente : clientes) {
                cliente.parar();
            }
            clienteExecutor.shutdownNow();
            
            // Para todas as lojas
            for (int i = 0; i < lojas.size(); i++) {
                SimulacaoLoja.pararLoja(lojas.get(i), threadsLojas.get(i), 1000);
            }
        }
    }
    
    private static void exibirEstatisticas(List<Cliente> clientes, List<Loja> lojas) {
        System.out.println("\n===== RESULTADOS DA SIMULAÇÃO =====");
        
        // Estatísticas das lojas
        System.out.println("\nEstatísticas das Lojas:");
        int totalCarrosVendidos = 0;
        for (Loja loja : lojas) {
            int carrosVendidos = loja.getCarrosVendidos();
            totalCarrosVendidos += carrosVendidos;
            System.out.println(loja.getId() + ": " + carrosVendidos + " carros vendidos");
        }
        System.out.println("Total de carros vendidos pelas lojas: " + totalCarrosVendidos);
        
        // Estatísticas dos clientes
        System.out.println("\nEstatísticas dos Clientes:");
        int totalCarrosComprados = 0;
        for (Cliente cliente : clientes) {
            int carrosComprados = cliente.getCarrosComprados();
            totalCarrosComprados += carrosComprados;
            System.out.println("Cliente " + cliente.getId() + ": " + carrosComprados + " carros comprados");
        }
        System.out.println("Total de carros comprados pelos clientes: " + totalCarrosComprados);
        
        // Validação
        if (totalCarrosVendidos == totalCarrosComprados) {
            System.out.println("\nValidação: OK - O número de carros vendidos pelas lojas (" + 
                    totalCarrosVendidos + ") corresponde ao número de carros comprados pelos clientes (" + 
                    totalCarrosComprados + ")");
        } else {
            System.out.println("\nValidação: ERRO - O número de carros vendidos pelas lojas (" + 
                    totalCarrosVendidos + ") NÃO corresponde ao número de carros comprados pelos clientes (" + 
                    totalCarrosComprados + ")");
        }
    }
} 