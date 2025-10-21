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

## 👥 Integrantes do Grupo
- **Gabriel Casagrande**  - <a href="https://github.com/Bielfer12">Bielfer12</a><br>
- **Guilherme Rabello Carrer** - <a href="https://github.com/GuilhermeCarrer">GuilhermeCarrer</a><br>
- **Jean Vitor Vieira** - <a href="https://github.com/jeanvitorvieira">Jeanvitorvieira</a><br>

---

## 🗄️ Banco de Dados – [PostgreSQL](https://www.postgresql.org/)
O banco Evently gerencia informações de eventos, organizadores e participantes.
Ele foi modelado de forma informativa, sem controle financeiro, priorizando relações entre usuários, eventos e interações como comentários e favoritos.
Inclui tabelas para organizadores, locais, categorias, eventos, programações, etiquetas, usuários, participações e imagens, garantindo integridade com chaves UUID e auditoria de criação e atualização.
