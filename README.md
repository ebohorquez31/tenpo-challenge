# Tenpo-challenge
Repositorio para challenge de Tenpo

# Autor: Edwin Alexander Bohorquez Gamba

# Proyecto

Repositorio: https://github.com/ebohorquez31/tenpo-challenge

# Descripcion: 

API REST en Spring Boot utilizando Java 21 y Programacion reactiva con Webflux, bajo una arquitectura limpia.
Se implementaron diferentes subproyectos para organizar todos los modulos y seguir una estructura de Clean Architecture. Esto con el fin de crear un proyecto que sea flexible, mantenible y escalable con el tiempo.

![image](https://github.com/user-attachments/assets/1980912b-32a1-46df-b0e4-c26ca84a119f)

# Instrucciones para ejecutar el servicio y la base de datos localmente:

1. Clonar el repositorio https://github.com/ebohorquez31/tenpo-challenge
2. Ejecutar
```sh
gradle clean build
```
Para generar el archivo JAR

![image](https://github.com/user-attachments/assets/9cfc07d8-f2ac-480e-a5ef-cc223fddd752)

3. Teniendo en cuenta las diferentes dependencias solicitadas se opto por contenerizar los servicios (mock-service, base de datos y aplicacion), para ello se uso el siguiente archivo docker-compose:
```sh
version: '3.1'
services:
  app:
    container_name: challenge-app
    image: challenge-app
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    networks:
      - tenpo
    depends_on:
      - postgresql_db
  postgresql_db: 
    container_name: postgresql_db
    restart: always
    volumes:
      - /tmp:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql    
    image: postgres:14.1-alpine
    ports:  
      - "5432:5432"
    networks:
      - tenpo
    environment:
      - POSTGRES_PASSWORD=12345
      - POSTGRES_USER=postgres
      - POSTGRES_DB=postgres
      - PGDATA=/var/lib/postgresql/data/pgdata
  percentage_mock:
    container_name: percentage_mock
    image: rvancea/soapui-mockservicerunner:latest
    command: >
      -a "/" -p "8099" /home/soapui/PercentageMock-soapui-project.xml
    ports:
      - "8099:8099"
    networks:
      - tenpo
    volumes:
      - .:/home/soapui
    restart: always

networks:
  tenpo:
    name: tenpo
```

Para construir las imagenes ejecutamos:
```sh
docker-compose build
```

![image](https://github.com/user-attachments/assets/48ab4600-bffe-4192-a97a-07cbfe82f9bf)

4. Iniciar los servicios definidos en el docker-compose.yml y ejecutarlos en segundo plano
```sh
docker-compose up -d
```

![image](https://github.com/user-attachments/assets/080333ef-c3a0-4ac1-b581-aa79211b48ff)

En Docker Desktop se veran asi:

![image](https://github.com/user-attachments/assets/55a32204-e847-4bd5-b56f-e489ba67c04b)

Con esto vemos que todos los contenedores iniciaron de forma correcta.

# Interactuar con la API

Para probar los servicios se adjunto a este repositorio una coleccion en Postman. Dicha coleccion tiene tres servicios: 
1. Servicio para sumar
2. Servicio para obtener el historial
3. Servicio para consultar el porcentaje

Probar el mock-service:

![image](https://github.com/user-attachments/assets/eb0339a3-daf5-4fcd-b53a-6b2997ff0001)

Probar el servicio para sumar dos numeros:

![image](https://github.com/user-attachments/assets/44263a46-d055-4ca3-a94a-8a78273a9907)

Probar el servicio para consultar el historial de llamadas:

![image](https://github.com/user-attachments/assets/564c93bf-aed0-437a-9651-18b82443c46a)

Para verificar los datos se ingresa a la base de datos y se ejecuta la siguiente consulta:
```sh
select m.endpoint, m.first_number, m.second_number, m."result", e.endpoint, e.percentage
from main_service_history m
join external_service_history e on(m.external_service_id = e.id) 
order by m.id desc;
```

![image](https://github.com/user-attachments/assets/ce4eaee4-49b7-4aa0-bbd7-e3ccc4057f44)

# Funcionalidades principales:

1. Cálculo con porcentaje dinámico:

Se implemento un servicio Rest que suma dos numeros y un porcentaje fijo. Dicho porcentaje se saca de un mock service que se implemento en SOAPUI.

![image](https://github.com/user-attachments/assets/25d82b26-1a7a-4b2d-945f-91e0e207b9e2)

2. Caché del porcentaje:

El porcentaje se obtiene de un servicio externo el cual se implemento como un mock-service en SOAPUI y esta adjunto a este repositorio.
Dentro del mock se desarrollo un script, el cual mantendra un porcentaje solo por 30 minutos.

![image](https://github.com/user-attachments/assets/2c02563d-6e1d-417a-a2c1-ae3c81a17667)

Si no hay un valor previamente almacenado, la API responde con el siguiente error HTTP:

![image](https://github.com/user-attachments/assets/7b1d4f21-9d78-4891-8e28-462613be105e)

3. Reintentos ante fallos del servicio externo:

Si el servicio falla, se implemento un maximo de 3 intentos como se puede ver en los logs:

![image](https://github.com/user-attachments/assets/d76e91ae-c806-419c-80e8-2dfe97dfae20)

4. Historial de llamadas:

Se implemento la paginacion:

![image](https://github.com/user-attachments/assets/13994d1e-9bef-4108-b587-5d9eab110aef)

Para el registro de las llamadas se implemento un Interceptor de peticiones, el cual invoca metodos asincronos para guardar los registros y de esa forma no afectar el
tiempo de respuesta del servicio principal y tampoco impactar la ejecución del endpoint invocado. 

5. Control de tasas (Rate Limiting):

![image](https://github.com/user-attachments/assets/46bbfbc7-788b-4605-9eea-3754ab4aa610)

6. Manejo de errores HTTP:

![image](https://github.com/user-attachments/assets/539a596b-c41e-4333-9eb0-f8770ea04be8)

![image](https://github.com/user-attachments/assets/46bbfbc7-788b-4605-9eea-3754ab4aa610)

![image](https://github.com/user-attachments/assets/7b1d4f21-9d78-4891-8e28-462613be105e)

# Requerimientos técnicos:

1. Base de datos:
 Se implemento una BD PostgreSQL por medio de un contenedor.
Se implemento R2DBC para tener una conexion reactiva con la base de datos relacional.

![image](https://github.com/user-attachments/assets/3dc445b9-4d62-4d7d-bca2-70699cd04bc2)

Modelo Relacional:

![image](https://github.com/user-attachments/assets/9294784d-b1b9-4781-8433-e345e0ad597a)

2. Despliegue:

Todos los servicios se implementaron por medio de contenedores.

![image](https://github.com/user-attachments/assets/55a32204-e847-4bd5-b56f-e489ba67c04b)

3. Documentación

En el repositorio se encuentra una coleccion en Postman la cual tiene los 3 servicios disponibles para probar.

4. Tests

Para los tests unitarios se usaron las siguientes dependencias
```sh
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.mockito:mockito-core:4.5.1'
testImplementation 'io.projectreactor:reactor-test'
```

5. Escalabilidad

Como se implemento la solucion bajo un enfoque reactivo, WebFlux utiliza un modelo no bloqueante que maximiza el uso de recursos en cada instancia. Esto significa que incluso con múltiples réplicas se puede manejar una gran cantidad de solicitudes por réplica sin necesitar tantos recursos adicionales.
  
6. Bonus (Plus):

La solucion se implemento bajo el enfoque de programacion reactiva usando Spring Webflux y R2DBC (para la conexion reactiva con la base de datos).
Para ello se usaron las siguientes dependencias:
```sh
implementation "org.springframework.boot:spring-boot-starter-webflux" 
implementation "org.springframework.boot:spring-boot-starter-data-r2dbc"
implementation "org.springframework.data:spring-data-r2dbc" 
implementation "io.r2dbc:r2dbc-postgresql"
implementation "io.r2dbc:r2dbc-pool"
```
