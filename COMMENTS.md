# Decisões Técnicas

## Stack inicial — Teste Web

### Playwright (automação web)
Escolhido no lugar do Selenium por três motivos práticos:
- **Auto-wait nativo:** aguarda automaticamente elementos ficarem acionáveis,
  reduzindo flakiness e a necessidade de waits explícitos.
- **Sem gerência de drivers:** os navegadores são baixados e versionados pela
  própria ferramenta, sem WebDriverManager ou chromedriver manual.
- **Ferramental de depuração:** trace viewer, screenshots e vídeo já integrados,
  úteis como evidência de execução.

### Java
Linguagem escolhida por alinhamento com o restante do desafio (o teste de API
pede Java preferencialmente) e por ser a stack que domino no dia a dia.

### JUnit 5
Runner de teste. O Playwright em Java não é um runner por si só, então o JUnit 5
faz a orquestração. Versões alinhadas via `junit-bom` para evitar conflito entre
os módulos (api/engine/params).

### Allure
Relatório de execução em HTML, com captura de screenshots (`@Attachment`) e
rastreio de passos (`@Step`).

## Versionamento
Fluxo enxuto com `main` (estável) e `develop` (integração).

# Decisões Técnicas — Teste Web

## Arquitetura
- **Page Object Model** com `BaseTest` (herança) gerenciando o ciclo de vida
  do browser. Herança escolhida por simplicidade adequada ao escopo.
- **Thread-safety via ThreadLocal**: Playwright/Browser/Context/Page isolados
  por thread. O projeto está pronto para execução paralela (o Playwright não é
  thread-safe e a forma recomendada é uma instância por thread). Mantido
  sequencial neste escopo por serem poucos cenários, mas a base já suporta...
- **Configuração externalizada** (`TestConfig`): precedência
  `-Dprop > variável de ambiente > config.properties > default`, permitindo
  headless no CI e headed no local sem editar arquivo.

## Captura de evidência
- Screenshot em falha via **`AfterTestExecutionCallback`**, não `TestWatcher`.
  Motivo: o `TestWatcher.testFailed` roda **depois** do `@AfterEach`, que já
  fechou a página — capturaria uma página inexistente. O
  `AfterTestExecutionCallback` roda antes do teardown, com a página ainda viva.
