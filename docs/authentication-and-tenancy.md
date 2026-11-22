# Identidad y aislamiento

## Desarrollo local

`docker compose up -d --build --wait` inicia API, Angular, MongoDB y Mailpit.
Los correos quedan en http://localhost:8026 y no salen a destinatarios externos.
Abrir http://localhost:4200, crear una cuenta con contraseña de al menos 12
caracteres (hasta 72 bytes UTF-8), abrir el enlace de Mailpit y después iniciar
sesión. Crear negocios desde Mis negocios. Cada negocio dispone de una URL
`/#/workspace/{id}` y selector. México usa MXN; Colombia usa COP.

Los archivos .env no se cargan automáticamente con Maven. En Docker, configurar
variables en Compose o exportarlas explícitamente. `.env.example` no contiene
secretos. Para SMTP externo configurar SPRING_MAIL_HOST, SPRING_MAIL_PORT,
SPRING_MAIL_USERNAME, SPRING_MAIL_PASSWORD y las propiedades SMTP STARTTLS/TLS
adecuadas al proveedor. STOREKEEPER_MAIL_FROM identifica el remitente.

## Sesiones y credenciales

- Spring Security guarda la autenticación en HttpSession; no hay tokens de
  acceso en localStorage. Cookie JSESSIONID HttpOnly, SameSite=Lax y Secure por
  defecto. Solo Compose local desactiva Secure para HTTP.
- GET /api/v1/auth/session entrega usuario público (o null) y token CSRF.
  Las operaciones que modifican estado requieren X-CSRF-TOKEN, incluso login,
  registro y recuperación. El identificador de sesión y CSRF cambian al acceder.
- BCrypt, factor 12; límite explícito de bytes evita truncamiento.
- Tokens de verificación y recuperación: 32 bytes aleatorios; solo SHA-256
  persiste; TTL de 24 horas/1 hora, comprobado también en cada consumo. Consumo
  atómico con findAndRemove. Reset compara versión de credenciales para evitar
  carreras, invalida otros tokens de recuperación y todas las sesiones previas.
- La versión de credenciales se comprueba contra Mongo en cada solicitud
  autenticada. POST /auth/revoke-sessions incrementa esa versión.
- Límites distribuidos por cuenta y dirección mediante contadores atómicos
  Mongo con TTL; ventanas de 15 minutos. No se guardan emails o IP en contadores.
- Respuestas de registro/recuperación son genéricas. Fallos SMTP quedan en
  platform_credential_audit como MAIL_DELIVERY_FAILED, sin tokens ni secretos.
- Sesiones de esta versión viven en memoria del servidor: reiniciar requiere
  volver a iniciar sesión; cuentas y datos permanecen en Mongo. No desplegar
  múltiples réplicas sin sesiones compartidas o afinidad de sesión.

## Google

Activar perfil `google` con SPRING_PROFILES_ACTIVE=google, GOOGLE_CLIENT_ID y
GOOGLE_CLIENT_SECRET. En Google configurar el callback exacto del origen
público: `https://tu-dominio/storekeeper-server/login/oauth2/code/google`.
STOREKEEPER_PUBLIC_URL y STOREKEEPER_ALLOWED_ORIGINS deben usar el origen real.
El proxy debe conservar Host con puerto y terminar HTTPS correctamente.
En despliegues detrás de un proxy confiable, configurar el manejo de forwarded
headers y URI de redirección de Spring de acuerdo con esa topología.

Scopes únicamente openid/email/profile. Spring valida estado, nonce y token
OIDC. Se exige email verificado. Un subject de Google ya vinculado reutiliza
la cuenta; una coincidencia de email con cuenta local no se vincula sin sesión.
Desde Perfil, Vincular Google inicia vinculación explícita limitada a cinco
minutos, misma cuenta verificada y misma versión de credenciales. El índice
parcial único de googleSubject impide duplicar la vinculación.
La opción no se muestra si Google no está configurado. El flujo externo aún
requiere credenciales y verificación real con el proveedor; tests de contratos
no equivalen a una prueba completa de Google.

## Datos empresariales

Account es global y no equivale a un empleado. Business tiene ownerId separado
de datos operativos. BusinessService autoriza propietario y estado ACTIVE antes
de crear BusinessScope; su constructor no es público. CustomerService y
CommerceService exigen ese scope y añaden businessId en cada consulta y cambio.
No hay interfaces de repositorio operativo que permitan findAll global.
La relación actual de propietario puede ampliarse con membresías sin cambiar
las claves businessId de los documentos.

