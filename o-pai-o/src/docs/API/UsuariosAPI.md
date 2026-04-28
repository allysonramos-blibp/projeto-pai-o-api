# API - Gestão de Usuários

API REST desenvolvida com Spring Boot para controle de acesso, perfis e autenticação.

## Base URL
```
http://localhost:8080/api/usuarios
```

---

## Autenticação no Postman

Para acessar os endpoints protegidos, é necessário configurar o Bearer Token:

1. Realize o login no endpoint `/auth/login`.
2. Copie o valor do campo `token` da resposta.
3. No Postman, vá em **Authorization** -> **Type: Bearer Token** e cole o código.

---

## Endpoints

### Autenticação (Login)

**POST** `http://localhost:8080/auth/login`

Realiza o acesso e retorna o token JWT.

### Body
```json
{
  "login": "allyson",
  "senha": "123"
}
```

### Resposta
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "nome": "Allyson",
    "perfil": "ADMIN"
}
```

---

### Criar usuário

**POST** `http://localhost:8080/api/usuarios`

Cria um novo usuário no sistema.

### Body
```json
{
  "nome": "João Silva",
  "login": "joao.dev",
  "senha": "123"
}
```     

### Resposta
```json
{
    "id": 15,
    "nome": "João Silva",
    "login": "joao.dev",
    "perfil": "USUARIO",
    "ativo": true,
    "data_cadastro": "2026-03-20T19:30:00"
}
```

---

### Listar usuários

**GET** `http://localhost:8080/api/usuarios`

Retorna todos os usuários cadastrados.

---

### Tornar Administrador

**PATCH** `http://localhost:8080/api/usuarios/tornar-admin/{id}`

Atualiza o perfil do usuário para **ADMIN**.

---

### Deletar usuário

**DELETE** `http://localhost:8080/api/usuarios/{id}`

Realiza a inativação lógica do usuário.

---

