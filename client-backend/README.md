# 🌐 CLIENT BACKEND v1.5.0 - Sistema Distribuido de Almacenamiento

[![Java Version](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-blue.svg)](https://maven.apache.org/)
[![Testing](https://img.shields.io/badge/Testing-JUnit%205-green.svg)](https://junit.org/junit5/)
[![RMI](https://img.shields.io/badge/RMI-Distributed-purple.svg)](https://docs.oracle.com/javase/tutorial/rmi/)

> **Cliente Backend para Sistema Distribuido de Almacenamiento**  
> Proporciona una interfaz de alto nivel para interactuar con múltiples nodos de almacenamiento distribuido, incluyendo replicación automática, tolerancia a fallos y monitoreo en tiempo real.

---

## 🚀 Características Principales

### ✨ Funcionalidades Core
- 📥 **Almacenamiento Distribuido** - Archivos distribuidos automáticamente entre múltiples nodos
- 📤 **Recuperación Inteligente** - Recuperación con tolerancia a fallos desde múltiples réplicas
- 🔄 **Replicación Automática** - Factor de replicación configurable para redundancia
- 🔍 **Verificación de Integridad** - Validación de checksums MD5 en tiempo real
- 📊 **Estadísticas Avanzadas** - Monitoreo completo del estado del sistema
- ⚡ **Operaciones Paralelas** - Procesamiento concurrente para mejor rendimiento

### 🧪 Módulo de Testing Removible
- 🎯 **Testing Interactivo** - Interfaz de consola para pruebas manuales
- 🤖 **Pruebas Automáticas** - Suite de 5 escenarios de testing automático
- 📋 **Reportes Detallados** - Generación y exportación de reportes de prueba
- 🗑️ **Completamente Removible** - No afecta el sistema principal en producción

### 🏗️ Arquitectura Avanzada
- 🌐 **Comunicación RMI** - Conexiones distribuidas entre nodos
- 🔧 **Balanceamiento de Carga** - Distribución inteligente de operaciones
- 💪 **Tolerancia a Fallos** - Funcionamiento con nodos parcialmente disponibles
- 📈 **Escalabilidad** - Soporte para múltiples nodos de almacenamiento

---

## 📦 Instalación y Configuración

### Requisitos Previos

```bash
# Java 17 o superior
java --version

# Maven 3.8 o superior
mvn --version

# Nodos de almacenamiento ejecutándose
# (Ver documentación de storage-nodes)
```

### Instalación Rápida

```bash
# 1. Clonar o navegar al proyecto
cd client-backend/

# 2. Compilar el proyecto
mvn clean compile

# 3. Ejecutar tests (opcional)
mvn test

# 4. Empaquetar
mvn package
```

### Configuración de Nodos

Por defecto, el sistema se conecta a 3 nodos de almacenamiento:

```java
// Configuración predeterminada
StorageNode1: localhost:2001
StorageNode2: localhost:2002  
StorageNode3: localhost:2003
```

Para configuración personalizada, modificar `DistributedStorageService.java`:

```java
private static final NodeConfig[] DEFAULT_NODES = {
    new NodeConfig("CustomNode1", "server1.domain.com", 2001),
    new NodeConfig("CustomNode2", "server2.domain.com", 2002),
    new NodeConfig("CustomNode3", "server3.domain.com", 2003)
};
```

---

## 🎮 Uso Rápido

### 1. Modo Línea de Comandos

```bash
# Almacenar archivo
java -jar client-backend-1.5.0.jar store /path/to/document.pdf

# Recuperar archivo
java -jar client-backend-1.5.0.jar retrieve 12345

# Ver estadísticas del sistema
java -jar client-backend-1.5.0.jar stats

# Verificar salud del sistema  
java -jar client-backend-1.5.0.jar health
```

### 2. Modo Programático

```java
import com.distribuidos.clientbackend.service.DistributedStorageService;
import com.distribuidos.clientbackend.model.DistributedFileResult;

// Inicializar servicio
DistributedStorageService service = new DistributedStorageService();

// Almacenar archivo
byte[] fileData = "Contenido del archivo".getBytes();
DistributedFileResult result = service.storeFile("documento.txt", fileData);

if (result.isSuccess()) {
    System.out.println("✅ Archivo almacenado con ID: " + result.getFileId());
    System.out.println("🔒 Checksum: " + result.getChecksum());
    System.out.println("🏠 Nodo principal: " + result.getPrimaryNode());
} else {
    System.out.println("❌ Error: " + result.getMessage());
}

// Recuperar archivo
DistributedFileResult file = service.retrieveFile(result.getFileId());
if (file.isSuccess()) {
    byte[] data = file.getFileData();
    // Usar los datos...
}

// Cerrar servicio
service.shutdown();
```

### 3. Testing Interactivo (Solo en Desarrollo)

```bash
# Ejecutar módulo de testing removible
java -cp target/classes com.distribuidos.clientbackend.testing.InteractiveTestingApplication
```

**Menú del Testing Interactivo:**
```
┌─────────────────────────────────────────────────────────┐
│                    MENÚ PRINCIPAL                      │
├─────────────────────────────────────────────────────────┤
│  1. 📥 Almacenar archivo                               │
│  2. 📤 Recuperar archivo                               │ 
│  3. 🗑️  Eliminar archivo                               │
│  4. 🔍 Verificar integridad                            │
│  5. 📊 Ver estadísticas del sistema                    │
│  6. 🤖 Ejecutar pruebas automáticas                    │
│  7. 📋 Mostrar reporte de pruebas                      │
│  8. 📁 Generar archivos de prueba                      │
│  9. 💾 Exportar reporte completo                       │
│  0. 🚪 Salir del módulo de testing                     │
└─────────────────────────────────────────────────────────┘
```

---

## 🔧 Compilación y Despliegue

### Perfiles de Maven

#### Desarrollo (Con Módulo de Testing)
```bash
# Compilar con testing incluido
mvn clean package -Pdevelopment

# El JAR incluirá el módulo de testing interactivo
```

#### Producción (Sin Módulo de Testing)
```bash
# Compilar sin testing - JAR más pequeño
mvn clean package -Pproduction

# El módulo de testing será excluido completamente
```

### Creación de JAR Ejecutable

```bash
# JAR con todas las dependencias
mvn assembly:single

# Resultado: client-backend-1.5.0-jar-with-dependencies.jar
```

### Docker (Opcional)

```dockerfile
FROM openjdk:17-jre-slim
COPY target/client-backend-1.5.0.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

---

## 📊 Monitoreo y Estadísticas

### Verificar Estado del Sistema

```java
SystemStatistics stats = service.getSystemStatistics();

System.out.println("🌐 Nodos activos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes());
System.out.println("📁 Archivos almacenados: " + stats.getTotalStoredFiles());
System.out.println("💾 Capacidad usada: " + stats.getFormattedUsedCapacity());
System.out.println("📈 Tasa de éxito: " + String.format("%.2f%%", stats.getSuccessRate()));
System.out.println("🎯 Salud del sistema: " + stats.getSystemHealth());
```

### Verificar Integridad de Archivos

```java
FileIntegrityReport report = service.verifyFileIntegrity(fileId);

System.out.println("✅ Integridad válida: " + report.isIntegrityValid());
System.out.println("📈 Porcentaje de integridad: " + String.format("%.2f%%", report.getIntegrityPercentage()));
System.out.println("🌐 Nodos verificados: " + report.getTotalNodes());
System.out.println("💡 Recomendación: " + report.getRecommendedAction());

if (!report.getCorruptedNodes().isEmpty()) {
    System.out.println("❌ Nodos corruptos: " + String.join(", ", report.getCorruptedNodes()));
}
```

---

## 🧪 Testing y Validación

### Tests Unitarios (JUnit 5)

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=DistributedStorageServiceTest

# Ver reporte detallado
mvn test -Dmaven.surefire.debug=true
```

**Suite de 12 Tests Incluida:**
- ✅ Verificación de estado inicial
- ✅ Almacenamiento básico de archivos
- ✅ Recuperación de archivos
- ✅ Verificación de integridad
- ✅ Eliminación de archivos
- ✅ Validación de parámetros
- ✅ Manejo de errores
- ✅ Archivos de diferentes tamaños
- ✅ Estadísticas del sistema
- ✅ Operaciones concurrentes
- ✅ Pruebas de rendimiento
- ✅ Estado final del sistema

### Pruebas Automáticas Integradas

El módulo de testing incluye 5 pruebas automáticas:

1. **📥 Almacenamiento Masivo** - Múltiples archivos en paralelo
2. **📤 Recuperación Masiva** - Validación de archivos almacenados  
3. **🔍 Verificación de Integridad** - Checksums y estado de nodos
4. **📊 Estadísticas del Sistema** - Métricas en tiempo real
5. **🗑️ Eliminación Selectiva** - Limpieza controlada

**Ejemplo de resultado:**
```
🏁 RESUMEN DE PRUEBAS AUTOMÁTICAS
═══════════════════════════════════
📊 Total de pruebas: 5
✅ Pruebas exitosas: 5  
❌ Pruebas fallidas: 0
📈 Tasa de éxito: 100.0%
🎉 ¡TODAS LAS PRUEBAS AUTOMÁTICAS EXITOSAS!
```

---

## 🚨 Troubleshooting

### Problemas Comunes

#### No se puede conectar a nodos
```bash
# Verificar que los nodos estén ejecutándose
ps aux | grep StorageNode

# Verificar puertos
netstat -tlnp | grep :200[1-3]

# Probar conectividad
telnet localhost 2001
```

#### Archivo no encontrado
```java
// Verificar estado de nodos
SystemStatistics stats = service.getSystemStatistics();
if (stats.getActiveNodes() == 0) {
    System.out.println("❌ No hay nodos activos disponibles");
}

// Verificar integridad del archivo
FileIntegrityReport report = service.verifyFileIntegrity(fileId);
System.out.println("Estado: " + report.getIntegrityStatus());
```

#### Rendimiento degradado
```java
// Verificar salud del sistema
SystemStatistics stats = service.getSystemStatistics();
if (stats.getSuccessRate() < 95.0) {
    System.out.println("⚠️ Tasa de éxito baja: " + stats.getSuccessRate() + "%");
}
if (stats.getInactiveNodes() > 0) {
    System.out.println("⚠️ Nodos inactivos: " + stats.getInactiveNodes());
}
```

### Diagnóstico del Sistema

```bash
# Script de verificación rápida
#!/bin/bash
echo "=== DIAGNÓSTICO RÁPIDO ==="

# Verificar Java
java -version

# Verificar nodos
for port in 2001 2002 2003; do
    if nc -z localhost $port; then
        echo "✅ Nodo puerto $port: ACTIVO"
    else  
        echo "❌ Nodo puerto $port: INACTIVO"
    fi
done

# Verificar memoria
free -h
```

---

## 📁 Estructura del Proyecto

```
client-backend/
├── pom.xml                                    # Configuración Maven con perfiles
├── README.md                                  # Este archivo
├── TECHNICAL_DOCUMENTATION.md                 # Documentación técnica completa
└── src/
    ├── main/java/com/distribuidos/clientbackend/
    │   ├── ClientBackendMain.java             # Punto de entrada principal
    │   ├── model/                             # Modelos de datos
    │   │   ├── DistributedFileResult.java     # Resultado de operaciones
    │   │   ├── FileIntegrityReport.java       # Reporte de integridad
    │   │   └── SystemStatistics.java          # Estadísticas del sistema
    │   ├── service/                           # Servicios principales
    │   │   └── DistributedStorageService.java # Servicio distribuido principal
    │   └── testing/                           # 🧪 MÓDULO REMOVIBLE
    │       └── InteractiveTestingApplication.java # Testing interactivo
    └── test/java/com/distribuidos/clientbackend/
        └── service/
            └── DistributedStorageServiceTest.java # Suite de tests JUnit
```

---

## 📋 API Reference

### Clase Principal: DistributedStorageService

```java
// Constructor
public DistributedStorageService()

// Operaciones principales
public DistributedFileResult storeFile(String fileName, byte[] fileData)
public DistributedFileResult retrieveFile(Long fileId)  
public DistributedFileResult deleteFile(Long fileId)

// Monitoreo
public FileIntegrityReport verifyFileIntegrity(Long fileId)
public SystemStatistics getSystemStatistics()

// Gestión
public void shutdown()
```

### Clase de Entrada: ClientBackendMain

```java
// API estática para uso programático
public static DistributedFileResult storeFile(String fileName, byte[] fileData)
public static DistributedFileResult retrieveFile(Long fileId)
public static DistributedFileResult deleteFile(Long fileId)
public static FileIntegrityReport verifyFileIntegrity(Long fileId)
public static SystemStatistics getSystemStatistics()
public static DistributedStorageService getStorageService()
```

### Comandos CLI Disponibles

| Comando | Descripción | Ejemplo |
|---------|-------------|---------|
| `store <archivo>` | Almacena archivo | `java -jar app.jar store document.pdf` |
| `retrieve <id>` | Recupera archivo | `java -jar app.jar retrieve 12345` |
| `delete <id>` | Elimina archivo | `java -jar app.jar delete 12345` |
| `verify <id>` | Verifica integridad | `java -jar app.jar verify 12345` |
| `stats` | Muestra estadísticas | `java -jar app.jar stats` |
| `health` | Verifica salud | `java -jar app.jar health` |

---

## 🎯 Casos de Uso

### Integración con Aplicación Web

```java
@RestController
public class FileController {
    private final DistributedStorageService storage = new DistributedStorageService();
    
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam MultipartFile file) {
        DistributedFileResult result = storage.storeFile(
            file.getOriginalFilename(), 
            file.getBytes()
        );
        
        return result.isSuccess() 
            ? ResponseEntity.ok("File ID: " + result.getFileId())
            : ResponseEntity.badRequest().body(result.getMessage());
    }
}
```

### Sistema de Backup

```java
public class BackupService {
    private final DistributedStorageService storage = new DistributedStorageService();
    
    public void backupDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        
        for (File file : directory.listFiles()) {
            byte[] data = Files.readAllBytes(file.toPath());
            DistributedFileResult result = storage.storeFile(file.getName(), data);
            
            if (result.isSuccess()) {
                System.out.println("✅ " + file.getName() + " respaldado (ID: " + result.getFileId() + ")");
            } else {
                System.out.println("❌ Error respaldando " + file.getName());
            }
        }
    }
}
```

### Monitoreo Automatizado

```java
@Scheduled(fixedRate = 300000) // Cada 5 minutos
public void monitorSystem() {
    SystemStatistics stats = ClientBackendMain.getSystemStatistics();
    
    if (stats.getActiveNodes() < stats.getTotalStorageNodes()) {
        alertService.sendAlert("Nodos inactivos detectados: " + stats.getInactiveNodes());
    }
    
    if (stats.getSuccessRate() < 95.0) {
        alertService.sendAlert("Tasa de éxito baja: " + stats.getSuccessRate() + "%");
    }
}
```

---

## 🔄 Workflow de Desarrollo

### Flujo de Trabajo Recomendado

1. **Desarrollo con Testing**
   ```bash
   mvn clean compile -Pdevelopment
   # Usar el módulo de testing interactivo para validar funcionalidades
   ```

2. **Testing Automatizado**
   ```bash
   mvn test
   # Ejecutar la suite completa de tests unitarios
   ```

3. **Validación Manual**
   ```bash
   java -cp target/classes com.distribuidos.clientbackend.testing.InteractiveTestingApplication
   # Usar las pruebas automáticas del módulo de testing
   ```

4. **Compilación para Producción**
   ```bash
   mvn clean package -Pproduction
   # JAR optimizado sin módulo de testing
   ```

5. **Despliegue**
   ```bash
   java -jar client-backend-1.5.0.jar
   # Sistema listo para uso programático o CLI
   ```

---

## 📈 Métricas y Rendimiento

### Benchmarks Esperados

| Operación | Archivo Pequeño | Archivo Mediano | Archivo Grande |
|-----------|----------------|-----------------|----------------|
| Almacenamiento | <100ms | <500ms | <2000ms |
| Recuperación | <50ms | <200ms | <1000ms |
| Verificación | <200ms | <800ms | <3000ms |
| Eliminación | <100ms | <300ms | <1000ms |

### Factores de Rendimiento

- **Nodos Activos**: Mayor número = mayor redundancia
- **Factor de Replicación**: Configurable (defecto: 2)
- **Latencia de Red**: Conexiones rápidas mejoran rendimiento
- **Tamaño de Archivo**: Archivos grandes requieren más tiempo
- **Carga Concurrente**: Operaciones simultáneas compiten por recursos

---

## 🛡️ Seguridad y Integridad

### Verificación de Integridad

- **Checksums MD5**: Validación automática de integridad
- **Verificación Multi-Nodo**: Comparación entre réplicas
- **Detección de Corrupción**: Identificación automática de datos corruptos
- **Reportes Detallados**: Información completa sobre estado de archivos

### Tolerancia a Fallos

- **Redundancia**: Factor de replicación configurable
- **Recuperación Automática**: Operación continúa con nodos disponibles
- **Reconexión**: Intentos automáticos de reconexión a nodos
- **Balanceamiento**: Distribución inteligente de carga

---

## 📞 Soporte y Documentación

### Recursos Disponibles

- 📖 **[TECHNICAL_DOCUMENTATION.md](TECHNICAL_DOCUMENTATION.md)** - Documentación técnica completa
- 🧪 **Testing Interactivo** - Módulo de testing con ejemplos prácticos
- ✅ **Suite de Tests** - 12 tests unitarios comprehensivos
- 🎯 **Casos de Uso** - Ejemplos de integración incluidos

### Información del Proyecto

- **Versión**: 1.5.0
- **Compatibilidad**: Java 17+
- **Arquitectura**: Sistema Distribuido con RMI
- **Licencia**: Academic Use

---

## 🎉 ¡Empezar Ahora!

### Inicio Rápido en 3 Pasos

1. **Compilar**
   ```bash
   mvn clean package
   ```

2. **Probar**
   ```bash
   java -cp target/classes com.distribuidos.clientbackend.testing.InteractiveTestingApplication
   ```

3. **Usar**
   ```bash
   java -jar target/client-backend-1.5.0.jar stats
   ```

### ¿Necesitas Ayuda?

- 📖 Lee la [documentación técnica completa](TECHNICAL_DOCUMENTATION.md)
- 🧪 Usa el módulo de testing interactivo
- ✅ Ejecuta los tests unitarios: `mvn test`
- 🔍 Revisa los casos de uso en la documentación

---

*Client Backend v1.5.0 - Sistema Distribuido de Almacenamiento*  
*Diseñado para ser robusto, escalable y fácil de usar* 🚀