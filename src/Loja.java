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
    
    public Carro venderCarro(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            Carro carro = esteira.removerCarro(timeout, unit);
            if (carro != null) {
                carrosVendidos++;
                System.out.println(id + " vendeu o carro " + carro + " (Total vendido: " + carrosVendidos + ")");
            }
            return carro;
        } catch (InterruptedException e) {
            System.out.println(id + " interrompido ao tentar vender carro");
            throw e;
        }
    }
    
    @Override
    public void run() {
        try {
            conectarFabrica();
            
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    if (esteira.temEspaco()) {
                        out.writeObject("SOLICITAR_CARRO");
                        
                        Object resposta = in.readObject();
                        
                        if (resposta instanceof Carro) {
                            Carro carro = (Carro) resposta;
                            System.out.println(id + " recebeu carro " + carro + " da fábrica");
                            esteira.adicionarCarro(carro);
                            
                            synchronized(this) {
                                notifyAll();
                            }
                        } else if ("SEM_PRODUCAO".equals(resposta)) {
                            System.out.println(id + " aguardando produção de carros...");
                            Thread.sleep(3000);
                        }
                    } else {
                        Thread.sleep(1000);
                    }
                    
                    Thread.sleep(200);
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    if (running.get()) {
                        System.err.println("Erro na comunicação com a fábrica: " + e.getMessage());
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