/README.md
# Sistema de E-commerce Full Stack - API RESTful & Angular

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen?style=for-the-badge&logo=spring)
![Angular](https://img.shields.io/badge/Angular-17+-red?style=for-the-badge&logo=angular)
![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue?style=for-the-badge&logo=typescript)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens)
![JaCoCo](https://img.shields.io/badge/Coverage-89%25-brightgreen?style=for-the-badge)

</div>

Sistema robusto de e-commerce Full Stack, desenvolvido com Spring Boot (Backend) e Angular (Frontend). O projeto implementa um fluxo real de vendas, desde o gerenciamento de produtos pelo administrador até a finalização de compra pelo cliente, com autenticação segura via JWT, controle de acesso baseado em papéis (RBAC) e uma suíte abrangente de testes automatizados.

---

## Screenshots do Sistema

### Tela de Login e Cadastro
<div align="center">
<img src="docs/screenshots/login.png" alt="Tela de Login" width="800"/>
</div>

### Página Inicial da Loja
<div align="center">
<img src="docs/screenshots/pageInicial.png" alt="Página Inicial" width="800"/>
</div>

### Catálogo de Produtos
<div align="center">
<img src="docs/screenshots/produtos.png" alt="Catálogo de Produtos" width="800"/>
</div>

### Painel Administrativo (Dashboard)
<div align="center">
<img src="docs/screenshots/dashboard.png" alt="Dashboard Administrativo" width="800"/>
</div>

### Carrinho de Compras
<div align="center">
<img src="docs/screenshots/carrinho.png" alt="Carrinho de Compras" width="800"/>
</div>

---

## Funcionalidades Principais

### Módulo do Cliente
- Cadastro e autenticação de usuários com tokens JWT.
- Catálogo de produtos com interface responsiva e Hero Carousel.
- Carrinho de compras persistente com funcionalidade "Comprar Agora".
- Checkout seguro com validação de estoque e baixa automática concorrente.
- Histórico completo de pedidos com detalhamento de itens e status.

### Módulo do Administrador
- Dashboard analítico com métricas de faturamento, volume de pedidos e alertas de estoque baixo.
- CRUD completo de produtos, incluindo upload e gerenciamento de imagens.
- CRUD de categorias para organização do catálogo.
- Gestão de fluxo de pedidos com atualização de status (Pendente, Pago, Enviado, etc.).
- Controle de acesso granular baseado em papéis (RBAC).

---

## Qualidade e Testes Automatizados

O projeto segue rigorosos padrões de engenharia de software, garantindo confiabilidade, manutenibilidade e preparação para ambiente de produção:

- **Cobertura de Código**: 89% de cobertura geral (JaCoCo), com todos os pacotes de negócio (Services, Controllers, DTOs) acima do threshold de 80%.
- **Suíte de Testes**: 103 testes automatizados executando com 100% de taxa de sucesso.
- **Estratégia de Testes**:
  - **Testes de Integração**: Validação completa dos endpoints REST utilizando `MockMvc` e banco de dados em memória (H2).
  - **Testes Unitários**: Isolamento e validação de regras de negócio complexas (ex: cálculo de totais, validação de estoque) utilizando `Mockito`.
  - **Testes de Validação**: Cobertura de constraints do Jakarta Validation (`@NotBlank`, `@NotNull`, `@Positive`) em todos os DTOs de requisição.
  - **Tratamento de Exceções**: Testes dedicados ao `GlobalExceptionHandler` para garantir respostas de erro padronizadas (400, 401, 403, 404).

---

## Stack Tecnológica

| Categoria | Tecnologia | Propósito |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.3.x | API RESTful e Injeção de Dependência |
| **Segurança** | Spring Security, JJWT, BCrypt | Autenticação stateless e criptografia de senhas |
| **Persistência** | Spring Data JPA, PostgreSQL, Flyway | Mapeamento objeto-relacional e versionamento de banco |
| **Frontend** | Angular 17+, TypeScript, RxJS, Tailwind CSS | Single Page Application (SPA) moderna e responsiva |
| **Qualidade** | JUnit 5, Mockito, MockMvc, JaCoCo | Testes automatizados e métricas de cobertura |

---

## Estrutura do Projeto

```text
Vendas/
├── backend/api/
│   ├── src/main/java/com/sualoja/api/
│   │   ├── config/           # Configurações de Security, JWT, CORS e Swagger
│   │   ├── controller/       # Endpoints REST documentados
│   │   ├── dto/              # Objetos de Transferência de Dados (Request/Response)
│   │   ├── exception/        # Tratamento global e padronizado de erros
│   │   ├── model/            # Entidades JPA e Enums de domínio
│   │   ├── repository/       # Interfaces Spring Data JPA
│   │   ├── security/         # Filtros JWT e implementação de UserDetailsService
│   │   └── service/          # Regras de negócio e orquestração de transações
│   ├── src/main/resources/
│   │   ├── application.yml   # Configurações de ambiente
│   │   └── db/migration/     # Scripts de versionamento do banco (Flyway)
│   ├── src/test/             # Suíte completa de testes unitários e de integração
│   └── uploads/produtos/     # Diretório de armazenamento de imagens
│
├── frontend/
│   └── src/app/
│       ├── core/             # Serviços singleton, Interceptors (JWT) e Models
│       └── features/         # Componentes de UI independentes (Standalone)
│           ├── admin/        # Dashboard e CRUDs de gerenciamento
│           ├── cart/         # Lógica e interface do carrinho de compras
│           ├── login/        # Fluxos de autenticação
│           ├── orders/       # Visualização e rastreamento de pedidos
│           ├── products/     # Catálogo público de produtos
│           └── shared/       # Componentes reutilizáveis e utilitários
│
├── docs/
│   └── screenshots/          # Documentação visual do sistema
└── README.md                 # Este arquivo


---


Pré-requisitos

    JDK 17+ e Maven 3.8+
    Node.js 18+ e Angular CLI
    PostgreSQL rodando localmente na porta 5432


1. Backend

cd backend/api
mvn clean spring-boot:run




2. Frontend

cd frontend
npm install
ng serve -o


Credenciais de Acesso
Administrador:

    Email: admin@teste.com
    Senha: 123456

Cliente:

    Email: teste@teste.com
    Senha: 123456
