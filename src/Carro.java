import java.io.Serializable;

// Representa um carro produzido por um funcionário em uma estação de produção
public class Carro implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int proximoId = 1;
    
    private final int id;
    private final int idEstacao;
    private final int idFuncionario;
    
    public Carro(int idEstacao, int idFuncionario) {
        this.id = proximoId++;
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
    }
    
    public int getId() {
        return id;
    }
    
    public int getIdEstacao() {
        return idEstacao;
    }
    
    public int getIdFuncionario() {
        return idFuncionario;
    }
    
    @Override
    public String toString() {
        return "Carro-" + id + " (Estação: " + idEstacao + ", Funcionário: " + idFuncionario + ")";
    }
} 