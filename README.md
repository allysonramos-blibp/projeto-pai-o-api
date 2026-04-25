# DevLapa — Projetos Integrados 2 (VIA231) — 2026/1

## 👥 Integrantes

| Nome | GitLab |
|------|--------|
| Allyson Ramos | @Allyson |
| Amanda Rezende | @Amanda |
| Clériston Lima | @Cleriston |
| Débora Neves | @Debora |

## 🎯 Problema Escolhido

> O projeto consiste no desenvolvimento de um sistema web para gerenciamento operacional 
> e financeiro desenvolvido para bares, restaurantes e estabelecimentos gastronômicos de 
> pequeno e medio porte. O sistema centraliza o controle de produtos, estoque, comandas,
> vendas, fornecedores e movimentações financeiras em uma única plataforma digital, 
> eliminando processos manuais e planilhas desconexas que resultam em erros e perda de informações..

## 🛠️ Stack Utilizada

| Camada | Tecnologia |
|--------|-----------|
| Frontend | React e Twind |
| Backend | Java 24 |
| Banco de Dados | PostgreSQL 18.1 |
| Hospedagem | |

## 🚀 Guia de Setup e Execução

### Pré-requisitos

```bash
 * Java JDK 24 (ambiente de execucao).
 * Maven 3.9+ (gerenciador de dependências).
 * PostgreSQL 18 (banco de dados local).
 * Postman (ferramenta oficial de testes da API).
Instalação
```

### Instalação

```bash
git clone <https://gitlab.horizonte.tech/projetos-integrados-2/devlapa/devlapa.git>
cd <DevLapa>

# Entre na pasta do projeto
cd devlapa-api

# Instale as dependências e gere o build
mvn clean install 
```

### Executar localmente

```bash
 * Subir o Projeto: No IntelliJ ou terminal, execute:
   mvn spring-boot:run

 * Importar Coleção: No Postman, use a opção Import e selecione o arquivo JSON da nossa collection (ou aponte para http://localhost:8080/v3/api-docs para gerar automaticamente).
 * Autenticação:
   * Execute o endpoint POST /auth/login.
   * Copie o Token JWT gerado.
   * No Postman, vá na aba Authorization, selecione Bearer Token e cole o código.
```

### Acessar

- **Local:** http://localhost:PORTA
- **Produção:** https://SEU-APP.vercel.app _(preencher no CP-5)_

## 📅 Checkpoints

| CP | Data | Entrega | Status |
|----|------|---------|--------|
| CP-1 | 12/03 | Banco de Dados (SQL + ER) | OK     |
| CP-2 | 26/03 | Backend (API CRUD) | OK     |
| CP-3 | 16/04 | Integração (Frontend + API) |        |
| CP-4 | 07/05 | MVP end-to-end |        |
| CP-5 | 21/05 | Hospedagem + README final |        |

## 📝 Licença

Este projeto é parte da disciplina Projetos Integrados 2 — Uniube Uberlândia, 2026/1.
