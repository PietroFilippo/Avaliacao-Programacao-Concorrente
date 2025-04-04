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
            while (!Thread.currentThread().isInterrupted()) {
                // Tenta adquirir as ferramentas necessárias
                if (adquirirFerramentas()) {
                    try {
                        // Verifica se há peças disponíveis
                        if (fabrica.solicitarPeca()) {
                            // Simula a produção do carro
                            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                                    " está produzindo um carro...");
                            Thread.sleep(random.nextInt(1000) + 500);
                            
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
                            Thread.sleep(2000);
                        }
                    } finally {
                        // Libera as ferramentas
                        liberarFerramentas();
                    }
                } else {
                    // Não conseguiu adquirir as ferramentas, aguarda um pouco e tenta novamente
                    Thread.sleep(random.nextInt(100) + 50);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Funcionário " + id + " da estação " + idEstacao + " foi interrompido.");
        }
    }
    
    // Tenta adquirir ambas as ferramentas
    private boolean adquirirFerramentas() {
        // Implementa uma solução para evitar deadlock - adquire a ferramenta da esquerda primeiro
        if (ferramentaEsquerda.pegar()) {
            System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                    " pegou a ferramenta da esquerda " + ferramentaEsquerda.getId());
            
            // Tenta adquirir a ferramenta da direita
            if (ferramentaDireita.pegar()) {
                System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                        " pegou a ferramenta da direita " + ferramentaDireita.getId());
                return true;
            } else {
                // Não conseguiu a ferramenta da direita, libera a da esquerda para evitar deadlock
                ferramentaEsquerda.soltar();
                System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                        " liberou a ferramenta da esquerda " + ferramentaEsquerda.getId() + " (não conseguiu a da direita)");
                return false;
            }
        }
        
        return false;
    }
    
    // Libera ambas as ferramentas
    private void liberarFerramentas() {
        ferramentaEsquerda.soltar();
        System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                " liberou a ferramenta da esquerda " + ferramentaEsquerda.getId());
        
        ferramentaDireita.soltar();
        System.out.println("Funcionário " + id + " da estação " + idEstacao + 
                " liberou a ferramenta da direita " + ferramentaDireita.getId());
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