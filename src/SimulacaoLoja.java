public class SimulacaoLoja {
    private static final int TEMPO_SIMULACAO_SEGUNDOS = 120;
    
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java SimulacaoLoja <id-loja> <ip-fabrica>");
            System.out.println("Exemplo: java SimulacaoLoja 1 localhost");
            System.exit(1);
        }
        
        String idLoja = "Loja-" + args[0];
        String ipFabrica = args[1];
        int portaFabrica = 8080;
        
        System.out.println("Iniciando " + idLoja + " conectando à fábrica em " + ipFabrica + ":" + portaFabrica);
        
        // Cria a loja
        Loja loja = new Loja(idLoja, ipFabrica, portaFabrica);
        
        // Inicia a loja em uma thread separada
        Thread lojaThread = new Thread(loja);
        lojaThread.start();
        
        // Executa por tempo determinado
        try {
            System.out.println("Loja em operação. Simulação rodando por " + TEMPO_SIMULACAO_SEGUNDOS + " segundos...");
            Thread.sleep(TEMPO_SIMULACAO_SEGUNDOS * 1000);
            
            // Para a loja
            loja.parar();
            lojaThread.join(5000);
            
            System.out.println(idLoja + " finalizou operação após " + TEMPO_SIMULACAO_SEGUNDOS + " segundos");
            System.out.println("Total de carros vendidos: " + loja.getCarrosComprados());
            
        } catch (InterruptedException e) {
            System.out.println("Simulação interrompida");
            loja.parar();
        }
    }
} 