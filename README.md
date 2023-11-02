# **MS [STOREKEEPER]**

A project about items and bills management for companies.


## **OBJETIVO Y CARACTERÍSTICAS**

El objetivo de este archivo es proporcionar un quickstart para ejecutar el proyecto siguiendo convenciones y buenas prácticas en cuanto a la estructura y contenido de una aplicación springboot.

- Define la versión 2.7.10 como base de springboot
- Utiliza Base NoSQL MongoDB
- Lenguaje: Java 11
- Sugiere la utilización de la especificación Swagger, mediante la implementación de OpenApi, para exponer la documentación de la API


## **RUN LOCAL - DOCKER**

Para ejecutar el proyecto de forma local, el proyecto cuenta con la posibilidad de simular un ambiente similar al ambiente desarrollo iniciando un contenedor Docker.

- Como requisito previo se requiere la instalación y ejecución de [docker](https://docs.docker.com/install/).
- Deben agregarse variables de entorno para el correcto funcionamiento del MS. Se sugiere a través de un archivo (.env) que por seguridad no es subido en el repositorio.
- Clonar el proyecto y desde la raiz abrir una terminal (CMD) donde se deberán ejecutar los comandos brindados a continuación.
- Debe exisitr una 'network' de Docker creada a la cual pertenecerán todos los contenedores. (Para este ejemplo se tomará la network: 'storekeeper-net'):

  ### **CREAR NETWORK DOCKER (OPCIONAL)**
  
  - Correr el siguiente comando para crear la network 'storekeeper-net' (solo si no existe la network creada):

    ```shell
        $ docker network create --driver bridge storekeeper-net
    ```

  - En caso de ser necesario, se puede borrar una network corriendo el siguiente comando:

    ```shell
        $ docker network rm storekeeper-net
    ```

- Build contenedor Docker:

    ```bash
        $ mvn -U clean install
        $ docker build -t cbprojects/storekeeper-server .
        $ docker run --env-file .env -dp 8085:8085 --name storekeeper-server --network storekeeper-net cbprojects/storekeeper-server
    ```

- Para verificar los contenedores iniciados correr el siguiente comando y deberá ver algo como:

    ```bash
        $ docker ps

        CONTAINER ID   IMAGE                            COMMAND                           CREATED          STATUS          PORTS                    NAMES
        55d0631305bb   cbprojects/storekeeper-server       "java -jar /contact-manager.j…"   50 minutes ago   Up 50 minutes   0.0.0.0:8080->8080/tcp   storekeeper-server
    ```

- Para inspeccionar las propiedades de la network correr el siguiente comando y deberá ver algo como:

    ```bash
        $ docker network inspect storekeeper-net
        
        [
            {
                "Name": "storekeeper-net",
                "Id": "4426de77cc4449cd3ae60eeb9f815f3ff12b7c2657f385086cb45e1bfb0db4dc",
                "Created": "2022-05-30T03:30:02.963358677Z",
                "Scope": "local",
                "Driver": "bridge",
                "EnableIPv6": false,
                "IPAM": {
                    "Driver": "default",
                    "Options": {},
                    "Config": [
                        {
                            "Subnet": "172.19.0.0/16",
                            "Gateway": "172.19.0.1"
                        }
                    ]
                },
                "Internal": false,
                "Attachable": false,
                "Ingress": false,
                "ConfigFrom": {
                    "Network": ""
                },
                "ConfigOnly": false,
                "Containers": {
                    "55d0631305bbc05dd1ffebedf7baebc01795188dd7f7e708f0ebf0a4ad6e1254": {
                        "Name": "storekeeper-server",
                        "EndpointID": "46f4ae633ad0c340c88e5cac7894accb88726a28828da6250631e0bdef714917",
                        "MacAddress": "02:42:ac:13:00:02",
                        "IPv4Address": "172.19.0.2/16",
                        "IPv6Address": ""
                    }
                },
                "Options": {},
                "Labels": {}
            }
        ]
    ```


## **IMPORTANTE**

Al momento de agregar las variables de entorno, tenga en cuenta que la visibilidad entre contenedores está dada por la IP interna asignada en la network. Fijarse en la propiedad 'Containers' del JSON anterior, allí se especifican las IP's para cada contenedor.

- Para el ejemplo, las IP's que deberá usar en el archivo de variables de entorno fueron asignadas así:

    ```bash
        CONTAINER_ID   IMAGE                   INTERNAL_NETWORK_IP:PORT     NAMES
        55d0631305bb   cbprojects/storekeeper-server       '172.19.0.2':8080              storekeeper-server
    ```

Esto permite el despliegue de la aplicación en un contenedor docker que puede ser usado en conjunto con otros contenedores, como por ejemplo una base de datos. Esto falicitará realizar tests de integracion entre frentes de trabajo y sistemas.


## Run SonarQube scanning

    Generate the next command from project SonarQube/SonarCloud and run in root folder (in the example "cbprojects_storekeeper-server")

    ```bash
        mvn clean verify sonar:sonar \
        -Dsonar.projectKey=cbprojects_storekeeper-server \
        -Dsonar.sources=. \
        -Dsonar.junit.reportPaths=target/site/jacoco/ \
        -Dsonar.host.url=https://sonarcloud.io \
        -Dsonar.login=XXXXXXXXXXX
    ```

## **DOCUMENTACIÓN**

- [Documentación de la API](https://cbaeneprojects.com:8443/Storekeeper/swagger)
- [Documentación Docker](https://docs.docker.com/)


