# API REST

Base URL: `http://localhost:8080`

Documentação interativa: **Swagger UI** em `/swagger-ui.html`.

Todas as respostas são JSON. Identificadores são **UUID**. Datas em ISO-8601.

---

## Tickets — `/api/tickets`

### Criar chamado

```http
POST /api/tickets
Content-Type: application/json
```

```json
{
  "title": "Erro ao gerar boleto",
  "description": "O boleto retorna 500 ao confirmar o pagamento.",
  "clientId": "3f1c9d2e-5a7b-4c8d-9e0f-1a2b3c4d5e6f",
  "category": "BILLING",
  "priority": "HIGH"
}
```

- `title`, `description` e `clientId` são obrigatórios.
- `category` (`BUG | BILLING | SUPPORT`) é **opcional**: se omitida, o pipeline classifica automaticamente a partir do texto (regras ou IA — ver [`getting-started.md`](getting-started.md)).
- `priority` (`LOW | MEDIUM | HIGH | URGENT`) é **opcional**: se omitida, é derivada pela Strategy de prioridade (palavras-chave no título/descrição).

**201 Created**

```json
{
  "id": "9a8b7c6d-5e4f-3a2b-1c0d-9e8f7a6b5c4d",
  "title": "Erro ao gerar boleto",
  "description": "O boleto retorna 500 ao confirmar o pagamento.",
  "status": "OPEN",
  "priority": "HIGH",
  "category": "BILLING",
  "clientId": "3f1c9d2e-5a7b-4c8d-9e0f-1a2b3c4d5e6f",
  "clientName": "ACME",
  "assignedTeamId": "…",
  "assignedTeamName": "Billing Team",
  "assignedUserId": null,
  "assignedUserName": null,
  "createdAt": "2026-06-27T14:30:00",
  "updatedAt": "2026-06-27T14:30:00",
  "closedAt": null
}
```

---

### Atualizar status

```http
PUT /api/tickets/{id}/status
Content-Type: application/json
```

```json
{ "status": "IN_PROGRESS" }
```

A transição dispara os **Observers** (e-mail, Slack, auditoria, dashboard). Ao mudar para `CLOSED`, `closedAt` é preenchido.

**200 OK** — chamado atualizado. **409 Conflict** se a transição for inválida (ver matriz de transições em [`data-model.md`](data-model.md)).

---

### Atribuir equipe/usuário

```http
POST /api/tickets/{id}/assign
Content-Type: application/json
```

```json
{ "teamId": "…", "userId": "…" }
```

Informe ao menos um entre `teamId` e `userId`. **200 OK** com o chamado atualizado; **400** se ambos forem nulos; **404** se time/usuário não existir.

---

### Fechar / Reabrir

```http
POST /api/tickets/{id}/close
POST /api/tickets/{id}/reopen
```

Atalhos de ação implementados via **Command**. Aplicam a transição de status correspondente (`CLOSED` / `REOPENED`) e disparam os Observers. **200 OK**, ou **409** se a transição não for permitida a partir do status atual.

---

### Chamados abertos

```http
GET /api/tickets/open
```

Retorna os chamados com status `OPEN`. **200 OK** — lista de chamados.

---

### Relatório

```http
GET /api/tickets/report
```

**200 OK**

```json
{
  "totalTickets": 128,
  "byStatus":   { "OPEN": 30, "IN_PROGRESS": 22, "RESOLVED": 18, "CLOSED": 55, "REOPENED": 3 },
  "byCategory": { "BUG": 60, "BILLING": 40, "SUPPORT": 28 },
  "byPriority": { "LOW": 25, "MEDIUM": 55, "HIGH": 40, "URGENT": 8 }
}
```

---

## Cadastros auxiliares

Endpoints CRUD padrão (`POST`, `GET` lista, `GET /{id}`, `PUT /{id}`, `DELETE /{id}`):

| Recurso | Base |
| --- | --- |
| Clientes | `/api/clients` |
| Usuários | `/api/users` |
| Equipes | `/api/support-teams` |

---

## Tratamento de erros

As respostas de erro seguem o formato **RFC 7807 / `ProblemDetail`** (`@RestControllerAdvice`), com um `timestamp` adicional:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 404,
  "detail": "Chamado não encontrado",
  "timestamp": "2026-06-27T14:30:00Z"
}
```

Erros de validação (`@Valid`) incluem ainda um mapa `errors` campo → mensagem.

| Código | Quando |
| --- | --- |
| `400` | corpo/validação inválida (`@Valid`) ou regra de negócio (ex.: atribuição sem time/usuário) |
| `404` | recurso inexistente |
| `409` | transição de status inválida |
| `500` | erro inesperado |
