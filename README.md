# Projeto Empréstimo de Equipamentos

API REST desenvolvida em **Java 25** com **Spring Boot 4 / 3** para gerenciamento e controle de empréstimos de equipamentos de TI para colaboradores.

---

## 🛠️ Tecnologias Utilizadas

- **Java 25**
- **Spring Boot**
- **Spring Data JPA**
- **H2 Database** (banco de dados em arquivo local)
- **Bean Validation** (Hibernate Validator)
- **SpringDoc OpenAPI / Swagger UI** (documentação interativa da API)
- **JUnit 5 & Mockito** (testes unitários)
- **Maven Wrapper**

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- JDK 25 instalado.

### Executar a aplicação
Dentro do diretório `emprestimo`:
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

- **Interface Web (Frontend)**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:file:./dados/emprestimos`)

### Executar os testes
```bash
./mvnw test
```

---

## 📚 Documentação da API (Swagger / OpenAPI)

Com a aplicação rodando, acesse a documentação interativa em:
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **H2 Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - JDBC URL: `jdbc:h2:file:./dados/emprestimos`
  - User: `sa`
  - Senha: *(em branco)*

---

## 📌 Endpoints da API

### 1. Equipamentos (`/equipamentos`)
- `GET /equipamentos`: Lista equipamentos. Suporta filtros opcionais:
  - `?tipo=NOTEBOOK` (NOTEBOOK, MONITOR, MOUSE, TECLADO, HEADSET, OUTRO)
  - `?disponivel=true|false`
- `GET /equipamentos/{id}`: Detalha um equipamento por ID.
- `POST /equipamentos`: Cadastra um novo equipamento (nasce com `disponivel=true`).
- `PUT /equipamentos/{id}`: Atualiza o nome e tipo de um equipamento.
- `DELETE /equipamentos/{id}`: Remove o equipamento (bloqueado se houver empréstimos registrados).

### 2. Colaboradores (`/colaboradores`)
- `GET /colaboradores`: Lista todos os colaboradores cadastrados.
- `GET /colaboradores/{id}`: Detalha um colaborador por ID.
- `POST /colaboradores`: Cadastra um colaborador (validação de e-mail único e CEP de 8 dígitos).
- `PUT /colaboradores/{id}`: Atualiza os dados de um colaborador.
- `DELETE /colaboradores/{id}`: Remove um colaborador (bloqueado se houver empréstimos registrados).
- `GET /colaboradores/{id}/emprestimos`: Lista todos os empréstimos associados ao colaborador.

### 3. Empréstimos (`/emprestimos`)
- `GET /emprestimos`: Lista todos os empréstimos.
  - Filtro: `?atrasados=true` para listar apenas empréstimos em atraso.
- `GET /emprestimos/{id}`: Detalha um empréstimo por ID.
- `POST /emprestimos`: Realiza um novo empréstimo.
  - Payload: `{"equipamentoId": 1, "colaboradorId": 1}`
  - Define automaticamente prazo de devolução de 14 dias.
  - Valida se o equipamento está disponível e se o colaborador não possui empréstimos em atraso.
- `PUT /emprestimos/{id}/devolucao`: Registra a devolução do empréstimo e libera o equipamento como disponível.

---

## 🧪 Regras de Negócio e Tratamento de Erros

- Respostas de erro padronizadas com status HTTP adequados:
  - `404 Not Found`: recurso não encontrado.
  - `409 Conflict`: conflito de dados (ex: e-mail duplicado, patrimônio duplicado, equipamento indisponível, colaborador com atraso, exclusão com vínculos).
  - `400 Bad Request`: dados de entrada inválidos com mensagem detalhada dos campos afetados.
