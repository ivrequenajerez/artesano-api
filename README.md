# Artesano API

Backend para una aplicación de gestión de obrador gastronómico: panadería, pastelería, cocina de producción o cualquier negocio donde fabricar, controlar materia prima, gestionar pedidos y calcular costes no debería depender de una hoja de cálculo con complejo de ERP.

El objetivo del sistema es claro: ayudar al dueño o encargado del obrador a saber qué tiene, qué puede producir, cuánto le cuesta producirlo y si está vendiendo con beneficio.

## Estado del proyecto

Proyecto en desarrollo.

Actualmente el foco está puesto en el núcleo funcional de producción:

* gestión de productos;
* recetas;
* ingredientes;
* materia prima;
* inventario;
* movimientos de stock;
* finalización de producción de pedidos;
* cálculo futuro de costes, merma, margen y rentabilidad.

La aplicación no intenta resolverlo todo desde el primer día. Primero debe responder bien a una pregunta básica, incómoda y bastante poco negociable:

> ¿Puedo producir este pedido con el stock que tengo y venderlo con beneficio?

## Visión funcional

La app está pensada para un obrador donde el trabajo real ocurre en eventos concretos:

1. llega materia prima;
2. se registra o actualiza el stock;
3. entra un pedido;
4. se produce lo necesario;
5. se marca la producción como finalizada;
6. se actualiza el stock;
7. se entrega el pedido;
8. se calcula el impacto económico.

La aplicación no debe pedir rituales administrativos innecesarios. Debe traducir hechos reales del obrador en movimientos de stock, costes y trazabilidad.

## Usuario principal

El usuario principal es el dueño o encargado del obrador.

Necesita:

* registrar stock;
* controlar materia prima;
* controlar productos terminados;
* recibir pedidos;
* finalizar producciones;
* actualizar inventario;
* controlar costes;
* calcular márgenes;
* evitar vender por debajo de coste;
* detectar faltantes;
* contactar proveedores con rapidez;
* revisar de dónde sale cada cálculo.

## Principios de diseño

### 1. El dominio manda

El sistema no se diseña pensando primero en tablas, endpoints o pantallas.

Primero se identifica qué ha ocurrido en el obrador. Después, la aplicación traduce ese hecho en cambios persistentes.

Ejemplo:

* hecho real: se finalizó la producción de un pedido;
* consecuencia de dominio: se consume materia prima;
* consecuencia de dominio: se genera producto terminado;
* consecuencia técnica: se registran movimientos de stock trazables.

La base de datos obedece. Como debe ser.

### 2. Trazabilidad antes que magia

La app puede automatizar cálculos, pero no puede inventarlos. Cada dato económico o movimiento de stock debe poder explicarse.

Para cada movimiento relevante se debe conservar:

* origen del dato;
* fecha;
* usuario responsable;
* proveedor, si aplica;
* precio aplicado;
* cantidad;
* unidad de medida;
* receta utilizada;
* merma aplicada;
* pedido relacionado;
* producción relacionada.

### 3. Producido no significa entregado

Una decisión importante del dominio:

* `PRODUCIDO` significa que la producción ha terminado;
* `ENTREGADO` significa que el producto salió hacia el cliente;
* `COBRADO` significa que se ha recibido el pago.

No son lo mismo. Mezclarlos sería cómodo durante cinco minutos y caro durante meses.

### 4. El stock cambia por eventos

Eventos principales detectados:

| Evento                 | Efecto sobre materia prima | Efecto sobre producto terminado |
| ---------------------- | -------------------------: | ------------------------------: |
| Materia prima recibida |                       Sube |                       No cambia |
| Producción finalizada  |                       Baja |                            Sube |
| Pedido entregado       |                  No cambia |                            Baja |

## Arquitectura

El proyecto sigue una orientación de arquitectura hexagonal, con separación entre dominio, aplicación e infraestructura.

