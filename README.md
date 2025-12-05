# Evently — API RESTful de Gerenciamento de Eventos

## Descrição do Projeto

O **Evently** é uma API RESTful desenvolvida para gerenciar e divulgar eventos culturais, sociais e corporativos.

O sistema permite:

- Cadastrar e gerenciar **eventos**, **locais**, **organizadores** e **categorias**
- Consultar e filtrar eventos por título, status, categoria, organizador, etc.
- Registrar **participações** e **comentários** em eventos
- Marcar eventos como **favoritos**
- Controlar acesso via **autenticação JWT** com perfis de usuário

A API foi projetada para servir como backend de aplicativos web/mobile que desejam consumir informações de eventos de forma simples, segura e escalável.

---

## Integrantes do Grupo

- **Gabriel Casagrande** — GitHub: [Bielfer12](https://github.com/Bielfer12)
- **Guilherme Rabello Carrer** — GitHub: [GuilhermeCarrer](https://github.com/GuilhermeCarrer)
- **Jean Vitor Vieira** — GitHub: [Jeanvitorvieira](https://github.com/Jeanvitorvieira)

---

## Descrição do Problema

A divulgação de eventos costuma ser fragmentada (redes sociais, sites isolados, panfletos físicos). Isso gera problemas como:

- Dificuldade para encontrar **eventos relevantes** em uma única fonte confiável
- Falta de **informações atualizadas** (horário, local, lotação, cancelamentos)
- Inexistência de um backend padronizado para integrar com apps e portais

O **Evently** surge como uma solução de backend que:

- Centraliza o cadastro e a gestão de eventos
- Oferece uma API RESTful bem definida
- Permite que múltiplos clientes (web, mobile, integrações) consumam os mesmos dados
- Implementa autenticação e autorização para proteger ações sensíveis (criação/edição/remoção de eventos)

---

## Tecnologias Utilizadas

- **Linguagem**
  - Java 17+

- **Backend / Frameworks**
  - Spring Boot
  - Spring Web (endpoints REST)
  - Spring Data JPA (acesso ao banco relacional)
  - Spring Security + JWT (autenticação e autorização)

- **Banco de Dados**
  - PostgreSQL

- **Build / Gerenciamento de Dependências**
  - Maven

- **Outros**
  - UUID como chave primária nas entidades
  - DTOs para entrada/saída de dados
  - Paginação, ordenação e filtros nas listagens

---

## Banco de Dados (PostgreSQL)

O banco **Evently** foi modelado para armazenar informações de:

- **Usuários** e perfis (`ADMIN`, `ORGANIZADOR`, `USUARIO`)
- **Eventos** e seus relacionamentos
- **Locais** onde os eventos acontecem
- **Categorias** para classificação
- **Participações**, **ingressos**, **comentários** e **favoritos**

Características principais:

- Chaves primárias em **UUID**
- Campos de auditoria:
  - `criado_em`, `atualizado_em`
  - `criado_por` (em algumas entidades)
- Relacionamentos normalizados (FKs explícitas)
- Ausência de controle financeiro (sem transações de pagamento)

---

## Entidades do Sistema

> As descrições abaixo são conceituais.

### Categoria

Classifica os eventos, facilitando filtros e organização.

Exemplos de categorias:

- Show
- Teatro
- Workshop
- Congresso
- Evento Corporativo

---

### Evento

Entidade central do sistema.

Principais informações:

- Título e descrições (curta e longa)
- Slug (identificador amigável na URL, único)
- Status (`RASCUNHO`, `PUBLICADO`, etc.)
- Capacidade de público
- Relacionamentos: Organizador, Local, Categoria
- Auditoria:
  - `criadoEm`, `atualizadoEm`, `criadoPor`

---

### Favorito / FavoritoId

Representa o interesse de um usuário em um evento.

- `FavoritoId` é uma classe para a chave composta:
  - `idUsuario` + `idEvento`
- `Favorito` associa:
  - Um usuário
  - Um evento

Permite que o usuário mantenha uma lista de eventos preferidos.

---

### Ingresso

Define tipos de entrada para um evento, por exemplo:

- Inteira
- Meia-entrada
- VIP
- Camarote

> Nesta versão não há lógica financeira (preços, pagamentos, transações).

---

### Local

Representa o endereço físico onde o evento ocorre.

Possíveis campos:

- Cidade
- Estado
- Logradouro
- Complemento
- Capacidade do local

---

### Organizador

Pessoa física ou jurídica responsável por um evento.

Contém dados públicos como:

- Nome
- Descrição
- Site
- Telefone de contato
- E-mail de contato

---

### Usuario

Gerencia o acesso à API.

Campos principais:

- Nome
- E-mail (único)
- Senha (hash)
- Papel/perfil (`papel`):
  - `ADMIN`
  - `ORGANIZADOR`
  - `USUARIO`

As permissões de acesso às rotas são baseadas nesse papel.

---

### Participação

Registra a participação de um usuário em um evento.

Relaciona:

- Usuario
- Evento
- Ingresso

Usada para construir:

- Histórico de presença
- Relatórios de engajamento

---

### Comentário

Permite interação social relacionada aos eventos.

Funcionalidades:

- Usuários podem deixar comentários e avaliações
- Possibilidade de respostas a comentários (thread)
- Atribuição de nota ao evento (quando aplicável)

---

## Rotas Principais da API

> A API segue o padrão REST.

### Rotas de Autenticação

#### `POST /api/v1/auth/register`

Cria um novo usuário com papel padrão `USUARIO`.

**Exemplo de request:**

```json
{
  "nome": "João Silva",
  "email": "joao@evently.com",
  "senha": "senhaSegura123"
}
```

**Exemplo de response (201 Created):**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "nome": "João Silva",
  "email": "joao@evently.com",
  "papel": "USUARIO"
}
```

---

#### `POST /api/v1/auth/login`

Autentica um usuário e retorna um JWT.

**Exemplo de request:**

```json
{
  "email": "usuario@evently.com",
  "senha": "minhaSenhaSegura"
}
```

**Exemplo de response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer"
}
```

O token deve ser enviado no header `Authorization`:

```text
Authorization: Bearer <token>
```

---

### Rotas de Eventos

#### `GET /api/v1/eventos`

Lista paginada de eventos, com filtros opcionais.

**Parâmetros de query (exemplos):**

- `pagina=0`
- `resultados=10`
- `ordenar=titulo,asc`
- `titulo=show`
- `status=PUBLICADO`
- `idCategoria=<uuid>`
- `idOrganizador=<uuid>`

**Exemplo de request:**

```text
GET /api/v1/eventos?pagina=0&resultados=10&titulo=show&status=PUBLICADO
Authorization: Bearer <token>
```

**Exemplo de response (200 OK):**

```json
{
  "content": [
    {
      "id": "c6e4c4d0-8f34-4bb2-9a5d-0b0d3e8ed111",
      "titulo": "Show de Rock",
      "slug": "show-de-rock",
      "descricaoCurta": "Banda cover anos 80",
      "descricao": "Show completo com duração de 2 horas...",
      "status": "PUBLICADO",
      "capacidade": 500,
      "metadados": null,
      "idOrganizador": "95b5c623-6dfe-497b-901f-7ee64067259d",
      "idLocal": "e8ab2d71-ea50-4e89-878a-7cc3c6b2d4ef",
      "idCategoria": "b3f0048e-4a3c-4d6c-b908-920ef1ea4d21"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "first": true,
  "numberOfElements": 1
}
```

---

#### `GET /api/v1/eventos/{id}`

Retorna os detalhes de um evento específico.

**Exemplo de response (200 OK):**

```json
{
  "id": "c6e4c4d0-8f34-4bb2-9a5d-0b0d3e8ed111",
  "titulo": "Show de Rock",
  "slug": "show-de-rock",
  "descricaoCurta": "Banda cover anos 80",
  "descricao": "Show completo com duração de 2 horas...",
  "status": "PUBLICADO",
  "capacidade": 500,
  "metadados": null,
  "idOrganizador": "95b5c623-6dfe-497b-901f-7ee64067259d",
  "idLocal": "e8ab2d71-ea50-4e89-878a-7cc3c6b2d4ef",
  "idCategoria": "b3f0048e-4a3c-4d6c-b908-920ef1ea4d21"
}
```

---

#### `POST /api/v1/eventos`

Cria um novo evento.
Restrito a usuários com papel `ADMIN` ou `ORGANIZADOR`.

**Exemplo de request:**

```json
{
  "titulo": "Festival de Jazz",
  "slug": "festival-de-jazz",
  "descricaoCurta": "Três dias de música ao vivo",
  "descricao": "O maior festival de jazz da região...",
  "status": "PUBLICADO",
  "capacidade": 1000,
  "metadados": null,
  "idOrganizador": "95b5c623-6dfe-497b-901f-7ee64067259d",
  "idLocal": "e8ab2d71-ea50-4e89-878a-7cc3c6b2d4ef",
  "idCategoria": "b3f0048e-4a3c-4d6c-b908-920ef1ea4d21"
}
```

**Exemplo de response (201 Created):**

```json
{
  "id": "d7f5e6c7-9a8b-4c3d-2e1f-0a9b8c7d6e5f",
  "titulo": "Festival de Jazz",
  "slug": "festival-de-jazz",
  "descricaoCurta": "Três dias de música ao vivo",
  "descricao": "O maior festival de jazz da região...",
  "status": "PUBLICADO",
  "capacidade": 1000,
  "metadados": null,
  "idOrganizador": "95b5c623-6dfe-497b-901f-7ee64067259d",
  "idLocal": "e8ab2d71-ea50-4e89-878a-7cc3c6b2d4ef",
  "idCategoria": "b3f0048e-4a3c-4d6c-b908-920ef1ea4d21"
}
```

---

#### `PUT /api/v1/eventos/{id}`

Atualiza um evento existente (respeitando regras de permissão).

**Exemplo de request:**

```json
{
  "titulo": "Festival de Jazz 2025",
  "capacidade": 1200
}
```

**Exemplo de response (200 OK):**

```json
{
  "id": "d7f5e6c7-9a8b-4c3d-2e1f-0a9b8c7d6e5f",
  "titulo": "Festival de Jazz 2025",
  "slug": "festival-de-jazz",
  "descricaoCurta": "Três dias de música ao vivo",
  "descricao": "O maior festival de jazz da região...",
  "status": "PUBLICADO",
  "capacidade": 1200,
  "metadados": null,
  "idOrganizador": "95b5c623-6dfe-497b-901f-7ee64067259d",
  "idLocal": "e8ab2d71-ea50-4e89-878a-7cc3c6b2d4ef",
  "idCategoria": "b3f0048e-4a3c-4d6c-b908-920ef1ea4d21"
}
```

---

#### `DELETE /api/v1/eventos/{id}`

Remove um evento.

**Exemplo de response (204 No Content):**

```text
(no body)
```

---

#### `GET /api/v1/eventos/exportacao-csv`

Exporta os eventos em formato CSV.

**Exemplo de response (200 OK):**

```csv
id;titulo;slug;status;capacidade;idOrganizador;idLocal;idCategoria;criadoEm;criadoPor;atualizadoEm
c6e4c4d0-8f34-4bb2-9a5d-0b0d3e8ed111;Show de Rock;show-de-rock;PUBLICADO;500;95b5c623-6dfe-497b-901f-7ee64067259d;e8ab2d71-ea50-4e89-878a-7cc3c6b2d4ef;b3f0048e-4a3c-4d6c-b908-920ef1ea4d21;2025-12-01T10:00:00;Admin;2025-12-05T14:30:00
```

---

### Outras Entidades

Para **categorias**, **locais**, **organizadores**, **usuários**, **participações**, **comentários** e **favoritos**, a API segue, em geral, o padrão:

- `GET /api/v1/<entidade>`
- `GET /api/v1/<entidade>/{id}`
- `POST /api/v1/<entidade>`
- `PUT /api/v1/<entidade>/{id}`
- `DELETE /api/v1/<entidade>/{id}`

Detalhes (campos, regras de negócio, autorização) podem ser consultados via Postman/Insomnia.

---

## Exemplos de Erros HTTP

A API utiliza um formato padronizado para erros.

### 400 Bad Request

```json
{
  "path": "/api/v1/eventos",
  "error": "Bad Request",
  "message": "Campo 'titulo' é obrigatório",
  "timestamp": "2025-12-05T18:21:55.801827337Z",
  "status": 400
}
```

---

### 401 Unauthorized

```json
{
  "path": "/api/v1/eventos",
  "error": "Unauthorized",
  "message": "Token inválido ou expirado",
  "status": 401
}
```

---

### 403 Forbidden

```json
{
  "path": "/api/v1/eventos",
  "error": "Forbidden",
  "message": "Você não tem permissão para criar eventos",
  "status": 403
}
```

---

### 404 Not Found

```json
{
  "path": "/api/v1/eventos/c6e4c4d0-8f34-4bb2-9a5d-0b0d3e8ed111",
  "error": "Not Found",
  "message": "Evento não encontrado",
  "status": 404
}
```

---

### 409 Conflict

```json
{
  "path": "/api/v1/eventos",
  "error": "Conflict",
  "message": "Já existe um evento com este slug",
  "status": 409
}
```

---

### 500 Internal Server Error

```json
{
  "path": "/api/v1/eventos/exportacao-csv",
  "error": "Internal Server Error",
  "message": "Erro interno no servidor",
  "status": 500
}
```

---

## Como Executar o Projeto Localmente

#### Pré-requisitos

- Java 17+
- Maven
- PostgreSQL em execução

#### Passo a passo

1. Criar o banco de dados:

   ```sql
   CREATE DATABASE evently;
   ```

2. Configurar o arquivo `application.properties` com as credenciais do seu PostgreSQL e a chave JWT.

3. Build e execução:

   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. Acessar a API:

   ```text
   http://localhost:8080
   ```

---

## Limitações da Versão Atual

- Sem upload de arquivos
  - Não há integração com:
    - AWS S3
    - Google Cloud Storage
    - Armazenamento local

- Sem sistema de notificações
  - Não há envio de:
    - E-mails de confirmação
    - Recuperação de senha
    - Lembretes de eventos

- Sem validação externa de endereço
  - Não há integração com:
    - Google Maps
    - ViaCEP
    - Serviços de geolocalização

- Sem fluxo financeiro
  - Não há cobrança, pagamentos ou integração com gateways (Stripe, PayPal, etc.).
  - A entidade `Ingresso` é apenas informativa.

---

## Outros Conteúdos Relevantes

### Autenticação JWT

- Token inclui e-mail e papel (`papel`) do usuário.
- O filtro `JwtAuthenticationFilter` valida o token em cada requisição.
- O token é enviado sempre no header `Authorization: Bearer <token>`.

### Autorização baseada em papéis

- `ADMIN` — acesso amplo a rotas administrativas.
- `ORGANIZADOR` — pode criar/editar/remover eventos próprios.
- `USUARIO` — pode consultar eventos, favoritar, comentar, participar.

### Paginação, ordenação e filtros

- Parâmetros comuns:
  - `pagina` (número da página)
  - `resultados` (tamanho da página)
  - `ordenar` (campo e direção, ex: `titulo,asc`)
- Filtros específicos por entidade (ex: `titulo`, `status`, `idCategoria`, `idOrganizador`).

### Exportação de dados

- Endpoint para exportar eventos em CSV:

  ```text
  GET /api/v1/eventos/exportacao-csv
  ```

- Retorna um arquivo CSV com os dados de eventos.
