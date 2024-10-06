# Usar una imagen de OpenJDK para correr aplicaciones Java
FROM tomcat:9.0-jdk17

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el contenido JAR de tu aplicacion dentro del contenedor
COPY target/shortify.war /usr/local/tomcat/webapps/

# Exponemos el puerto 8080 para acceder a la aplicacion
EXPOSE 8080

# Comando por defecto para arrancar Tomcat
CMD ["catalina.sh", "run"]