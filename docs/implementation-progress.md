# Implementación SaaS Store Keeper

## Estado

### Ajuste de entrada: login antes de contenido privado

sessionGuard valida la sesión del servidor antes de montar portafolio, workspace
y perfil. Un visitante o una sesión que no pueda verificarse se redirige a
/account/login; una sesión válida continúa al portafolio. Registro, confirmación
y recuperación siguen accesibles. Compilación Angular y 43 pruebas PASS con
`docker build --target test -t storekeeper-site:login-first-tests .`.
El recorrido saas.mjs ahora comienza desde la raíz para comprobar la entrada
anónima en su siguiente ejecución E2E; no se volvió a ejecutar ese recorrido
integrado para este ajuste.

- Fase 0 completada: auditoría estática, inventario y plan persistente.
- Fase 1 completada para la base nueva: catálogo, capacidades efectivas,
  identidad modular, BusinessScope, contratos y errores ProblemDetail.
- Fase 2 parcial: registro, SMTP real configurable, confirmación, recuperación,
  sesiones, perfil, revocación y Google OIDC implementados. SMTP local probado;
  falta verificar Google con credenciales reales y el proveedor SMTP externo.
- Fase 3 parcial: portafolio y aislamiento de todos los módulos nuevos probados
  con dos propietarios. API heredada cerrada; migración de sus datos pendiente.
- Fase 4 parcial: wizard real de cinco pasos, identidad visual y configuración
  persistida. Faltan dashboards/flujos especializados y edición de negocio.
- Fase 5 parcial: clientes, productos/servicios, stock/movimientos, ventas,
  pagos, devoluciones completas, proveedores y empleados administrativos,
  reporte por fecha. Faltan compras, POS/caja, categorías/variantes, edición del
  catálogo, comprobantes y exportación.
- Fase 6 parcial: flujo de comercio obligatorio probado por API y navegador.
  El alcance completo de comercio aún requiere los módulos pendientes de fase 5.
- Fases 7–15 pendientes. Las plantillas no cuentan como industrias operativas.
- Fase 16 pendiente, salvo la base de evaluación de capacidades disponibles.
- Fase 17 parcial: pruebas nuevas pasan; no existe QA integral de diez industrias.

## Continuación: implementación y verificación actual

Archivos nuevos bajo platform/identity, platform/business, platform/commerce y
src/test/java/com/cafe/storekeeper/platform. Frontend bajo src/app/platform.
Rutas /account/:mode, /businesses y /workspace/:id con carga diferida.

- Backend: `docker build -t storekeeper-server:saas-auth .` PASS; Maven verify,
  27 tests JUnit/MockMvc y gate de 80 % por paquete platform.* satisfecho.
- Frontend: `docker build --target test -t storekeeper-site:saas-auth-tests .`:
  compilación PASS y 40 tests Karma/Chromium PASS, incluida la simplificación
  final del guard de rutas heredadas.
- Integración: `docker compose -f compose.saas-test.yaml run --build --rm tests`
  PASS contra MongoDB replica set y SMTP de Mailpit reales y aislados. Verifica
  registro sin login previo a confirmación, token de un uso, login/logout,
  revocación, reset, CSRF, propietario ajeno, referencias cruzadas, dos países,
  idempotencia, carrera de clientes, venta con impuestos, pagos parciales,
  reversión de una venta incompleta, devolución y dos ventas de la última unidad.
- Navegador PASS: login, cinco pasos de wizard, creación persistida de cliente,
  recarga, selector, preferencia de tema y anchos 1440/390. Se amplió también a
  producto → stock → venta → pago → devolución → reporte desde UI y pasó.
- Capturas: ../test-results/saas/workspace-1440.png y workspace-390.png.
- No se ejecutaron push, producción, rotación de secretos ni migraciones sobre
  colecciones anteriores. El entorno temporal no comparte el volumen de usuario.
- Verificación final: ambos archivos Compose válidos y git diff --check PASS
  en ambos repositorios. Capturas revisadas sin cargador superpuesto. Se retiró
  el entorno storekeeper-saas-test con `down`, sin tocar el entorno principal ni
  sus volúmenes; capturas y código permanecen en el workspace.

