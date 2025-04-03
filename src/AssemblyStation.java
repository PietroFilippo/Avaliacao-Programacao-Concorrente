import java.util.Random;

// Representa uma estação de montagem que solicita peças da fábrica para montar veículos
public class AssemblyStation implements Runnable {
    private final String stationId;
    private final Factory factory;
    private final Random random = new Random();
    
    public AssemblyStation(String stationId, Factory factory) {
        this.stationId = stationId;
        this.factory = factory;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Solicita uma peça da fábrica
                boolean partAcquired = factory.requestPart();
                
                if (partAcquired) {
                    System.out.println("Estação " + stationId + " adquiriu uma peça para montagem.");
                    
                    // Simula o trabalho de montagem
                    Thread.sleep(random.nextInt(1000) + 500);
                    
                    System.out.println("Estação " + stationId + " completou o trabalho de montagem.");
                } else {
                    // Aviso se não há peças disponíveis
                    System.out.println("Estação " + stationId + " aguardando peças disponíveis.");
                    Thread.sleep(2000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("A operação da estação " + stationId + " foi interrompida.");
        }
    }
} 