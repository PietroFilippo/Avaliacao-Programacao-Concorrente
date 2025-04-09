import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Logger {
    private static final String ARQUIVO_LOG_PRODUCAO = "log_producao.txt";
    private static final String ARQUIVO_LOG_VENDAS_FABRICA = "log_vendas_fabrica.txt";
    private static final String ARQUIVO_LOG_RECEBIMENTO_LOJA = "log_recebimento_loja_%s.txt";
    private static final String ARQUIVO_LOG_VENDAS_LOJA = "log_vendas_loja_%s.txt";
    
    private static final Lock lockLogProducao = new ReentrantLock();
    private static final Lock lockLogVendasFabrica = new ReentrantLock();
    private static final Lock lockLogRecebimentoLoja = new ReentrantLock();
    private static final Lock lockLogVendasLoja = new ReentrantLock();
    
    private static final SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
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
    
    public static void logVendaCarro(Carro carro) {
        lockLogVendasFabrica.lock();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(ARQUIVO_LOG_VENDAS_FABRICA, true)))) {
            
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
            System.out.println("Log de venda da fábrica registrado: Carro " + carro.getId() + " vendido para " + carro.getIdLoja());
            
        } catch (IOException e) {
            System.err.println("Erro ao registrar log de venda da fábrica: " + e.getMessage());
        } finally {
            lockLogVendasFabrica.unlock();
        }
        
        logRecebimentoCarroLoja(carro);
    }
    
    public static void logRecebimentoCarroLoja(Carro carro) {
        if (carro.getIdLoja() == null) {
            System.err.println("Erro: Tentativa de registrar recebimento de carro sem ID de loja");
            return;
        }
        
        lockLogRecebimentoLoja.lock();
        try {
            String nomeArquivo = String.format(ARQUIVO_LOG_RECEBIMENTO_LOJA, carro.getIdLoja().replace("-", "_"));
            
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(nomeArquivo, true)))) {
                
                StringBuilder sb = new StringBuilder();
                sb.append(formatoData.format(new Date()))
                  .append(" | ID: ").append(carro.getId())
                  .append(" | Cor: ").append(carro.getCor())
                  .append(" | Tipo: ").append(carro.getTipo())
                  .append(" | Estação de Produção: ").append(carro.getIdEstacao())
                  .append(" | Funcionário Produtor: ").append(carro.getIdFuncionario())
                  .append(" | Posição na Esteira da Fábrica: ").append(carro.getPosicaoEsteiraFabrica())
                  .append(" | Posição na Esteira da Loja: ").append(carro.getPosicaoEsteiraLoja());
                
                writer.println(sb.toString());
                
            } catch (IOException e) {
                System.err.println("Erro ao registrar log de recebimento de carro pela loja: " + e.getMessage());
            }
        } finally {
            lockLogRecebimentoLoja.unlock();
        }
    }
    
    public static void logVendaCarroLoja(Carro carro) {
        if (carro.getIdLoja() == null || carro.getIdCliente() == null) {
            System.err.println("Erro: Tentativa de registrar venda de carro sem ID de loja ou cliente");
            return;
        }
        
        lockLogVendasLoja.lock();
        try {
            String nomeArquivo = String.format(ARQUIVO_LOG_VENDAS_LOJA, carro.getIdLoja().replace("-", "_"));
            
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new FileWriter(nomeArquivo, true)))) {
                
                StringBuilder sb = new StringBuilder();
                sb.append(formatoData.format(new Date()))
                  .append(" | ID: ").append(carro.getId())
                  .append(" | Cor: ").append(carro.getCor())
                  .append(" | Tipo: ").append(carro.getTipo())
                  .append(" | Estação de Produção: ").append(carro.getIdEstacao())
                  .append(" | Funcionário Produtor: ").append(carro.getIdFuncionario())
                  .append(" | Posição na Esteira da Fábrica: ").append(carro.getPosicaoEsteiraFabrica())
                  .append(" | Posição na Esteira da Loja: ").append(carro.getPosicaoEsteiraLoja())
                  .append(" | ID do Cliente: ").append(carro.getIdCliente())
                  .append(" | Posição na Garagem: ").append(carro.getPosicaoGaragem());
                
                writer.println(sb.toString());
                System.out.println("Log de venda da loja " + carro.getIdLoja() + " registrado: Carro " 
                        + carro.getId() + " vendido para Cliente " + carro.getIdCliente());
                
            } catch (IOException e) {
                System.err.println("Erro ao registrar log de venda de carro pela loja: " + e.getMessage());
            }
        } finally {
            lockLogVendasLoja.unlock();
        }
    }
} 