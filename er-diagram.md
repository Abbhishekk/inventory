erDiagram
    PRODUCT {
        BIGINT id PK
        VARCHAR name
        TEXT description
        DECIMAL price
        INT stock
        INT reorder_level
        BIGINT category_id FK
        BIGINT supplier_id FK
    }
    CATEGORY {
        BIGINT id PK
        VARCHAR name
        TEXT description
    }
    SUPPLIER {
        BIGINT id PK
        VARCHAR name
        VARCHAR contact_email
        VARCHAR phone
        TEXT address
    }
    USER {
        BIGINT id PK
        VARCHAR username
        VARCHAR email
        VARCHAR password_hash
    }
    ROLE {
        BIGINT id PK
        VARCHAR name
    }
    USER_ROLE {
        BIGINT user_id FK
        BIGINT role_id FK
    }

    CATEGORY ||--o{ PRODUCT : "contains"
    SUPPLIER ||--o{ PRODUCT : "supplies"
    USER ||--o{ USER_ROLE : "assigned"
    ROLE ||--o{ USER_ROLE : "granted"
