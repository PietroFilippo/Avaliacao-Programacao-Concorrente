import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
        
        for (int i = 0; i < NUM_FUNCIONARIOS; i++) {
            ferramentas.add(new Ferramenta(i + 1));
        }
        
        for (int i = 0; i < NUM_FUNCIONARIOS; i++) {
            Ferramenta ferramentaEsquerda = ferramentas.get(i);
            Ferramenta ferramentaDireita = ferramentas.get((i + 1) % NUM_FUNCIONARIOS);
            
            Funcionario funcionario = new Funcionario(i + 1, idEstacao, ferramentaEsquerda, ferramentaDireita, esteira, fabrica);
            funcionarios.add(funcionario);
        }
    }
    
    public void iniciar() {
        for (Funcionario funcionario : funcionarios) {
            executorService.submit(funcionario);
        }
        System.out.println("Estação de produção " + idEstacao + " iniciada com " + NUM_FUNCIONARIOS + " funcionários.");
    }
    
    public void parar() {
        System.out.println("Iniciando encerramento da estação de produção " + idEstacao + "...");
        
        for (Funcionario funcionario : funcionarios) {
            funcionario.parar();
        }
        
        executorService.shutdown();
        
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Alguns funcionários não terminaram a tempo, forçando encerramento...");
                executorService.shutdownNow();
                
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Não foi possível encerrar todos os funcionários da estação " + idEstacao);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
        
        System.out.println("Estação de produção " + idEstacao + " parada.");
    }
    
    public int getIdEstacao() {
        return idEstacao;
    }
    
    public EsteiraCircular getEsteira() {
        return esteira;
    }
    
    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
    
    public int getTotalCarrosProduzidos() {
        int total = 0;
        for (Funcionario funcionario : funcionarios) {
            total += funcionario.getCarrosProduzidos();
        }
        return total;
    }
    
    public Fabrica getFabrica() {
        return fabrica;
    }
} 