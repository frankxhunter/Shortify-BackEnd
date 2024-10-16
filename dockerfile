# Usar una imagen de OpenJDK para correr aplicaciones Java
FROM tomcat:10.1-jdk17

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el contenido WAR de tu aplicacion dentro del contenedor
COPY target/shortify.war /usr/local/tomcat/webapps/

# Desactivar el puerto de shutdown en el archivo server.xml
RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml

# Exponemos el puerto 8080 para acceder a la aplicacion
EXPOSE 8080

# Comando por defecto para arrancar Tomcat
CMD ["catalina.sh", "run"]