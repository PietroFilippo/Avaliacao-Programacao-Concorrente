import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// Classe que representa a garagem de um cliente, onde são armazenados os veículos comprados
public class Garagem {
    private final String idCliente;
    private final List<Carro> carros;
    private final Lock lock = new ReentrantLock();
    

    // Cria uma nova garagem para um cliente
    public Garagem(String idCliente) {
        this.idCliente = idCliente;
        this.carros = new ArrayList<>();
    }
    
    // Adiciona um carro à garagem do cliente
    public int adicionarCarro(Carro carro) {
        lock.lock();
        try {
            carro.setIdCliente(idCliente);
            int posicao = carros.size() + 1;
            carro.setPosicaoGaragem(posicao);
            carros.add(carro);
            
            // Registra a venda do carro pela loja ao cliente
            Logger.logVendaCarroLoja(carro);
            
            return posicao;
        } finally {
            lock.unlock();
        }
    }
    

    // Retorna uma lista não modificável com todos os carros na garagem
    public List<Carro> getCarros() {
        lock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(carros));
        } finally {
            lock.unlock();
        }
    }
    
    // Retorna o número de carros na garagem
    public int getQuantidadeCarros() {
        lock.lock();
        try {
            return carros.size();
        } finally {
            lock.unlock();
        }
    }
    
    // Retorna o ID do cliente dono da garagem
    public String getIdCliente() {
        return idCliente;
    }
} 