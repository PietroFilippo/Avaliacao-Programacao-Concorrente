import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Cliente implements Runnable {
    private final int id;
    private final List<Loja> lojas;
    private final Random random = new Random();
    private volatile boolean running = true;
    private int carrosComprados = 0;
    
    public Cliente(int id, List<Loja> lojas) {
        this.id = id;
        this.lojas = lojas;
    }
    
    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                // Escolhe uma loja aleatoriamente
                Loja lojaEscolhida = lojas.get(random.nextInt(lojas.size()));
                
                System.out.println("Cliente " + id + " está tentando comprar um carro da " + lojaEscolhida.getId());
                
                // Tenta comprar um carro da loja escolhida
                if (comprarCarro(lojaEscolhida)) {
                    // Espera um tempo aleatório antes da próxima compra (1-5 segundos)
                    Thread.sleep(random.nextInt(4000) + 1000);
                } else {
                    // Se não conseguiu comprar, tenta novamente após um curto período
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Cliente " + id + " foi interrompido");
        } finally {
            System.out.println("Cliente " + id + " encerrou. Total de carros comprados: " + carrosComprados);
        }
    }
    
    private boolean comprarCarro(Loja loja) throws InterruptedException {
        try {
            // Tenta comprar um carro, com timeout para evitar bloqueio indefinido
            Carro carro = loja.venderCarro(2, TimeUnit.SECONDS);
            
            if (carro != null) {
                carrosComprados++;
                System.out.println("Cliente " + id + " comprou o carro " + carro + " da " + loja.getId() + 
                        " (Total comprado: " + carrosComprados + ")");
                return true;
            } else {
                System.out.println("Cliente " + id + " não conseguiu comprar carro da " + loja.getId() + 
                        " (sem carros disponíveis)");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Cliente " + id + " encontrou erro ao tentar comprar: " + e.getMessage());
            return false;
        }
    }
    
    public void parar() {
        this.running = false;
    }
    
    public int getId() {
        return id;
    }
    
    public int getCarrosComprados() {
        return carrosComprados;
    }
} 