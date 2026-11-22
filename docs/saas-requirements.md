# PROMPT MAESTRO PARA CODEX
# Store Keeper — Implementación SaaS Multiempresa y Multinegocio

## 0. Tu rol y objetivo general

Actúa como un equipo de ingeniería de software compuesto por:

- Arquitecto de Software Senior.
- Technical Lead Java y Angular.
- Ingeniero Backend Java.
- Ingeniero Frontend Angular.
- Especialista en MongoDB.
- Especialista en Seguridad y Autenticación.
- Diseñador UX/UI.
- Ingeniero QA y automatización.

Tu misión es evolucionar mi aplicación existente **Store Keeper** hasta convertirla en una plataforma SaaS multitenant, modular, multiempresa, escalable y preparada para monetización futura.

**No estás creando un proyecto desde cero.**

Ya existe un repositorio con frontend Angular, backend Java, API y base de datos MongoDB.

Primero debes inspeccionar la implementación existente, comprender su arquitectura y determinar cómo extenderla con el menor riesgo y el mayor aprovechamiento posible de sus componentes.

No quiero solamente propuestas, diagramas o documentación. **Debes implementar código funcional, integrado, probado y verificable.**

La plataforma deberá admitir diez categorías de negocio funcionales, con módulos comunes reutilizables y experiencias de usuario especializadas.

## 1. Principios de arquitectura obligatorios

1. Mantener la arquitectura y el stack existente siempre que sea técnicamente viable.
2. Preferir un monolito modular inicialmente; no crear microservicios sin una justificación concreta.
3. Separar identidad de usuarios, propiedad de negocios y datos operativos.
4. Implementar aislamiento multitenant desde el backend.
5. Reutilizar servicios, componentes, entidades y reglas comunes.
6. Construir funcionalidades especializadas mediante módulos por dominio.
7. Aplicar separación clara de responsabilidades.
8. Exponer APIs REST consistentes y versionadas.
9. Utilizar contratos explícitos y validaciones.
10. Mantener compatibilidad con los datos y funcionalidades existentes.
11. Implementar controles de autorización en el servidor, nunca depender solamente de Angular.
12. Crear pruebas automáticas para cada funcionalidad.
13. Mantener trazabilidad de decisiones y progreso.
14. Diseñar teniendo en cuenta extensibilidad, seguridad, rendimiento, observabilidad y mantenibilidad.
15. No implementar funcionalidades simuladas como sustitutos de procesos reales.

Adopta patrones como Strategy, Factory, Adapter, Repository y arquitectura modular cuando aporten valor real. Evita introducir abstracciones innecesarias.

## 2. Stack tecnológico

Frontend:
- Angular, conservando su versión y configuración actual cuando sea viable.
- TypeScript.
- Componentes y librerías existentes.
- Formularios reactivos cuando corresponda.
- Carga diferida de módulos.
- Gestión centralizada del contexto del negocio.
- Tema claro y oscuro.

Backend:
- Java.
- Framework y versiones identificados mediante inspección.
- APIs REST.
- Autenticación segura.
- Validación de DTOs.
- Servicios de dominio.
- Persistencia MongoDB.
- Manejo centralizado de errores.

Base de datos:
- MongoDB.
- Índices apropiados.
- Aislamiento lógico de datos por negocio.
- Versionado y migraciones controladas cuando sean necesarias.

Antes de introducir dependencias nuevas, verifica si la solución existente ya proporciona la funcionalidad requerida.

## 3. Modelo de plataforma

Store Keeper tendrá tres niveles conceptuales:

### Nivel A — Usuario

Una persona tiene una cuenta global en Store Keeper.

Puede:
- Registrarse.
- Confirmar su correo.
- Iniciar sesión.
- Iniciar sesión con Google.
- Recuperar su contraseña.
- Editar su perfil.
- Administrar sesiones según las capacidades implementadas.
- Crear y administrar múltiples negocios.

Una misma identidad no debe duplicarse cada vez que crea un negocio.

### Nivel B — Portafolio de negocios

Un usuario puede tener varios negocios independientes.

Ejemplo:

- Tienda Central.
- Gimnasio PowerFit.
- Restaurante La Terraza.
- Hotel El Descanso.
- Despacho Jurídico.

