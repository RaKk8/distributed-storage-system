# Guía de Pruebas del Sistema Distribuido

## Funcionalidades Implementadas y Probadas

### ✅ Modelo de Datos
- **Entidades**: User, Directory, File, Node, FileReplica, Permission
- **Relaciones**: Configuradas con JPA/Hibernate
- **Base de datos**: H2 en memoria (preparada para MySQL)

### ✅ Servicio de Autenticación
- **Usuarios hardcodeados**: admin, juan, maria
- **Generación de tokens**: Sistema preparado para migrar a JWT
- **Validación de tokens**: Con expiración automática
- **Gestión de sesiones**: Invalidación y renovación de tokens

### ✅ Servidor de Base de Datos TCP
- **Comunicación TCP**: Puerto 9001
- **Protocolo**: Serialización de objetos Java
- **Operaciones CRUD**: Save, Find, Update, Delete
- **Pool de conexiones**: HikariCP
- **Manejo concurrente**: ThreadPool para múltiples clientes

### ✅ Comunicación TCP
- **Mensajes**: DatabaseMessage y DatabaseResponse
- **Serialización**: Objetos Java serializables
- **Manejo de errores**: Respuestas estructuradas
- **Cliente de prueba**: DatabaseClient

## Instrucciones de Prueba

### 1. Compilación del Proyecto
```bash
# Ejecutar desde el directorio raíz
scripts\01-compile.bat
```

### 2. Prueba del Servicio de Autenticación
```bash
# Prueba usuarios hardcodeados y generación de tokens
scripts\03-test-auth.bat
```

**Resultado esperado:**
- ✅ Autenticación exitosa para admin, juan, maria
- ✅ Generación de tokens únicos
- ✅ Validación de tokens
- ✅ Invalidación de tokens
- ✅ Búsqueda de usuarios por username e ID

### 3. Prueba del Servidor de Base de Datos

#### Paso 1: Iniciar el servidor
```bash
# En una ventana de comando separada
scripts\02-start-database.bat
```

**Resultado esperado:**
```
Servidor de base de datos iniciado en puerto 9001
Esperando conexiones de clientes...
```

#### Paso 2: Ejecutar cliente de prueba
```bash
# En otra ventana de comando
scripts\04-test-database.bat
```

**Resultado esperado:**
- ✅ Conexión exitosa al servidor
- ✅ Consulta de todos los usuarios
- ✅ Búsqueda de usuario por ID
- ✅ Consulta personalizada
- ✅ Verificación de estructura de BD

### 4. Pruebas Completas
```bash
# Ejecuta todas las pruebas automáticamente
scripts\05-run-all-tests.bat
```

## Funcionalidades Pendientes de Implementar

### 🔄 Nodos de Almacenamiento (RMI)
- Implementación de 3 nodos con RMI
- Comunicación con servidor de aplicación
- Almacenamiento físico de archivos

### 🔄 Servidor de Aplicación (Spring Boot + SOAP)
- APIs SOAP/HTTPS para cliente
- Coordinación con nodos RMI
- Comunicación TCP con base de datos
- Lógica de replicación

### 🔄 Cliente Backend (SOAP)
- Interfaz SOAP para operaciones de archivos
- Comunicación HTTPS con servidor

### 🔄 Replicación y Redundancia
- Distribución automática en 3 nodos
- Verificación de integridad
- Re-replicación en caso de fallo

## Arquitectura de Comunicación

```
[Cliente Backend] ←--SOAP/HTTPS--> [Servidor de Aplicación]
                                         ↓
                                    RMI ↓ ↑ TCP
                                         ↓
[Nodo 1] [Nodo 2] [Nodo 3]         [Servidor BD]
```

## Próximos Pasos

1. **Implementar nodos RMI**: Crear 3 nodos de almacenamiento
2. **Servidor de aplicación**: Spring Boot con SOAP
3. **Cliente backend**: Interfaz SOAP
4. **Integración completa**: Conectar todos los componentes
5. **Pruebas de extremo a extremo**: Subir/descargar archivos

## Comandos Útiles

### Desarrollo
```bash
# Compilar solo un módulo
mvn compile -pl shared
mvn compile -pl database-server

# Ejecutar directamente
mvn exec:java -pl database-server -Dexec.mainClass="com.distribuidos.database.DatabaseServer"
```

### Depuración
```bash
# Ver logs detallados
mvn exec:java -pl database-server -Dexec.mainClass="com.distribuidos.database.DatabaseServer" -Dlogging.level.com.distribuidos=DEBUG
```

### Verificación
```bash
# Verificar puerto en uso
netstat -an | find "9001"

# Verificar procesos Java
jps -l
```