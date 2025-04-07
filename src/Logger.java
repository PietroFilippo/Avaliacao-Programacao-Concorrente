import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Logger para registrar informações de produção e venda de veículos
 */
public class Logger {
    private static final String ARQUIVO_LOG_PRODUCAO = "log_producao.txt";
    private static final String ARQUIVO_LOG_VENDAS = "log_vendas.txt";
    
    private static final Lock lockLogProducao = new ReentrantLock();
    private static final Lock lockLogVendas = new ReentrantLock();
    
    private static final SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Registra informações sobre um carro produzido na fábrica
     */
    public static void logProducaoCarro(Carro carro) {
        lockLogProducao.lock();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(ARQUIVO_LOG_PRODUCAO, true)))) {
            
            StringBuilder sb = new StringBuilder();
            sb.append(formatoData.format(new Date()))
              .append(" | ID: ").append(carro.getId())
              .append(" | Cor: ").append(carro.getCor())
              .append(" | Tipo: ").append(carro.getTipo())
              .append(" | Estação: ").append(carro.getIdEstacao())
              .append(" | Funcionário: ").append(carro.getIdFuncionario())
              .append(" | Posição na Esteira: ").append(carro.getPosicaoEsteiraFabrica());
            
            writer.println(sb.toString());
            System.out.println("Log de produção registrado: " + carro);
            
        } catch (IOException e) {
            System.err.println("Erro ao registrar log de produção: " + e.getMessage());
        } finally {
            lockLogProducao.unlock();
        }
    }
    
    /**
     * Registra informações sobre um carro vendido a uma loja
     */
    public static void logVendaCarro(Carro carro) {
        lockLogVendas.lock();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(ARQUIVO_LOG_VENDAS, true)))) {
            
            StringBuilder sb = new StringBuilder();
            sb.append(formatoData.format(new Date()))
              .append(" | ID: ").append(carro.getId())
              .append(" | Cor: ").append(carro.getCor())
              .append(" | Tipo: ").append(carro.getTipo())
              .append(" | Estação: ").append(carro.getIdEstacao())
              .append(" | Funcionário: ").append(carro.getIdFuncionario())
              .append(" | Posição na Esteira da Fábrica: ").append(carro.getPosicaoEsteiraFabrica())
              .append(" | Loja: ").append(carro.getIdLoja())
              .append(" | Posição na Esteira da Loja: ").append(carro.getPosicaoEsteiraLoja());
            
            writer.println(sb.toString());
            System.out.println("Log de venda registrado: Carro " + carro.getId() + " vendido para " + carro.getIdLoja());
            
        } catch (IOException e) {
            System.err.println("Erro ao registrar log de venda: " + e.getMessage());
        } finally {
            lockLogVendas.unlock();
        }
    }
} 