import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.TimeUnit;

public class Loja implements Runnable {
    private final String id;
    private final EsteiraCircular esteira;
    private final String hostname;
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private int carrosComprados = 0;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final ExecutorService clienteExecutor;
    private static final int NUM_CLIENTES = 3;
    
    public Loja(String id, String hostname, int port) {
        this.id = id;
        this.esteira = new EsteiraCircular(Integer.parseInt(id.substring(id.lastIndexOf('-') + 1)));
        this.hostname = hostname;
        this.port = port;
        this.clienteExecutor = Executors.newFixedThreadPool(NUM_CLIENTES);
        
        // Inicia threads de clientes que compram carros da esteira
        for (int i = 0; i < NUM_CLIENTES; i++) {
            final int clienteId = i + 1;
            clienteExecutor.submit(() -> {
                try {
                    while (running.get() && !Thread.currentThread().isInterrupted()) {
                        Carro carro = esteira.removerCarro();
                        carrosComprados++;
                        System.out.println("Cliente " + clienteId + " da " + id + " comprou " + carro);
                        // Simula tempo para próxima compra
                        Thread.sleep((long) (Math.random() * 5000) + 1000);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
    
    @Override
    public void run() {
        try {
            conectarFabrica();
            
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    // Solicita um carro da fábrica
                    out.writeObject("SOLICITAR_CARRO");
                    
                    // Espera resposta
                    Object resposta = in.readObject();
                    
                    if (resposta instanceof Carro) {
                        Carro carro = (Carro) resposta;
                        System.out.println(id + " recebeu carro " + carro + " da fábrica");
                        esteira.adicionarCarro(carro);
                    } else if ("SEM_PRODUCAO".equals(resposta)) {
                        System.out.println(id + " aguardando produção de carros...");
                        // Aguarda um tempo antes de tentar novamente
                        Thread.sleep(3000);
                    }
                    
                    // Pequeno intervalo entre solicitações
                    Thread.sleep(500);
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
        clienteExecutor.shutdownNow();
        try {
            clienteExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println(id + " parada. Total de carros comprados: " + carrosComprados);
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
    
    public int getCarrosComprados() {
        return carrosComprados;
    }
} 