La plantilla declara configuredModules previstos. `modules` contiene solo la
intersección autorizada con capacidades implementadas de CapabilityEngine.
Opcionales no disponibles se rechazan en el servidor aunque se manipule Angular.
El plan sigue gratuito; no hay cuotas ni cobros de suscripción aplicados.

Los endpoints heredados /client/v1, /product/v1, etc. están cerrados para todos:
autenticarse no los vuelve globalmente accesibles. Sus colecciones se conservan.
No se ha migrado ningún dato anterior ni inferido un propietario. La migración
deberá usar mapa explícito, dry-run, respaldo y validación de referencias antes
de habilitar contratos compatibles por tenant. Esto es una limitación vigente,
no una migración completada.

## API

Prefijo /storekeeper-server/api/v1:

| Ruta | Operación |
| --- | --- |
| GET /auth/options | Disponibilidad de Google |
| GET /auth/session | Usuario público y CSRF |
| POST /auth/register | nombre, email, password |
| POST /auth/login | email, password |
| POST /auth/verify | token |
| POST /auth/resend, /auth/forgot | email |
| POST /auth/reset | token, password |
| PUT /auth/profile | name |
| POST /auth/logout | Cierra sesión actual |
| POST /auth/revoke-sessions | Revoca todas las sesiones |
| POST /auth/google-link | Inicia vinculación autenticada |
| GET, POST /businesses | Listado propio y creación |
| GET /businesses/{id} | Configuración autorizada |
| GET, POST /businesses/{id}/customers | Clientes del negocio |
| PUT /businesses/{id}/customers/{customerId} | Edición con version |
| DELETE /businesses/{id}/customers/{customerId}?version=N | Baja con version |

Crear negocio requiere Idempotency-Key (16–100 caracteres alfanuméricos, guion
o guion bajo). Repetir clave y datos devuelve el mismo negocio; datos distintos
con esa clave devuelven 409. Cambios concurrentes en clientes devuelven 409.
Negocios ajenos y desconocidos devuelven el mismo 404. Errores nuevos usan
ProblemDetail sin excepciones internas ni secretos.

Referencias técnicas: [persistencia de autenticación en Spring Security](https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html)
y [configuración OAuth2 de Spring Boot](https://docs.spring.io/spring-boot/reference/security/oauth2.html).

## Plantillas y entrega de correo

Todos los correos actuales (confirmación, reenvío y recuperación) usan HTML y
texto plano multipart/alternative, UTF-8, nombre de remitente Storekeeper,
Subject, Date, Message-ID único, MIME-Version y Content-Language. Se identifican
como automáticos con Auto-Submitted y X-Auto-Response-Suppress. Incluyen enlace
alternativo, vencimiento real y aviso de seguridad; no cargan imágenes remotas
ni píxeles de seguimiento. Nombre y enlaces se escapan antes de insertarse en HTML.

En local los correos se consultan en http://localhost:8026.

En producción configurar un remitente de dominio propio verificado en
STOREKEEPER_MAIL_FROM y una URL pública HTTPS en STOREKEEPER_PUBLIC_URL.
Configurar SPRING_MAIL_HOST/PORT/USERNAME/PASSWORD y STOREKEEPER_SMTP_AUTH=true.
Usar STOREKEEPER_SMTP_STARTTLS=true para STARTTLS obligatorio (normalmente 587),
o STOREKEEPER_SMTP_SSL=true para TLS implícito (normalmente 465), según proveedor.
No desactivar la validación de certificados. Compose local usa Mailpit sin TLS;
los archivos .env no se inyectan automáticamente en el contenedor: estas variables
se deben declarar en el despliegue de producción.

Los encabezados no garantizan entrega a la bandeja de entrada. Antes de enviar:
- Publicar SPF con los emisores autorizados por el proveedor (un solo registro).
- Activar la firma DKIM en el proveedor y publicar sus selectores DNS.
- Publicar DMARC y comprobar alineación del dominio From con SPF o DKIM;
  comenzar con monitoreo y endurecer la política tras validar los emisores.
- Configurar el dominio de rebotes/envelope sender en el proveedor y verificar
  PTR/HELO, TLS, reputación y gestión de rebotes y quejas.
- Comprobar SPF/DKIM/DMARC en Authentication-Results del mensaje recibido real.
  No fabricar Authentication-Results, Return-Path ni firmas DKIM desde la app.

Estos correos son transaccionales de seguridad. No llevan List-Unsubscribe:
si se añade marketing, requiere consentimiento y baja funcional independiente.
No se configuraron DNS ni proveedor real desde este entorno local.
Referencia: https://support.google.com/mail/answer/81126
