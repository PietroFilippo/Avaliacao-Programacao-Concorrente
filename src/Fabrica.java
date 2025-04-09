import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class Fabrica {
    private static final int MAX_PECAS = 500;
    private static final int CAPACIDADE_ESTEIRA = 5;
    
    private final AtomicInteger estoquePecas = new AtomicInteger(MAX_PECAS);
    private final Semaphore esteiraTransporte = new Semaphore(CAPACIDADE_ESTEIRA, true);
    private final AtomicBoolean estoqueVazioMessageDisplayed = new AtomicBoolean(false);
    private volatile boolean encerrada = false;
    

    public boolean solicitarPeca() throws InterruptedException {
        if (encerrada) {
            return false;
        }
        
        if (!esteiraTransporte.tryAcquire(100, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            return false;
        }
        
        try {
            int pecasAtuais = estoquePecas.get();
            if (pecasAtuais > 0) {
                boolean sucesso = estoquePecas.compareAndSet(pecasAtuais, pecasAtuais - 1);
                if (sucesso) {
                    estoqueVazioMessageDisplayed.set(false);
                    System.out.println("Parte obtida do estoque. Peças restantes: " + estoquePecas.get());
                    return true;
                } else {
                    return solicitarPeca();
                }
            } else {
                if (!estoqueVazioMessageDisplayed.getAndSet(true)) {
                    System.out.println("Não há peças disponíveis no estoque!");
                }
                return false;
            }
        } finally {
            esteiraTransporte.release();
        }
    }
    
    public void encerrar() {
        this.encerrada = true;
        System.out.println("Fábrica encerrada. Não aceitando mais solicitações de peças.");
    }

    public int getQuantidadePecas() {
        return estoquePecas.get();
    }
} 