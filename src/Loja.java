import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Loja implements Runnable {
    private final String id;
    private final EsteiraCircular esteira;
    private final String hostname;
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private int carrosVendidos = 0;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    public Loja(String id, String hostname, int port) {
        this.id = id;
        this.esteira = new EsteiraCircular(Integer.parseInt(id.substring(id.lastIndexOf('-') + 1)), id);
        this.hostname = hostname;
        this.port = port;
    }
    
    // Vende um carro para um cliente, esperando até que um carro esteja disponível ou até timeout
    public synchronized Carro venderCarro(long timeout, TimeUnit unit) throws InterruptedException {
        long endTime = System.currentTimeMillis() + unit.toMillis(timeout);
        
        while (esteira.getTamanhoAtual() == 0) {
            long waitTime = endTime - System.currentTimeMillis();
            if (waitTime <= 0) {
                // Timeout expirado, retorna null
                return null;
            }
            
            // Espera até que um carro esteja disponível ou até o timeout
            wait(waitTime);
            
            // Verifica novamente se ainda estamos executando
            if (!running.get() || Thread.currentThread().isInterrupted()) {
                return null;
            }
        }
        
        // Remove um carro da esteira
        Carro carro = esteira.removerCarro();
        carrosVendidos++;
        
        // Notifica outras threads que estão esperando
        notifyAll();
        
        return carro;
    }
    
    @Override
    public void run() {
        try {
            conectarFabrica();
            
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    // Solicita um carro da fábrica se houver espaço na esteira
                    if (esteira.temEspaco()) {
                        out.writeObject("SOLICITAR_CARRO");
                        
                        // Espera resposta
                        Object resposta = in.readObject();
                        
                        if (resposta instanceof Carro) {
                            Carro carro = (Carro) resposta;
                            System.out.println(id + " recebeu carro " + carro + " da fábrica");
                            esteira.adicionarCarro(carro);
                            
                            // Notifica threads que possam estar esperando por carros
                            synchronized(this) {
                                notifyAll();
                            }
                        } else if ("SEM_PRODUCAO".equals(resposta)) {
                            System.out.println(id + " aguardando produção de carros...");
                            // Aguarda um tempo antes de tentar novamente
                            Thread.sleep(3000);
                        }
                    } else {
                        // Esteira está cheia, aguarda um pouco
                        Thread.sleep(1000);
                    }
                    
                    // Pequeno intervalo entre solicitações
                    Thread.sleep(200);
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    if (running.get()) {
                        System.err.println("Erro na comunicação com a fábrica: " + e.getMessage());
                        // Tenta reconectar
                        try {
                            Thread.sleep(5000);
                            conectarFabrica();
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
            }
        } finally {
            desconectar();
        }
    }
    
    private void conectarFabrica() throws IllegalStateException {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            
            System.out.println(id + " conectando à fábrica em " + hostname + ":" + port);
            socket = new Socket(hostname, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println(id + " conectado à fábrica");
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível conectar à fábrica: " + e.getMessage(), e);
        }
    }
    
    public void parar() {
        running.set(false);
        desconectar();
        
        // Notifica todas as threads que possam estar esperando
        synchronized(this) {
            notifyAll();
        }
        
        System.out.println(id + " parada. Total de carros vendidos: " + carrosVendidos);
    }
    
    private void desconectar() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar conexões: " + e.getMessage());
        }
    }
    
    public String getId() {
        return id;
    }
    
    public int getCarrosVendidos() {
        return carrosVendidos;
    }
    
    public int getCarrosDisponiveisEsteira() {
        return esteira.getTamanhoAtual();
    }
} 