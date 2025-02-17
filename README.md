# Lab4 AREP


## Comenzando

Se debe clonar el proyecto localmente con el comando:

` git clone https://github.com/lalaro/LAB3AREP.git`

Y luego revisar las intrucciones a continuación para el manejo de soluciones del proyecto.

### Prerrequisitos

Se necesita de Maven (La versión más reciente) y Java 19, la instalación debe realizarse desde las paginas oficiales de cada programa.


### Instalación

Para Maven debe irse a https://maven.apache.org/download.cgi, descargar la versión más nueva que allá de Maven (En este caso tenemos la versión 3.9.6) y agregarse en la carpeta de Program Files, luego se hace la respectiva configuración de variables de entorno según la ubicación que tenemos para el archivo de instalación, tanto de MAVEN_HOME y de Path.
Luego revisamos que haya quedado bien configurado con el comando para Windows:

` mvn - v `
o
` mvn -version `

Para Java debe irse a https://www.oracle.com/java/technologies/downloads/?er=221886, descargar la versión 19 de Java y agregarse en la carpeta de Program Files, luego se hace la respectiva configuración de variables de entorno según la ubicación que tenemos para el archivo de instalación, tanto de JAVA_HOME y de Path.
Luego revisamos que haya quedado bien configurado con el comando para Windows:

` java -version `

Si no tenemos la versión solicitada podemos hacer lo siguiente, para el caso de Windows:

Ir al Windows PowerShell y ejecutar como administrador los siguientes codigos:

` [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jdk-19.0.2", [System.EnvironmentVariableTarget]::Machine) `

Revisar las rutas de la máquina

`  $env:JAVA_HOME = "C:\Program Files\Java\jdk-19.0.2" `

`  $env:Path = "C:\Program Files\Java\jdk-19.0.2\bin;" + $env:Path `

`  echo $env:JAVA_HOME `

`  javac -version `

`  java -version `

Así se debe ver:

![image5.jpeg](src/main/resources/image5.jpeg)

## Solución del lab

El desarrollo del Laboratorio es el siguiente:

Como arquitectura tenemos:

![image1.jpeg](src/main/resources/image1.jpeg)

Explicación de arquitectura:

Este diagrama representa una arquitectura de sistema distribuido que involucra un cliente web, un servidor HTTP y un servidor backend, todos comunicándose a través de una red local. El cliente web realiza solicitudes al servidor HTTP, donde el servidor tiene manejo de rutas (API), con los controladores asignados (/square, /pi, /greeting), quien a su vez puede solicitar datos JSON al servidor backend. Además, el servidor HTTP sirve archivos estáticos (HTML, CSS, JS, PNG, JPEG) directamente al cliente.
En el diagrama se establece el puerto 35000 utilizado para la comunicación y la especificación de la ruta GET. El Local Server indica que todos los componentes residen en el mismo entorno local.

Desarrollo del lab:

1. Para su primera versión cargue el POJO desde la línea de comandos , de manera similar al framework de TEST. Es decir pásela como parámetro cuando invoque el framework. Ejemplo de invocación:

java -cp target/classes co.edu.escuelaing.reflexionlab.MicroSpringBoot co.edu.escuelaing.reflexionlab.FirstWebService

Debemos tener en cuenta que este comando cambiara porque se va implementar en el servidor y todo va estar sobre la clase de **WebApplication**

![image2.jpeg](src/main/resources/image2.jpeg)

Entonces tendremos

` java -version `
` mvn dependency:copy-dependencies `
` java -cp "target/classes;target/dependency/*" edu.escuelaing.app.AppSvr.server.WebApplication `

Para probar las clases que usan las anotaciones de forma independiente se puede:

` java -cp "target/classes;target/dependency/*" edu.escuelaing.app.AppSvr.server.WebApplication edu.escuelaing.appAppSvr.controller.GreetingController ` o ` java -cp "target/classes;target/dependency/*" edu.escuelaing.app.AppSvr.server.WebApplication edu.escuelaing.appAppSvr.controller.MathController `

![image3.jpeg](src/main/resources/image3.jpeg)

Es necesario traer las dependencias, ya que si no se traen no puede explorar el directorio raiz, con las respectivas anotaciones

![image3.1.jpeg](src/main/resources/image3.1.jpeg)

![image20.jpeg](src/main/resources/image20.jpeg)

Para probar todo el funcionamiento podemos traer las rutas del WebApplication, HttpServer, GreetingController y MathController.
http://localhost:35000/greeting?name=Laura

![image21.jpeg](src/main/resources/image21.jpeg)

http://localhost:35000/greeting

![image22.jpeg](src/main/resources/image22.jpeg)

http://localhost:35000/pi

![image23.jpeg](src/main/resources/image23.jpeg)

http://localhost:35000/square?n=88

![image24.jpeg](src/main/resources/image24.jpeg)

http://localhost:35000/index.html

![image25.jpeg](src/main/resources/image25.jpeg)

