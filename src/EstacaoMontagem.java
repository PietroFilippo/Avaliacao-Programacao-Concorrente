import java.util.Random;

// Representa uma estação de montagem que simula o trabalho de montagem sem consumir peças da fábrica
public class EstacaoMontagem implements Runnable {
    private final String idEstacao;
    private final Random random = new Random();
    private volatile boolean running = true;
    private int trabalhosRealizados = 0;
    
    public EstacaoMontagem(String idEstacao, Fabrica fabrica) {
        this.idEstacao = idEstacao;
    }
    
    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                // Verifica rapidamente se foi interrompido
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                
                // Em vez de consumir peças, apenas simula o trabalho de montagem
                System.out.println("Estação " + idEstacao + " iniciando trabalho de montagem.");
                
                // Simula o trabalho de montagem com verificações periódicas de interrupção
                for (int i = 0; i < 5 && running && !Thread.currentThread().isInterrupted(); i++) {
                    Thread.sleep(random.nextInt(200) + 100);
                }
                
                if (!running || Thread.currentThread().isInterrupted()) {
                    break;
                }
                
                trabalhosRealizados++;
                System.out.println("Estação " + idEstacao + " completou o trabalho de montagem #" + trabalhosRealizados);
                
                // Pequena pausa entre trabalhos
                Thread.sleep(random.nextInt(300) + 200);
            }
        } catch (InterruptedException e) {
            // Apenas registramos a interrupção
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("A operação da estação " + idEstacao + " foi interrompida. Trabalhos realizados: " + trabalhosRealizados);
        }
    }
    
    // Método para sinalizar a estação que deve parar graciosamente
    public void parar() {
        this.running = false;
    }
    
    // Retorna o número de trabalhos realizados
    public int getTrabalhosRealizados() {
        return trabalhosRealizados;
    }
    
    // Retorna o ID da estação
    public String getIdEstacao() {
        return idEstacao;
    }
} 