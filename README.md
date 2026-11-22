# Storekeeper Server

API de gestión con Java 25, Spring Boot 4.1.2, MongoDB, Jakarta Validation y OpenAPI.

## Entorno completo

Desde la carpeta padre, que contiene ambos repositorios:

```sh
docker compose up -d --build --wait
docker compose --profile test run --build --rm tests
```

API: http://localhost:8402/storekeeper-server
Swagger: http://localhost:8402/storekeeper-server/swagger
Salud: http://localhost:8402/storekeeper-server/actuator/health

Consultar `../README.md` para operación, pruebas y alcance de la modernización.

## Desarrollo sin Docker

Requiere Java 25, Maven 3.9 y MongoDB. `STOREKEEPER_MONGO_URL` tiene por defecto
`mongodb://localhost:27017/storekeeper`; `SERVER_PORT` vale 8080. Ejecutar:

```sh
mvn verify
mvn spring-boot:run
```

Los archivos `.env` no se cargan automáticamente con Maven. Exportar las variables si se requieren otros valores. Docker Compose configura su propia conexión a MongoDB y no utiliza las credenciales históricas ni los certificados del repositorio.

## Docker

El Dockerfile ejecuta `mvn verify` y genera un JAR en una etapa de compilación.
La imagen final sólo contiene el JRE y el JAR; se ejecuta sin privilegios.
`docker-compose.yml` remite al Compose de la carpeta padre.

El backend tiene pruebas JUnit/MockMvc y un gate JaCoCo obligatorio del **100 % de
líneas ejecutables**, sin exclusiones de paquetes. Ejecutar `mvn clean verify`;
el informe queda en `target/site/jacoco/index.html`. El gate mide líneas: las
ramas se informan por separado y no se presentan como 100 %.

Mockito se carga como agente al iniciar las pruebas, junto con JaCoCo, para
evitar la carga dinámica del agente. El compilador comprueba `-Xlint:all` salvo
`processing` (Spring/JUnit usan anotaciones en runtime). La advertencia de
Lombok por `sun.misc.Unsafe` en Java 25 procede del procesador externo;
no se desactiva globalmente la advertencia de la JVM.

Las pruebas de integración de la carpeta padre ejecutan flujos reales con
MongoDB y SMTP local, aislamiento, concurrencia, cargas CSV/Excel y descargas
de reportes Excel/PDF. Consultar
[progreso SaaS](docs/implementation-progress.md),
[identidad](docs/authentication-and-tenancy.md) y [comercio](docs/commerce.md).