http://localhost:35000/app/pi

![image26.jpeg](src/main/resources/image26.jpeg)

http://localhost:35000/app/e

![image27.jpeg](src/main/resources/image27.jpeg)

http://localhost:35000/hello

![image28.jpeg](src/main/resources/image28.jpeg)


El resumen en nuestra consola de nuestras busquedas se verá así:
![image29.jpeg](src/main/resources/image29.jpeg)
![image30.jpeg](src/main/resources/image30.jpeg)

## Ejecutando las pruebas

Podemos Abrir en terminal el proyecto y ejecutar las pruebas desde el PowerShell, en el caso de Windows. Y ejecutamos el comando:

` mvn test `

O de igual forma en el ID que deseemos.

Así se vera:

### Desglose en pruebas de extremo a extremo

1. testGreetingWithParameter

Qué prueba: Verifica que la ruta /greeting con el parámetro name=Lala devuelva "Hola Lala".
Por qué la prueba: Asegura que el servidor maneja correctamente los parámetros en la solicitud y personaliza el saludo según el nombre proporcionado.

2. testPiEndpoint

Qué prueba: Comprueba que la ruta /pi devuelve una respuesta con el código "200 OK" y el valor de π (3.141592653589793).
Por qué la prueba: Garantiza que el servidor responde correctamente a la solicitud y devuelve un valor matemático predefinido.

3. testNotFoundRoute

Qué prueba: Verifica que al acceder a una ruta inexistente (/notfound), el servidor responda con "404 Not Found".
Por qué la prueba: Asegura que el servidor maneja adecuadamente rutas inválidas y proporciona un error claro cuando no se encuentra el recurso solicitado.

4. testGreetingEndpoint

Qué prueba: Verifica que la ruta /greeting sin parámetros devuelva "Hola World" con un estado "200 OK".
Por qué la prueba: Asegura que el servidor tiene un valor predeterminado para el parámetro name cuando no se proporciona en la solicitud.

5. testSquareEndpoint

Qué prueba: Comprueba que la ruta /square con el parámetro n=100 devuelva "10000".
Por qué la prueba: Verifica que el servidor realice correctamente la operación de elevar un número al cuadrado.

6. testSquareInvalidInput

Qué prueba: Verifica que la ruta /square?n=abc devuelva un mensaje de error "Invalid input: 'n' must be an integer".
Por qué la prueba: Asegura que el servidor maneja correctamente valores no numéricos y proporciona una respuesta adecuada en lugar de generar un fallo inesperado.

7. testSimulatedRequest

Qué prueba: Comprueba nuevamente que la ruta /pi responde con "200 OK" y el valor de π (3.141592653589793).
Por qué la prueba: Es una validación adicional de que la simulación de solicitudes funciona correctamente y devuelve respuestas esperadas.

8. testMissingRequiredParam

Qué prueba: Verifica que la ruta /square sin el parámetro n devuelve un mensaje de error.
Por qué la prueba: Asegura que el servidor maneja correctamente la falta de parámetros obligatorios y responde con un mensaje de error en lugar de fallar silenciosamente.

9. testExecuteServiceWithNumber

Qué prueba: Comprueba que la ruta /square con n=5 devuelve "25".
Por qué la prueba: Verifica que el cálculo del cuadrado se realice correctamente con diferentes valores de entrada.

10. testRegisteredServicesExist

Qué prueba: Confirma que las rutas /greeting y /square están registradas en el servidor.
Por qué la prueba: Asegura que los servicios esperados existen en la aplicación y pueden manejar solicitudes.

### Y pruebas de estilo de código

El propósito principal de estas pruebas es asegurar que el servidor funcione de manera estable y confiable, respondiendo correctamente a diferentes solicitudes y manejando errores de forma adecuada. Estas pruebas ayudan a prevenir problemas en producción al detectar posibles fallos desde el desarrollo.

## Despliegue

Podemos Abrir en terminal el proyecto y compilar y empaquetar el proyecto desde el PowerShell, en el caso de Windows. Y ejecutamos los comandos:

` mvn clean `

` mvn compile `

` mvn package `

O de igual forma en el ID que deseemos.

Así se vera:



## Construido con

* [Maven](https://maven.apache.org/) - Gestión de dependencias.
* [Java](https://www.java.com/es/) - Versionamiento en Java.

## Contribuyendo

Por favor, lee [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) para detalles sobre nuestro código de conducta y el proceso para enviarnos solicitudes de cambios (*pull requests*).

## Versionado

Usamos [SemVer](http://semver.org/) para el versionado.

## Autores

* **Laura Valentina Rodríguez Ortegón** - *Lab3 AREP* - [Repositorio](https://github.com/lalaro/LAB3AREP.git)

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo [LICENSE.md](LICENSE.md) para más detalles.

## Reconocimientos

* Agradecimientos a la Escuela Colombiana de Ingeniería
* La documentación de Git Hub
* Al profesor Luis Daniel Benavides
