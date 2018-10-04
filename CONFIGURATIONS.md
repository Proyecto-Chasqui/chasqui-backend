# chasqui-backend

##Requerimientos: 

* Tomcat
* JDK 1.7
* Maven2


# Development stack para Linux

##Instalacion Java

[Link] (http://askubuntu.com/questions/56104/how-can-i-install-sun-oracles-proprietary-java-jdk-6-7-8-or-jre) (The easy way)

> sudo apt-get install python-software-properties

> sudo add-apt-repository ppa:webupd8team/java

> sudo apt-get update

> sudo apt-get install oracle-java7-installer



##Instalar Apache Tomcat

[Link] (https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-7-on-ubuntu-14-04-via-apt-get)

##Instalar Mysql

> sudo apt-get install mysql-server-5.5 mysql-client-5.5

Por consola, conectarse a mysql para crear base de datos CHASQUI: 

>
>mysql -u root - p
>
>mysql > CREATE DATABASE CHASQUI;
>mysql > exit
>

##Instalar Maven

> apt-get install maven2

## Descargar eclipse

>https://eclipse.org/downloads/

###Configurar eclipse

* Copiar el archivo chasqui.properties.example (con nombre chasqui.properties) ubicado en la carpeta "/src/test/java" y completar las propiedades
* Definir un nuevo server (tomcat 7) con el path de la carpeta donde se descomprimió el tomcat
* Incluir webapp en dicho server: con botón derecho sobre el server, elegir add and remove y luego elegir la aplicación chasqui

## Configurar la BD para que no se blanquee con cada arranque del servidor

* Con el backend levantado, hacer un dump de la base de datos: 

>mysqldump --user root CHASQUI -p > dumpdechasqui

* Bajar el servidor

* Editar el archivo datasources.xml reemplazando: 

```
<prop key="hibernate.hbm2ddl.auto">create-drop</prop>
```

**por**


```
<prop key="hibernate.hbm2ddl.auto">validate</prop> 
```

* Restituir el dump de la base de datos: 

> mysql -u root -p CHASQUI < dumpchasquidb 

## Para levantar el backend (y backoffice) por fuera del eclipse

El siguiente es un script para deploy de backend en apache tomcat. Lo ideal es editar uno para cada colaborador del 
proyecto e ignorar el mismo en el versionado de git. 
Observar que $TOMCAT_PATH y $BACKEND_ROOT_PATH estén correctos según tu file system.

```
#!/bin/bash
```
>BACKEND_ROOT_PATH="/directorio_de_repositorio/chasqui-backend"
>
>TOMCAT_PATH="/directorio_de_tomcat/apache-tomcat-7.0.70"
>
>echo "### STOP TOMCAT  ###"
>
>cd $TOMCAT_PATH
>
>cd bin
>
>./catalina.sh stop
>
>cd ../webapps
>
>echo "### REMOVING OLD FILES  ###"
>
>rm -f -r chasqui/
>
>rm -f chasqui.war
>
>echo “### MAKING THE .WAR AND COPY IN TOMCAT ###”
>
>cd $BACKEND_ROOT_PATH
>
>mvn clean install
>
>cd target
>
>cp chasqui.war  $TOMCAT_PATH/webapps
>
>echo "### START TOMCAT  ###"
>
>cd $TOMCAT_PATH
>
>cd bin
>
>./catalina.sh start
>
>echo "### DEPLOY FINISHED  ###"


Guardar como deploy.sh y darle permisos de ejecución. Correr cada vez que  se quieran probar cambios del backend o backoffice

#Development stack para Windows

[Java Jdk] (http://docs.oracle.com/javase/7/docs/webnotes/install/windows/jdk-installation-windows.html)

[Java Jre] (http://docs.oracle.com/javase/7/docs/webnotes/install/windows/jre-installation-windows.html)

###Apache Tomcat
[**Opcion 1**] (https://tomcat.apache.org/tomcat-7.0-doc/setup.html#Windows)
[**Opcion 2**] (http://www.c-sharpcorner.com/UploadFile/fd0172/how-to-configure-and-install-apache-tomcat-server-in-windows/)
[**Opcion 3**] (http://www.coreservlets.com/Apache-Tomcat-Tutorial/tomcat-7-with-eclipse.html)

###Mysql
[**Opcion 1**] (https://dev.mysql.com/downloads/mysql/)
[**Opcion 1**] (http://corlewsolutions.com/articles/article-21-how-to-install-mysql-server-5-6-on-windows-7-development-machine)

###Maven
[**Recomendado**] (https://www.mkyong.com/maven/how-to-install-maven-in-windows/)
[**Opcion 1**] (https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
[**Opcion 2**] (https://maven.apache.org/install.html)
[**Opcion 3**] (http://www.avajava.com/tutorials/lessons/what-is-maven-and-how-do-i-install-it.html)
[**Opcion 4**] (https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project/set-up-the-eclipse-ide-for-windows)

[Git for  windows] (https://git-for-windows.github.io/)














