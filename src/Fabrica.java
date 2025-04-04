import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;


// Classe que simula uma fábrica de veículos com um estoque limitado de peças e uma esteira para estações de montagem
public class Fabrica {
    private static final int MAX_PECAS = 500;
    private static final int CAPACIDADE_ESTEIRA = 5;
    
    private final AtomicInteger estoquePecas = new AtomicInteger(MAX_PECAS);
    private final Semaphore esteiraTransporte = new Semaphore(CAPACIDADE_ESTEIRA, true);
    private final AtomicBoolean estoqueVazioMessageDisplayed = new AtomicBoolean(false);
    private volatile boolean encerrada = false;
    

    // Solicita uma peça do estoque da fábrica
    public boolean solicitarPeca() throws InterruptedException {
        // Se a fábrica foi encerrada, retorna false imediatamente
        if (encerrada) {
            return false;
        }
        
        // Tenta adquirir um slot na esteira com timeout para evitar bloqueio indefinido
        if (!esteiraTransporte.tryAcquire(100, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            return false;
        }
        
        try {
            // Verifica se há peças disponíveis no estoque
            int pecasAtuais = estoquePecas.get();
            if (pecasAtuais > 0) {
                // Decrementa o contador de peças
                boolean sucesso = estoquePecas.compareAndSet(pecasAtuais, pecasAtuais - 1);
                if (sucesso) {
                    // Resetamos o flag pois agora temos peças disponíveis novamente
                    estoqueVazioMessageDisplayed.set(false);
                    System.out.println("Parte obtida do estoque. Peças restantes: " + estoquePecas.get());
                    return true;
                } else {
                    // Aviso se alguém modificou o estoque
                    return solicitarPeca();
                }
            } else {
                // Mostra a mensagem de falta de peças apenas se não foi mostrada recentemente
                if (!estoqueVazioMessageDisplayed.getAndSet(true)) {
                    System.out.println("Não há peças disponíveis no estoque!");
                }
                return false;
            }
        } finally {
            // Libera o slot na esteira
            esteiraTransporte.release();
        }
    }
    

    // Adiciona peças ao estoque (não excedendo a capacidade máxima)
    public int reabastecerPecas(int quantidade) {
        if (encerrada) {
            return 0;
        }
        
        int pecasAtuais = estoquePecas.get();
        int pecasParaAdicionar = Math.min(quantidade, MAX_PECAS - pecasAtuais);
        
        if (pecasParaAdicionar > 0) {
            estoquePecas.addAndGet(pecasParaAdicionar);
            System.out.println("Adicionadas " + pecasParaAdicionar + " peças ao estoque. Total atual: " + estoquePecas.get());
            // Resetamos o flag pois agora temos peças disponíveis novamente
            estoqueVazioMessageDisplayed.set(false);
        }
        
        return pecasParaAdicionar;
    }
    
    // Marca a fábrica como encerrada
    public void encerrar() {
        this.encerrada = true;
        System.out.println("Fábrica encerrada. Não aceitando mais solicitações de peças.");
    }

    // Obtém o número atual de peças no estoque
    public int getQuantidadePecas() {
        return estoquePecas.get();
    }
} 