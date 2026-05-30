# Shortify Backend

Backend de **Shortify**, una API REST para acortar URLs, registrar accesos a cada enlace y gestionar autenticacion con
JWT, refresh token, verificacion por correo y login con Google.

## Resumen

- Crea URLs cortas a partir de enlaces largos.
- Permite crear enlaces de forma anonima o autenticada.
- Guarda estadisticas basicas de acceso: IP, navegador, sistema operativo, arquitectura y fecha.
- Incluye autenticacion con email/password.
- Soporta verificacion de correo.
- Soporta login con Google ID token.
- Usa access token y refresh token.

## Stack

- Java 21
- Spring Boot 3.4.4
- Spring Web
- Spring Security
- Spring Data JPA
- Spring Validation
- PostgreSQL
- Spring Mail
- Maven

## Modelo principal

### `Url`

Representa una URL acortada.

Campos que devuelve la API:

| Campo          | Tipo             | Descripcion                        |
|----------------|------------------|------------------------------------|
| `id`           | `long`           | Identificador interno              |
| `clickCounter` | `integer`        | Numero de clics registrados        |
| `name`         | `string \| null` | Nombre opcional del enlace         |
| `shortUrl`     | `string`         | Hash corto generado por el backend |
| `originalUrl`  | `string`         | URL original                       |
| `creationDate` | `datetime`       | Fecha de creacion                  |

### `InfoRequest`

Representa un acceso registrado sobre una URL.

| Campo          | Tipo        | Descripcion                 |
|----------------|-------------|-----------------------------|
| `id`           | `long`      | Identificador del acceso    |
| `ip`           | `string`    | IP detectada                |
| `browser`      | `string`    | Navegador detectado         |
| `os`           | `string`    | Sistema operativo detectado |
| `architecture` | `string`    | Arquitectura detectada      |
| `date`         | `timestamp` | Fecha del acceso            |

## DTOs de entrada

### `UserDto`

Se usa en `register` y `login`.

```json
{
  "email": "user@example.com",
  "password": "StrongPass1!"
}
```

Reglas:

- `email`: obligatorio, entre 8 y 40 caracteres, formato email.
- `password`: obligatoria, entre 8 y 20 caracteres y validada como password segura.

### `CreateUrlDto`

Se usa en `POST /api/urls/create`.

```json
{
  "url": "https://example.com/very/long/path",
  "name": "Mi enlace"
}
```

Reglas:

- `url`: obligatoria y con formato de URL valido.
- `name`: opcional; si se informa, debe tener entre 2 y 80 caracteres.

### `GoogleToken`

Se usa en `POST /api/auth/google`.

```json
{
  "token": "google-id-token"
}
```

### `RefreshTokenRequest`

Se usa en `POST /api/auth/refresh` y `POST /api/auth/logout`.

```json
{
  "refreshToken": "jwt-refresh-token"
}
```

## Autenticacion

La API usa el header:

```http
Authorization: Bearer <access_token>
```

Respuesta de autenticacion en `login`, `google` y `refresh`:

```json
{
  "token": "access-jwt",
  "refreshToken": "refresh-jwt",
  "tokenType": "Bearer",
  "expiresInMs": 300000,
  "refreshExpiresInMs": 2592000000,
  "email": "user@example.com"
}
```

Notas:

- `POST /api/urls/create` es publico.
- El resto de rutas bajo `/api/**` requieren JWT, salvo `/api/auth/**` y `/api/checkStatus`.
- Un usuario registrado con email/password debe confirmar su correo para poder autenticarse.

## Endpoints

Base URL local:

```text
http://localhost:8080
```

### Estado y redireccion

| Metodo | Ruta               | Auth | Descripcion                                     |
|--------|--------------------|------|-------------------------------------------------|
| `GET`  | `/api/checkStatus` | No   | Health check simple. Devuelve `ok`              |
| `GET`  | `/`                | No   | Redirige al frontend `https://app.shortfy.link` |
| `GET`  | `/{hash}`          | No   | Redirige a la URL original y registra el acceso |

### Autenticacion

| Metodo | Ruta                                | Auth | Descripcion                                            |
|--------|-------------------------------------|------|--------------------------------------------------------|
| `POST` | `/api/auth/register`                | No   | Registra usuario y envia verificacion por email        |
| `POST` | `/api/auth/login`                   | No   | Login con email y password                             |
| `GET`  | `/api/auth/login`                   | Si   | Comprueba sesion actual; devuelve el email autenticado |
| `POST` | `/api/auth/google`                  | No   | Login o alta con token de Google                       |
| `POST` | `/api/auth/refresh`                 | No   | Rota refresh token y devuelve nuevo access token       |
| `GET`  | `/api/auth/confirm-email?token=...` | No   | Confirma el correo del usuario                         |
| `POST` | `/api/auth/logout`                  | No   | Revoca el refresh token recibido                       |

### URLs

| Metodo | Ruta                      | Auth | Descripcion                                          |
|--------|---------------------------|------|------------------------------------------------------|
| `GET`  | `/api/urls`               | Si   | Lista URLs del usuario autenticado                   |
| `GET`  | `/api/urls/{id}`          | Si   | Obtiene una URL concreta del usuario                 |
| `POST` | `/api/urls/create`        | No   | Crea una URL corta; si hay JWT, se asocia al usuario |
| `PUT`  | `/api/urls/{id}?url=...`  | Si   | Actualiza la URL original de una URL del usuario     |
| `GET`  | `/api/urls/{id}/requests` | Si   | Lista accesos registrados de una URL del usuario     |

## Ejemplos de uso

### Registrar usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "StrongPass1!"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "StrongPass1!"
  }'
```

### Crear URL corta de forma anonima

```bash
curl -X POST http://localhost:8080/api/urls/create \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/article/123",
    "name": "Articulo"
  }'
```

### Crear URL corta autenticado

```bash
curl -X POST http://localhost:8080/api/urls/create \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com/private",
    "name": "Privada"
  }'
```

### Listar URLs del usuario

```bash
curl http://localhost:8080/api/urls \
  -H "Authorization: Bearer <access_token>"
```

### Ver accesos de una URL

```bash
curl http://localhost:8080/api/urls/1/requests \
  -H "Authorization: Bearer <access_token>"
```

### Refrescar tokens

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<refresh_token>"
  }'
```

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
| `MY_DATABASE`               | Nombre de BD usado en `docker-compose.yml` | Sin default en compose |

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
./mvnw spring-boot:run
```

La API quedara disponible en:

```text
http://localhost:8080
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

## Manejo de errores

La aplicacion devuelve errores simples en texto plano para validaciones y excepciones comunes.

Casos habituales:

- `400 Bad Request`: validaciones, parametros invalidos, token de confirmacion invalido o expirado.
- `401 Unauthorized`: refresh token invalido o expirado.
- `403 Forbidden`: credenciales incorrectas o token de Google invalido.
- `404 Not Found`: recurso no encontrado.
- `409 Conflict`: email ya existente.
- `500 Internal Server Error`: error no controlado.

## Frontend relacionado

Repositorio del frontend:

[Shortify-Frontend](https://github.com/frankxhunter/Shortify-Frontend)
