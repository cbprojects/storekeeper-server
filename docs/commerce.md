# Operación comercial inicial

Prefijo `/api/v1/businesses/{businessId}/commerce`. Todas las rutas requieren
sesión, pertenencia y módulos efectivos; las escrituras requieren CSRF.

| Método / ruta | Datos |
| --- | --- |
| GET /products | Catálogo del negocio (máximo 500) |
| POST /products | sku, name, type=PRODUCT/SERVICE, price decimal, image opcional |
| PUT /products/{id}/image | image como data URL PNG/JPEG/WebP, máximo 200 KB; null quita la imagen |
| POST /products/{id}/stock | quantity entera con signo, reason |
| GET /movements | Historial (máximo 500) |
| GET /sales | Ventas (máximo 500) |
| POST /sales | lines:[{productId,quantity}], taxPercent, discount |
| POST /sales/{id}/payments | amount decimal, method=CASH/TRANSFER/CARD_RECORDED/OTHER |
| POST /sales/{id}/return | reason; devolución completa |
| GET /report?from=...&to=... | Instantes ISO, inicio incluido y fin excluido |

Stock, venta, pago y devolución requieren Idempotency-Key. Cada operación
persiste su resultado dentro de la misma transacción que sus cambios. La misma
clave con datos distintos se rechaza. Una venta obtiene precios del servidor,
calcula descuentos antes del impuesto y redondea HALF_UP a dos decimales con
BigDecimal. No acepta precios ni totales del cliente.

Los productos físicos requieren INVENTORY, existencias suficientes y descuento
atómico; servicios no tienen stock. Si una línea falla, toda la venta se
revierte. Pagos parciales no pueden superar el saldo. No se procesan tarjetas,
transferencias ni reembolsos externos: se registran operaciones ya realizadas.
La devolución completa registra el importe a devolver y repone existencias una
sola vez. No hay devolución parcial en esta entrega.

Mongo requiere replica set para transacciones. Compose local configura rs0 de
un nodo y conserva el volumen anterior; la configuración no equivale a alta
disponibilidad. No se ha aplicado sobre datos de producción. El entorno de
pruebas usa otra red, otra base y almacenamiento temporal. Una instalación
standalone externa debe migrar su topología antes de usar comercio.

El reporte selecciona ventas por createdAt y muestra su total y saldo pagado
actual, excluyendo ventas completamente devueltas. No es un estado de flujo de
efectivo por fecha de pago ni un reporte fiscal. El formulario actual de fechas
usa límites UTC; adaptar límites al timezone empresarial sigue pendiente.

La UI permite una línea por venta; la API admite hasta 100. SKU es único por
negocio. Directorios de proveedores y empleados administrativos cuentan con
alta, edición, búsqueda local, estado activo/inactivo y control de versión bajo
`/api/v1/businesses/{id}/directory/suppliers` y `/employees` (esta última también
bajo directory). Empleados no son cuentas de acceso. Categorías de productos,
variantes, edición del catálogo, compras, caja, facturación y exportación aún
no están migrados al núcleo nuevo.
Los comprobantes heredados no son facturas fiscales certificadas.


## Autorización de devoluciones

Cada POST /businesses/{businessId}/commerce/sales/{saleId}/return requiere
reason, email y password en el mismo cuerpo, además de sesión, CSRF e
Idempotency-Key. La cuenta debe ser la misma de la sesión y estar verificada;
una cuenta distinta, aunque tenga contraseña válida, no autoriza la operación.
Se comprueba el acceso al negocio antes de verificar las credenciales. El límite
de reautorización es de 10 intentos por cuenta por ventana de 15 minutos,
independiente del límite de login. Los errores de credenciales devuelven 403
para conservar la sesión; no modifican la venta, pagos ni inventario.

La interfaz usa un diálogo modal con correo, contraseña y motivo. Cancelar o
Escape no envían la operación. La contraseña se vacía tras cada intento y al
cerrar; no entra en cachés de idempotencia ni almacenamiento del navegador.
El cuerpo de autorización oculta la contraseña al serializar o representar
el DTO como texto. El despliegue de producción debe usar HTTPS.