Correcciones encontradas por las pruebas: Host del proxy conservando el puerto,
CORS explícito por origen, generación criptográfica de claves compatible con
hosts HTTP, etiquetas de selects, respuesta 409 para conflictos transaccionales
de Mongo, JSON monetario como cadenas decimales y separación entre módulos
previstos (configuredModules) y efectivos (modules).

Documentación de operación: authentication-and-tenancy.md y commerce.md.
Compose local ahora configura un replica set de un nodo y Mailpit; conserva el
volumen original. La imagen Mailpit se fijó por digest a la versión probada.
Secure de cookies solo se desactiva en desarrollo HTTP. Revisar configuración
HTTPS, SMTP y Google antes de cualquier despliegue real.

## Limitaciones pendientes explícitas

Los endpoints heredados están denegados incluso tras autenticarse para impedir
que evadan el aislamiento. Sus datos permanecen intactos, pero esas pantallas
se redirigen al portafolio. Falta mapa explícito de propietario/negocio y un
procedimiento de migración con dry-run para recuperarlos bajo autorización.

Las sesiones no se comparten entre réplicas de aplicación. No hay invitaciones
ni acceso de empleados. No se procesan cobros externos. No hay certificación
SAT/DIAN ni información médica/jurídica sensible. El reporte actual usa fechas
UTC de creación de venta y saldos pagados actuales, no flujo de caja histórico.
Listados operativos limitados a 500 requieren paginación para uso a gran escala.

## Verificaciones de la entrega anterior

- `docker build -t storekeeper-server:saas-foundation .`: PASS; Java 25,
  Maven verify, 3 tests JUnit/MockMvc, cero fallos y gate JaCoCo satisfecho.
- `docker build --target test -t storekeeper-site:saas-foundation-tests .`:
  compilación de producción PASS; Karma/Chromium: 35 tests PASS, incluyendo
  2 nuevos del servicio de catálogo.
- `git diff --check` en ambos repositorios: PASS; avisos de normalización CRLF
  en archivos previos del frontend.
- Primer build backend falló porque las primeras pruebas activaron el gate
  heredado sin cobertura. Ahora el umbral de 80 % aplica a platform.*;
  reporte global conservado, deuda de paquetes heredados pendiente.
- Intentos locales: npm.ps1 bloqueado por política de ejecución; npm.cmd no
  encontró Angular CLI instalado. Se verificó con dependencias del Dockerfile.
- Advertencias existentes de frontend: claves duplicadas de Chartist,
  selectores SCSS y fuentes 404 durante Karma; no fallaron los tests. No se
  realizó nueva QA visual ni pruebas E2E de SaaS.

## Informe de fases de la entrega anterior (histórico)

**Fase 0 — completada (auditoría del código y despliegue configurado).**
Inventario en repository-audit.md; no se inspeccionaron datos reales de Mongo.
La seguridad actual y la asignación de propietario de datos heredados son los
riesgos principales. Verificación manual: contrastar rutas, entidades y
servicios citados en la auditoría.

**Fase 1 — parcial.** Catálogo versionado de diez categorías, identificadores
de módulos, resolución de opcionales autorizados por plantilla, API REST
pública de metadatos, contrato y consumidor Angular. Probado por JUnit,
MockMvc y HttpTestingController. Consultar platform-catalog.md para comandos
y verificación manual. Pendientes: contexto de negocio autorizado, contratos
de errores nuevos, capacidades disponibles, navegación y persistencia de negocio.
No hay cambios de datos, autenticación implementada ni despliegue de producción.

## Decisiones y orden de trabajo

1. Monolito modular: platform/catalog, identity, business, commerce y dominios especializados.
2. API nueva bajo /api/v1; conservar API heredada hasta migración explícita.
3. Identidad global independiente de Employee y Company heredados.
4. Relación BusinessMembership con propietario inicial; prepara miembros futuros.
5. Contexto autorizado derivado de sesión y pertenencia; nunca aceptar un header
   businessId como autorización. Repositorios operativos nuevos deben exigir
   BusinessScope explícito; sin métodos de lectura global expuestos.
6. Mongo compartido con businessId e índices compuestos; migración manualmente
   mapeada para datos heredados. No se ha ejecutado ninguna migración.
