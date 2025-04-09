import java.io.Serializable;

public class Carro implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum Cor { VERMELHO, VERDE, AZUL }
    public enum Tipo { SUV, SEDAN }
    
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
    
    private String idCliente;
    private Integer posicaoGaragem;
    
    public Carro(Integer idEstacao, Integer idFuncionario) {
        this.id = proximoId++;
        this.idEstacao = idEstacao;
        this.idFuncionario = idFuncionario;
        
        this.cor = Cor.values()[contadorCor];
        contadorCor = (contadorCor + 1) % Cor.values().length;
        
        this.tipo = Tipo.values()[contadorTipo];
        contadorTipo = (contadorTipo + 1) % Tipo.values().length;
    }

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