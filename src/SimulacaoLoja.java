public class SimulacaoLoja {

     // Cria e configura uma nova loja
    public static Loja criarLoja(String id, String ipFabrica, int porta) {
        String idLoja = "Loja-" + id;
        System.out.println("Criando " + idLoja + " conectando à fábrica em " + ipFabrica + ":" + porta);
        return new Loja(idLoja, ipFabrica, porta);
    }
    
    // Inicia uma loja em uma nova thread
    public static Thread iniciarLoja(Loja loja) {
        Thread lojaThread = new Thread(loja);
        lojaThread.start();
        System.out.println(loja.getId() + " iniciada");
        return lojaThread;
    }
    
    // Para uma loja em execução e aguarda o encerramento
    public static void pararLoja(Loja loja, Thread lojaThread, long timeout) {
        try {
            loja.parar();
            lojaThread.join(timeout);
            System.out.println(loja.getId() + " finalizada");
            System.out.println("Total de carros vendidos: " + loja.getCarrosVendidos());
        } catch (InterruptedException e) {
            System.out.println("Interrompido durante o encerramento da " + loja.getId());
            Thread.currentThread().interrupt();
        }
    }
} 