Cada negocio tendrá:
- Identificador único.
- Propietario.
- Nombre comercial.
- Categoría.
- País y moneda.
- Configuración regional.
- Identidad visual.
- Módulos habilitados.
- Información comercial.
- Configuración operativa.
- Datos independientes.
- Estado de activación.
- Configuración de capacidades futuras.

En la primera versión, el propietario es el único usuario con acceso administrativo.

Diseña la relación usuario-negocio de manera que posteriormente sea posible agregar miembros, invitaciones, roles y permisos sin reconstruir la arquitectura.

### Nivel C — Módulos del negocio

Cada negocio utiliza funcionalidades comunes y especializadas.

Ejemplos de módulos compartidos:
- Clientes.
- Productos y servicios.
- Ventas.
- Registro de pagos.
- Inventario.
- Caja/POS.
- Compras.
- Proveedores.
- Empleados como registros administrativos.
- Reportes.
- Comprobantes comerciales y configuración de facturación.

Los módulos especializados deben adaptarse al dominio correspondiente.

**Un restaurante y una tienda comparten servicios comerciales, pero no necesariamente los mismos flujos de usuario ni las mismas reglas operativas.**

## 4. Autenticación y usuarios

Implementa:

### Registro
- Registro con nombre, correo y contraseña.
- Validación de datos.
- Detección segura de cuentas existentes.
- Verificación de correo mediante enlace.
- Estados de verificación.
- Reenvío controlado de confirmación.
- Mensajes de error claros.

### Inicio de sesión
- Email y contraseña.
- Google mediante OpenID Connect.
- Vinculación segura de identidades.
- Manejo correcto de sesiones.
- Cierre de sesión.
- Expiración y revocación de credenciales.
- Protección contra fuerza bruta.

No solicites acceso a los correos de Gmail. El acceso con Google debe requerir únicamente los permisos necesarios para identificar al usuario.

### Recuperación de contraseña
- Solicitar recuperación por correo.
- Token temporal, aleatorio y de un solo uso.
- Expiración.
- Actualización segura de contraseña.
- Invalidación del token después de utilizarlo.
- Respuestas que no permitan enumerar cuentas existentes.

### Seguridad
- Hash seguro de contraseñas.
- Protección CSRF cuando corresponda.
- Cookies seguras y HttpOnly cuando se utilicen sesiones por cookies.
- CORS restringido.
- Rate limiting.
- Validación de entradas.
- Auditoría de cambios de credenciales.
- No exponer secretos en código, logs o respuestas.
- Variables de entorno para configuración sensible.
- Pruebas de autorización y seguridad.

Si el sistema tiene un mecanismo de autenticación existente, reutilízalo o evoluciona sus contratos de manera compatible.

No sustituyas innecesariamente un sistema de sesiones seguro por otro más complejo.

## 5. Pantalla de portafolio

Después de autenticarse, el usuario debe acceder a **Mis negocios**.

La pantalla debe presentar tarjetas visuales con:

- Logo e identidad del negocio.
- Nombre comercial.
- Categoría.
- Estado.
- País.
- Acceso al dashboard.
- Acceso a configuración.
- Acciones de administración permitidas.

Debe incluir un botón destacado:

**+ Crear nuevo negocio**

Si el usuario todavía no tiene negocios, presentar un estado de bienvenida con el robot de Store Keeper y una invitación clara para crear su primer negocio.

Debe existir un selector de negocio para cambiar entre espacios de trabajo sin cerrar sesión.

Al cambiar de negocio:
- Actualizar el contexto activo.
- Cargar su configuración y módulos.
- Actualizar la navegación.
- Actualizar el dashboard.
- Invalidar o separar las cachés correspondientes al negocio anterior.
- Evitar cualquier mezcla de datos.

Permitir recordar el último negocio utilizado, respetando el control de acceso vigente.

## 6. Asistente de creación de negocios

Implementa un wizard dinámico de cinco pasos.

### Paso 1 — Categoría

Mostrar las diez categorías disponibles mediante tarjetas atractivas con iconografía.

### Paso 2 — Información del negocio

