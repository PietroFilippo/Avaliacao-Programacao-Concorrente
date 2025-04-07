# Avaliacao-Programacao-Concorrente
Avaliação de programação concorrente em Java simulando uma cadeia de produção de veículos.

# Execução:

Para compilar todos os arquivos fonte:

```bash
javac -d bin src/*.java
```

O sistema é composto por dois programas principais que devem ser executados em terminais separados (ou computadores diferentes):

Terminal 1: Simulação da Fábrica

```bash
java -cp src SimulacaoProducaoVeiculos
```

Este programa inicia:
- 4 estações de produção com 5 funcionários cada
- Um estoque de 500 peças
- Um servidor que atende às lojas remotas na porta 8080

Terminal 2: Simulação dos Clientes e Lojas

```bash
java -cp src SimulacaoClientes
```

Este programa inicia:
- 3 lojas que se conectam à fábrica
- 20 clientes que compram carros das lojas

### Execução em Computadores Diferentes

Para executar em computadores diferentes:

1. Primeiro, modifique o arquivo `SimulacaoClientes.java` para usar o endereço IP da máquina onde a fábrica está executando, em vez de "localhost" ou "127.0.0.1"

2. Execute `SimulacaoProducaoVeiculos` na primeira máquina

3. Execute `SimulacaoClientes` na segunda máquina

4. Certifique-se de que a porta 8080 está aberta no firewall do computador da fábrica

## Arquivos de Log

Durante a execução, o sistema gera vários arquivos de log:

- `log_producao.txt`: Registra todos os veículos produzidos na fábrica
- `log_vendas_fabrica.txt`: Registra vendas da fábrica para as lojas
- `log_vendas_loja_Loja_X.txt`: Registra vendas de cada loja para os clientes
- `log_recebimento_loja_Loja_X.txt`: Registra veículos recebidos pelas lojas

## Arquitetura do Sistema

O sistema implementa o modelo produtor-consumidor com vários níveis:
- Funcionários da fábrica são produtores de veículos
- Lojas são consumidoras de veículos da fábrica e produtoras para os clientes
- Clientes são consumidores finais

A disposição dos funcionários nas estações de produção implementa uma variação do problema clássico do "Jantar dos Filósofos", com cada funcionário precisando adquirir duas ferramentas adjacentes para produzir um veículo.

## Finalização

Os programas executam por um tempo determinado e então encerram automaticamente, exibindo estatísticas da simulação. Para encerrar manualmente, use Ctrl+C no terminal.