import java.util.Random;

// Representa um funcionário em uma estação de produção que precisa de duas ferramentas para produzir um carro
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
                // Verifica rapidamente se foi interrompido
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                
                // Tenta adquirir as ferramentas necessárias
                if (adquirirFerramentas()) {
                    try {
                        // Verifica se há peças disponíveis
                        if (fabrica.solicitarPeca()) {
                            // Simula a produção do carro
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " está produzindo um carro...");
                            // Verifica interrupção durante o sleep
                            for (int i = 0; i < 5; i++) {
                                if (!running || Thread.currentThread().isInterrupted()) {
                                    return;
                                }
                                Thread.sleep(random.nextInt(200) + 100);
                            }
                            
                            // Cria um novo carro
                            Carro carro = new Carro(idEstacao, id);
                            
                            // Adiciona o carro à esteira
                            esteira.adicionarCarro(carro);
                            
                            // Incrementa o contador de carros produzidos
                            carrosProduzidos++;
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " produziu o carro " + carro.getId() + " (Total: " + carrosProduzidos + ")");
                        } else {
                            // Não há peças disponíveis, espera um pouco
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " aguardando peças disponíveis.");
                            // Espera com verificação de interrupção
                            for (int i = 0; i < 10 && running && !Thread.currentThread().isInterrupted(); i++) {
                                Thread.sleep(200);
                            }
                        }
                    } finally {
                        // Libera as ferramentas
                        liberarFerramentas();
                    }
                } else {
                    // Não conseguiu adquirir as ferramentas, aguarda um tempo aleatório antes de tentar novamente
                    // Adiciona uma espera com um tempo aleatório para reduzir contenção
                    Thread.sleep(random.nextInt(100) + id * 20);
                }
            }
        } catch (InterruptedException e) {
            // Tratamento adequado de interrupção
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Funcionário " + id + " da estação " + idEstacao + " foi encerrado. Carros produzidos: " + carrosProduzidos);
        }
    }
    
    // Tenta adquirir ambas as ferramentas com uma estratégia para evitar deadlock e starvation
    private boolean adquirirFerramentas() throws InterruptedException {
        // Verifica se foi interrompido antes de tentar adquirir
        if (!running || Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Thread interrompida");
        }
        
        // Para quebrar a simetria que causa deadlock, introduz ordem aleatória de aquisição
        Ferramenta primeira, segunda;
        
        // Randomiza a ordem de aquisição para evitar um padrão que leve à starvation
        if (random.nextBoolean()) {
            primeira = ferramentaEsquerda;
            segunda = ferramentaDireita;
        } else {
            primeira = ferramentaDireita;
            segunda = ferramentaEsquerda;
        }
        
        // Tenta pegar a primeira ferramenta com timeout
        if (primeira.pegar()) {
            try {
                // Verifica novamente interrupção
                if (!running || Thread.currentThread().isInterrupted()) {
                    primeira.soltar();
                    throw new InterruptedException("Thread interrompida");
                }
                
                // Tenta pegar a segunda ferramenta com timeout - espera no máximo 100ms
                if (segunda.pegar()) {
                    return true;
                } else {
                    // Não conseguiu a segunda ferramenta, libera a primeira
                    primeira.soltar();
                    return false;
                }
            } catch (Exception e) {
                // Se ocorrer algum erro, libera a primeira ferramenta
                primeira.soltar();
                throw e;
            }
        }
        
        return false;
    }
    
    // Libera ambas as ferramentas
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
    
    // Sinaliza para o funcionário parar o trabalho
    public void parar() {
        this.running = false;
    }
    
    // Obtém o ID do funcionário
    public int getId() {
        return id;
    }
    
    // Obtém o ID da estação onde o funcionário trabalha
    public int getIdEstacao() {
        return idEstacao;
    }
    
    // Obtém o número de carros produzidos pelo funcionário
    public int getCarrosProduzidos() {
        return carrosProduzidos;
    }
} 