Solicitar:
- Nombre comercial.
- Descripción opcional.
- País.
- Moneda.
- Zona horaria.
- Datos comerciales pertinentes.
- Información de contacto opcional.

México y Colombia deben estar soportados desde el principio.

### Paso 3 — Identidad visual

Permitir:
- Subir logo.
- Elegir color de acento.
- Seleccionar estilo visual permitido para la categoría.
- Previsualizar tema claro y oscuro.

La identidad principal de Store Keeper debe permanecer consistente.

### Paso 4 — Configuración funcional

Cargar automáticamente los módulos sugeridos de la categoría.

Permitir configurar únicamente los módulos opcionales que el catálogo autorice.

No permitir activar capacidades no disponibles simplemente manipulando el frontend.

### Paso 5 — Confirmación

Mostrar un resumen y crear el negocio.

Gestionar adecuadamente fallos de creación, reintentos y estados incompletos. Evitar duplicados accidentales.

Al finalizar, dirigir al usuario al dashboard especializado.

## 7. Motor de categorías y módulos

Implementa un sistema centralizado de plantillas por categoría.

Cada plantilla debe declarar:

- Identificador y versión.
- Nombre.
- Descripción.
- Iconografía.
- Configuración visual.
- Distribución principal.
- Módulos obligatorios.
- Módulos opcionales.
- Navegación disponible.
- Dashboard asociado.
- Funcionalidades especializadas.
- Configuración inicial.

Crea un registro o catálogo de módulos compartido y estable.

No implementes diez copias completas del frontend.

Utiliza componentes comunes y pantallas especializadas cuando los procesos realmente sean distintos.

El backend debe determinar las capacidades efectivamente autorizadas del negocio. Angular utilizará esa información para componer la interfaz.

Las nuevas categorías deben poder añadirse mediante una extensión controlada, sin modificar indiscriminadamente todo el sistema.

## 8. Arquitectura multitenant con MongoDB

Utiliza una estrategia de colecciones compartidas y aislamiento lógico por `businessId`, salvo que el análisis del repositorio identifique una razón técnica para elegir otra estrategia.

Reglas obligatorias:

1. Todo documento empresarial debe estar asociado a un negocio.
2. Validar que el usuario autenticado tenga acceso al negocio solicitado.
3. Filtrar todas las consultas por contexto autorizado.
4. Aplicar ese aislamiento a lecturas, inserciones, actualizaciones, eliminaciones y agregaciones.
5. No confiar en el `businessId` recibido del cliente sin validación.
6. Crear índices compuestos con `businessId` cuando corresponda.
7. Limitar la unicidad de códigos empresariales al negocio cuando sea adecuado.
8. Separar caches por negocio.
9. Evitar filtraciones por referencias a entidades de otros negocios.
10. Crear pruebas de aislamiento cruzado.

No basta con utilizar filtros en los controladores. El diseño debe hacer difícil que un desarrollador olvide aplicar el contexto de tenant.

En operaciones con múltiples documentos, analiza transacciones o mecanismos alternativos de consistencia según el despliegue real de MongoDB.

Para operaciones comerciales críticas utiliza idempotencia, control de concurrencia y reglas de integridad explícitas.

## 9. Módulos comerciales compartidos

Implementa módulos funcionales reutilizables:

### Clientes
- Alta y edición.
- Búsqueda y filtrado.
- Información de contacto.
- Historial comercial según permisos.
- Relación con el negocio correspondiente.

### Productos y servicios
- Catálogo.
- Categorías.
- SKU cuando aplique.
- Variantes cuando sean necesarias.
- Precios.
- Estado activo/inactivo.
- Productos físicos y servicios diferenciados.

### Inventario
- Existencias.
- Movimientos.
- Ajustes.
- Entradas y salidas.
- Alertas de stock bajo.
- Historial.
- Consistencia con ventas y compras.

### Ventas y pagos
- Registro de ventas.
- Detalle de artículos.
- Impuestos y descuentos configurables.
- Registro de métodos de pago.
- Pagos parciales cuando aplique.
- Anulación y devolución controladas.
- Estados comerciales.
- Historial y consulta.

No implementes procesamiento real de tarjetas o cobros externos sin proveedor y configuración explícitos.

