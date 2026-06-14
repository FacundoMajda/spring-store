# Spring Store API

E-commerce backend con **Spring Boot 4**, **JWT auth**, y **OpenAPI docs**.

## Stack

| Capa | Tecnología |
|------|-----------|
| Runtime | Java 21 + Spring Boot 4.1.0 |
| Auth | JWT (jjwt 0.12.6) |
| DB | H2 (default) / PostgreSQL en Neon (profile `local`) |
| Docs | springdoc-openapi + Swagger UI |
| Build | Maven |

## Arrancar

### Default (H2 en memoria)

```bash
./mvnw spring-boot:run
```

### Con Neon (PostgreSQL)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

La app arranca en `http://localhost:8080`.

## Perfiles

| Perfil | DB | DataSource |
|--------|----|------------|
| `default` | H2 en memoria | `jdbc:h2:mem:dev` |
| `local` | PostgreSQL (Neon) | configurado en `application-local.yml` |

El profile `local` apunta a una base Neon compartida. Si necesitás tu propia instancia, editá `application-local.yml` con tus credenciales.

## Seed Data

Al iniciar, `DataInitializer` crea:

- **Admin**: `admin@springstore.com` / `admin123`
- **Productos**: Laptop Gamer, Mouse RGB
- **Users de prueba**: varios con contraseña `123456`

## Endpoints

### Auth

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Login, devuelve JWT |

### Products

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/products` | - | Listar productos (paginado) |
| GET | `/api/products/{id}` | - | Producto por ID |
| POST | `/api/products` | ADMIN | Crear producto |
| PUT | `/api/products/{id}` | ADMIN | Actualizar producto |
| DELETE | `/api/products/{id}` | ADMIN | Eliminar producto |

### Cart

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/cart` | Ver carrito |
| POST | `/api/cart/items` | Agregar item |
| PUT | `/api/cart/items/{itemId}` | Actualizar cantidad |
| DELETE | `/api/cart/items/{itemId}` | Eliminar item |

### Orders

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/orders` | Checkout (carrito → orden) |
| GET | `/api/orders` | Historial de órdenes (paginado) |
| GET | `/api/orders/{id}` | Detalle de orden |

### Users (admin)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/users` | Listar usuarios (paginado) |

## Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Hacé clic en **Authorize** y pegá el token JWT que devuelve `/api/auth/login`.

## Tests

```bash
./mvnw test
```

## Paginación

Todos los endpoints GET de listas aceptan:

```
?page=0&size=10&sort=name,asc
```

Respuesta:

```json
{
  "content": [...],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 25,
    "totalPages": 3
  }
}
```

## Status Codes

| Código | Cuándo |
|--------|--------|
| 200 | OK |
| 400 | Validación / cart vacío |
| 401 | Credenciales inválidas / token inválido |
| 403 | Sin permisos de admin |
| 404 | Recurso no encontrado |
| 409 | Stock insuficiente / conflicto |
