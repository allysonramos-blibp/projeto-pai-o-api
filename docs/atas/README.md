# 📝 Atas de Reunião

# Ata — 12/03/2026

**Presentes:** Allyson,Amanda,Clériston, Débora 
**Duração:** 21:15 — 21:45

## Pauta
- Apresentação do projeto Sistema de Gerenciamento para Bares
- Definição do problema e proposta de solução 
— Apresentação do prototipo FIGMA
- Estrutura do banco de dados
- Organização do projeto no Trello(Sprints)
- Definição das tecnologias e metodologia de desenvolvimento

## Decisões
- Foi definido o desenvolvimento do sistema DevLap, voltado para gestão de bates de pequeno porte, visando solucionar problemas como controle manual e falta de organização 
- Uso de protótipos no Figma para validação das telas e fluxos do sistema  
- Utilização de banco de dados relacional (PostgreSQL)
- Foi decidido utilizar Scrum + Kanban, com organização das tarefas no trello e a divisão em sprints 
- Principais módulos do sistema:
Controle de comandas
 Estoque
 Vendas 
 Financeiro 
 Fornecedores 

## Tarefas atribuídas
| Tarefa | Responsável | Prazo | 
|--------|-------------|-------|
| Prototipo Figma | Amanda | 12/03 |
| Modelagem do BD | Clériston | 12/03 |
| Organização do Trello e sprints | Allyson | 12/03 |
| Definição das tecnologias e estrutura do sistemas | Equipe | 12/03 |
| Documentação e Levantamento de requisitos | Débora | 12/03 |


# Ata — 26/03/2026

*Presentes:* Allyson,Amanda,Clériston, Débora
*Duração:* 21:50— 22:15

## Pauta
- Apresentação da arquitetura do Sistema
- Definição das camadas de aplicação
- Apresentação dos endpoints da API REST
- Apresentação dos testes manuais(MVP)
- Organização das tarefas da equipe

## Decisões
- Definida a arquitetura do sistema baseada em camadas utilizando:
  Controller: responsáveis pelas requisições HTTP e retorno em JSON
  Service: responsável pela regra de negócios
  Repository: responsável pelo acesso ao banco de dados
  Database: PostgreSQL com uso de migrations flyway
- Definidos os principais endpoints da API
  POST/api/login
  POST/api/produtos
  POST/api/contas-pagar
  GET/api/produtos
  GET/api/estoque
- Definidos os primeiros testes do sistema
- Atualização da organização do Trello e sprints

## Tarefas atribuídas
| Tarefa | Responsável | Prazo   |
|--------|-------------|---------|
|Diagrama de Classes UML|Cleriston| 26/03   |
|Endpoints|Allyson| 26/03   |
|Organização do Trello e sprints|Amanda| 26/03   |
|Arquitetura do Sistema|Debora| 26/03   |
|API- rest | Débora | 26/03   |