La intención es mantener el núcleo de negocio aislado de detalles externos como REST, PostgreSQL, frameworks, colas, proveedores o interfaces futuras.

Estructura conceptual:

```txt
artesano-api/
├── sharedkernel/
│   └── domain/
│       ├── Dinero
│       ├── UnidadMedida
│       └── Porcentaje
│
├── producto/
│   └── domain/
│       ├── Producto
│       └── ProductoId
│
├── receta/
│   └── domain/
│       ├── Receta
│       └── IngredienteReceta
│
├── inventario/
│   └── domain/
│       ├── ItemInventario
│       └── MovimientoStock
│
├── produccion/
│   └── application/
│       └── FinalizarProduccionPedidoUseCase
│
└── app/
    └── Aplicación Spring Boot
```

## Módulos principales

### Shared Kernel

Contiene conceptos mínimos, estables y compartidos entre módulos.

Ejemplos:

* `Dinero`;
* `UnidadMedida`;
* `Porcentaje`.

Este módulo debe ser pequeño. Si empieza a convertirse en trastero arquitectónico, se detiene. Sin ceremonia.

### Producto

Representa los productos que el obrador vende o produce.

Ejemplos:

* pan de masa madre;
* croissant;
* tarta;
* empanada;
* focaccia;
* caja de bollería.

Responsabilidades:

* identificar productos;
* asociar productos a recetas;
* preparar cálculo futuro de costes, margen y rentabilidad;
* relacionar producto vendido con producto terminado en inventario.

### Receta

Representa la composición técnica de un producto.

Una receta contiene ingredientes y cantidades exactas.

Debe permitir calcular:

* consumo de materia prima;
* coste estimado;
* efecto de la merma;
* rendimiento;
* unidades producidas.

Ejemplo conceptual:

```txt
Receta: Pan de masa madre

Ingredientes:
- Harina: 1000 g
- Agua: 700 g
- Sal: 20 g
- Masa madre: 200 g

Merma estimada:
- 20 %
```

### Inventario

Gestiona el stock de materia prima y producto terminado.

El inventario no debería limitarse a guardar cantidades. Debe registrar movimientos.

Ejemplos de movimientos:

* entrada de harina por factura de proveedor;
* salida de harina por producción;
* entrada de panes terminados;
* salida de panes por entrega de pedido.

### Producción

Gestiona el proceso por el cual un pedido pasa a estar producido.

El caso de uso central es:

```txt
FinalizarProduccionPedidoUseCase
```

Este caso de uso:

* valida que el pedido existe;
* valida que el pedido no está ya producido;
* obtiene productos y cantidades;
* obtiene recetas asociadas;
* calcula ingredientes necesarios;
* comprueba stock disponible;
* descuenta materia prima;
* incrementa producto terminado;
* registra movimientos trazables;
* cambia el pedido a estado `PRODUCIDO`;
* emite el evento de dominio correspondiente.

## Caso de uso principal: finalizar producción de pedido

### Intención

Registrar que la producción necesaria para un pedido ha sido terminada.

No significa que el pedido haya sido entregado.
No significa que el pedido haya sido cobrado.
No significa que el pedido haya sido facturado.

Significa solo esto:

* la materia prima fue consumida;
* el producto terminado fue generado;
* el stock quedó actualizado y trazado.

### Comando de entrada

```txt
FinalizarProduccionPedidoCommand
- pedidoId
- fechaFinalizacion
- usuarioId
```

### Precondiciones

* El pedido existe.
* El pedido contiene productos y cantidades.
* Cada producto tiene una receta asociada.
* Cada receta tiene ingredientes y cantidades.
* Existe stock suficiente de materia prima.
* El pedido no está ya producido.

### Reglas de dominio

1. Un pedido solo puede pasar a `PRODUCIDO` una vez.
2. Finalizar producción consume materia prima según receta.
3. Finalizar producción incrementa stock de producto terminado.
4. La merma debe aplicarse cuando la receta la define.
5. Todo movimiento de stock debe ser trazable.
6. `PRODUCIDO` no significa `ENTREGADO`.
7. `ENTREGADO` no significa `COBRADO`.

