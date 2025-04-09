import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServidorFabrica implements Runnable {
    private final int porta;
    private final ExecutorService threadPool;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private ServerSocket serverSocket;
    private final LinkedBlockingQueue<Carro> carrosDisponiveis = new LinkedBlockingQueue<>();
    
    public ServidorFabrica(int porta, Fabrica fabrica) {
        this.porta = porta;
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(porta);
            System.out.println("Servidor da fábrica iniciado na porta " + porta);
            
            threadPool.submit(this::coletarCarrosDasEsteiras);
            
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Nova conexão de loja: " + clientSocket.getInetAddress());
                    
                    threadPool.submit(() -> atenderLoja(clientSocket));
                } catch (IOException e) {
                    if (running.get()) {
                        System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor da fábrica: " + e.getMessage());
        } finally {
            parar();
        }
    }
    
    private void coletarCarrosDasEsteiras() {
        try {
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                for (EstacaoProducao estacao : SimulacaoProducaoVeiculos.getEstacoesProducao()) {
                    EsteiraCircular esteira = estacao.getEsteira();
                    
                    if (esteira.getTamanhoAtual() > 0) {
                        try {
                            Carro carro = esteira.removerCarro();
                            carrosDisponiveis.put(carro);
                            System.out.println("Carro " + carro + " disponibilizado para venda às lojas");
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao coletar carros das esteiras: " + e.getMessage());
        }
    }
    
    private void atenderLoja(Socket clientSocket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            while (running.get() && !Thread.currentThread().isInterrupted() && !clientSocket.isClosed()) {
                try {
                    Object solicitacao = in.readObject();
                    
                    if ("SOLICITAR_CARRO".equals(solicitacao)) {
                        Carro carro = carrosDisponiveis.poll(1, TimeUnit.SECONDS);
                        
                        if (carro != null) {
                            out.writeObject(carro);
                            out.flush();
                            System.out.println("Carro " + carro + " enviado para " + clientSocket.getInetAddress());
                        } else {
                            out.writeObject("SEM_PRODUCAO");
                            out.flush();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erro na comunicação com loja: " + e.getMessage());
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao manipular conexão com loja: " + e.getMessage());
        } finally {
            try {
                if (!clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }
    
    public void adicionarCarroDisponivel(Carro carro) {
        try {
            carrosDisponiveis.put(carro);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void parar() {
        running.set(false);
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar servidor: " + e.getMessage());
            }
        }
        
        threadPool.shutdownNow();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("Servidor da fábrica encerrado.");
    }
} 