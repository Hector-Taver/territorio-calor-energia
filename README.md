# Territory Predictive Insights: Calor e Energia

Sistema de console em Java + Spring Boot que lê sensores de temperatura, umidade e demanda de energia, grava o histórico em banco de dados, prevê o próximo valor de cada sensor e emite alertas com explicação.

## Como rodar

Pré-requisitos: JDK 17 ou superior e Maven. Não é necessário instalar banco de dados: o projeto usa H2, que roda embutido.

```bash
mvn test              # roda os testes unitários
mvn spring-boot:run   # abre o menu no console
```

### Banco de dados

- Banco: **H2** (embutido), acessado com **Spring Data JPA** (Hibernate).
- Os dados ficam no arquivo `./data/monitoramento.mv.db`, criado na primeira execução. Por isso sobrevivem ao reinício do programa.
- As tabelas `medicao` e `alerta` são criadas automaticamente (`spring.jpa.hibernate.ddl-auto=update`).
- Para zerar o histórico, apague a pasta `data/` com o programa fechado.
- A configuração está em `src/main/resources/application.properties`.

## Menu principal

1. Verificar sensores (ler e gravar leitura)
2. Rodar previsões (média móvel ou comparação entre períodos)
3. Ver histórico por sensor
4. Ver alertas emitidos
5. Gerenciar dados do território (ver dados, alterar limiares com código de acesso, registrar leitura manual, simular defeito em sensor)
6. Status completo
0. Encerrar

## Mapa dos requisitos

| Requisito do enunciado | Onde está |
| --- | --- |
| Classe abstrata com método abstrato e filha concreta | `ComponenteTerritorial` (`verificarStatus`), `SetorUrbano` |
| Interface `Sensor` com 3 ou mais implementações | `Sensor`, `SensorSimulado`, `SensorTemperatura`, `SensorUmidade`, `SensorDemandaEnergia` |
| Dados com atributos privados e código de acesso para alterar limiares | `DadosTerritorio.autorizar`, `ServicoMonitoramento.alterarLimiares` |
| Estratégia abstrata com 2 filhos que sobrescrevem `prever()` e usam `super()` | `EstrategiaPrevisao`, `PrevisaoMediaMovel`, `PrevisaoComparacao` |
| Exceções customizadas, lançadas e capturadas com mensagem clara | `DadosInvalidosException`, `FonteIndisponivelException`, captura em `MenuConsole` |
| Persistência em banco (medições e alertas) | `Medicao`, `Alerta` (JPA), `MedicaoRepository`, `AlertaRepository` |
| Padrão criacional | Factory: `FabricaPrevisao`. Singleton: `ConfiguracaoSistema`. Builder: `RelatorioBuilder` |
| Testes AAA, `assertThrows` e mock do repositório | `src/test/java/...` |
| Menu de console | `MenuConsole` |

## Decisões da equipe

> Preencha com as decisões reais do grupo.

- Público beneficiado: _quem sofre o problema e qual decisão o sistema apoia_
- Por que JPA e H2 em arquivo: _justificativa_
- Limiares: _de onde vieram os valores padrão_
- Por que o padrão criacional foi aplicado nesses pontos e não em outros: _justificativa_

## Reflexão crítica

> Escreva esta seção com as suas palavras. Responda às três perguntas do enunciado.

1. Quando o sistema deve se calar?
2. O que os dados não representam?
3. Por que o alerta apoia a decisão da pessoa, em vez de decidir por ela?

## Divisão de tarefas

| Integrante | Parte |
| --- | --- |
| _nome_ | Domínio e exceções |
| _nome_ | Persistência |
| _nome_ | Previsões, alertas e testes |

## Pendências conhecidas

- Trocar o código de acesso padrão (`1234`) em `ServicoMonitoramento`.
- Limiares alterados pelo menu são guardados só em memória; ao reiniciar, voltam aos padrões.
- Validar os limiares padrão com uma fonte real, se a equipe quiser o bônus.