### Resultado esperado

Si la operación es válida:

* se registra la producción;
* se descuentan ingredientes;
* se añaden productos terminados;
* el pedido queda en estado `PRODUCIDO`;
* se emite el evento `ProduccionPedidoFinalizada`.

Si la operación no es válida:

* no se modifica el stock;
* se devuelve un error de dominio;
* se informa qué ingrediente falta y en qué cantidad.

### Evento de dominio

```txt
ProduccionPedidoFinalizada
- pedidoId
- produccionId
- productosGenerados
- ingredientesConsumidos
- movimientosStock
- fecha
- usuarioId
```

## Puertos previstos

### Puerto de entrada

```txt
FinalizarProduccionPedidoUseCase
```

### Puertos de salida

```txt
PedidoRepository
RecetaRepository
StockRepository
ProduccionRepository
UnitOfWork / TransactionManager
DomainEventPublisher
```

## Adaptadores previstos

### Driving adapters

Interfaces que invocan la aplicación:

* REST Controller;
* app móvil;
* panel web;
* comando CLI de pruebas.

### Driven adapters

Implementaciones externas usadas por la aplicación:

* PostgreSQL;
* SQLite local;
* Supabase;
* sistema de eventos;
* logger de auditoría.

## Flujo funcional simplificado

```txt
1. Entra un pedido.
2. La app comprueba productos y cantidades.
3. El obrador produce lo necesario.
4. El encargado pulsa “Finalizar producción”.
5. La app calcula ingredientes consumidos.
6. La app valida stock suficiente.
7. La app descuenta materia prima.
8. La app incrementa producto terminado.
9. La app registra movimientos de stock.
10. El pedido pasa a PRODUCIDO.
```

## Flujo posterior: entrega

La entrega debe ser un flujo separado.

```txt
1. El pedido está PRODUCIDO.
2. El pedido se entrega al cliente.
3. La app descuenta producto terminado.
4. El pedido pasa a ENTREGADO.
5. Queda registrada la entrega.
```

Separar producción y entrega evita confundir fabricación con logística. Parece una sutileza; no lo es.

## Costes, rentabilidad y merma

La app debe evolucionar hacia el cálculo fiable de costes.

Variables relevantes:

* ingredientes;
* cantidades;
* precio de compra;
* proveedor;
* unidad de medida;
* merma;
* rendimiento;
* unidades producidas;
* precio de venta;
* margen;
* rentabilidad.

En productos de panadería o pastelería, la merma puede ser decisiva. Si se pierde un porcentaje de peso durante horneado, fermentación, manipulación o corte, ese coste debe entrar en el escandallo.

La regla económica es sencilla:

> Si la app no sabe cuánto cuesta producir algo, no debería fingir que sabe cuánto se gana vendiéndolo.

## Facturas y recepción de materia prima

Flujo previsto para registrar materia prima:

```txt
1. Llega mercancía del proveedor.
2. El encargado hace foto a la factura o documento.
3. La app procesa la información.
4. La app identifica ítems, cantidades y precios.
5. La app actualiza stock de materia prima.
6. La app conserva el origen del dato para auditoría.
```

Posibles mecanismos futuros:

* OCR sobre factura;
* lectura de factura electrónica estructurada;
* integración con proveedor;
* lectura de códigos internos;
* validación mediante QR, si el contenido disponible lo permite.

Punto abierto: validar qué información real puede obtenerse del QR de factura y qué parte requiere OCR o integración externa.

## Tecnologías

Stack previsto / actual del backend:

* Java;
* Spring Boot;
* Maven;
* PostgreSQL;
* Docker;
* arquitectura hexagonal;
* enfoque modular por contexto de dominio.

## Ejecución local

### Requisitos

* Java instalado.
* Maven instalado.
* Docker instalado.
* PostgreSQL local o levantado mediante Docker Compose.

