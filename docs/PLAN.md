# spring-store — Backend API

## Stack

| Capa | Tecnología |
|---|---|
| Framework | Spring Boot 4.1 |
| Java | 21 |
| Build | Maven |
| DB | PostgreSQL (Neon) / H2 default |
| ORM | Spring Data JPA + Hibernate |
| Auth | Spring Security + JWT (jjwt) |
| Validation | spring-boot-starter-validation |

## Arquitectura

Feature-first: cada feature es un paquete autocontenido con su entity, repository, service, controller y DTOs.

```
com.example.demo/
├── DemoApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── JwtAuthFilter.java
├── user/
│   ├── User.java
│   ├── UserRepository.java
│   ├── UserService.java
│   ├── UserController.java
│   └── dto/
│       ├── UserRequest.java
│       └── UserResponse.java
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── JwtService.java
│   └── dto/
│       ├── LoginRequest.java
│       └── AuthResponse.java
├── product/
│   ├── Product.java
│   ├── ProductRepository.java
│   ├── ProductService.java
│   ├── ProductController.java
│   └── dto/
│       ├── ProductRequest.java
│       └── ProductResponse.java
├── cart/
│   ├── Cart.java
│   ├── CartItem.java
│   ├── CartRepository.java
│   ├── CartService.java
│   ├── CartController.java
│   └── dto/
├── order/
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── OrderRepository.java
│   ├── OrderService.java
│   ├── OrderController.java
│   └── dto/
└── common/
    ├── GlobalExceptionHandler.java
    └── ErrorResponse.java
```

## Entities

### User
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK, auto |
| name | String | |
| email | String | unique, @Email |
| password | String | hasheada (BCrypt) |
| role | Enum(USER, ADMIN) | |
| createdAt | LocalDateTime | |

### Product
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK, auto |
| name | String | |
| description | String | text |
| price | BigDecimal | |
| stock | Integer | |
| imageUrl | String | nullable |

### Cart
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK, auto |
| user | @OneToOne User | |

### CartItem
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK, auto |
| cart | @ManyToOne Cart | |
| product | @ManyToOne Product | |
| quantity | Integer | |

### Order
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK, auto |
| user | @ManyToOne User | |
| items | @OneToMany OrderItem | cascade |
| total | BigDecimal | |
| status | Enum(PENDING, CONFIRMED, SHIPPED, DELIVERED) | |
| createdAt | LocalDateTime | |

### OrderItem
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK, auto |
| order | @ManyToOne Order | |
| product | @ManyToOne Product | |
| quantity | Integer | |
| unitPrice | BigDecimal | captura precio al comprar |

## Endpoints

```
POST   /api/auth/register
POST   /api/auth/login

GET    /api/users/me
GET    /api/users              [ADMIN]

GET    /api/products
GET    /api/products/{id}
POST   /api/products           [ADMIN]
PUT    /api/products/{id}      [ADMIN]
DELETE /api/products/{id}      [ADMIN]

GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{id}
DELETE /api/cart/items/{id}

POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
```

## Orden de implementación

1. **Dependencias**: validation, security, jjwt → pom.xml
2. **User**: entity → repo → service → controller → dto
3. **Auth**: SecurityConfig + JwtAuthFilter + JwtService + AuthController
4. **Product**: CRUD completo
5. **Cart**: agregar/quitar items
6. **Order**: checkout desde cart + historial
7. **GlobalExceptionHandler**: errores consistentes

## Perfiles

| Perfil | DB | Cómo se activa |
|---|---|---|
| default | H2 mem | `./mvnw spring-boot:run` |
| local | Neon | `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` |

## Dependencias faltantes

```xml
spring-boot-starter-validation
spring-boot-starter-security
io.jsonwebtoken:jjwt-api:0.12.6
io.jsonwebtoken:jjwt-impl:0.12.6 (runtime)
io.jsonwebtoken:jjwt-jackson:0.12.6 (runtime)
```
