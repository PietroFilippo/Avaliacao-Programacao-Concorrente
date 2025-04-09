import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ferramenta {
    private final int id;
    private final Lock lock = new ReentrantLock(true);
    
    public Ferramenta(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
    
    public boolean pegar() {
        try {
            return lock.tryLock(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public void soltar() {
        try {
            if (lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                lock.unlock(); 
            }
            lock.unlock(); 
        } catch (Exception e) {
        }
    }
    
    @Override
    public String toString() {
        return "Ferramenta-" + id;
    }
} 