### Caja/POS
- Apertura y cierre de caja.
- Registro de movimientos.
- Ventas.
- Totales y conciliación.
- Diferencias de cierre.

### Compras y proveedores
- Catálogo de proveedores.
- Órdenes de compra.
- Recepción.
- Actualización de inventario cuando corresponda.
- Estados y trazabilidad.

### Empleados
- Información administrativa del personal.
- Asignación al negocio.
- Estado y datos básicos.

Por ahora, un registro de empleado no es una cuenta que pueda iniciar sesión.

### Reportes
- Ventas.
- Ingresos registrados.
- Productos.
- Existencias.
- Compras.
- Indicadores relevantes.
- Filtros por fechas.
- Exportación cuando la infraestructura existente lo permita.

### Facturación
Implementar datos fiscales empresariales, datos de clientes y emisión o registro de comprobantes comerciales internos.

Diferenciar expresamente comprobante interno de factura fiscal electrónica autorizada.

Preparar interfaces de integración para México y Colombia, sin simular certificación del SAT o validación de la DIAN.

Utilizar un modelo monetario de precisión decimal, nunca `float` o `double` para cálculos monetarios.

## 10. Diez categorías funcionales

No consideres completa una categoría solo porque aparece en el menú o porque tiene una pantalla vacía.

Cada categoría debe tener una operación real, persistente y verificable de principio a fin.

### Categoría 1 — Tienda / comercio

Prioridad de desarrollo: primera categoría.

Implementar:
- Dashboard de comercio.
- Productos y variantes.
- Categorías.
- Inventario.
- Clientes.
- Proveedores.
- Compras.
- Ventas.
- POS.
- Pagos registrados.
- Devoluciones y ajustes.
- Reportes de ventas e inventario.

Flujo obligatorio:
Crear producto → ingresar existencias → registrar venta → descontar stock correctamente → registrar pago → consultar reporte.

### Categoría 2 — Gimnasio

Implementar:
- Dashboard de gimnasio.
- Socios.
- Planes de membresía.
- Inscripción.
- Vigencia.
- Renovaciones.
- Registro de asistencia.
- Servicios adicionales.
- Entrenadores como registros de personal.
- Pagos.
- Alertas de vencimiento.
- Reportes de membresías e ingresos.

Flujo obligatorio:
Crear plan → registrar socio → contratar membresía → registrar pago → validar vigencia → registrar asistencia → consultar vencimientos.

### Categoría 3 — Restaurante

Implementar:
- Dashboard gastronómico.
- Categorías de menú.
- Platillos y precios.
- Mesas.
- Apertura de cuentas.
- Comandas.
- Estados de preparación.
- Consumos.
- Cierre de cuenta.
- Cobros registrados.
- Inventario de productos o insumos según configuración.
- Reportes.

Flujo obligatorio:
Crear platillo → abrir mesa → agregar comanda → actualizar preparación → servir → cerrar cuenta → registrar pago.

Evitar dobles cierres y pérdidas de actualización cuando varias operaciones afecten la misma mesa.

### Categoría 4 — Hotel

Implementar:
- Dashboard hotelero.
- Tipos de habitación.
- Habitaciones.
- Tarifas.
- Calendario de disponibilidad.
- Reservas.
- Huéspedes.
- Check-in.
- Check-out.
- Estados de habitación.
- Cargos adicionales.
- Pagos registrados.
- Reportes de ocupación.

Flujo obligatorio:
Crear habitación → consultar disponibilidad → reservar → registrar huésped → check-in → registrar cargos → check-out.

Impedir reservas superpuestas incompatibles para la misma habitación.

### Categoría 5 — Salón / barbería

Implementar:
- Catálogo de servicios.
- Profesionales.
- Horarios.
- Agenda.
- Citas.
- Duración de servicios.
- Clientes.
- Servicios completados.
- Venta de productos.
- Registro de pagos.
- Reportes de citas y ventas.

Flujo obligatorio:
Crear servicio y profesional → definir disponibilidad → reservar cita → completar atención → registrar cobro.

Evitar asignaciones simultáneas incompatibles a un mismo profesional.

### Categoría 6 — Academia / escuela

