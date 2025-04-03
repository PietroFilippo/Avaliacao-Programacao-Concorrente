import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


 // Aplicação principal para simular a produção de veículos
public class VehicleProductionSimulation {

    public static void main(String[] args) {
        // Cria a fábrica
        Factory factory = new Factory();
        
        // Número de estações de montagem
        int numStations = 10;
        
        // Cria e inicia as estações de montagem
        ExecutorService executorService = Executors.newFixedThreadPool(numStations);
        List<AssemblyStation> stations = new ArrayList<>();
        
        for (int i = 0; i < numStations; i++) {
            AssemblyStation station = new AssemblyStation("Station-" + (i + 1), factory);
            stations.add(station);
            executorService.submit(station);
        }
        
        // Deixa a simulação rodar por algum tempo
        try {
            // Executa a simulação por 1 minuto
            Thread.sleep(60 * 1000);
            
            // Desliga o executor
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            
            System.out.println("Simulação concluída.");
            System.out.println("Partes restantes no estoque: " + factory.getPartsCount());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("A simulação foi interrompida.");
            
            // Desliga o executor imediatamente
            executorService.shutdownNow();
        }
    }
} 