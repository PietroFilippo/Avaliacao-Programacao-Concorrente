import java.util.Random;

// Representa uma estação de montagem que solicita peças da fábrica para montar veículos
public class EstacaoMontagem implements Runnable {
    private final String idEstacao;
    private final Fabrica fabrica;
    private final Random random = new Random();
    
    public EstacaoMontagem(String idEstacao, Fabrica fabrica) {
        this.idEstacao = idEstacao;
        this.fabrica = fabrica;
    }
    
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                // Solicita uma peça da fábrica
                boolean pecaAdquirida = fabrica.solicitarPeca();
                
                if (pecaAdquirida) {
                    System.out.println("Estação " + idEstacao + " adquiriu uma peça para montagem.");
                    
                    // Simula o trabalho de montagem
                    Thread.sleep(random.nextInt(1000) + 500);
                    
                    System.out.println("Estação " + idEstacao + " completou o trabalho de montagem.");
                } else {
                    // Aviso se não há peças disponíveis
                    System.out.println("Estação " + idEstacao + " aguardando peças disponíveis.");
                    Thread.sleep(2000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("A operação da estação " + idEstacao + " foi interrompida.");
        }
    }
} 