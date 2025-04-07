import java.io.Serializable;

// Representa um carro produzido por um funcionário em uma estação de produção
public class Carro implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Enums para Cor e Tipo
    public enum Cor { VERMELHO, VERDE, AZUL }
    public enum Tipo { SUV, SEDAN }
    
    // Contadores para alternar cor e tipo
    private static int contadorCor = 0;
    private static int contadorTipo = 0;
    private static int proximoId = 1;
    
    private final Integer id;
    private final Integer idEstacao;
    private final Integer idFuncionario;
    private final Cor cor;
    private final Tipo tipo;
    
    private String idLoja;
    private Integer posicaoEsteiraFabrica;
    private Integer posicaoEsteiraLoja;
    
    // Atributos relacionados ao cliente
    private String idCliente;
    private Integer posicaoGaragem;
    
    // Construtor
    public Carro(Integer idEstacao, Integer idFuncionario) {
        this.id = proximoId++;
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
        
        // Alternância de cor: VERMELHO -> VERDE -> AZUL -> VERMELHO ...
        this.cor = Cor.values()[contadorCor];
        contadorCor = (contadorCor + 1) % Cor.values().length;
        
        // Alternância de tipo: SUV -> SEDAN -> SUV ...
        this.tipo = Tipo.values()[contadorTipo];
        contadorTipo = (contadorTipo + 1) % Tipo.values().length;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Integer getIdEstacao() {
        return idEstacao;
    }

    public Integer getIdFuncionario() {
        return idFuncionario;
    }
    
    public Cor getCor() {
        return cor;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public String getIdLoja() {
        return idLoja;
    }
    
    public void setIdLoja(String idLoja) {
        this.idLoja = idLoja;
    }
    
    public Integer getPosicaoEsteiraFabrica() {
        return posicaoEsteiraFabrica;
    }
    
    public void setPosicaoEsteiraFabrica(Integer posicaoEsteiraFabrica) {
        this.posicaoEsteiraFabrica = posicaoEsteiraFabrica;
    }
    
    public Integer getPosicaoEsteiraLoja() {
        return posicaoEsteiraLoja;
    }
    
    public void setPosicaoEsteiraLoja(Integer posicaoEsteiraLoja) {
        this.posicaoEsteiraLoja = posicaoEsteiraLoja;
    }
    
    // Métodos relacionados ao cliente
    public String getIdCliente() {
        return idCliente;
    }
    
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
    
    public Integer getPosicaoGaragem() {
        return posicaoGaragem;
    }
    
    public void setPosicaoGaragem(Integer posicaoGaragem) {
        this.posicaoGaragem = posicaoGaragem;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carro #").append(id)
          .append(" [").append(cor).append(", ").append(tipo).append("]")
          .append(", Produzido por: Est.").append(idEstacao)
          .append(", Func.").append(idFuncionario);
        
        if (idLoja != null) {
            sb.append(", Loja: ").append(idLoja);
        }
        
        if (idCliente != null) {
            sb.append(", Cliente: ").append(idCliente);
            if (posicaoGaragem != null) {
                sb.append(" (Pos. Garagem: ").append(posicaoGaragem).append(")");
            }
        }
        
        return sb.toString();
    }
} 