7. Autenticación con sesiones servidor y cookies HttpOnly, CSRF, límites de
   intentos; OIDC Google solo openid/email/profile. SMTP y Google requieren
   configuración externa; nunca simular verificación o identidad.
8. Catálogo describe módulos previstos; disponibilidad y autorización efectiva
   se evaluarán aparte. No habilitar módulos incompletos en el wizard.
9. Operaciones críticas: idempotencia persistida, versiones optimistas y
   transacciones con replica set de desarrollo aislado cuando corresponda.

## Plan por fases

| Fase | Entrega y criterio para continuar |
| --- | --- |
| 1 | Catálogo, contratos, errores y contexto autorizado; compilación y tests |
| 2 | Registro, SMTP, verificación, recuperación, sesiones, perfil, Google y tests de seguridad |
| 3 | Portafolio, membresías, selector y migración de todos los CRUD; tests con dos propietarios |
| 4 | Wizard persistente de cinco pasos, branding, módulos permitidos, diez dashboards |
| 5 | Núcleo comercial, dinero decimal, pagos, stock, compras, POS y reportes |
| 6 | Comercio: producto → stock → venta → pago → reporte y devoluciones |
| 7 | Gimnasio: plan → socio → membresía → pago → asistencia |
| 8 | Restaurante: mesa → comanda → preparación → servicio → cierre → pago |
| 9 | Hotel: disponibilidad → reserva → check-in → cargos → check-out |
| 10 | Salón: servicio → profesional → disponibilidad → cita → cobro |
| 11 | Academia: curso → grupo → alumno → inscripción → asistencia → cuota |
| 12 | Consultorio: agenda administrativa sin información clínica sensible |
| 13 | Servicios: proyecto → actividad → tiempo → cargo → pago |
| 14 | Genérico: módulos permitidos → catálogo → operación → resultados |
| 15 | Legal: asunto administrativo → actividad → honorario → pago |
| 16 | Plan/capacidades/cuotas/consumo separados; acceso gratuito |
| 17 | E2E de diez industrias, seguridad, concurrencia, temas, migración y rendimiento |

## Archivos relevantes

- docs/repository-audit.md: diagnóstico y riesgos iniciales.
- docs/saas-requirements.md: copia exacta del prompt original para reanudar.
- docs/platform-catalog.md: contrato, verificación y alcance de cobertura.
- src/main/java/com/cafe/storekeeper/platform/catalog: catálogo nuevo.
- src/test/java/com/cafe/storekeeper/platform/catalog: pruebas del catálogo.
- ../storekeeper-site/src/app/config/Routing.ts: rutas heredadas.
- ../storekeeper-site/src/app/services/appearance.service.ts: temas reutilizables.
- ../tests: pruebas API/UI existentes; ../compose.yaml: desarrollo integrado.

## Reanudación exacta

Leer estos documentos, saas-requirements.md y el estado Git de ambos repositorios.
No hacer reset ni commits que incluyan trabajo previo ajeno. No repetir la
auditoría ni reconstruir autenticación, catálogo o wizard.

Siguiente actividad exacta: completar fase 5 con compras y caja/POS. Usar
BusinessScope, consultas con businessId y TransactionTemplate existentes.
Compras deben validar proveedor y productos del mismo negocio, recibir una
sola vez y registrar movimientos de stock. Caja debe permitir una apertura
activa por negocio (índice único parcial), movimientos y cierre con versión,
totales decimales y conciliación. Integrar efectivo con pagos/devoluciones en
la misma transacción y probar carreras con Mongo real. Después completar
categorías/variantes/edición de catálogo, comprobantes internos y configuración
fiscal sin SAT/DIAN simulados. Continuar luego con gimnasios y demás industrias
en el orden del prompt. No se aplicó ningún archivo OperationsModels/Service:
solo existen los directorios y el núcleo CommerceService de esta entrega.

Al añadir capacidades, actualizar CapabilityEngine y sus pruebas; no activarlas
solo mediante catálogo. BusinessService calcula capacidades efectivas al leer
para incorporar módulos terminados sin alterar la configuración original.
La vinculación real de Google requiere configurar el perfil google con
credenciales; no declarar ese escenario E2E completado sin hacerlo.
No hay permisos para push, producción ni cambios destructivos de datos.
