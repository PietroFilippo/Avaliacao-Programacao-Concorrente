import java.util.concurrent.TimeUnit;
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
        try {
            // Tenta adquirir o lock com timeout de 100ms para evitar espera indefinida
            return lock.tryLock(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public void soltar() {
        try {
            if (lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                lock.unlock(); // Já tinha o lock, libera o extra
            }
            lock.unlock(); // Libera o lock original
        } catch (Exception e) {
            // Ignora erro se o lock já estava liberado ou não era possuído por esta thread
        }
    }
    
    @Override
    public String toString() {
        return "Ferramenta-" + id;
    }
} 