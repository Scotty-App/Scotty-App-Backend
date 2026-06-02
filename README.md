# 🖥️ Scotty App — Backend de Administración

> Panel de administración de escritorio para **Scotty App**, plataforma gamificada para repasar y afianzar lenguajes de programación. Desarrollado en **JavaFX** con conexión directa a **MariaDB**.

---

## 📁 Estructura del proyecto

```
Scotty-App-Backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── scottyapp/
│       │       ├── Main/
│       │       │   ├── Launcher.java             → Punto de entrada del JAR
│       │       │   ├── MainApplication.java      → Arranque de JavaFX y gestión de escenas
│       │       │   └── MainController.java       → Controlador del menú principal
│       │       ├── Item/
│       │       │   ├── Item.java                 → Modelo de producto
│       │       │   ├── ItemController.java       → Controlador CRUD de productos
│       │       │   └── MantenimientoItem.java    → Acceso a datos de la tabla PRODUCT
│       │       ├── Order/
│       │       │   ├── Order.java                → Modelo de pedido
│       │       │   ├── OrderController.java      → Controlador CRUD de pedidos
│       │       │   └── MantenimientoOrder.java   → Acceso a datos de la tabla ORDER
│       │       ├── OrderDetails/
│       │       │   ├── OrderDetails.java         → Modelo de línea de pedido
│       │       │   ├── OrderDetailsController.java → Controlador CRUD de líneas
│       │       │   └── MantenimientoOrderDetails.java → Acceso a datos de ORDER_DETAILS
│       │       └── Usuario/
│       │           ├── Usuario.java              → Modelo de usuario
│       │           ├── UsuarioController.java    → Controlador CRUD de usuarios
│       │           └── MantenimientoUsuario.java → Acceso a datos de la tabla USER
│       └── resources/
│           └── scottyapp/
│               ├── main-view.fxml                → Menú principal
│               ├── item-view.fxml                → Pantalla de productos
│               ├── order-view.fxml               → Pantalla de pedidos
│               ├── orderDetails-view.fxml        → Pantalla de líneas de pedido
│               ├── usuario-view.fxml             → Pantalla de usuarios
│               └── style.css                     → Estilos globales
├── pom.xml
└── README.md
```

---

## 📚 Módulos del panel

| Módulo | Descripción |
|---|---|
| **Usuarios** | CRUD completo. Roles ALUMNI y ADMINISTRATOR. Validación de email, teléfono y rol antes de guardar. |
| **Productos** | CRUD completo. Categorías con check constraint. Validación de precio y stock. |
| **Pedidos** | Creación con cliente y fecha. Edición de estado (PENDING / PROCESSED / CANCELLED). Contadores en tiempo real. Filtro rápido por estado. |
| **Líneas de pedido** | Gestión del contenido de cada pedido. Añadir productos, editar cantidades, eliminar líneas. Total recalculado automáticamente. Cabecera con datos completos del cliente. |

---

## 🔄 Flujo de navegación

```
Menú principal
├── Usuarios      → Lista y gestión de cuentas
├── Productos     → Catálogo e inventario
└── Pedidos       → Lista de pedidos
                      └── Ver detalles → Líneas del pedido
```

Al crear un pedido nuevo, la app navega automáticamente a la pantalla de líneas para añadir productos.

---

## 🛠️ Tecnologías

- **Lenguaje:** Java 17
- **Interfaz gráfica:** JavaFX 17 + FXML + Scene Builder
- **Base de datos:** MariaDB (vía XAMPP, puerto 3307)
- **Conector:** MariaDB JDBC Driver
- **Gestión de dependencias:** Maven
- **Control de versiones:** Git + GitHub (SourceTree)

---

## ⚙️ Configuración de la base de datos

La conexión está definida en cada clase `Mantenimiento*.java`. Por defecto apunta a:

```
Host:     localhost:3307
Base de datos: bd_scottyapp
Usuario:  root
Contraseña: (vacía)
```

Para cambiar la conexión, edita los valores en cualquiera de los cuatro archivos `Mantenimiento*.java`. En una versión futura se centralizaría en un único archivo de configuración.

---

## 🚀 Cómo ejecutar el proyecto

1. Clona el repositorio:

```bash
git clone https://github.com/Scotty-App/Scotty-App-Backend
```

2. Asegúrate de tener XAMPP con MariaDB activo en el puerto `3307`.

3. Importa el script DDL de la base de datos desde el repositorio de base de datos:

```
https://github.com/Scotty-App/Scotty-App-Backend/tree/main/sql/ddl
```

4. Abre el proyecto en **IntelliJ IDEA**.

5. Ejecuta la clase `Launcher.java` como punto de entrada.

---

## ⚠️ Limitaciones conocidas y deuda técnica

| Limitación | Descripción |
|---|---|
| **SQL Injection** | Las queries se construyen por concatenación de strings. La solución correcta es `PreparedStatement`, no implementada por alcance del proyecto. |
| **Contraseñas en texto plano** | Las contraseñas se almacenan sin cifrado en la base de datos. |
| **Conexión por pantalla** | Cada módulo abre su propia conexión JDBC. No hay pool de conexiones ni cierre explícito. |
| **Sin transacciones** | Las operaciones compuestas (insertar pedido + líneas) no usan `commit/rollback`. |
| **Un usuario activo** | No hay sistema de sesión ni control de acceso dentro del panel. |

---

## 📎 Repositorios relacionados

| Repositorio | Descripción |
|---|---|
| [Scotty-App-Frontend](https://github.com/Scotty-App/Scotty-App-Frontend) | Aplicación web con HTML, CSS y JavaScript |
| [Scotty-App-Backend (BBDD)](https://github.com/Scotty-App/Scotty-App-Backend) | Diseño e implementación de la base de datos |

---

*Proyecto Scotty App · IES Mutxamel · DAW 1º · 2025–2026*
