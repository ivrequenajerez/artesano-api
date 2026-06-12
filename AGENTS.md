# AGENTS.md — Artesano API

## Contexto del proyecto

Este repositorio contiene una API Java/Spring Boot para una app de gestión de obrador gastronómico.

El núcleo funcional es controlar:
- producción;
- materia prima;
- producto terminado;
- recetas;
- stock;
- costes;
- trazabilidad.

La arquitectura sigue una separación inspirada en arquitectura hexagonal y DDD ligero.

## Estructura de módulos

- app: aplicación Spring Boot principal.
- sharedkernel: value objects compartidos estables.
- infrastructure-postgres: infraestructura relacionada con PostgreSQL.

## Reglas arquitectónicas

- `domain` contiene negocio puro.
- `application` contiene casos de uso, comandos y puertos.
- `adapter` contiene entrada/salida técnica: REST, persistencia, eventos, integraciones.
- El dominio no debe depender de Spring.
- No usar `@Component`, `@Service`, `@Repository`, `@Entity` en clases de dominio.
- No meter clases de dominio dentro de `com.artesano.api`.
- No introducir JPA hasta que se pida explícitamente.
- No crear controladores REST hasta que se pida explícitamente.
- No tocar `application-local.properties`.
- No subir secretos ni credenciales.
- Mantener `.gitignore` respetando configuración local.

## Estilo Java

Para favorecer la legibilidad y la mantenibilidad, es deseable que el estilo de código sea lo más consistente posible entre clases y entre proyectos.

Por ello, para todos los desarrollos nuevos, tenemos que intentar usar un estilo de código homogéneo y legible, para que cuando alguien ajeno a un trozo de código lo vea, le sea fácil entenderlo.

Idealmente se refactorizaría el código antiguo también para que tenga el mismo estilo, pero no es prioritario y se puede ir haciendo progresivamente cuando haya que irlo modificando.

Se puede configurar el editor IntelliJ IDEA para reformatear automáticamente el código (Code / Reformat Code, o Ctrl + Alt + L) y/o generar warnings cuando no se cumplan las reglas de estilo. Estos son los perfiles para importarlos:

Inspecciones (generan warnings): Tour10InspectionsIntelliJ.xml  → Settings / Editor / Code Style / Scheme / Import Scheme

Reglas de estilo (para reformateo): Tour10CodeStyleIntellij.xml  → Settings / Editor / Inspections / Profile /  Import Profile

Algunas reglas deseables:

- Ajustar la longitud de las líneas a 150 caracteres máximo.
- Cada línea debería tener una sola sentencia.
- Usar líneas en blanco para separar secciones, entre definición de clases e interfaces, entre métodos, para separar partes diferentes del flujo o de la lógica, etc
- Mantener el nivel de sangría/tabulado lo más fiel posible al nivel de anidamiento de la línea.
- Configurar las tabulaciones a 4 espacios.
- Al usar streams, separar cada operación del stream con un salto de línea.
- Usar Lombok cuando sea posible para la reducción de boilerplate.
- Reglas de nomenclatura:
    - Nombres de clase / interfaz / enum: PascalCase`public class ClienteSirio {}`
    - Nombre de método: camelCase`public void guardarReserva(){}`
    - Variables de instancia: camelCase`private int valorDevuelto;`
    - Variables estáticas: camelCase`private static int valorDevuelto;`
    - Variables locales: camelCase`int valorDevuelto;`
    - Parámetros de método: camelCase`public void guardarReserva(ReservaBean reserva){}`
    - Parámetros de Lambda: camelCase`Function<String, Integer> longitud = str > str.length;`
    - Constantes: SNAKE_CASE en mayúsculas`private static final int VALOR_DEFECTO;`
    - Valores de enum: PascalCase`public enum TipoReserva { Hotel, Actividad, Servicio}`
- Java moderno.
- Value objects inmutables cuando sea razonable.
- Validaciones en constructores/factorías.
- Mensajes de error en español.
- Evitar setters indiscriminados.
- Preferir métodos de intención de negocio:
    - `renombrar`
    - `cambiarPrecioVenta`
    - `activar`
    - `desactivar`

## Buenas prácticas a tomar en cuenta

### **¿Qué es la programación defensiva? ¿Por qué es importante?**

Cuando construyes una aplicación, estás preparándola para los peligros a los que se va a enfrentar?

La programación defensiva es una técnica que ayuda a los programadores a escribir programas que son más resistentes a bugs y a vulnerabilidades. Esta técnica es, en realidad, un conjunta de guías y hábitos de programación que mejorarán inmesurablemente la comprensibilidad, calidad y predecibilidad de tu código.

