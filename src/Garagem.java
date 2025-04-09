import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Garagem {
    private final String idCliente;
    private final List<Carro> carros;
    private final Lock lock = new ReentrantLock();
    
    public Garagem(String idCliente) {
        this.idCliente = idCliente;
        this.carros = new ArrayList<>();
    }
    
    public int adicionarCarro(Carro carro) {
        lock.lock();
        try {
            carro.setIdCliente(idCliente);
            int posicao = carros.size() + 1;
            carro.setPosicaoGaragem(posicao);
            carros.add(carro);
            
            Logger.logVendaCarroLoja(carro);
            
            return posicao;
        } finally {
            lock.unlock();
        }
    }
    
    public List<Carro> getCarros() {
        lock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(carros));
        } finally {
            lock.unlock();
        }
    }
    
    public int getQuantidadeCarros() {
        lock.lock();
        try {
            return carros.size();
        } finally {
            lock.unlock();
        }
    }
    
    public String getIdCliente() {
        return idCliente;
    }
} 