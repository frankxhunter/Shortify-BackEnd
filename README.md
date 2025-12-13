# Shortify BackEnd

Este es el backend de la aplicación **Shortify**, un acortador de URLs que permite crear enlaces cortos y llevar un
historial de los accesos realizados a través de ellos.

---

## ✨ Características principales

- 🔗 Generación de enlaces cortos a partir de URLs largas
- 📊 Registro y visualización del historial de accesos a cada enlace
- 🛠️ Operaciones CRUD básicas sobre los enlaces
- 🚦 Estadísticas básicas de uso
- 🔒 Posibilidad de ampliar con autenticación y más funcionalidades (personalizable)

---

## 🚀 Tecnologías utilizadas

- **Java**
- **Spring Boot**
- **Base de datos relacional** (por ejemplo, PostgreSQL o MySQL)
- **JPA/Hibernate**
- **Maven**
- **REST API**

---

## ⚙️ Endpoints principales

| Método | Endpoint              | Descripción                              |
|--------|-----------------------|------------------------------------------|
| GET    | `/urls`               | Obtener todos los enlaces del usuario    |
| GET    | `/urls/{id}`          | Obtener los detalles de un enlace por ID |
| POST   | `/urls/create`        | Crear un nuevo enlace corto              |
| GET    | `/{hash}`             | Redireccionar a la URL original          |
| GET    | `/urls/{id}/requests` | Ver el historial de accesos a un enlace  |
| POST   | `/register`           | Registrar un nuevo usuario               |
| POST   | `/login`              | Iniciar sesión                           |
| GET    | `/login`              | Verificar estado de sesión               |
| POST   | `/google/auth`        | Iniciar session con google               |

> Nota: Puedes ver todos los endpoints y detalles en el código fuente del repositorio.

---

## ⚙️ Cómo ejecutar el backend localmente

1. Clona este repositorio:
   ```bash
   git clone https://github.com/frankxhunter/Shortify-BackEnd.git
   ```
2. Ingresa al directorio del proyecto:
   ```bash
   cd Shortify-BackEnd
   ```
3. Configura la base de datos en `src/main/resources/application.properties` según tus credenciales.
4. Construye y ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```
5. La API estará disponible en:
   ```
   http://localhost:8080
   ```

---

## Docker hub

Este proyecto esta en DockerHub
como [frankxhunter/shoritfy-backend](https://hub.docker.com/repository/docker/frankxhunter/shortify-backend/general):

Para desplegarlo la imagen se utiliza el siguiente comando:

```cmd
docker pull frankxhunter/shortify-backend
```

```cmd
docker run -d  --name shortify-backend -e POSTGRES_USER=<<postgres>> -e POSTGRES_PASSWORD=<root> -e POSTGRES_DB=<mydb> -p 5432:5432 shortify-backend
```

## Como crear la base de datos con Docker

```cmd
docker pull postgres
```

```cmd
docker run -d  --name my-postgres -e POSTGRES_USER=<postgres> -e POSTGRES_PASSWORD=<root> -e POSTGRES_DB=<mydb> -p 5432:5432 postgres

// Ejemplo real
 docker run -d  --name my-postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -e POSTGRES_DB=shortify -p 5432:5432 postgres  
```

## Ejemplo de .env necesario o variables requiridas

```cmd
MY_DATABASE_URL=jdbc:postgresql://localhost:5432/shortify
#MY_DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/shortify
MY_USER=postgres
MY_PASSWORD=root
```

## Como deployar el proyecto a traves de docker-compose

Para deployar con docker-compose debes tener el fichero .env en la misma ubicacion que el archivo docker-compose.yml
Y ejecutar el siguiente comando:

```cmd
docker compose up -d
```

## 📦 Frontend relacionado

¿Buscas el frontend?  
[Shortify-Frontend](https://github.com/frankxhunter/Shortify-Frontend)

---

## 📄 Licencia

Este proyecto se distribuye bajo licencia abierta.

---

Desarrollado con ❤️ por [frankxhunter](https://github.com/frankxhunter)