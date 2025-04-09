import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class EsteiraCircular {
    private static final int CAPACIDADE = 40;
    
    private final BlockingQueue<Carro> carros;
    private final int idEstacao;
    private final String idLoja;
    
    public EsteiraCircular(int idEstacao) {
        this.idEstacao = idEstacao;
        this.idLoja = null; 
        this.carros = new ArrayBlockingQueue<>(CAPACIDADE);
    }
    
    public EsteiraCircular(int idEstacao, String idLoja) {
        this.idEstacao = idEstacao;
        this.idLoja = idLoja;
        this.carros = new ArrayBlockingQueue<>(CAPACIDADE);
    }
    

    public void adicionarCarro(Carro carro) throws InterruptedException {
        int posicao = carros.size() + 1;
        
        if (idLoja == null) {
            carro.setPosicaoEsteiraFabrica(posicao);
            
            Logger.logProducaoCarro(carro);
        } else {
            carro.setIdLoja(idLoja);
            carro.setPosicaoEsteiraLoja(posicao);
            
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
    
    public synchronized Carro removerCarro(long timeout, TimeUnit unit) throws InterruptedException {
        long endTime = System.currentTimeMillis() + unit.toMillis(timeout);
        
        while (carros.isEmpty()) {
            long waitTime = endTime - System.currentTimeMillis();
            if (waitTime <= 0) {
                return null;
            }
            
            wait(waitTime);
            
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
    
    public boolean temEspaco() {
        return carros.size() < CAPACIDADE;
    }
    
    public int getTamanhoAtual() {
        return carros.size();
    }
    
    public int getIdEstacao() {
        return idEstacao;
    }
    
    public String getIdLoja() {
        return idLoja;
    }
    
    public boolean isEsteiraFabrica() {
        return idLoja == null;
    }
} 