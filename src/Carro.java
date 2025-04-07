import java.io.Serializable;

// Representa um carro produzido por um funcionário em uma estação de produção
public class Carro implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int proximoId = 1;
    
    // Enumerações para cor e tipo de veículo
    public enum Cor { VERMELHO, VERDE, AZUL }
    public enum Tipo { SUV, SEDAN }
    
    // Contadores estáticos para alternar cor e tipo
    private static int contadorCor = 0;
    private static int contadorTipo = 0;
    
    private final int id;
    private final int idEstacao;
    private final int idFuncionario;
    private final Cor cor;
    private final Tipo tipo;
    private int posicaoEsteiraFabrica = -1;
    private String idLoja = null;
    private int posicaoEsteiraLoja = -1;
    
    public Carro(int idEstacao, int idFuncionario) {
        this.id = proximoId++;
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
        
        // Alternar cores (VERMELHO, VERDE, AZUL)
        this.cor = Cor.values()[contadorCor];
        contadorCor = (contadorCor + 1) % 3;
        
        // Alternar tipos (SUV, SEDAN)
        this.tipo = Tipo.values()[contadorTipo];
        contadorTipo = (contadorTipo + 1) % 2;
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
    
    public Cor getCor() {
        return cor;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public int getPosicaoEsteiraFabrica() {
        return posicaoEsteiraFabrica;
    }
    
    public void setPosicaoEsteiraFabrica(int posicaoEsteiraFabrica) {
        this.posicaoEsteiraFabrica = posicaoEsteiraFabrica;
    }
    
    public String getIdLoja() {
        return idLoja;
    }
    
    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }
    
    public int getPosicaoEsteiraLoja() {
        return posicaoEsteiraLoja;
    }
    
    public void setPosicaoEsteiraLoja(int posicaoEsteiraLoja) {
        this.posicaoEsteiraLoja = posicaoEsteiraLoja;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro-").append(id)
          .append(" (Tipo: ").append(tipo)
          .append(", Cor: ").append(cor)
          .append(", Estação: ").append(idEstacao)
          .append(", Funcionário: ").append(idFuncionario).append(")");
        
        return sb.toString();
    }
} 