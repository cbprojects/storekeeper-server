# Catálogo de plataforma v1

`GET /storekeeper-server/api/v1/catalog` devuelve metadatos públicos:

```json
{
  "version": 1,
  "categories": [{
    "id": "retail",
    "version": 1,
    "name": "Tienda / comercio",
    "description": "Ventas e inventario",
    "icon": "pi pi-shopping-cart",
    "accent": "#087BFF",
    "layout": "commerce",
    "dashboard": "retail",
    "requiredModules": ["CUSTOMERS", "CATALOG"],
    "optionalModules": ["EMPLOYEES"]
  }],
  "modules": ["CUSTOMERS", "CATALOG"],
  "availableModules": ["CUSTOMERS", "CATALOG"]
}
```

Ejemplo abreviado: la respuesta real contiene diez plantillas y todos los módulos.
Los arrays de módulos no garantizan orden. Los identificadores son estables y
se versionará la plantilla si cambia su política. El catálogo no contiene datos
personales ni empresariales; no requiere sesión. No crea negocios ni concede
permisos. `modules` identifica el registro completo previsto;
`availableModules` identifica las capacidades implementadas en este despliegue.
El negocio conserva configuredModules previstos y recibe modules efectivos.

`CategoryCatalog.resolveModules` exige que los opcionales solicitados sean un
subconjunto de los permitidos, y añade siempre los obligatorios. Un identificador
desconocido o una petición manipulada se rechaza. Esta validación interna se
combinará con pertenencia y disponibilidad real en el servicio de creación del
negocio; no se expone como endpoint que habilite capacidades.

Angular consume el mismo contrato con PlatformCatalogService; no mantiene una
copia del registro de categorías. Los errores HTTP se propagan al consumidor.
El wizard consume este servicio y deshabilita opcionales no disponibles.
BusinessService repite esa validación en servidor; modificar Angular no activa
una capacidad ausente. Los flujos de las industrias especializadas siguen
pendientes.

## Verificación

Backend: `docker build -t storekeeper-server:saas-foundation .` desde el servidor.
Ejecuta Maven verify, tests JUnit y MockMvc sin requerir MongoDB.
Frontend: `docker build --target test -t storekeeper-site:saas-foundation-tests .`.
Con el servidor iniciado, consultar el endpoint y comprobar diez categorías;
este cambio no requiere migraciones ni modifica registros existentes.

## Cobertura heredada

El gate JaCoCo de 80 % por paquete estaba inactivo al no existir ejecución de
tests. Las primeras pruebas lo activaron y revelaron cobertura cero en paquetes
heredados. Se mantiene 80 % sobre `com.cafe.storekeeper.platform.*`; el reporte
sigue analizando los paquetes anteriores, que tienen deuda de cobertura
explícita. Se debe ampliar progresivamente el gate al migrar cada dominio.
Este cambio reduce el alcance del gate anterior; no se presenta como cobertura
global del backend.
