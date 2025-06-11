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

## 📦 Frontend relacionado

¿Buscas el frontend?  
[Shortify-Frontend](https://github.com/frankxhunter/Shortify-Frontend)

---

## 📄 Licencia

Este proyecto se distribuye bajo licencia abierta (puedes añadir la específica si lo deseas).

---

Desarrollado con ❤️ por [frankxhunter](https://github.com/frankxhunter)