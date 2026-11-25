# Projeto Empréstimo de Equipamentos

API REST desenvolvida em **Java 25** com **Spring Boot 4.1.2** para gerenciamento e controle de empréstimos de equipamentos de TI para colaboradores.

- **Deploy (Render):** https://projetoemprestimo-i7aa.onrender.com/

## Tecnologias Utilizadas

- Java 25
- Spring Boot 4.1.2
- Spring Data JPA
- H2 Database (banco de dados em arquivo local)
- Bean Validation (Hibernate Validator)
- JUnit 5 e Mockito

## Bônus Implementados

- Validação com Bean Validation (`@Valid`, `@NotBlank`, `@Email`)
- Tratamento global de erros com `@RestControllerAdvice` (`400`, `404` e `409`)

## Como Executar o Projeto

### Pré-requisitos
- JDK 25 instalado.

### Executar a aplicação
Dentro do diretório `emprestimo`:

```bash
./mvnw spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080`.

- **Interface Web:** `http://localhost:8080`
- **H2 Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./dados/emprestimos`)

### Executar os testes

```bash
./mvnw test
```

## Endpoints da API

### Equipamentos (`/equipamentos`)
- `GET /equipamentos` — Lista equipamentos. Filtros opcionais:
  - `?tipo=NOTEBOOK` (`NOTEBOOK`, `MONITOR`, `MOUSE`, `TECLADO`, `HEADSET`, `OUTRO`)
  - `?disponivel=true|false`
- `GET /equipamentos/{id}` — Detalha um equipamento por ID.
- `POST /equipamentos` — Cadastra um novo equipamento (inicia com `disponivel=true`).
- `PUT /equipamentos/{id}` — Atualiza nome e tipo de um equipamento.
- `DELETE /equipamentos/{id}` — Remove o equipamento (bloqueado se houver empréstimos registrados).

### Colaboradores (`/colaboradores`)
- `GET /colaboradores` — Lista todos os colaboradores cadastrados.
- `GET /colaboradores/{id}` — Detalha um colaborador por ID.
- `POST /colaboradores` — Cadastra um colaborador (valida e-mail único).
- `PUT /colaboradores/{id}` — Atualiza os dados de um colaborador.
- `DELETE /colaboradores/{id}` — Remove um colaborador (bloqueado se houver empréstimos registrados).
- `GET /colaboradores/{id}/emprestimos` — Lista os empréstimos associados ao colaborador.

### Empréstimos (`/emprestimos`)
- `GET /emprestimos` — Lista todos os empréstimos (aceita o filtro `?atrasados=true`).
- `GET /emprestimos/{id}` — Detalha um empréstimo por ID.
- `POST /emprestimos?equipamentoId=1&colaboradorId=1` — Registra um novo empréstimo com prazo de devolução de 14 dias. Valida se o equipamento está disponível e se o colaborador não possui atrasos.
- `PUT /emprestimos/{id}/devolucao` — Registra a devolução e libera o equipamento como disponível.

## Tratamento de Erros

- `400 Bad Request`: dados de entrada inválidos, com detalhamento dos campos com erro.
- `404 Not Found`: recurso não encontrado.
- `409 Conflict`: violação de regra de negócio (e-mail ou patrimônio duplicado, equipamento indisponível, colaborador com empréstimo em atraso ou tentativa de exclusão com vínculos).
