import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Representa uma ferramenta usada por funcionários nas estações de produção
public class Ferramenta {
    private final int id;
    private final Lock lock = new ReentrantLock(true); // Lock justo para evitar starvation
    
    public Ferramenta(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean pegar() {
        return lock.tryLock();
    }
    
    public void soltar() {
        lock.unlock();
    }
    
    @Override
    public String toString() {
        return "Ferramenta-" + id;
    }
} 