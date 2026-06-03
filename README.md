# Shortify Backend

Backend de **Shortify**, una API REST para acortar URLs, registrar accesos a cada enlace y gestionar autenticacion con JWT, refresh token, verificacion por correo y login con Google.

## Resumen

- Crea URLs cortas a partir de enlaces largos.
- Permite crear enlaces de forma anonima o autenticada.
- Guarda estadisticas de acceso: IP, navegador, sistema operativo, arquitectura y fecha.
- Autenticacion con email/password y verifyicacion de correo.
- Login con Google ID token.
- Access token + refresh token con rotacion.
- Rate limiting por IP (100 req/min).
- Actualizacion en tiempo real del contador de clics via WebSocket.

## Stack

- Java 21
- Spring Boot 3.4.4
- Spring Web / Security / Data JPA / Validation / Mail
- Spring WebSocket (STOMP)
- PostgreSQL
- Bucket4j (rate limiting)
- Maven

## Endpoints

Base URL local:

```text
http://localhost:8080
```

---

### Autenticacion (`/api/auth`)

---

#### `POST /api/auth/register` -- Registrar usuario

Crea un usuario y envia un email de verificacion.

**Request body:**

```json
{
  "email": "user@example.com",
  "password": "StrongPass1!"
}
```

| Campo      | Tipo     | Reglas                                     |
|------------|----------|--------------------------------------------|
| `email`    | `string` | Obligatorio, 8-40 chars, formato email     |
| `password` | `string` | Obligatorio, 8-20 chars, mayus, minus, digito, simbolo |

**Response:** `201 Created`
```
User registered. Please check your email to confirm your account.
```

| Status | Significado                          |
|--------|--------------------------------------|
| `201`  | Usuario registrado, email enviado    |
| `409`  | El email ya existe y ya esta verificado |
| `400`  | Validacion fallida                   |

> Si el email ya existe pero no esta verificado, se reenvia el email y se actualiza la password.

---

#### `POST /api/auth/login` -- Iniciar sesion

**Request body:**

```json
{
  "email": "user@example.com",
  "password": "StrongPass1!"
}
```

**Response:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresInMs": 300000,
  "refreshExpiresInMs": 2592000000,
  "email": "user@example.com"
}
```

| Campo                | Tipo     | Descripcion                       |
|----------------------|----------|-----------------------------------|
| `token`              | `string` | Access token JWT                  |
| `refreshToken`       | `string` | Refresh token JWT                 |
| `tokenType`          | `string` | Siempre `"Bearer"`                |
| `expiresInMs`        | `long`   | TTL del access token en ms        |
| `refreshExpiresInMs` | `long`   | TTL del refresh token en ms       |
| `email`              | `string` | Email del usuario autenticado     |

El access token tambien se devuelve en el header `Authorization: Bearer <token>`.

| Status | Significado                     |
|--------|---------------------------------|
| `200`  | Login exitoso                   |
| `403`  | Credenciales incorrectas        |
| `400`  | Validacion fallida              |

---

#### `GET /api/auth/login` -- Verificar sesion

Requiere JWT.

**Response:** `200 OK`
```
user@example.com
```

| Status | Significado          |
|--------|----------------------|
| `200`  | Email del usuario    |
| `403`  | No autenticado       |

---

#### `POST /api/auth/google` -- Login con Google

**Request body:**

```json
{
  "token": "google-id-token"
}
```

**Response:** `200 OK` -- Misma estructura que `login`.

| Status | Significado                        |
|--------|------------------------------------|
| `200`  | Login exitoso                      |
| `403`  | Token de Google invalido           |
| `400`  | Validacion fallida                 |

> Si el usuario no existe, se crea automaticamente con `emailVerified = true`.

---

#### `POST /api/auth/refresh` -- Rotar tokens

**Request body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:** `200 OK` -- Misma estructura que `login`, con nuevos tokens.

| Status | Significado                         |
|--------|-------------------------------------|
| `200`  | Nuevo access + refresh token        |
| `401`  | Refresh token invalido, revocado o expirado |

> El refresh token anterior se revoca (token rotation).

---

#### `GET /api/auth/confirm-email` -- Confirmar email

**Query param:** `token` (string)

```
GET /api/auth/confirm-email?token=abc123...
```

**Response:** `200 OK`
```
Email confirmed successfully
```

| Status | Significado                                   |
|--------|-----------------------------------------------|
| `200`  | Email confirmado                              |
| `400`  | Token invalido, expirado o ya usado           |

---

#### `POST /api/auth/logout` -- Cerrar sesion

**Request body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response:** `200 OK`
```
Logout successfully. Delete the access token on the client side.
```

> El refresh token se revoca. Si ya era invalido, se ignora silenciosamente.

---

### URLs (`/api/urls`)

---

#### `GET /api/urls` -- Listar URLs del usuario

Requiere JWT.

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "clickCounter": 42,
    "name": "Mi enlace",
    "shortUrl": "aB3xK9mZ",
    "originalUrl": "https://example.com/muy/larga",
    "creationDate": "2025-03-15T10:30:00"
  }
]
```

