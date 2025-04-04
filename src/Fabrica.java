import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


// Classe que simula uma fábrica de veículos com um estoque limitado de peças e uma esteira para estações de montagem
public class Fabrica {
    private static final int MAX_PECAS = 500;
    private static final int CAPACIDADE_ESTEIRA = 5;
    
    private final AtomicInteger estoquePecas = new AtomicInteger(MAX_PECAS);
    private final Semaphore esteiraTransporte = new Semaphore(CAPACIDADE_ESTEIRA, true);
    

    // Solicita uma peça do estoque da fábrica
    public boolean solicitarPeca() throws InterruptedException {
        // Tenta adquirir um slot na esteira
        esteiraTransporte.acquire();
        try {
            // Verifica se há peças disponíveis no estoque
            int pecasAtuais = estoquePecas.get();
            if (pecasAtuais > 0) {
                // Decrementa o contador de peças
                boolean sucesso = estoquePecas.compareAndSet(pecasAtuais, pecasAtuais - 1);
                if (sucesso) {
                    System.out.println("Parte obtida do estoque. Peças restantes: " + estoquePecas.get());
                    return true;
                } else {
                    // Aviso se alguém modificou o estoque
                    return solicitarPeca();
                }
            } else {
                System.out.println("Não há peças disponíveis no estoque!");
                return false;
            }
        } finally {
            // Libera o slot na esteira
            esteiraTransporte.release();
        }
    }
    

    // Adiciona peças ao estoque (não excedendo a capacidade máxima)
    public int reabastecerPecas(int quantidade) {
        int pecasAtuais = estoquePecas.get();
        int pecasParaAdicionar = Math.min(quantidade, MAX_PECAS - pecasAtuais);
        
        if (pecasParaAdicionar > 0) {
            estoquePecas.addAndGet(pecasParaAdicionar);
            System.out.println("Adicionadas " + pecasParaAdicionar + " peças ao estoque. Total atual: " + estoquePecas.get());
        }
        
        return pecasParaAdicionar;
    }
    

    // Obtém o número atual de peças no estoque
    public int getQuantidadePecas() {
        return estoquePecas.get();
    }
} 