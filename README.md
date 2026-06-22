# Solicitation API SEA

![Version](https://img.shields.io/badge/version-1.0.0-blue?style=flat)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.13-yellow)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![CI/CD](https://github.com/Martins20321/Solicitation-API-SEA/actions/workflows/ci-cd.yml/badge.svg)


API REST desenvolvida como desafio técnico para a SEA Tecnologia, para gerenciamento de solicitações de atendimento com controle de acesso por perfil (CLIENT, ANALYST, ADMIN).

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Stack](#stack)
- [Arquitetura](#arquitetura)
- [Como Rodar](#como-rodar)
- [Autenticação](#autenticação)
- [Perfis de Acesso](#perfis-de-acesso)
- [Endpoints](#endpoints)
- [Decisões Arquiteturais](#decisões-arquiteturais)

---

## Sobre o Projeto

Sistema de Solicitações de Atendimento com fluxo multi-step, onde clientes criam solicitações em 3 etapas com persistência parcial (podem salvar e continuar depois). Analistas avaliam as solicitações apenas dos estados (UFs) sob sua responsabilidade. Administradores gerenciam usuários e coberturas.

---

## Stack

- **Java 21** + **Spring Boot 3.5**
- **Spring Security** + **JWT (Auth0)**
- **PostgreSQL 15** — persistência transacional
- **Elasticsearch 8.13** — indexação e busca com filtros
- **Docker** + **Docker Compose**
- **AOP** — auditoria automática de endpoints críticos
- **ViaCEP** — enriquecimento automático de endereços
- **Spring Boot Actuator** — métricas e health check
- **Swagger/OpenAPI** — documentação interativa
- **GitHub Actions** — CI/CD

---

## Arquitetura

```
com.martinsdev.caretrack.api
├── controller/         # Endpoints REST
├── dto/                # Data Transfer Objects
├── infra/
│   ├── aop/            # Auditoria com AOP
│   ├── client/         # Integração ViaCEP
│   ├── exception/      # Exceções customizadas
│   ├── handler/        # GlobalExceptionHandler
│   ├── security/       # JWT, Filters, Configurations
│   └── springdoc/      # Documentação com OpenAPI/Swagger
├── model/
│   ├── document/       # Documentos Elasticsearch
│   ├── embedded/       # Steps da solicitação (@Embeddable)
│   └── enums/          # Enums do domínio
├── repository/         # Spring Data JPA + Elasticsearch
└── service/            # Regras de negócio
```

---

## Como Rodar

### Pré-requisitos

- Docker + Docker Compose instalados

### 1. Clone o repositório

```bash
git clone https://github.com/Martins20321/Solicitation-API-SEA.git
cd Solicitation-API-SEA
```

### 2. Configure as variáveis de ambiente

Crie o arquivo `.env` na raiz do projeto:

```env
POSTGRES_DB=solicitation
POSTGRES_USER=postgres
POSTGRES_PASSWORD=solicitationsea@2026!
JWT_SECRET=solicitationSecretKey@2026!
```

### 3. Suba os containers

```bash
docker compose up -d --build
```

Aguarde todos os containers subirem:

```
✔ Container solicitation-db   Healthy
✔ Container solicitation-es   Healthy
✔ Container solicitation-api  Started
```

### 4. Verifique o health check

```bash
GET http://localhost:8080/actuator/health
```

### 5. Acesse a documentação

```
http://localhost:8080/swagger-ui.html
```

### Migrações
O projeto utiliza `spring.jpa.hibernate.ddl-auto=update` - o Hibernate cria e atualiza as tabelas automaticamente na inicialização. Não é necessário rodar migrações manualmente.

---

## Autenticação

A API usa **JWT Bearer Token**. Para autenticar:

1. Faça login no endpoint `/auth/login`
2. Copie o token retornado
3. Adicione no header: `Authorization: Bearer {token}`

---

## Perfis de Acesso

| Perfil | Descrição |
|--------|-----------|
| `CLIENT` | Cria e gerencia suas próprias solicitações |
| `ANALYST` | Avalia solicitações dos estados sob sua responsabilidade |
| `ADMIN` | Acesso total — gerencia usuários e coberturas |

### Credenciais iniciais (ADMIN)

```
Email: admin@solicitation.com
Senha: admin123456
```

> O ADMIN é criado automaticamente na inicialização da aplicação.

---

## Endpoints

### Autenticação

```
POST /auth/register   → Cria conta CLIENT
POST /auth/login      → Autentica qualquer perfil
```

**Register:**
```json
{
  "name": "José Gabriel",
  "email": "gabriel@email.com",
  "password": "123456"
}
```

**Login:**
```json
{
  "email": "admin@solicitation.com",
  "password": "admin123456"
}
```

---

### Admin

```
POST /admin/users                    → Cria usuário (CLIENT, ANALYST ou ADMIN)
PUT  /admin/users/{id}/coverage      → Configura UFs do analista
```

**Criar ANALYST:**
```json
POST /admin/users
{
  "name": "Analista DF",
  "email": "analyst@solicitation.com",
  "password": "123456",
  "role": "ANALYST"
}
```

**Configurar cobertura:**
```json
PUT /admin/users/{id}/coverage
{
  "states": ["DF", "GO", "MG"]
}
```

---

### Solicitações (CLIENT)

```
POST /solicitations              → Cria rascunho (DRAFT)
PUT  /solicitations/{id}/step1   → Preenche dados básicos
PUT  /solicitations/{id}/step2   → Preenche endereço (integra ViaCEP)
PUT  /solicitations/{id}/step3   → Preenche confirmação
POST /solicitations/{id}/submit  → Envia para análise
GET  /solicitations              → Lista suas solicitações
GET  /solicitations/{id}         → Detalha uma solicitação
```

**Step 1:**
```json
{
  "serviceType": "INSTALLATION",
  "title": "Instalação de equipamento",
  "description": "Preciso de instalação de equipamento industrial na minha empresa"
}
```

**Step 2:**
```json
{
  "cep": "01310100",
  "number": "1000",
  "complement": "Sala 5"
}
```

**Step 3:**
```json
{
  "priority": "HIGH",
  "preferredDate": "2026-12-01",
  "estimatedValue": 150.00,
  "termsAccepted": true
}
```

---

### Analista

```
GET  /analyst/solicitations                    → Lista solicitações das UFs cobertas
GET  /analyst/solicitations/{id}               → Detalha uma solicitação
POST /analyst/solicitations/{id}/start         → Inicia análise (SUBMITTED → IN_REVIEW)
POST /analyst/solicitations/{id}/decide        → Aprova ou rejeita
GET  /analyst/solicitations/search             → Busca com filtros no Elasticsearch
```

**Decidir:**
```json
{
  "decision": "APPROVE",
  "comment": "Solicitação aprovada após análise dos documentos e verificação do endereço."
}
```

**Busca com filtros:**
```
GET /analyst/solicitations/search?q=equipamento&status=SUBMITTED&page=0&size=10
```

---

## Fluxo Multi-Step

```
CLIENT cria DRAFT
    ↓
Preenche Step 1 (dados básicos)
    ↓
Preenche Step 2 (endereço + ViaCEP automático)
    ↓
Preenche Step 3 (confirmação)
    ↓
Submit → status: SUBMITTED (bloqueado para edição)
    ↓
ANALYST inicia análise → status: IN_REVIEW
    ↓
ANALYST decide → status: APPROVED ou REJECTED
```

> O cliente pode salvar cada step e continuar depois — a solicitação fica em DRAFT até o submit.

---

## Decisões Arquiteturais

**Roles como enum simples** — os perfis são fixos e conhecidos (CLIENT, ANALYST, ADMIN). Tabela dinâmica de roles seria complexidade desnecessária.

**`AnalystCoverage` separado do `User`** — Single Responsibility: autenticação e cobertura geográfica são responsabilidades distintas.

**`@Embeddable` para steps** — organização sem tabela extra. Os três steps ficam na mesma tabela `tb_solicitation`, mas com código organizado por responsabilidade.

**Builder Pattern** — construção explícita de objetos sem dependência da ordem dos parâmetros.

**Role no claim JWT** — performance: sem consulta ao banco por requisição para verificar permissões.

**PostgreSQL + Elasticsearch** — PostgreSQL como fonte da verdade, Elasticsearch para busca full-text com filtros e paginação eficiente.

**AOP para auditoria** — comportamento transversal sem poluir a lógica de negócio. Auditoria persistida em `tb_audit_logs`.

**`if` simples ao invés de Strategy Pattern** — para a criação de usuários, a diferença entre roles é mínima (ANALYST cria AnalystCoverage). Strategy seria complexidade desnecessária.

---

## Autor

**José Gabriel Martins**
- GitHub: [Martins20321](https://github.com/Martins20321)
- Email: jgmsilva11@gmail.com
