import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

// Representa uma esteira circular com capacidade limitada para armazenar carros produzidos
public class EsteiraCircular {
    private static final int CAPACIDADE = 40;
    
    private final BlockingQueue<Carro> carros;
    private final int idEstacao;
    private final String idLoja; // Pode ser null para esteiras de fábrica
    
    public EsteiraCircular(int idEstacao) {
        this.idEstacao = idEstacao;
        this.idLoja = null; // Esteira da fábrica
        this.carros = new ArrayBlockingQueue<>(CAPACIDADE);
    }
    
    public EsteiraCircular(int idEstacao, String idLoja) {
        this.idEstacao = idEstacao;
        this.idLoja = idLoja; // Esteira da loja
        this.carros = new ArrayBlockingQueue<>(CAPACIDADE);
    }
    

    // Adiciona um carro à esteira
    public void adicionarCarro(Carro carro) throws InterruptedException {
        // Atualiza a posição do carro antes de adicioná-lo à esteira
        int posicao = carros.size() + 1;
        
        if (idLoja == null) {
            // Esteira da fábrica
            carro.setPosicaoEsteiraFabrica(posicao);
            
            // Registra o log de produção
            Logger.logProducaoCarro(carro);
        } else {
            // Esteira da loja
            carro.setIdLoja(idLoja);
            carro.setPosicaoEsteiraLoja(posicao);
            
            // Não registramos log aqui mais - isso é feito pelo Logger.logVendaCarro
        }
        
        carros.put(carro);
        
        if (idLoja == null) {
            System.out.println("Carro " + carro + " adicionado à esteira da estação " + idEstacao + 
                    " (Posição: " + posicao + ", Total: " + carros.size() + "/" + CAPACIDADE + ")");
        } else {
            System.out.println("Carro " + carro + " adicionado à esteira da loja " + idLoja + 
                    " (Posição: " + posicao + ", Total: " + carros.size() + "/" + CAPACIDADE + ")");
        }
    }
    
    // Remove e retorna um carro da esteira
    public synchronized Carro removerCarro() throws InterruptedException {
        while (carros.isEmpty()) {
            wait();
        }
        
        Carro carro = carros.take();
        
        if (idLoja == null) {
            System.out.println("Carro " + carro + " removido da esteira da estação " + idEstacao + 
                    " (Restante: " + carros.size() + "/" + CAPACIDADE + ")");
        } else {
            System.out.println("Carro " + carro + " removido da esteira da loja " + idLoja + 
                    " (Restante: " + carros.size() + "/" + CAPACIDADE + ")");
        }
        
        notifyAll();
        return carro;
    }
    
    // Remove e retorna um carro da esteira, com timeout
    public synchronized Carro removerCarro(long timeout, TimeUnit unit) throws InterruptedException {
        long endTime = System.currentTimeMillis() + unit.toMillis(timeout);
        
        while (carros.isEmpty()) {
            long waitTime = endTime - System.currentTimeMillis();
            if (waitTime <= 0) {
                // Timeout expirado, retorna null
                return null;
            }
            
            // Espera até que um carro esteja disponível ou até o timeout
            wait(waitTime);
            
            // Verifica se houve interrupção
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Thread interrompida enquanto aguardava carro na esteira");
            }
        }
        
        Carro carro = carros.take();
        
        if (idLoja == null) {
            System.out.println("Carro " + carro + " removido da esteira da estação " + idEstacao + 
                    " (Restante: " + carros.size() + "/" + CAPACIDADE + ")");
        } else {
            System.out.println("Carro " + carro + " removido da esteira da loja " + idLoja + 
                    " (Restante: " + carros.size() + "/" + CAPACIDADE + ")");
        }
        
        notifyAll();
        return carro;
    }
    
    // Verifica se há espaço na esteira
    public boolean temEspaco() {
        return carros.size() < CAPACIDADE;
    }
    
    // Obtém o número atual de carros na esteira
    public int getTamanhoAtual() {
        return carros.size();
    }
    
    // Obtém o ID da estação associada a esta esteira
    public int getIdEstacao() {
        return idEstacao;
    }
    
    // Obtém o ID da loja (se for uma esteira de loja)
    public String getIdLoja() {
        return idLoja;
    }
    
    // Verifica se esta é uma esteira de fábrica ou de loja
    public boolean isEsteiraFabrica() {
        return idLoja == null;
    }
} 