| Campo          | Tipo       | Descripcion                         |
|----------------|------------|-------------------------------------|
| `id`           | `long`     | Identificador interno               |
| `clickCounter` | `integer`  | Numero de accesos registrados       |
| `name`         | `string`   | Nombre opcional del enlace          |
| `shortUrl`     | `string`   | Hash corto de 8 caracteres          |
| `originalUrl`  | `string`   | URL original                        |
| `creationDate` | `datetime` | Fecha de creacion                   |

> Solo devuelve las URLs del usuario autenticado.

---

#### `GET /api/urls/{id}` -- Obtener una URL

Requiere JWT.

**Response:** `200 OK`

```json
{
  "id": 1,
  "clickCounter": 42,
  "name": "Mi enlace",
  "shortUrl": "aB3xK9mZ",
  "originalUrl": "https://example.com/muy/larga",
  "creationDate": "2025-03-15T10:30:00"
}
```

| Status | Significado                    |
|--------|--------------------------------|
| `200`  | URL encontrada                 |
| `404`  | URL no encontrada o no es tuya |

---

#### `POST /api/urls/create` -- Crear URL corta

Publico. Si se envia JWT, la URL se asocia al usuario.

**Request body:**

```json
{
  "url": "https://example.com/muy/larga",
  "name": "Mi enlace"
}
```

| Campo  | Tipo     | Reglas                                |
|--------|----------|---------------------------------------|
| `url`  | `string` | Obligatorio, URL valida (http/https/ftp), max 2048 chars |
| `name` | `string` | Opcional, 2-80 caracteres o vacio     |

**Response:** `201 Created`

```json
{
  "id": 1,
  "clickCounter": 0,
  "name": "Mi enlace",
  "shortUrl": "aB3xK9mZ",
  "originalUrl": "https://example.com/muy/larga",
  "creationDate": "2025-03-15T10:30:00"
}
```

| Status | Significado          |
|--------|----------------------|
| `201`  | URL creada           |
| `400`  | Validacion fallida   |

> El hash se genera con SHA-256(email + url + name) truncado a 8 chars Base64URL. Si ya existe, devuelve la URL existente (deduplicacion).

---

#### `PUT /api/urls/{id}` -- Actualizar URL original

Requiere JWT. El body es un string plano con la nueva URL.

**Request body** (raw text):
```
https://example.com/nueva-url
```

**Response:** `200 OK`

```json
{
  "id": 1,
  "clickCounter": 42,
  "name": "Mi enlace",
  "shortUrl": "aB3xK9mZ",
  "originalUrl": "https://example.com/nueva-url",
  "creationDate": "2025-03-15T10:30:00"
}
```

| Status | Significado                              |
|--------|------------------------------------------|
| `200`  | URL actualizada                          |
| `400`  | Validacion fallida                       |
| `404`  | URL no encontrada o no es tuya           |

---

#### `DELETE /api/urls/{id}` -- Eliminar URL

Requiere JWT.

**Response:** `204 No Content`

| Status | Significado                |
|--------|----------------------------|
| `204`  | URL eliminada              |

> Si la URL no pertenece al usuario, la operacion es un no-op (no hay error).

---

### Accesos (`/api/urls/{id}/requests`)

---

#### `GET /api/urls/{id}/requests` -- Listar accesos

Requiere JWT. Lista los accesos registrados de una URL del usuario.

**Response:** `200 OK`

```json
[
  {
    "id": 1,
    "ip": "192.168.1.1",
    "browser": "Chrome",
    "os": "Windows",
    "architecture": "64-bit",
    "date": "2025-03-15T10:30:00.000+00:00"
  }
]
```

| Campo          | Tipo        | Descripcion                     |
|----------------|-------------|---------------------------------|
| `id`           | `long`      | Identificador del acceso        |
| `ip`           | `string`    | IP del cliente                  |
| `browser`      | `string`    | Chrome / Firefox / Safari / Edge / Unknown |
| `os`           | `string`    | Windows / MacOS / Unix / Android / iOS / Unknown |
| `architecture` | `string`    | 64-bit / 32-bit / Unknown       |
| `date`         | `timestamp` | Fecha y hora del acceso         |

| Status | Significado                    |
|--------|--------------------------------|
| `200`  | Lista de accesos               |
| `404`  | URL no encontrada o no es tuya |

---

### Redireccion y estado

---

#### `GET /{hash}` -- Redirigir a URL original

**Path param:** `hash` (string, 4+ caracteres alfanumericos)

Redirige al navegador a la URL original y registra el acceso (IP, browser, OS, arquitectura, fecha).