Implementar:
- Dashboard académico administrativo.
- Alumnos.
- Cursos.
- Grupos.
- Instructores.
- Horarios.
- Inscripciones.
- Asistencia.
- Cuotas y pagos.
- Estados de inscripción.
- Reportes de alumnos y cursos.

Flujo obligatorio:
Crear curso → crear grupo → registrar alumno → inscribir → registrar asistencia → registrar cuota y pago.

No implementar un sistema de expedientes académicos sensibles sin controles y requisitos definidos.

### Categoría 7 — Consultorio

Implementar inicialmente una solución administrativa:
- Pacientes con datos mínimos de contacto.
- Servicios.
- Profesionales.
- Agenda.
- Citas.
- Estados de atención.
- Registro administrativo de servicios realizados.
- Cobros.
- Reportes operativos.

Flujo obligatorio:
Crear servicio y profesional → registrar paciente → reservar cita → completar atención administrativa → registrar pago.

Importante:
No almacenar historias clínicas, diagnósticos, tratamientos ni información médica sensible como parte de este alcance sin una evaluación específica de privacidad, seguridad y cumplimiento regulatorio.

### Categoría 8 — Servicios profesionales

Implementar:
- Clientes.
- Catálogo de servicios.
- Proyectos.
- Tareas.
- Seguimiento de actividades.
- Presupuestos.
- Cotizaciones.
- Registro de tiempo.
- Servicios prestados.
- Cobros.
- Reportes por proyecto.

Flujo obligatorio:
Registrar cliente → crear proyecto → añadir actividades → registrar trabajo → generar cargo → registrar pago.

### Categoría 9 — Negocio genérico

Implementar una experiencia configurable:
- Dashboard general.
- Clientes.
- Productos.
- Servicios.
- Ventas.
- Inventario opcional.
- Compras opcionales.
- Proveedores.
- Pagos.
- Reportes.
- Configuración de módulos.

Flujo obligatorio:
Crear negocio genérico → habilitar módulos permitidos → crear producto o servicio → registrar operación → consultar resultados.

Esta categoría debe demostrar que el núcleo modular permite construir negocios sin una plantilla especializada.

### Categoría 10 — Despacho legal

Implementar:
- Clientes.
- Asuntos o expedientes administrativos.
- Tipos de asunto.
- Estado y seguimiento de casos.
- Actividades y tareas.
- Calendario de compromisos.
- Profesionales asignados como registros administrativos.
- Honorarios.
- Registro de pagos.
- Reportes de asuntos y cobros.

Flujo obligatorio:
Registrar cliente → abrir asunto → asignar responsable → registrar actividad y fecha → registrar honorarios → registrar pago → consultar estado.

No almacenar documentos jurídicos confidenciales, pruebas sensibles ni expedientes completos sin diseñar previamente controles adicionales de privacidad, acceso, cifrado y retención.

## 11. Identidad visual y layouts

La marca oficial es **Store Keeper**.

Debes conservar:
- Mascota oficial: robot sonriente.
- Colores principales: azul eléctrico, cian y azul marino.
- Personalidad tecnológica, amigable y profesional.
- Tipografía existente o la adoptada por el proyecto.
- Modo claro y modo oscuro.
- Switch de cambio de tema en el menú.

Cada categoría tendrá una experiencia propia.

Ejemplos:
- Comercio: azul y cian, diseño centrado en ventas e inventario.
- Gimnasio: verde energético, membresías y asistencia.
- Restaurante: tonos cálidos, mesas y comandas.
- Hotel: azul profundo y dorado, ocupación y reservas.
- Barbería: rosa o violeta, agenda y profesionales.
- Academia: azul y verde, grupos y alumnos.
- Consultorio: turquesa, agenda administrativa.
- Servicios profesionales: índigo, tareas y proyectos.
- Genérico: identidad principal Store Keeper.
- Despacho legal: azul marino y dorado, asuntos y seguimiento.

Cada plantilla debe tener:
- Dashboard propio.
- Widgets pertinentes.
- Navegación específica.
- Iconografía adecuada.
- Distribución funcional.
- Adaptación responsive.
- Compatibilidad con temas claro y oscuro.

La personalización no debe duplicar el sistema completo de estilos. Utiliza tokens y componentes compartidos con variantes controladas.

## 12. México y Colombia

