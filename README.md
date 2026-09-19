# Agi Blog — Testes Web (Playwright + Java)

Automação de testes da funcionalidade de **busca** do
[blog do Agibank](https://blog.agibank.com.br), desenvolvida como parte de um
desafio técnico de QA.

## Stack

- **Java 21**
- **Playwright** — automação web (auto-wait nativo, sem gerência de drivers)
- **JUnit 5** — runner de testes
- **Allure** — relatório com evidências (screenshots em falha)
- **Maven** — build e gerenciamento de dependências

## Pré-requisitos

- JDK 21+
- Maven 3.8+
- Conexão com internet (a primeira execução baixa os navegadores do Playwright)

## Como rodar

Clonar o repositório e, na raiz do projeto:

```bash
# Executar os testes (baixa os navegadores do Playwright na primeira vez)
mvn test

# Gerar e abrir o relatório Allure no navegador
mvn allure:serve
```

Por padrão os testes rodam com o navegador **visível** (headed), para facilitar
o acompanhamento local. Para rodar em modo **headless** (ex.: em CI):

```bash
mvn test -Dheadless=true
```

## Configuração

Os parâmetros ficam em `src/test/resources/config.properties`:

| Chave      | Descrição                       | Padrão                       |
|------------|---------------------------------|------------------------------|
| `base.url` | URL base do blog                | `https://blog.agibank.com.br`|
| `headless` | Executar sem interface gráfica  | `false`                      |

A precedência de configuração é:
`-Dprop` > variável de ambiente > `config.properties` > padrão.

## Cenários automatizados

| # | Cenário | O que valida |
|---|---------|--------------|
| 1 | Busca com resultados | O termo aparece no título da página e os artigos retornados são relevantes ao termo buscado |
| 2 | Busca sem resultados | Exibição da mensagem amigável de "nada encontrado" e ausência de artigos |
| 3 | Nova busca pelo campo funcional | Refazer a busca por interação real de UI, validando a transição de vazio para com resultado |

A justificativa detalhada da escolha de cada cenário está no
[`COMMENTS.md`](./COMMENTS.md).

## Estrutura do projeto

```
src/test/java/br/com/samuellima/qa/
├── base/ # BaseTest (ciclo de vida do browser) e ScreenshotExtension
├── config/ # TestConfig (leitura de configuração)
├── pages/ # Page Objects (SearchResultsPage)
└── tests/ # Cenários de teste (SearchTest)
```

Arquitetura em **Page Object Model**, com a `BaseTest` gerenciando o ciclo de
vida do Playwright via `ThreadLocal` (preparado para execução paralela) e
captura de screenshot em falha via `AfterTestExecutionCallback`, anexada ao
Allure.

## Observação sobre a busca no desktop

Durante a investigação, identifiquei que a busca do blog apresenta
comportamento diferente no desktop por conta do recurso "Delay JS" do
LiteSpeed, que adia a execução do JavaScript até a primeira interação do
usuário. Os detalhes da causa e a estratégia de automação adotada estão
documentados no [`COMMENTS.md`](./COMMENTS.md).