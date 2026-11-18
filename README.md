# expense-service-backend

Expenses, categories and budgets.

Part of [Finance Master](https://github.com/financemaster-proj/platform) — a
personal finance system built as four backend services behind an API gateway.

## What this service owns

Expenses, categories, budgets and recurring expense rules. All money arithmetic for spending.

## What it does not own

Investments, reports or user identity. It stores a `user_id` UUID with no foreign key — the users table lives in auth-service and is never replicated here.

## Interface

| | |
|---|---|
| Port | `8082` |
| Database | `fm_expense` |
| Emits | `ExpenseRecorded`, `ExpenseUpdated`, `ExpenseDeleted`, `BudgetExceeded` |
| Consumes | `UserDeleted` |

## Running locally

Infrastructure lives in the [`platform`](https://github.com/financemaster-proj/platform)
repo. Clone it as a sibling of this one, then:

```bash
cd ../platform && docker compose -f infra/docker-compose.yml up -d
cd - && cp .env.example .env && ./gradlew bootRun
```

## Conventions

This service follows the project-wide rules in
[`platform/docs`](https://github.com/financemaster-proj/platform/tree/main/docs) —
in particular **no synchronous calls to another service**
([ADR-0004](https://github.com/financemaster-proj/platform/blob/main/docs/adr/0004-no-synchronous-service-calls.md)),
package by feature, Flyway for every schema change, and money as `BigDecimal`
with an explicit currency.
