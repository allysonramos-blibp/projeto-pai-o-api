# 📡 Especificação da API — DevLapa

> **Base URL:** `http://localhost:PORTA/api`
> **Formato:** JSON
> **Autenticação:** _(definir: JWT, session, etc.)_

---

## Endpoints

### Recurso: `/exemplo`

#### `GET /api/exemplo`
Retorna a lista de itens.

**Response 200:**
```json
[
  {{
    "id": 1,
    "nome": "Item exemplo",
    "criado_em": "2026-03-26T10:00:00Z"
  }}
]
```

#### `POST /api/exemplo`
Cria um novo item.

**Request Body:**
```json
{{
  "nome": "Novo item"
}}
```

**Response 201:**
```json
{{
  "id": 2,
  "nome": "Novo item",
  "criado_em": "2026-03-26T10:05:00Z"
}}
```

#### `PUT /api/exemplo/:id`
Atualiza um item existente.

#### `DELETE /api/exemplo/:id`
Remove um item.

---

> **Instruções:** Substitua `/exemplo` pelos recursos reais do seu projeto.
> Documente todos os endpoints, incluindo códigos de erro (400, 401, 404, 500).