Implementar soporte regional para:
- País.
- Moneda MXN/COP.
- Zona horaria por negocio.
- Formato de fechas.
- Precisión monetaria.
- Información fiscal configurable.
- Datos comerciales específicos.

No convertir monedas automáticamente entre negocios sin tipos de cambio explícitos.

Mantener separados los adaptadores de facturación o reglas fiscales específicas de cada país.

La plataforma debe poder extenderse posteriormente a otros países.

## 13. Monetización futura

Actualmente el sistema será de uso gratuito.

Sin embargo, prepara la arquitectura para soportar:
- Planes comerciales.
- Módulos por plan.
- Límites de negocios.
- Límites de productos.
- Cuotas de uso.
- Capacidades adicionales.
- Suscripciones.
- Estados de suscripción.
- Posible integración futura con pasarelas de pago.

Separar:
- Plan contratado o asignado.
- Capacidades habilitadas.
- Cuotas configuradas.
- Consumo real.

No implementar cobros reales ni bloquear funcionalidad gratuitamente disponible sin una decisión comercial explícita.

Debe existir un mecanismo centralizado para evaluar capacidades y cuotas.

## 14. Fases obligatorias de implementación

No realices todos los cambios en un único bloque gigantesco.

Trabaja en fases pequeñas, integrables, probadas y documentadas.

### Fase 0 — Auditoría del repositorio

Inspecciona:
- Frontend.
- Backend.
- API actual.
- Estructura de MongoDB.
- Seguridad.
- Rutas.
- Modelos.
- Componentes.
- Dependencias.
- Pruebas.
- Configuración de despliegue.

Entrega un diagnóstico y un inventario de funcionalidades existentes.

Identifica qué puede reutilizarse, extenderse o necesita migración.

Crea un plan técnico persistente dentro del repositorio.

No cambies código antes de conocer el estado inicial.

### Fase 1 — Arquitectura modular

Define e implementa:
- Límites de módulos.
- Contratos compartidos.
- Contexto de negocio.
- Estrategia multitenant.
- Catálogo de categorías.
- Catálogo de módulos.
- Convenciones de API.
- Manejo de errores.
- Estructura para nuevas funcionalidades.

Criterio de aceptación:
El proyecto compila y la arquitectura permite introducir nuevas categorías sin romper componentes actuales.

### Fase 2 — Autenticación

Implementa:
- Registro.
- Login.
- Google.
- Confirmación de correo.
- Recuperación de contraseña.
- Perfil.
- Sesiones.
- Pruebas de seguridad.

Criterio de aceptación:
Un usuario puede registrarse, verificar su correo, autenticarse y recuperar el acceso mediante flujos reales configurables.

### Fase 3 — Portafolio y multitenancy

Implementa:
- Entidades de negocio.
- Propietario.
- Asociación usuario-negocio.
- Listado de negocios.
- Selector.
- Contexto activo.
- Aislamiento de datos.
- Autorización.

Criterio de aceptación:
Un usuario puede crear dos negocios, alternar entre ellos y gestionar datos separados. Otro usuario no puede acceder a ninguno sin autorización.

### Fase 4 — Wizard y plantillas

Implementa:
- Selección de categoría.
- Datos del negocio.
- Identidad visual.
- Configuración inicial.
- Catálogo de módulos.
- Confirmación y creación.
- Dashboard especializado.

Criterio de aceptación:
Pueden crearse negocios de las diez categorías con configuración persistida, navegación y dashboard pertinentes.

### Fase 5 — Núcleo comercial compartido

Implementa los módulos comunes:
- Clientes.
- Productos y servicios.
- Inventario.
- Proveedores.
- Compras.
- Ventas.
- Pagos.
- Caja/POS.
- Empleados administrativos.
- Reportes.
- Comprobantes comerciales.

Criterio de aceptación:
Los módulos compartidos funcionan realmente, persisten información y respetan el aislamiento por negocio.

### Fase 6 — Tienda / comercio

Implementa la experiencia comercial especializada y completa su flujo funcional.

Verifica especialmente existencias, ventas, devoluciones y cierre de caja.

### Fase 7 — Gimnasio

Implementa membresías, socios, asistencia, pagos y reportes.