Es importante para los desarrolladores de software porque la programación defensiva puede ayudarles a evitar errores en el código, reducir el número de fallos en sus programas y hacerlos más seguros. Por eso es importante para el desarrollo de software.

Descuidar la seguridad del software puede acarrear graves complicaciones y el fracaso de la aplicación. Para muchas personas, la seguridad no suele ser una prioridad hasta que es demasiado tarde. Lo que quizá no sepan es que hay cosas sencillas que pueden hacer para crear software seguro desde el principio.

### **Comprensibilidad del código**

> “Cualquiera puede escribir código que un ordenador pueda entender. Un buen programador escribe código que los humanos pueden entender.” Martin Fowler
>

Hay muchos principios y buenas prácticas para mejorar la comprensión del código que podemos aplicar. Algunos de ellos son "Principio de responsabilidad única", "Separación de preocupaciones" y NTR (No Te Repitas). A continuación se presentan algunas consideraciones básicas que deben tenerse en cuenta para mejorar la comprensibilidad del código mientras se escribe:

- El código debe ser **fácil de leer**, con un tamaño razonable de clases y métodos.
- El código debe tener **una intención clara**. Cada pieza de código debe tener un propósito claro y fácil de entender para otros desarrolladores.
- El código debe ser **sencillo**. El código complejo es difícil de mantener.
- El código de estar bien pensado

### **Calidad del código**

La calidad del código es una medida de lo bien que está programada una aplicación. También mide lo bien que funciona una aplicación y lo bien que cumple los requisitos.

> *Una aplicación bellamente construida no es útil si no funciona y sigue sin serlo si funciona bien pero no cumple los requisitos.*
>

Existen varias herramientas (para revisión de código, pruebas unitarias, análisis estático de código, etc.) y técnicas disponibles para mejorar la calidad del código que deberían aplicarse desde el primer día en que se empieza a escribir la aplicación o, al menos, cuando se tiene la oportunidad de refactorizar el código.

### **Previsibilidad del código**

El mantra para construir código predecible consiste en preguntarse **"Qué pasaría si..."**. Pregúnteselo a su usuario, a su empresa o simplemente a usted mismo antes de programar ninguna lógica de la que espere un resultado.

### **¿De qué estamos defendiendo nuestro código?**

**Entrada de datos incorrecta**

Para que cualquier aplicación produzca resultados correctos, el usuario debe introducir los datos correctos. Defendemos nuestro código contra la entrada incorrecta añadiendo la validación adecuada de la entrada del usuario.

**Operaciones no válidas**

Para producir resultados válidos, debemos pasar datos precisos a nuestros métodos para que puedan realizar correctamente su operación. Defendemos nuestro código contra las operaciones no válidas comprobando los argumentos pasados a esos métodos y haciendo pruebas unitarias de las operaciones.

**Errores del sistema**

Cuando se trabaja con software o aplicaciones, siempre hay algo que puede salir mal, por ejemplo, el usuario pierde la conectividad mientras guarda los datos o la llamada api utilizada para obtener datos falla de repente. Para proteger nuestro código de estos contratiempos, añadimos comprobaciones y gestionamos las excepciones. Por ejemplo, comprobamos que la red esté disponible antes de guardar los datos y lanzamos una excepción si la red falla y guardamos los datos localmente hasta que la red esté disponible

**Futuros desarrolladores**

Puede que alguna vez hayas visto comentarios de otra persona en el código del tipo ***“Esto es magia negra”*** o ***"¡Esto es mejor no tocarlo!".*** Significa que el fragmento de código es complejo y puede causar problemas si se toca sin un conocimiento exhaustivo de sus propósitos y efectos secundarios. Si nuestro código no es claro y otro desarrollador no entiende nuestra intención, puede hacer suposiciones incorrectas acerca de ese código y hacer cambios inapropiados causando que nuestro código falle. Defendemos nuestro código frente a futuros desarrolladores escribiendo un código limpio que sea fácil de leer y comprender y disponiendo de un conjunto de pruebas unitarias para verificar que ningún cambio futuro afecte negativamente a la funcionalidad de la aplicación.

A veces puede parecer imposible escribir un buen código cuando nos enfrentamos a requisitos que cambian constantemente, problemas heredados, la presión del tiempo y un entorno que cambia con rapidez. Pero la programación defensiva puede ayudar.

## Comandos de verificación

Después de cambios relevantes, ejecutar:

```bash
mvn clean compile
```

Si se añaden tests:

```bash
mvn test
```

## Forma de trabajar

- Hacer cambios pequeños.
- Explicar brevemente qué se ha cambiado.
- No hacer commits salvo instrucción explícita.
- No reestructurar módulos sin pedir permiso.
- Si algo no compila, corregir la causa mínima.