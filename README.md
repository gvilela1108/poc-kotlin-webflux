# Desafio Backend da SandboxClient — Kotlin, Spring Boot, Redis e API com múltiplos formatos

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-7F52FF)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen)
![Cache](https://img.shields.io/badge/Cache-Redis-red)
![Formats](https://img.shields.io/badge/Formatos-JSON%20%7C%20CSV%20%7C%20XML-blue)
![Resilience](https://img.shields.io/badge/Resiliência-Resilience4j-purple)

## Visão geral

Este projeto implementa o desafio Backend Developer da SandboxClient. Ele disponibiliza uma API REST que busca as localizações de estabelecimentos na API da SandboxClient, mantém um snapshot local e disponibiliza aos consumidores o subconjunto de campos solicitado nos formatos JSON, CSV e XML.

A implementação foi estruturada com foco na separação de responsabilidades, extensibilidade dos formatos de saída, validação, tratamento de erros, cache e resiliência na comunicação com uma API externa.

> **Enunciado do desafio:** as instruções originais do desafio são mantidas separadamente em `README.md` (ou no nome original do arquivo, caso ele seja preservado). Este documento descreve a implementação e suas decisões técnicas.

## Requisitos contemplados

A API disponibiliza os seguintes campos para cada localização:

| Field | Description |
|---|---|
| `name` | Nome do estabelecimento |
| `city` | Cidade |
| `zip` | CEP |
| `streetAndNo` | Rua e número |
| `openingHours` | Horários de funcionamento, representados como dados estruturados |
| `keywords` | Palavras-chave que descrevem a localização |
| `lat` | Latitude |
| `lng` | Longitude |

O identificador da localização também é utilizado pelo endpoint de consulta de uma única localização.

## Tecnologias utilizadas

- **Kotlin** and **JDK 21**
- **Spring Boot / Spring WebFlux** for HTTP endpoints and reactive request handling
- **Spring Data and H2** for local persistence (according to the active application profile)
- **Redis** for caching
- **Feign** for calls to the external SandboxClient API
- **Resilience4j** for circuit-breaker protection
- **Gradle** for builds and dependency management
- **Docker Compose** for running the application and its supporting services
- **OpenAPI/Swagger** for API documentation
- **JUnit 5** for automated tests
- **JaCoCo** for test coverage reporting
- **ktlint** for Kotlin code style checks

## Arquitetura e responsabilidades

A implementação separa as responsabilidades HTTP das regras de negócio e dos detalhes de infraestrutura:

- **Controller:** recebe as requisições, valida os parâmetros de query e path e negocia a representação solicitada na resposta.
- **Camada de serviço:** coordena as consultas de localizações e os casos de uso de sincronização.
- **Provider/client da SandboxClient:** comunica-se com a API externa e trata a paginação da origem.
- **Repository:** persiste e consulta o snapshot local das localizações.
- **Abstração de formatadores:** converte os mesmos dados de localização para os formatos suportados, evitando lógica específica de formato no controller.
- **Cache:** reduz leituras repetidas de dados consultados com frequência.
- **Tratamento de exceções:** mapeia falhas conhecidas para respostas HTTP consistentes, sem expor detalhes internos da implementação.
- **Configuração de resiliência:** evita que chamadas repetidas a um serviço externo indisponível provoquem falhas em cascata na API.

Essa estrutura mantém o endpoint enxuto e facilita a adição de novos formatos de saída ou a substituição de componentes de infraestrutura sem reescrever a lógica de negócio.

## Busca e sincronização de dados

A aplicação busca as localizações na API externa da SandboxClient e percorre o mecanismo de paginação para construir um snapshot local, em vez de depender de uma única página da origem.

A API local passa então a servir os dados da camada de persistência. Isso reduz a necessidade de chamar a API externa a cada requisição e permite que os formatos de resposta compartilhem a mesma fonte de dados.

### Comportamentos importantes a validar

- Defina explicitamente como interpretar uma resposta vazia da API externa: como um snapshot vazio válido ou como falha de sincronização. Evite apagar dados locais válidos por causa de uma falha temporária na origem.
- Para conjuntos de dados maiores, considere mover a sincronização para uma tarefa agendada/em segundo plano e publicar o snapshot atomicamente somente depois que todas as páginas forem obtidas com sucesso.
- Se várias instâncias puderem sincronizar ao mesmo tempo, adicione coordenação ou utilize um único worker de sincronização para evitar atualizações conflitantes do snapshot.

## API

Os exemplos abaixo consideram a aplicação em execução local na porta `8080`.

### Consultar uma página de localizações

```http
GET /api/stores?page=0&size=20
Accept: application/json
```

CSV example:

```http
GET /api/stores?page=0&size=20
Accept: text/csv
```

XML example:

```http
GET /api/stores?page=0&size=20
Accept: application/xml
```

### Consultar uma localização

```http
GET /api/stores/{id}
Accept: application/json
```

Example:

```bash
curl -H "Accept: application/json" "http://localhost:8080/api/stores/620416"
```

### Respostas de erro

Os erros são retornados em um corpo estruturado que contém timestamp, status HTTP, código de erro da aplicação, mensagem e ID de correlação quando aplicável. Exemplos de status esperados:

- `400 Bad Request` para parâmetros de requisição inválidos
- `404 Not Found` quando a localização solicitada não existe
- `406 Not Acceptable` quando a representação solicitada não é suportada
- `503 Service Unavailable` quando o serviço externo está indisponível
- `500 Internal Server Error` para falhas inesperadas

### Documentação da API e saúde da aplicação

Se estiverem habilitados na configuração atual, a interface OpenAPI e os endpoints do Actuator podem ser utilizados para inspecionar a API e verificar a saúde da aplicação. Utilize os caminhos efetivamente configurados no projeto.

## Formatos de resposta

A API negocia o formato da resposta por meio do cabeçalho `Accept`.

| Format | Example `Accept` header |
|---|---|
| JSON | `application/json` |
| CSV | `text/csv` |
| XML | `application/xml` |

Todos os formatadores devem expor o mesmo subconjunto de campos acordado. O identificador interno da localização pode ser utilizado para consulta, mas não substitui os oito campos exigidos pelo desafio. A codificação de CSV e a serialização XML devem seguir os padrões, especialmente para vírgulas, aspas, e-comercial e outros caracteres especiais.

## Cache

O Redis é utilizado para reduzir o processamento repetido de dados consultados com frequência. As configurações de cache devem ser parametrizáveis por variáveis de ambiente, em vez de ficarem fixas no código.

O comportamento do cache deve ser verificado sempre que os dados forem sincronizados ou atualizados. Garanta que entradas antigas sejam invalidadas ou expirem conforme o TTL configurado. Se a aplicação armazenar páginas individuais em cache, a estratégia de invalidação deve considerar todas as chaves afetadas.

## Resiliência e tratamento de erros

- A API externa é isolada por uma abstração de provider/client.
- A proteção de circuit breaker do Resilience4j ajuda a evitar chamadas repetidas a um serviço externo que está falhando.
- Exceções do serviço externo são convertidas em respostas de erro consistentes da API.
- Validações e parâmetros malformados devem resultar em `400 Bad Request`, e não em um `500` genérico.
- Mensagens internas de exceção e segredos não devem ser retornados aos consumidores da API.
- IDs de correlação ajudam a relacionar erros da API aos logs da aplicação.

## Configuração

Copie `.env.example` para `.env` e substitua os valores de exemplo pelos valores adequados ao seu ambiente local.

```bash
cp .env.example .env
```

Não versione `.env`, credenciais reais da API, senhas ou outros segredos. Mantenha `.env.example` limitado aos nomes das variáveis e a valores de exemplo seguros.

Os nomes exatos das variáveis devem corresponder aos arquivos de configuração da aplicação e à configuração do Docker Compose. Consulte `.env.example` para ver o modelo de configuração local documentado.

## Executar localmente

Pré-requisitos:

- JDK 21
- Docker and Docker Compose, if running the supporting services in containers
- Access to the SandboxClient sandbox/API and a valid API credential

Compile o projeto e execute os testes com o Gradle Wrapper:

```bash
./gradlew clean test
```

On Windows PowerShell:

```powershell
.\gradlew.bat clean test
```

Execute as verificações de estilo Kotlin:

```bash
./gradlew ktlintCheck
```

Gere o artefato da aplicação:

```bash
./gradlew bootJar
```

Gere o relatório do JaCoCo:

```bash
./gradlew jacocoTestReport
```

Se o projeto disponibilizar uma configuração Docker Compose, inicie os serviços utilizando o arquivo Compose e o arquivo de ambiente documentados nessa configuração. Por exemplo, ajuste o caminho conforme a estrutura do repositório:

```bash
docker compose --env-file .env -f docker/docker-compose.yml up --build
```

Não presuma o caminho do arquivo Compose nem o profile ativo caso sua cópia do repositório utilize uma configuração diferente.

## Testes e verificações de qualidade

Antes de entregar o desafio, execute:

```bash
./gradlew clean test
./gradlew ktlintCheck
./gradlew bootJar
./gradlew jacocoTestReport
```

Valide também a API manualmente ou por meio de testes de integração automatizados:

1. Solicite JSON, CSV e XML utilizando os respectivos cabeçalhos `Accept`.
2. Confira se cada formato contém os campos exigidos.
3. Verifique a paginação utilizando valores válidos de `page` e `size`.
4. Verifique se parâmetros inválidos retornam `400 Bad Request`.
5. Verifique se um ID de localização válido retorna `200 OK`.
6. Verifique se um ID desconhecido retorna `404 Not Found`.
7. Verifique se um cabeçalho `Accept` não suportado retorna `406 Not Acceptable`.
8. Simule uma indisponibilidade da API externa e confirme que a API retorna um erro controlado.
9. Verifique a expiração e a invalidação do cache após a sincronização.

Os percentuais de cobertura devem ser informados somente com base no relatório JaCoCo atual; por isso, não são fixados neste documento.

## Decisões técnicas

### Snapshot local em vez de encaminhar todas as requisições

Buscar e armazenar o conjunto completo de dados permite que as requisições dos consumidores utilizem uma fonte local e evita que cada leitura dependa da API externa. Também cria um ponto natural para implementar paginação e cache para os consumidores.

### Formatadores separados

JSON, CSV e XML são representações diferentes dos mesmos dados. Manter a serialização atrás de uma abstração de formatadores evita espalhar condicionais específicas de formato pelo controller e facilita a adição de novos formatos.

### Cache com Redis

O cache pode reduzir consultas repetidas ao banco e melhorar o tempo de resposta. O TTL e a invalidação precisam ser explícitos para que uma sincronização bem-sucedida não deixe os consumidores lendo dados desatualizados indefinidamente.

### Circuit breaker

A API externa da SandboxClient está fora do controle da aplicação. A proteção de circuit breaker limita chamadas repetidas durante uma indisponibilidade e ajuda a aplicação a falhar de maneira previsível.

### Tratamento centralizado de exceções

Um componente centralizado de tratamento de exceções mantém as respostas consistentes e evita expor detalhes internos aos consumidores. Parâmetros de query inválidos devem ser mapeados para erros do cliente, em vez de registrados como falhas inesperadas do servidor.

### Configuração through environment variables

URLs externas, credenciais, portas e configurações de cache variam conforme o ambiente. A configuração por variáveis de ambiente mantém a aplicação portátil e evita que credenciais sejam enviadas ao controle de versão.

## Compromissos técnicos e melhorias futuras

- Mover a sincronização completa para fora do fluxo da requisição caso a latência de inicialização ou atualização se torne significativa.
- Publicar o snapshot concluído de forma atômica para que os leitores nunca vejam dados parcialmente sincronizados.
- Definir o significado de um conjunto de dados vazio na origem e preservar o último snapshot válido caso uma falha externa seja confundida com um resultado vazio.
- Adicionar testes de integração para todos os formatos de saída, casos extremos de paginação, paginação da API externa e invalidação do Redis.
- Adicionar métricas de sincronização, logs estruturados e rastreamento distribuído caso seja necessária maior visibilidade operacional.
- Revisar as chaves e a invalidação do cache em cenários com paginação e atualizações concorrentes.
- Manter artefatos gerados, arquivos de banco local e segredos fora do repositório.

## Documentação do repositório

Estrutura de arquivos recomendada:

```text
README.md                  # Original challenge instructions — keep unchanged
README-implementation.md   # This project/implementation documentation
.env.example               # Safe environment-variable template
.env                       # Local secrets; do not commit
```

## Licença

Este repositório foi criado para a submissão de um desafio técnico. Consulte as instruções originais do desafio para verificar os termos aplicáveis.
