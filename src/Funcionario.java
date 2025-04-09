import java.util.Random;

public class Funcionario implements Runnable {
    private final int id;
    private final int idEstacao;
    private final Ferramenta ferramentaEsquerda;
    private final Ferramenta ferramentaDireita;
    private final EsteiraCircular esteira;
    private final Fabrica fabrica;
    private final Random random = new Random();
    private volatile boolean running = true;
    
    private int carrosProduzidos = 0;
    
    public Funcionario(int id, int idEstacao, Ferramenta ferramentaEsquerda, Ferramenta ferramentaDireita, 
                      EsteiraCircular esteira, Fabrica fabrica) {
        this.id = id;
        this.idEstacao = idEstacao;
        this.ferramentaEsquerda = ferramentaEsquerda;
        this.ferramentaDireita = ferramentaDireita;
        this.esteira = esteira;
        this.fabrica = fabrica;
    }
    
    @Override
    public void run() {
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                
                if (adquirirFerramentas()) {
                    try {
                        if (fabrica.solicitarPeca()) {
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " está produzindo um carro...");
                            for (int i = 0; i < 5; i++) {
                                if (!running || Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                Thread.sleep(random.nextInt(200) + 100);
                            }
                            
                            Carro carro = new Carro(idEstacao, id);
                            
                            esteira.adicionarCarro(carro);
                            
                            carrosProduzidos++;
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " produziu o carro " + carro.getId() + " (Total: " + carrosProduzidos + ")");
                        } else {
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " aguardando peças disponíveis.");
                            for (int i = 0; i < 10 && running && !Thread.currentThread().isInterrupted(); i++) {
                                Thread.sleep(200);
                            }
                        }
                    } finally {
                        liberarFerramentas();
                    }
                } else {
                    Thread.sleep(random.nextInt(100) + id * 20);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Funcionário " + id + " da estação " + idEstacao + " foi encerrado. Carros produzidos: " + carrosProduzidos);
        }
    }
    
    private boolean adquirirFerramentas() throws InterruptedException {
        if (!running || Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Thread interrompida");
        }
        
        Ferramenta primeira, segunda;
        
        if (random.nextBoolean()) {
            primeira = ferramentaEsquerda;
            segunda = ferramentaDireita;
        } else {
            primeira = ferramentaDireita;
            segunda = ferramentaEsquerda;
        }
        
        if (primeira.pegar()) {
            try {
                if (!running || Thread.currentThread().isInterrupted()) {
                    primeira.soltar();
                    throw new InterruptedException("Thread interrompida");
                }
                
                if (segunda.pegar()) {
                    return true;
                } else {
                    primeira.soltar();
                    return false;
                }
            } catch (Exception e) {
                primeira.soltar();
                throw e;
            }
        }
        
        return false;
    }
    
    private void liberarFerramentas() {
        try {
            ferramentaEsquerda.soltar();
        } catch (Exception e) {
            System.err.println("Erro ao liberar ferramenta esquerda: " + e.getMessage());
        }
        
        try {
            ferramentaDireita.soltar();
        } catch (Exception e) {
            System.err.println("Erro ao liberar ferramenta direita: " + e.getMessage());
        }
    }
    
    public void parar() {
        this.running = false;
    }
    
    public int getId() {
        return id;
    }
    
    public int getIdEstacao() {
        return idEstacao;
    }
    
    public int getCarrosProduzidos() {
        return carrosProduzidos;
    }
} 