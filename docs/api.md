# API REST

Base URL: `http://localhost:8080`

Documentação interativa: **Swagger UI** em `/swagger-ui.html`.

Todas as respostas são JSON. Datas em ISO-8601 (`2026-06-27T14:30:00Z`).

---

## Tickets

### Criar chamado

```http
POST /tickets
Content-Type: application/json
```

```json
{
  "type": "BUG",
  "title": "Erro ao gerar boleto",
  "description": "O boleto retorna 500 ao confirmar o pagamento.",
  "clientId": 1
}
```

`type` ∈ `BUG | BILLING | FEATURE_REQUEST | SUPPORT`. A categoria e a prioridade iniciais são derivadas pelo pipeline (Chain + Strategy).

**201 Created**

```json
{
  "id": 42,
  "title": "Erro ao gerar boleto",
  "status": "ABERTO",
  "priority": "HIGH",
  "category": "BILLING",
  "client": { "id": 1, "name": "ACME" },
  "team": null,
  "createdAt": "2026-06-27T14:30:00Z"
}
```

---

### Listar chamados

```http
GET /tickets
```

Suporta paginação Spring Data: `?page=0&size=20&sort=createdAt,desc`.

**200 OK** — lista paginada de chamados.

---

### Detalhar chamado

```http
GET /tickets/{id}
```

**200 OK** — o chamado. **404** se não existir.

---

### Atualizar status

```http
PUT /tickets/{id}/status
Content-Type: application/json
```

```json
{ "status": "EM_ANDAMENTO" }
```

A transição dispara os **Observers** (e-mail, Slack, auditoria, dashboard).

**200 OK** — chamado atualizado. **422** se a transição for inválida.

---

### Atribuir equipe

```http
POST /tickets/{id}/assign
Content-Type: application/json
```

```json
{ "teamId": 3 }
```

**200 OK** — chamado com a equipe atribuída.

---

### Chamados abertos

```http
GET /tickets/open
```

Atalho para `status != FINALIZADO`. **200 OK**.

---

### Relatório

```http
GET /tickets/report
```

**200 OK**

```json
{
  "total": 128,
  "byStatus":   { "ABERTO": 30, "EM_ANALISE": 18, "EM_ANDAMENTO": 22, "FINALIZADO": 58 },
  "byPriority": { "URGENT": 8, "HIGH": 40, "MEDIUM": 55, "LOW": 25 },
  "byCategory": { "BUG": 50, "BILLING": 30, "FEATURE_REQUEST": 28, "SUPPORT": 20 }
}
```

---

## Cadastros auxiliares

Endpoints CRUD padrão para os agregados de apoio:

| Recurso | Endpoints |
| --- | --- |
| Clientes | `POST /clients`, `GET /clients`, `GET /clients/{id}` |
| Usuários | `POST /users`, `GET /users`, `GET /users/{id}` |
| Equipes | `POST /teams`, `GET /teams`, `GET /teams/{id}` |

---

## Tratamento de erros

Respostas de erro seguem um corpo padronizado (`@RestControllerAdvice`):

```json
{
  "timestamp": "2026-06-27T14:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket 42 não encontrado",
  "path": "/tickets/42"
}
```

| Código | Quando |
| --- | --- |
| `400` | corpo/validação inválida (`@Valid`) |
| `404` | recurso inexistente |
| `409` | conflito (ex.: e-mail duplicado) |
| `422` | transição de status inválida |
| `500` | erro inesperado |