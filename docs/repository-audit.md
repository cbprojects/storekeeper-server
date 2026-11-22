# Auditoría inicial — 2026-10-29

## Alcance y evidencia

Workspace con dos repositorios Git independientes: `storekeeper-server` y
`storekeeper-site`; Compose y pruebas Playwright en el directorio padre.
Ambos repositorios contienen numerosos cambios previos sin commit: se conservan.
No se inspeccionaron valores de secretos ni se modificaron datos existentes.
No se encontró AGENTS.md dentro del workspace.

## Inventario

| Área | Implementación observada | Reutilización / trabajo pendiente |
| --- | --- | --- |
| Backend | Java 25, Spring Boot 4.1.2, Maven 3.9, MapStruct, Lombok | Conservar monolito y separación controller/service/repository/DTO |
| Frontend | Angular 21.2, TypeScript 5.9, PrimeNG 21, Bootstrap, RxJS | Conservar componentes y formularios; rutas actuales cargadas conjuntamente |
| Operación | Clientes, proveedores, empleados, empresas, productos, categorías, comprobantes y asignaciones | CRUD existente; no equivale a ventas con inventario, POS ni compras integradas |
| MongoDB | Colecciones assignments, bills, clients, companies, employees, products, products_categories, providers; verificar nombres exactos en ConstantsTableNames | Colecciones compartidas; sin businessId ni propietario global |
| Dinero | ProductEntity usa BigDecimal; stock long | Conservar precisión; revisar todos los contratos antes de ventas |
| Identidad | SesionService usa localStorage; Guardian retorna true; no dependencia Spring Security | Construir identidad global y sesiones verificadas en servidor |
| Autorización | Repositorios con findAll/findById globales | No existe aislamiento multitenant; migrar cada operación y referencia |
| CORS | Filtro permite origen * | Reemplazar por orígenes explícitos al introducir sesiones |
| API | /{dominio}/v1, validaciones y ControllerAdvice existentes | Conservar contratos durante migración; evitar mensajes internos en errores nuevos |
| Integridad | BillService guarda comprobantes y cambia estados sin movimiento de inventario | Añadir idempotencia, concurrencia e integridad comercial |
| UI | Marca, logos, AppearanceService con temas light/dark y menú adaptable | Reutilizar tokens y preferencia; no duplicar diez aplicaciones |
| Pruebas | Specs Angular; Playwright smoke, themes, mobile; backend inicialmente sin tests | Añadir pruebas Java y aislamiento; no asumir que los specs actuales pasan |
| Despliegue | Mongo 8, backend y nginx en Docker Compose; build Maven verify | Mongo standalone: transacciones multidocumento requieren replica set o diseño alternativo |

## Riesgos y migración

Los endpoints actuales son públicos. La presencia de métodos llamados secure en
RestService no aporta autorización del servidor. No habilitar uso SaaS con datos
de distintos propietarios hasta cerrar también los endpoints heredados.

No inferir dueño de empresas existentes ni asignar documentos automáticamente a
un usuario nuevo. La migración deberá ofrecer dry-run, mapa explícito de
propietario/negocio, copia de seguridad, validación de referencias y conteos,
backfill de businessId e índices compuestos antes del corte de acceso.
La unicidad global de code en productos deberá migrarse de forma controlada.

Hay archivos de certificados históricos en resources, excluidos del contexto
Docker. No se utilizarán para nuevos flujos. No se han abierto ni rotado.

## Verificación inicial

La actualización previa a Java 25 completó docker build y mvn verify, sin tests
Java. En esta sesión el acceso inicial a Docker Compose fue denegado por el
entorno; posteriormente se autorizaron los builds Docker de verificación.
Los resultados de nuevas verificaciones se registran en
implementation-progress.md. No se ha consultado la base real: el inventario de
Mongo procede de las entidades y configuración, no de datos de producción.
