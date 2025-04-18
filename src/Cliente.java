import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Cliente implements Runnable {
    private final String id;
    private final List<Loja> lojas;
    private final Garagem garagem;
    private final Random random = new Random();
    private volatile boolean running = true;
    private int carrosComprados = 0;
    
    public Cliente(int numeroCliente, List<Loja> lojas) {
        this.id = "Cliente-" + numeroCliente;
        this.lojas = lojas;
        this.garagem = new Garagem(id);
    }
    
    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                Loja lojaEscolhida = lojas.get(random.nextInt(lojas.size()));
                
                System.out.println(id + " está tentando comprar um carro da " + lojaEscolhida.getId());
                
                if (comprarCarro(lojaEscolhida)) {
                    Thread.sleep(random.nextInt(4000) + 1000);
                } else {
                    Thread.sleep(500);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(id + " foi interrompido");
        } finally {
            System.out.println(id + " encerrou. Total de carros comprados: " + carrosComprados + 
                    " (Na garagem: " + garagem.getQuantidadeCarros() + ")");
        }
    }
    
    private boolean comprarCarro(Loja loja) throws InterruptedException {
        try {
            Carro carro = loja.venderCarro(2, TimeUnit.SECONDS);
            
            if (carro != null) {
                int posicaoGaragem = garagem.adicionarCarro(carro);
                carrosComprados++;
                
                System.out.println(id + " comprou o carro " + carro + " da " + loja.getId() + 
                        " (Posição na garagem: " + posicaoGaragem + 
                        ", Total comprado: " + carrosComprados + ")");
                return true;
            } else {
                System.out.println(id + " não conseguiu comprar carro da " + loja.getId() + 
                        " (sem carros disponíveis)");
                return false;
            }
        } catch (Exception e) {
            System.out.println(id + " encontrou erro ao tentar comprar: " + e.getMessage());
            return false;
        }
    }
    
    public void parar() {
        this.running = false;
    }
    
    public String getId() {
        return id;
    }
    
    public int getCarrosComprados() {
        return carrosComprados;
    }
    
    public Garagem getGaragem() {
        return garagem;
    }
} 