**Response:** `302 Found`
- Header: `Location: https://example.com/original`

| Status | Significado     |
|--------|-----------------|
| `302`  | Redireccion     |
| `404`  | Hash no encontrado |

> Ademas incrementa `clickCounter` y notifica via WebSocket a `/topic/url/{id}`.

---

#### `GET /api/checkStatus` -- Health check

**Response:** `200 OK`
```
ok
```

---

#### `GET /` -- Redirigir al frontend

**Response:** `302 Found`
- Header: `Location: https://app.shortfy.link`

---

## Autenticacion

La API usa el header:

```http
Authorization: Bearer <access_token>
```

| Endpoint pattern         | Seguridad      |
|--------------------------|----------------|
| `/api/auth/**`           | Publico        |
| `/api/urls/create`       | Publico        |
| `/api/checkStatus`       | Publico        |
| `/{hash:[a-zA-Z0-9]+}`  | Publico        |
| `/`                      | Publico        |
| `/ws/**`                 | Publico        |
| Otros `/api/**`          | Requiere JWT   |

- Un usuario registrado con email/password debe confirmar su correo antes de poder autenticarse.
- Los usuarios de Google se crean con `emailVerified = true` automaticamente.

## WebSocket

El servidor expone un endpoint STOMP para actualizaciones en tiempo real del contador de clics.

- **Endpoint:** `/ws`
- **Broker:** `/topic`
- **Suscripcion:** `/topic/url/{urlId}`
- **Mensaje:** El nuevo valor de `clickCounter` (integer)

Cuando alguien visita `/{hash}`, el backend envia el contador actualizado a todos los clientes suscritos a esa URL.

## Manejo de errores

La aplicacion devuelve errores en texto plano.

| Status | Significado                                      |
|--------|--------------------------------------------------|
| `400`  | Validaciones, parametros invalidos, token confirmacion invalido/expirado |
| `401`  | Refresh token invalido, revocado o expirado      |
| `403`  | Credenciales incorrectas o token Google invalido |
| `404`  | Recurso no encontrado                            |
| `409`  | Email ya existente                               |
| `500`  | Error interno                                    |

## Variables de entorno

La aplicacion carga configuracion desde variables de entorno y/o `.env`.

### Requeridas

| Variable           | Descripcion                                |
|--------------------|--------------------------------------------|
| `MY_DATABASE_URL`  | URL JDBC de PostgreSQL                     |
| `MY_USER`          | Usuario de base de datos                   |
| `MY_PASSWORD`      | Password de base de datos                  |
| `JWT_SECRET`       | Secreto para firmar JWT                    |
| `GOOGLE_CLIENT_ID` | Client ID de Google para validar ID tokens |
| `MAIL_HOST`        | Host SMTP                                  |
| `MAIL_PORT`        | Puerto SMTP                                |
| `MAIL_USERNAME`    | Usuario SMTP                               |
| `MAIL_PASSWORD`    | Password o app password SMTP               |

### Opcionales

| Variable                    | Descripcion                                | Default                |
|-----------------------------|--------------------------------------------|------------------------|
| `JWT_ACCESS_EXPIRATION_MS`  | Duracion del access token                  | `300000`               |
| `JWT_REFRESH_EXPIRATION_MS` | Duracion del refresh token                 | `2592000000`           |
| `MY_DATABASE`               | Nombre de BD usado en `docker-compose.yml` | Sin default            |

### Ejemplo de `.env`

```env
MY_DATABASE_URL=jdbc:postgresql://localhost:5432/shortify
MY_USER=postgres
MY_PASSWORD=postgres
MY_DATABASE=shortify

JWT_SECRET=change-this-super-secret-key
JWT_ACCESS_EXPIRATION_MS=300000
JWT_REFRESH_EXPIRATION_MS=2592000000

GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=no-reply@example.com
MAIL_PASSWORD=your-app-password
```

## Ejecucion local

### Requisitos

- Java 21
- Maven 3.9+
- PostgreSQL

### Arrancar en local

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

La API quedara disponible en `http://localhost:8080`.

### Tests

```bash
./mvnw test
```

## Docker

### Levantar base de datos y backend con Compose

Con el archivo `.env` en la raiz del proyecto:

```bash
docker compose up -d
```

Servicios expuestos:

- Backend: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

### Construir imagen manualmente

```bash
docker build -t shortify-backend .
```

### Ejecutar la imagen

```bash
docker run -p 8080:8080 --env-file .env shortify-backend
```

### Publicar imagen en Docker Hub

```bash
docker login
docker tag shortify-backend <tu-usuario>/shortify-backend:latest
docker push <tu-usuario>/shortify-backend:latest
```

## Frontend relacionado

[Shortify-Frontend](https://github.com/frankxhunter/Shortify-Frontend)
