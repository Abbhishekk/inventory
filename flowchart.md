# Inventory Management System — Flow Chart

```mermaid
flowchart TD
  %% Clients and Edge
  Client[Client (Web / Postman)]
  APIGateway[API Gateway<br/>(8080, HTTP)]
  Eureka[Eureka Server<br/>(8761, HTTP)]

  %% Product service group
  subgraph ProductSvc [Product Service]
    PService[Product Service<br/>(8081, HTTP)]
    PDB[(PostgreSQL — product DB<br/>port 5432, JDBC)]
    SMTP[External SMTP Server<br/>(587, SMTP/TLS)]
  end

  %% Category service group
  subgraph CategorySvc [Category Service]
    CService[Category Service<br/>(8085, HTTP)]
    CDB[(PostgreSQL — category DB<br/>port 5432, JDBC)]
  end

  %% Supplier service group
  subgraph SupplierSvc [Supplier Service]
    SService[Supplier Service<br/>(8082, HTTP)]
    SDB[(PostgreSQL — supplier DB<br/>port 5432, JDBC)]
  end

  %% Flows
  Client -- "HTTP (Bearer JWT) ->" --> APIGateway

  APIGateway -- "HTTP -> (route) Product (8081)" --> PService
  APIGateway -- "HTTP -> (route) Category (8085)" --> CService
  APIGateway -- "HTTP -> (route) Supplier (8082)" --> SService

  %% Service discovery registrations
  APIGateway -- "Eureka register/discover (HTTP 8761)" --> Eureka
  PService -- "Eureka register/discover (HTTP 8761)" --> Eureka
  CService -- "Eureka register/discover (HTTP 8761)" --> Eureka
  SService -- "Eureka register/discover (HTTP 8761)" --> Eureka

  %% DB access
  PService -- "JDBC (Postgres) 5432" --> PDB
  CService -- "JDBC (Postgres) 5432" --> CDB
  SService -- "JDBC (Postgres) 5432" --> SDB

  %% Product notifications
  PService -- "SMTP (TLS) 587 — stock alerts / reorder" --> SMTP

  %% Optional direct inter-service calls
  PService -. "Optional: HTTP (REST) ->" .-> SService
  PService -. "Optional: HTTP (REST) ->" .-> CService

  %% Legend
  classDef info fill:#f9f,stroke:#333,stroke-width:1px;
  class Client,APIGateway,Eureka,PService,CService,SService,PDB,CDB,SDB,SMTP info;
```