Verifica vigencia de membresías y operaciones recurrentes.

### Fase 8 — Restaurante

Implementa menú, mesas, comandas, preparación y cierre de cuentas.

Verifica concurrencia y consistencia del consumo.

### Fase 9 — Hotel

Implementa habitaciones, disponibilidad, reservas y estancia.

Verifica que no puedan existir reservas superpuestas incompatibles.

### Fase 10 — Salón / barbería

Implementa profesionales, horarios, servicios, agenda y cobros.

Verifica disponibilidad y prevención de citas incompatibles.

### Fase 11 — Academia / escuela

Implementa cursos, grupos, alumnos, inscripciones, asistencia y cuotas.

Verifica relaciones y estados de inscripción.

### Fase 12 — Consultorio

Implementa agenda administrativa, pacientes con datos mínimos, servicios, citas y cobros.

Aplica las restricciones de tratamiento de información sensible.

### Fase 13 — Servicios profesionales

Implementa proyectos, tareas, actividades, cotizaciones, tiempo y cobros.

Verifica que los reportes correspondan con las operaciones registradas.

### Fase 14 — Negocio genérico

Implementa una experiencia modular configurable sin depender de pantallas especializadas.

Verifica la activación de módulos autorizados.

### Fase 15 — Despacho legal

Implementa clientes, asuntos, actividades, responsables, fechas, honorarios y pagos.

Aplica controles de privacidad y restringe los datos confidenciales fuera del alcance autorizado.

### Fase 16 — Planes y capacidades futuras

Implementa los contratos, estructuras y servicios necesarios para evaluar módulos y cuotas.

Mantén el acceso gratuito en esta versión.

No integrar cobros reales.

### Fase 17 — QA integral y estabilización

Realiza:
- Pruebas unitarias.
- Pruebas de integración.
- Pruebas de API.
- Pruebas frontend.
- Pruebas end-to-end.
- Pruebas multitenant.
- Pruebas de autorización.
- Pruebas de concurrencia en operaciones críticas.
- Pruebas de temas y responsividad.
- Validación de migraciones.
- Validación de rendimiento.
- Revisión de seguridad.
- Documentación técnica.

Criterio final:
Las diez categorías tienen al menos un flujo operativo integral probado y los módulos declarados para cada una funcionan según su alcance.

## 15. Pruebas de aceptación transversales

Debes verificar los siguientes escenarios:

**Escenario A — Registro**

El usuario crea su cuenta, confirma su correo y accede a Store Keeper.

**Escenario B — Google**

El usuario inicia sesión con Google y accede a su cuenta sin crear identidades duplicadas.

**Escenario C — Múltiples negocios**

El usuario crea una tienda, un restaurante y un gimnasio. Cada uno conserva su configuración, datos y navegación.

**Escenario D — Separación de información**

Los clientes, productos, ventas y registros de un negocio no aparecen en otro.

**Escenario E — Seguridad multitenant**

Un usuario manipula identificadores en solicitudes HTTP para intentar leer o modificar registros de otro negocio. El backend rechaza la operación.

**Escenario F — Personalización**

La aplicación cambia de plantilla comercial a plantilla de restaurante sin perder su identidad global ni la preferencia claro/oscuro.

**Escenario G — Integridad comercial**

Una venta confirmada actualiza correctamente el inventario y no aplica movimientos duplicados ante reintentos.

**Escenario H — Persistencia**

Los datos se conservan correctamente al cerrar sesión, recargar o reiniciar la aplicación.

**Escenario I — Concurrencia**

Dos operaciones simultáneas no generan doble reserva, doble cierre de cuenta o movimientos incompatibles de inventario.

**Escenario J — País y moneda**

Un negocio de México trabaja con MXN y uno de Colombia con COP, sin mezclar registros monetarios.

**Escenario K — Capacidad futura**

El motor de capacidades puede indicar qué funciones corresponden a un negocio, sin aplicar cobros ni restricciones comerciales no aprobadas.

**Escenario L — Diez industrias**

Ejecuta el flujo de negocio obligatorio definido para cada una de las diez categorías.

## 16. Estrategia de trabajo y entregas

Para cada fase:

1. Identifica el código existente relacionado.
2. Define los cambios mínimos necesarios.
3. Implementa backend, persistencia y frontend.
4. Agrega o actualiza pruebas.
5. Ejecuta verificaciones.
6. Corrige fallos.
7. Documenta los cambios.
8. Registra el estado de la fase.
9. Continúa con la siguiente fase cuando los criterios de aceptación se cumplan.

Mantén un documento de progreso, por ejemplo:

`docs/implementation-progress.md`

Este archivo deberá contener:
- Fases completas.
- Fases pendientes.
- Archivos relevantes.
- Decisiones arquitectónicas.
- Migraciones.
- Pruebas ejecutadas.
- Riesgos conocidos.
- Próxima actividad.

Deja instrucciones para reanudar el trabajo en otra sesión de Codex sin perder contexto.

Utiliza cambios y commits lógicos por fase si el entorno y los permisos lo permiten.

No hagas `push`, despliegues a producción, modificaciones destructivas de datos reales ni cambios de secretos sin autorización explícita.

Si encuentras cambios de otro desarrollador, consérvalos y evita sobrescribirlos.

Cuando una fase no pueda completarse por credenciales externas, servicios de terceros o infraestructura faltante, implementa lo verificable, documenta el bloqueo y no declares la funcionalidad completamente operativa.

## 17. Documentación esperada

Mantén actualizada la documentación de:

- Arquitectura general.
- Modelo multitenant.
- Modelo de identidad.
- Modelo de datos MongoDB.
- Categorías y módulos.
- Contratos API.
- Flujos de autenticación.
- Configuración de Google.
- Configuración del servicio de correos.
- Variables de entorno.
- Configuración regional.
- Estrategia de migración.
- Pruebas.
- Limitaciones conocidas.
- Desarrollo local.
- Procedimiento de despliegue.

Incluye ejemplos de configuración sin secretos reales.

Genera diagramas Mermaid cuando faciliten la comprensión.

## 18. Reglas de calidad

No acepto como terminadas:
- Pantallas sin backend.
- Botones sin funcionalidad.
- Datos simulados presentados como persistencia real.
- APIs sin autorización.
- Consultas sin aislamiento multitenant.
- Formularios sin validación.
- Flujos críticos sin pruebas.
- Copias completas de módulos para cada categoría.
- Interfaces rotas en tema oscuro.
- Funcionalidades que no soporten recarga o persistencia.
- Manejo de errores silencioso.
- Facturación fiscal simulada presentada como autorizada.

Sí puedes incorporar datos de demostración explícitamente identificados y aislados para desarrollo y pruebas.

Prioriza funcionalidades completas de extremo a extremo sobre una gran cantidad de pantallas parcialmente implementadas.

## 19. Entrega final por fase

Al finalizar cada fase, muestra un informe conciso:

**Fase:** nombre y número.

**Estado:** completada, parcial o bloqueada.

**Implementado:** funcionalidades terminadas.

**Cambios:** principales archivos y módulos modificados.

**Pruebas:** comandos ejecutados, resultados y fallos.

**Verificación:** pasos concretos para probarlo manualmente.

**Riesgos:** problemas conocidos.

**Siguiente fase:** trabajo que continúa.

No declares que algo funciona si no lo has probado.

## 20. Instrucción final

Comienza inspeccionando el repositorio y ejecutando la Fase 0.

Después implementa las fases en orden, manteniendo el sistema compilable, trazable y funcional.

No pidas confirmación por cada modificación ordinaria que forme parte del alcance aprobado. Toma decisiones de ingeniería fundamentadas y documéntalas.

Si detectas un conflicto arquitectónico importante, una operación destructiva, un requisito legal que no pueda resolverse técnicamente o una dependencia externa bloqueante, explica el problema y evita suposiciones peligrosas.

Si una sesión termina antes de completar todo, deja el progreso persistido y los próximos pasos exactos para continuar sin repetir trabajo.

**El resultado final debe ser una única plataforma Store Keeper, con diez categorías funcionales, identidad visual consistente, experiencia especializada por negocio, autenticación segura, aislamiento multitenant y arquitectura preparada para evolucionar hacia un SaaS comercial.**

No te limites a planificar. **Implementa, prueba, verifica y documenta.**