flowchart TD
    subgraph Clients
        Client[Client_Web_Postman]
    end

    subgraph Gateway
        APIGateway[API_Gateway_8080_HTTP]
    end

    subgraph Discovery
        Eureka[Eureka_Server_8761_HTTP]
    end

    subgraph Product
        ProductService[Product_Service_8081_HTTP]
        ProductDB[(PostgreSQL_Product)]
        SMTP[SMTP_Server_External]
    end

    subgraph Category
        CategoryService[Category_Service_8085_HTTP]
        CategoryDB[(PostgreSQL_Category)]
    end

    subgraph Supplier
        SupplierService[Supplier_Service_8082_HTTP]
        SupplierDB[(PostgreSQL_Supplier)]
    end

    %% Client to API Gateway
    Client -- "HTTP" --> APIGateway

    %% API Gateway to Services
    APIGateway -- "HTTP" --> ProductService
    APIGateway -- "HTTP" --> CategoryService
    APIGateway -- "HTTP" --> SupplierService

    %% Services to their DBs
    ProductService -- "JDBC:5432" --> ProductDB
    CategoryService -- "JDBC:5432" --> CategoryDB
    SupplierService -- "JDBC:5432" --> SupplierDB

    %% Product Service to SMTP
    ProductService -- "SMTP" --> SMTP

    %% Service Discovery
    ProductService -- "Register/Discover_HTTP" --> Eureka
    CategoryService -- "Register/Discover_HTTP" --> Eureka
    SupplierService -- "Register/Discover_HTTP" --> Eureka
    APIGateway -- "Register/Discover_HTTP" --> Eureka