### Levantar servicios de infraestructura

```bash
docker compose up -d
```

### Compilar el proyecto

```bash
mvn clean verify
```

### Ejecutar la aplicación

```bash
mvn spring-boot:run -pl app
```

> Ajustar el módulo de arranque si el nombre final cambia.

## Convenciones de diseño

### Entidades y Value Objects

El dominio debe favorecer objetos expresivos frente a tipos primitivos dispersos.

Preferible:

```txt
Dinero
UnidadMedida
Porcentaje
ProductoId
PedidoId
```

Antes que una colección interminable de `BigDecimal`, `String` y `Long` sin intención semántica.

### Errores de dominio

Los errores deben expresar reglas de negocio incumplidas.

Ejemplos:

```txt
PedidoYaProducido
StockInsuficiente
RecetaNoDefinida
ProductoSinReceta
UnidadMedidaIncompatible
```

### Transacciones

Finalizar producción debe ser atómico.

Si falta stock, falla todo.

No se debe permitir este estado grotesco:

* pedido marcado como producido;
* ingredientes no descontados;
* producto terminado no generado;
* movimientos incompletos.

La contabilidad del caos tiene mala prensa por motivos razonables.

## Roadmap inicial

### Fase 1: núcleo de dominio

* Crear `sharedkernel/domain`.
* Crear `producto/domain`.
* Crear `receta/domain`.
* Crear `inventario/domain`.
* Crear `produccion/application`.
* Implementar `FinalizarProduccionPedidoUseCase`.

### Fase 2: persistencia

* Repositorios.
* Adaptadores PostgreSQL.
* Transacciones.
* Migraciones.
* Tests de integración.

### Fase 3: API

* Endpoints REST.
* DTOs de entrada y salida.
* Validación.
* Manejo de errores.
* Documentación de API.

### Fase 4: costes

* Escandallos.
* Merma.
* Precio histórico de proveedor.
* Coste por receta.
* Margen.
* Rentabilidad.

### Fase 5: automatización de entrada de datos

* Lectura de facturas.
* OCR.
* Importación estructurada.
* Asociación con proveedores.
* Actualización asistida de stock.

## Tests esperados

Casos mínimos para `FinalizarProduccionPedidoUseCase`:

* finaliza producción correctamente;
* descuenta materia prima;
* incrementa producto terminado;
* registra movimientos de stock;
* emite evento de dominio;
* rechaza pedido inexistente;
* rechaza pedido ya producido;
* rechaza producto sin receta;
* rechaza stock insuficiente;
* no modifica stock si la operación falla.

## Glosario

### Obrador

Lugar donde se producen productos gastronómicos desde cero o mediante procesos de elaboración propios.

### Materia prima

Ingrediente o recurso usado para producir.

Ejemplos:

* harina;
* azúcar;
* mantequilla;
* huevos;
* chocolate;
* levadura.

### Producto terminado

Producto ya producido y disponible para entregar o vender.

### Receta

Definición técnica de cómo producir un producto, incluyendo ingredientes, cantidades, unidades, rendimiento y posible merma.

### Merma

Pérdida de materia prima o rendimiento durante el proceso productivo.

### Escandallo

Cálculo detallado del coste de producción de un producto a partir de ingredientes, cantidades, precios, rendimiento y merma.

### Movimiento de stock

Registro trazable de una entrada o salida de inventario.

### Producción finalizada

Evento que indica que se ha terminado de producir lo necesario para un pedido.

### Entrega

Evento posterior a la producción, en el que el producto terminado sale hacia el cliente.

## Filosofía del proyecto

Modelar el obrador como realmente funciona: entra materia prima, se transforma, se vende, se entrega y deja —o no deja— margen.

El software debe encargarse del ruido administrativo para que el usuario pueda tomar mejores decisiones: producir con stock suficiente, vender por encima de coste y entender de dónde sale cada número.
