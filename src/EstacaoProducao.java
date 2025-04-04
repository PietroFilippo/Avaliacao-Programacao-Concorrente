import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Representa uma estação de produção com 5 funcionários dispostos em círculo, cada um com uma ferramenta à esquerda e à direita
public class EstacaoProducao {
    private static final int NUM_FUNCIONARIOS = 5;
    
    private final int idEstacao;
    private final Fabrica fabrica;
    private final EsteiraCircular esteira;
    private final List<Ferramenta> ferramentas = new ArrayList<>();
    private final List<Funcionario> funcionarios = new ArrayList<>();
    private final ExecutorService executorService;
    
    public EstacaoProducao(int idEstacao, Fabrica fabrica) {
        this.idEstacao = idEstacao;
        this.fabrica = fabrica;
        this.esteira = new EsteiraCircular(idEstacao);
        this.executorService = Executors.newFixedThreadPool(NUM_FUNCIONARIOS);
        
        // Cria as ferramentas
        for (int i = 0; i < NUM_FUNCIONARIOS; i++) {
            ferramentas.add(new Ferramenta(i + 1));
        }
        
        // Cria os funcionários em arranjo circular
        for (int i = 0; i < NUM_FUNCIONARIOS; i++) {
            // Para cada funcionário, a ferramenta da esquerda é a sua própria, 
            // e a da direita é a do próximo funcionário no círculo
            Ferramenta ferramentaEsquerda = ferramentas.get(i);
            Ferramenta ferramentaDireita = ferramentas.get((i + 1) % NUM_FUNCIONARIOS);
            
            Funcionario funcionario = new Funcionario(i + 1, idEstacao, ferramentaEsquerda, ferramentaDireita, esteira, fabrica);
            funcionarios.add(funcionario);
        }
    }
    
    // Inicia todos os funcionários da estação
    public void iniciar() {
        for (Funcionario funcionario : funcionarios) {
            executorService.submit(funcionario);
        }
        System.out.println("Estação de produção " + idEstacao + " iniciada com " + NUM_FUNCIONARIOS + " funcionários.");
    }
    
    // Para todos os funcionários da estação
    public void parar() {
        System.out.println("Iniciando encerramento da estação de produção " + idEstacao + "...");
        
        // Primeiro sinaliza a todos os funcionários para pararem
        for (Funcionario funcionario : funcionarios) {
            funcionario.parar();
        }
        
        // Primeiro tenta shutdown normal
        executorService.shutdown();
        
        try {
            // Aguarda por até 5 segundos para término normal
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Alguns funcionários não terminaram a tempo, forçando encerramento...");
                // Se não terminar em 5 segundos, força encerramento
                executorService.shutdownNow();
                
                // Aguarda mais 5 segundos pela interrupção
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Não foi possível encerrar todos os funcionários da estação " + idEstacao);
                }
            }
        } catch (InterruptedException e) {
            // Preserva o status de interrupção
            Thread.currentThread().interrupt();
            // Força encerramento em caso de interrupção
            executorService.shutdownNow();
        }
        
        System.out.println("Estação de produção " + idEstacao + " parada.");
    }
    
    // Obtém o ID da estação
    public int getIdEstacao() {
        return idEstacao;
    }
    
    // Obtém o transportador circular associado à estação
    public EsteiraCircular getEsteira() {
        return esteira;
    }
    

    // Obtém a lista de funcionários da estação
    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
    
    // Obtém o número total de carros produzidos pela estação
    public int getTotalCarrosProduzidos() {
        int total = 0;
        for (Funcionario funcionario : funcionarios) {
            total += funcionario.getCarrosProduzidos();
        }
        return total;
    }
    
    // Obtém a fábrica associada à estação
    public Fabrica getFabrica() {
        return fabrica;
    }
} 