import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


// Classe que simula uma fábrica de veículos com um estoque limitado de peças e uma esteira para estações de montagem
public class Factory {
    private static final int MAX_PARTS = 500;
    private static final int CONVEYOR_CAPACITY = 5;
    
    private final AtomicInteger partsInventory = new AtomicInteger(MAX_PARTS);
    private final Semaphore conveyorBelt = new Semaphore(CONVEYOR_CAPACITY, true);
    

    // Solicita uma peça do estoque da fábrica
    public boolean requestPart() throws InterruptedException {
        // Tenta adquirir um slot na esteira
        conveyorBelt.acquire();
        try {
            // Verifica se há peças disponíveis no estoque
            int currentParts = partsInventory.get();
            if (currentParts > 0) {
                // Decrementa o contador de peças
                boolean success = partsInventory.compareAndSet(currentParts, currentParts - 1);
                if (success) {
                    System.out.println("Parte obtida do estoque. Peças restantes: " + partsInventory.get());
                    return true;
                } else {
                    // Aviso se alguém modificou o estoque
                    return requestPart();
                }
            } else {
                System.out.println("Não há peças disponíveis no estoque!");
                return false;
            }
        } finally {
            // Libera o slot na esteira
            conveyorBelt.release();
        }
    }
    

    // Adiciona peças ao estoque (não excedendo a capacidade máxima)
    public int restockParts(int count) {
        int currentParts = partsInventory.get();
        int partsToAdd = Math.min(count, MAX_PARTS - currentParts);
        
        if (partsToAdd > 0) {
            partsInventory.addAndGet(partsToAdd);
            System.out.println("Adicionadas " + partsToAdd + " peças ao estoque. Total atual: " + partsInventory.get());
        }
        
        return partsToAdd;
    }
    

    // Obtém o número atual de peças no estoque
    public int getPartsCount() {
        return partsInventory.get();
    }
} 