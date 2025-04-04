import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// Representa uma esteira circular com capacidade limitada para armazenar carros produzidos
public class EsteiraCircular {
    private static final int CAPACIDADE = 40;
    
    private final BlockingQueue<Carro> carros;
    private final int idEstacao;
    
    public EsteiraCircular(int idEstacao) {
        this.idEstacao = idEstacao;
        this.carros = new ArrayBlockingQueue<>(CAPACIDADE);
    }
    

    // Adiciona um carro à esteira
    public void adicionarCarro(Carro carro) throws InterruptedException {
        carros.put(carro);
        System.out.println("Carro " + carro.getId() + " adicionado à esteira da estação " + idEstacao + 
                " (Itens na esteira: " + carros.size() + "/" + CAPACIDADE + ")");
    }
    
    // Remove um carro da esteira
    public Carro removerCarro() throws InterruptedException {
        Carro carro = carros.take();
        System.out.println("Carro " + carro.getId() + " removido da esteira da estação " + idEstacao + 
                " (Itens na esteira: " + carros.size() + "/" + CAPACIDADE + ")");
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
} 