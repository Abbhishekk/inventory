flowchart LR
  Client[Client]
  APIGateway[API Gateway]
  Eureka[Eureka Server]

  subgraph ProductSvc [Product Service]
    PService[Product Service]
    PDB[(PostgreSQL — product DB)]
    SMTP[External SMTP]
  end

  subgraph CategorySvc [Category Service]
    CService[Category Service]
    CDB[(PostgreSQL — category DB)]
  end

  subgraph SupplierSvc [Supplier Service]
    SService[Supplier Service]
    SDB[(PostgreSQL — supplier DB)]
  end

  Client -- "HTTP (Bearer JWT)" --> APIGateway
  APIGateway -- "HTTP (REST) / routes" --> PService
  APIGateway -- "HTTP (REST) / routes" --> CService
  APIGateway -- "HTTP (REST) / routes" --> SService
  APIGateway -- "Eureka: register/discover (HTTP)" --> Eureka
  PService -- "Eureka: register/discover (HTTP)" --> Eureka
  CService -- "Eureka: register/discover (HTTP)" --> Eureka
  SService -- "Eureka: register/discover (HTTP)" --> Eureka
  PService -- "JDBC (Postgres)" --> PDB
  CService -- "JDBC (Postgres)" --> CDB
  SService -- "JDBC (Postgres)" --> SDB
  PService -- "SMTP (TLS)" --> SMTP
  PService -- "Optional: REST to Supplier/Category" --> SService
  PService -- "Optional: REST to Supplier/Category" --> CService
