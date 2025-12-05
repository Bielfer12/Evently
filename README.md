# 🎟️ Evently — API RESTful de Gerenciamento de Eventos

## 🧾 Descrição do Projeto
O **Evently** é uma API RESTful desenvolvida para gerenciar e divulgar eventos culturais, sociais e corporativos.  
O sistema permite cadastrar **eventos**, **locais** e **organizadores**, possibilitando que usuários consultem, filtrem e acompanhem eventos de forma simples e eficiente.

---

## ❗ Descrição do Problema
A falta de um sistema centralizado para **divulgação e gestão de eventos** locais dificulta que pessoas encontrem informações atualizadas sobre atividades culturais, palestras e festivais.  
O **Evently** surge como uma solução backend que organiza e disponibiliza esses dados de forma acessível, segura e escalável, permitindo que aplicativos e sites possam consumir as informações via API.

---

## 🧠 Tecnologias Utilizadas
- **Java 17+** — linguagem principal do projeto  
- **Spring Boot** — framework para desenvolvimento de aplicações Java  
- **Spring Web** — criação de endpoints RESTful  
- **Maven** — gerenciamento de dependências e build  

---

## 🧩 Modelagem de Dados e Funcionalidades da API

O **documento complementar** detalha a **modelagem de dados** e as **principais funcionalidades** da API **Evently**, apresentando a estrutura completa do banco de dados e os recursos de autenticação e integração.  

O modelo inclui entidades como **Eventos**, **Organizadores**, **Locais**, **Usuários**, **Categorias**, **Participações**, **Comentários**, **Favoritos** e **Imagens**, todas estruturadas com **UUIDs**, auditoria de criação e atualização, e relacionamentos normalizados.  

A API segue o padrão **RESTful**, oferecendo **endpoints CRUD** para cada entidade, **autenticação JWT**, uso de **DTOs** para entrada e saída de dados, e suporte a **paginação**, **ordenação** e **filtros** nas listagens.  
A documentação interativa está disponível via **Swagger** no endpoint `/api-docs`.  

📄 [**Abrir Documentação Completa (PDF)**](documentacao/EventlyDocumentacao_2.pdf)


## 👥 Integrantes do Grupo
- **Gabriel Casagrande**  - <a href="https://github.com/Bielfer12">Bielfer12</a><br>
- **Guilherme Rabello Carrer** - <a href="https://github.com/GuilhermeCarrer">GuilhermeCarrer</a><br>
- **Jean Vitor Vieira** - <a href="https://github.com/jeanvitorvieira">Jeanvitorvieira</a><br>

---

## 🗄️ Banco de Dados – [PostgreSQL](https://www.postgresql.org/)
O banco Evently gerencia informações de eventos, organizadores e participantes.
Ele foi modelado de forma informativa, sem controle financeiro, priorizando relações entre usuários, eventos e interações como comentários e favoritos.
Inclui tabelas para organizadores, locais, categorias, eventos, programações, etiquetas, usuários, participações e imagens, garantindo integridade com chaves UUID e auditoria de criação e atualização.

---

## 📚 Entidades do Sistema

### 🔵 **Categoria**
Classifica os eventos (ex: *Show*, *Teatro*, *Workshop*) facilitando filtros e buscas.

### 🔵 **Evento**
Entidade central do sistema. Armazena:
- Título e descrição  
- Status (*rascunho/publicado*)  
- Capacidade  
- Datas  
- Relacionamentos: **Organizador**, **Local**, **Categoria**

### 🔵 **Favorito**
Representa o interesse de um **Usuário** em um **Evento**, permitindo salvar eventos em uma lista pessoal.

### 🔵 **FavoritoId**
Classe embutida (*Embeddable*) utilizada para a **chave composta** da tabela Favorito  
→ Une `idUsuario` + `idEvento`.

### 🔵 **Ingresso**
Define os tipos de entrada disponíveis para um evento  
(ex: *VIP*, *Pista*, *Meia-entrada*).  
> Nesta versão, não há regras financeiras ou transações.

### 🔵 **Local**
Armazena o endereço onde o evento ocorrerá:
- Cidade  
- Estado  
- Logradouro  
- Capacidade do local

### 🔵 **Organizador**
Representa uma pessoa ou empresa responsável pelo evento.  
Contém dados públicos como:
- Site  
- Telefone  
- Email  

### 🔵 **Usuario**
Gerencia o acesso ao sistema. Contém:
- Email e senha  
- Nível de permissão via campo `papel`  
  - `ADMIN`  
  - `ORGANIZADOR`  
  - `USUARIO`  

### 🔵 **Participação**
Registra a **inscrição ou compra de ingresso** de um usuário em um evento.  
É a entidade associativa que conecta:
- **Usuario**
- **Evento**
- **Ingresso**

Utilizada para gerar um **histórico de presença e participação**.

### 🔵 **Comentário**
Permite a interação social na plataforma. Usuários podem:
- Publicar opiniões e avaliações (notas) sobre eventos  
- Responder comentários de outros usuários  

---

## 🚧 Limitações da Versão Atual

### ❌ **Sem Upload de Arquivos**
As imagens dos eventos são apenas **URLs (strings)**.  
Nenhum uso de:
- AWS S3  
- Google Cloud Storage  
- Armazenamento local

### ❌ **Sem Sistema de Notificações**
Não há envio de:
- Emails de confirmação  
- Recuperação de senha  
- Lembretes de evento  

### ❌ **Sem Validação de Endereço**
O cadastro de locais **não** usa APIs externas como:
- Google Maps  
- ViaCEP  
- Geolocalização real  

