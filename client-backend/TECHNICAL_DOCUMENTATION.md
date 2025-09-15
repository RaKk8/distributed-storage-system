# CLIENT BACKEND v1.5.0 - DOCUMENTACIÓN TÉCNICA

## 📋 Tabla de Contenidos
1. [Descripción General](#descripción-general)
2. [Arquitectura del Sistema](#arquitectura-del-sistema)
3. [Componentes Principales](#componentes-principales)
4. [Módulo de Testing Removible](#módulo-de-testing-removible)
5. [APIs y Interfaces](#apis-y-interfaces)
6. [Configuración y Despliegue](#configuración-y-despliegue)
7. [Casos de Uso](#casos-de-uso)
8. [Pruebas y Validación](#pruebas-y-validación)
9. [Troubleshooting](#troubleshooting)
10. [Apéndices](#apéndices)

---

## 📖 Descripción General

El **Client Backend v1.5.0** es el componente principal del sistema distribuido de almacenamiento que proporciona una interfaz de alto nivel para interactuar con múltiples nodos de almacenamiento distribuido. Este sistema está diseñado para ofrecer:

- **Almacenamiento distribuido** con replicación automática
- **Tolerancia a fallos** mediante múltiples nodos
- **Balanceamiento de carga** automático
- **Verificación de integridad** de datos
- **Monitoreo en tiempo real** del estado del sistema
- **Módulo de testing removible** para validación sin impacto en producción

### 🎯 Características Principales

| Característica | Descripción |
|---|---|
| **Distribución Transparente** | Los archivos se distribuyen automáticamente entre múltiples nodos |
| **Replicación Automática** | Factor de replicación configurable para redundancia |
| **Recuperación ante Fallos** | Continúa operando aunque algunos nodos fallen |
| **Integridad de Datos** | Verificación mediante checksums MD5 |
| **API Programática** | Interfaces Java para integración con otras aplicaciones |
| **Testing Interactivo** | Módulo removible para pruebas exhaustivas |
| **Monitoreo Avanzado** | Estadísticas detalladas del sistema en tiempo real |

---

## 🏗️ Arquitectura del Sistema

### Diagrama de Arquitectura

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Cliente GUI   │    │ Aplicación Web  │    │ Otras Aplicac.  │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │     CLIENT BACKEND       │
                    │        v1.5.0           │
                    └─────────────┬─────────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                  │                  │
    ┌─────────▼─────────┐ ┌──────▼──────┐ ┌─────────▼─────────┐
    │   Storage Node 1  │ │ Storage N.2 │ │   Storage Node 3  │
    │   (Puerto 2001)   │ │ (Puerto 2002│ │   (Puerto 2003)   │
    └───────────────────┘ └─────────────┘ └───────────────────┘
```

### Componentes de la Arquitectura

#### 1. **Client Backend (Core)**
- **DistributedStorageService**: Servicio principal que orquesta todas las operaciones
- **Modelos de Datos**: Representación de resultados y reportes
- **APIs Programáticas**: Interfaces para integración externa

#### 2. **Capa de Comunicación RMI**
- Conexiones persistentes a múltiples nodos de almacenamiento
- Pool de conexiones con reconexión automática
- Balanceamiento de carga round-robin

#### 3. **Nodos de Almacenamiento Distribuido**
- Múltiples instancias independientes
- Comunicación mediante RMI
- Almacenamiento local con metadatos

#### 4. **Módulo de Testing (Removible)**
- Interfaz interactiva de consola
- Pruebas automatizadas
- Completamente independiente del core

---

## 🔧 Componentes Principales

### 1. DistributedStorageService

**Ubicación**: `com.distribuidos.clientbackend.service.DistributedStorageService`

#### Responsabilidades:
- Gestión de conexiones a nodos de almacenamiento
- Distribución y replicación de archivos
- Balanceamiento de carga entre nodos
- Monitoreo de salud del sistema
- Verificación de integridad de datos

#### Métodos Principales:

```java
// Almacenar archivo en el sistema distribuido
public DistributedFileResult storeFile(String fileName, byte[] fileData)

// Recuperar archivo por ID
public DistributedFileResult retrieveFile(Long fileId)

// Eliminar archivo del sistema
public DistributedFileResult deleteFile(Long fileId)

// Verificar integridad de archivo
public FileIntegrityReport verifyFileIntegrity(Long fileId)

// Obtener estadísticas del sistema
public SystemStatistics getSystemStatistics()

// Cerrar servicio y liberar recursos
public void shutdown()
```

#### Configuración de Nodos:

```java
// Configuración por defecto de nodos
private static final NodeConfig[] DEFAULT_NODES = {
    new NodeConfig("StorageNode1", "localhost", 2001),
    new NodeConfig("StorageNode2", "localhost", 2002),
    new NodeConfig("StorageNode3", "localhost", 2003)
};
```

### 2. Modelos de Datos

#### DistributedFileResult

Representa el resultado de operaciones sobre archivos en el sistema distribuido.

```java
public class DistributedFileResult {
    private boolean success;
    private String message;
    private Long fileId;
    private String fileName;
    private long fileSizeBytes;
    private String checksum;
    private String primaryNode;
    private int replicationFactor;
    private List<String> replicatedNodes;
    // ... métodos y getters
}
```

#### FileIntegrityReport

Proporciona información detallada sobre la integridad de archivos.

```java
public class FileIntegrityReport {
    private Long fileId;
    private String fileName;
    private boolean integrityValid;
    private double integrityPercentage;
    private String integrityStatus;
    private int totalNodes;
    private int validNodeCount;
    private List<String> corruptedNodes;
    private List<String> unavailableNodes;
    private String recommendedAction;
    // ... métodos y getters
}
```

#### SystemStatistics

Estadísticas completas del sistema distribuido.

```java
public class SystemStatistics {
    private int totalStorageNodes;
    private int activeNodes;
    private int inactiveNodes;
    private long totalStoredFiles;
    private long totalCapacityBytes;
    private long usedCapacityBytes;
    private long availableCapacityBytes;
    private long totalOperations;
    private long successfulOperations;
    private long failedOperations;
    private String systemHealth;
    private Map<String, NodeStatistics> nodeStatistics;
    // ... métodos y getters
}
```

### 3. ClientBackendMain

**Ubicación**: `com.distribuidos.clientbackend.ClientBackendMain`

#### Funcionalidades:
- Punto de entrada principal del sistema
- Interfaz de línea de comandos
- API programática para integración
- Modo standby para uso como servicio

#### Comandos Disponibles:

```bash
# Almacenar archivo
java ClientBackendMain store /path/to/file.txt

# Recuperar archivo
java ClientBackendMain retrieve 12345

# Eliminar archivo
java ClientBackendMain delete 12345

# Verificar integridad
java ClientBackendMain verify 12345

# Ver estadísticas
java ClientBackendMain stats

# Verificar salud del sistema
java ClientBackendMain health
```

#### API Programática:

```java
// Inicializar servicio
ClientBackendMain.main(new String[]{});

// Usar APIs programáticas
DistributedFileResult result = ClientBackendMain.storeFile("test.txt", data);
DistributedFileResult file = ClientBackendMain.retrieveFile(fileId);
SystemStatistics stats = ClientBackendMain.getSystemStatistics();
```

---

## 🧪 Módulo de Testing Removible

### Diseño del Módulo

El módulo de testing está diseñado para ser **completamente removible** sin afectar el funcionamiento del sistema principal. Esto se logra mediante:

#### 1. **Separación por Paquetes**
```
com.distribuidos.clientbackend.testing/
├── InteractiveTestingApplication.java
└── [otros archivos de testing]
```

#### 2. **Configuración Maven**

**Perfiles de Compilación:**

```xml
<!-- Perfil de Desarrollo (incluye testing) -->
<profile>
    <id>development</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <!-- Incluye todos los paquetes -->
</profile>

<!-- Perfil de Producción (excluye testing) -->
<profile>
    <id>production</id>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>**/testing/**</exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

#### 3. **Compilación para Producción**

```bash
# Compilar sin módulo de testing
mvn clean package -Pproduction

# Compilar con módulo de testing (desarrollo)
mvn clean package -Pdevelopment
```

### Funcionalidades del Módulo de Testing

#### InteractiveTestingApplication

**Ubicación**: `com.distribuidos.clientbackend.testing.InteractiveTestingApplication`

##### Características:
- **Interfaz de consola interactiva** usando JLine
- **Pruebas manuales** de todas las operaciones
- **Pruebas automáticas** con múltiples escenarios
- **Generación de reportes** detallados
- **Exportación de resultados** a archivos

##### Menú Principal:

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

##### Pruebas Automáticas:

El módulo incluye una suite de **5 pruebas automáticas**:

1. **Almacenamiento masivo**: Múltiples archivos en paralelo
2. **Recuperación masiva**: Verificación de todos los archivos almacenados
3. **Verificación de integridad**: Validación de checksums
4. **Estadísticas del sistema**: Obtención de métricas en tiempo real
5. **Eliminación selectiva**: Limpieza controlada de archivos de prueba

##### Ejecutar el Módulo de Testing:

```bash
# Ejecutar módulo de testing interactivo
java -cp client-backend.jar com.distribuidos.clientbackend.testing.InteractiveTestingApplication
```

---

## 🔌 APIs y Interfaces

### API Principal

#### Almacenamiento de Archivos

```java
/**
 * Almacena un archivo en el sistema distribuido
 * @param fileName Nombre del archivo
 * @param fileData Contenido del archivo en bytes
 * @return Resultado de la operación con metadatos
 */
public DistributedFileResult storeFile(String fileName, byte[] fileData)
```

**Ejemplo de uso:**
```java
DistributedStorageService service = new DistributedStorageService();
byte[] data = "Contenido del archivo".getBytes();
DistributedFileResult result = service.storeFile("documento.txt", data);

if (result.isSuccess()) {
    System.out.println("Archivo almacenado con ID: " + result.getFileId());
    System.out.println("Checksum: " + result.getChecksum());
    System.out.println("Replicado en " + result.getReplicationFactor() + " nodos");
} else {
    System.out.println("Error: " + result.getMessage());
}
```

#### Recuperación de Archivos

```java
/**
 * Recupera un archivo del sistema distribuido
 * @param fileId ID único del archivo
 * @return Resultado con el archivo y metadatos
 */
public DistributedFileResult retrieveFile(Long fileId)
```

**Ejemplo de uso:**
```java
DistributedFileResult result = service.retrieveFile(12345L);

if (result.isSuccess()) {
    byte[] fileData = result.getFileData();
    String fileName = result.getFileName();
    // Usar los datos del archivo...
} else {
    System.out.println("Archivo no encontrado: " + result.getMessage());
}
```

#### Verificación de Integridad

```java
/**
 * Verifica la integridad de un archivo en todos los nodos
 * @param fileId ID del archivo a verificar
 * @return Reporte detallado de integridad
 */
public FileIntegrityReport verifyFileIntegrity(Long fileId)
```

**Ejemplo de uso:**
```java
FileIntegrityReport report = service.verifyFileIntegrity(12345L);

System.out.println("Integridad: " + report.getIntegrityPercentage() + "%");
System.out.println("Estado: " + report.getIntegrityStatus());
System.out.println("Recomendación: " + report.getRecommendedAction());

if (!report.isIntegrityValid()) {
    System.out.println("Nodos corruptos: " + report.getCorruptedNodes());
}
```

### API de Estadísticas

```java
/**
 * Obtiene estadísticas completas del sistema
 * @return Estadísticas detalladas de todos los nodos
 */
public SystemStatistics getSystemStatistics()
```

**Ejemplo de análisis:**
```java
SystemStatistics stats = service.getSystemStatistics();

// Información general
System.out.println("Nodos activos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes());
System.out.println("Archivos almacenados: " + stats.getTotalStoredFiles());
System.out.println("Capacidad usada: " + stats.getFormattedUsedCapacity());
System.out.println("Tasa de éxito: " + stats.getSuccessRate() + "%");

// Análisis por nodo
for (SystemStatistics.NodeStatistics nodeStat : stats.getNodeStatistics().values()) {
    System.out.println("Nodo " + nodeStat.getNodeId() + ": " + 
                      nodeStat.getStatus() + " - " + 
                      nodeStat.getFilesStored() + " archivos");
}
```

---

## ⚙️ Configuración y Despliegue

### Requisitos del Sistema

#### Software Requerido:
- **Java 17** o superior
- **Maven 3.8+** para compilación
- **Red TCP/IP** para comunicación RMI

#### Recursos Recomendados:
- **RAM**: 512MB mínimo, 1GB recomendado
- **Disco**: 100MB para el JAR, espacio adicional para metadatos
- **Red**: Conexión estable a nodos de almacenamiento

### Configuración de Nodos

#### Archivo de Configuración (Opcional)

Crear `config/nodes.properties`:
```properties
# Configuración de nodos de almacenamiento
node.1.id=StorageNode1
node.1.host=localhost
node.1.port=2001

node.2.id=StorageNode2
node.2.host=192.168.1.100
node.2.port=2002

node.3.id=StorageNode3
node.3.host=192.168.1.101
node.3.port=2003

# Configuración general
replication.factor=2
connection.timeout=5000
health.check.interval=30
```

#### Configuración Programática

```java
// Configuración personalizada de nodos
DistributedStorageService.NodeConfig[] customNodes = {
    new DistributedStorageService.NodeConfig("Node1", "server1.company.com", 2001),
    new DistributedStorageService.NodeConfig("Node2", "server2.company.com", 2002),
    new DistributedStorageService.NodeConfig("Node3", "server3.company.com", 2003)
};

DistributedStorageService service = new DistributedStorageService(customNodes);
```

### Compilación y Empaquetado

#### Desarrollo (con testing):
```bash
# Compilar con módulo de testing incluido
mvn clean compile -Pdevelopment

# Empaquetar con dependencias
mvn package -Pdevelopment

# Ejecutar tests
mvn test -Pdevelopment
```

#### Producción (sin testing):
```bash
# Compilar sin módulo de testing
mvn clean compile -Pproduction

# Empaquetar para producción
mvn package -Pproduction

# Crear JAR ejecutable
mvn assembly:single -Pproduction
```

### Despliegue

#### Como Aplicación Standalone:
```bash
# Ejecutar en modo standby
java -jar client-backend-1.5.0.jar

# Ejecutar con comando específico
java -jar client-backend-1.5.0.jar stats
```

#### Como Servicio de Sistema (Linux):

Crear `/etc/systemd/system/client-backend.service`:
```ini
[Unit]
Description=Client Backend - Distributed Storage System
After=network.target

[Service]
Type=simple
User=storage
ExecStart=/usr/bin/java -jar /opt/distributed-storage/client-backend-1.5.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Comandos de gestión:
```bash
sudo systemctl enable client-backend
sudo systemctl start client-backend
sudo systemctl status client-backend
sudo journalctl -u client-backend -f
```

#### Integración con Docker:

**Dockerfile:**
```dockerfile
FROM openjdk:17-jre-slim

WORKDIR /app

COPY target/client-backend-1.5.0.jar app.jar
COPY config/ config/

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
```

**docker-compose.yml:**
```yaml
version: '3.8'
services:
  client-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xmx1g
    networks:
      - storage-network
    depends_on:
      - storage-node-1
      - storage-node-2
      - storage-node-3

networks:
  storage-network:
    driver: bridge
```

---

## 📋 Casos de Uso

### Caso de Uso 1: Integración con Aplicación Web

**Escenario**: Una aplicación web necesita almacenar archivos de usuarios de forma distribuida.

```java
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    private final DistributedStorageService storageService;
    
    public FileController() {
        this.storageService = new DistributedStorageService();
    }
    
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file) {
        
        try {
            byte[] fileData = file.getBytes();
            String fileName = file.getOriginalFilename();
            
            DistributedFileResult result = storageService.storeFile(fileName, fileData);
            
            if (result.isSuccess()) {
                FileUploadResponse response = new FileUploadResponse(
                    result.getFileId(), 
                    result.getFileName(),
                    result.getFormattedFileSize(),
                    result.getChecksum()
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError()
                    .body(new FileUploadResponse(result.getMessage()));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new FileUploadResponse("Error uploading file: " + e.getMessage()));
        }
    }
    
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        
        DistributedFileResult result = storageService.retrieveFile(fileId);
        
        if (result.isSuccess()) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", result.getFileName());
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(result.getFileData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<SystemHealthResponse> getSystemHealth() {
        SystemStatistics stats = storageService.getSystemStatistics();
        
        SystemHealthResponse response = new SystemHealthResponse(
            stats.getSystemHealth(),
            stats.getActiveNodes(),
            stats.getTotalStorageNodes(),
            stats.getSuccessRate()
        );
        
        return ResponseEntity.ok(response);
    }
}
```

### Caso de Uso 2: Migración de Datos

**Escenario**: Migrar archivos existentes de un sistema legacy al sistema distribuido.

```java
public class DataMigrationService {
    
    private final DistributedStorageService distributedStorage;
    private final LegacyFileSystem legacySystem;
    
    public DataMigrationService() {
        this.distributedStorage = new DistributedStorageService();
        this.legacySystem = new LegacyFileSystem();
    }
    
    public MigrationReport migrateAllFiles() {
        List<LegacyFile> filesToMigrate = legacySystem.getAllFiles();
        MigrationReport report = new MigrationReport();
        
        for (LegacyFile legacyFile : filesToMigrate) {
            try {
                // Leer archivo del sistema legacy
                byte[] fileData = legacySystem.readFile(legacyFile.getId());
                
                // Almacenar en sistema distribuido
                DistributedFileResult result = distributedStorage.storeFile(
                    legacyFile.getName(), 
                    fileData
                );
                
                if (result.isSuccess()) {
                    // Crear mapeo ID legacy -> ID distribuido
                    createIdMapping(legacyFile.getId(), result.getFileId());
                    
                    // Verificar integridad
                    FileIntegrityReport integrityReport = 
                        distributedStorage.verifyFileIntegrity(result.getFileId());
                    
                    if (integrityReport.isIntegrityValid()) {
                        report.addSuccessfulMigration(legacyFile, result);
                    } else {
                        report.addIntegrityIssue(legacyFile, integrityReport);
                    }
                } else {
                    report.addFailedMigration(legacyFile, result.getMessage());
                }
                
            } catch (Exception e) {
                report.addError(legacyFile, e);
            }
        }
        
        return report;
    }
    
    public void validateMigration(MigrationReport report) {
        System.out.println("=== REPORTE DE MIGRACIÓN ===");
        System.out.println("Archivos migrados exitosamente: " + report.getSuccessCount());
        System.out.println("Archivos con errores: " + report.getErrorCount());
        System.out.println("Archivos con problemas de integridad: " + report.getIntegrityIssueCount());
        
        // Verificación aleatoria de archivos migrados
        List<Long> migratedFileIds = report.getMigratedFileIds();
        Random random = new Random();
        
        for (int i = 0; i < Math.min(10, migratedFileIds.size()); i++) {
            Long fileId = migratedFileIds.get(random.nextInt(migratedFileIds.size()));
            
            DistributedFileResult result = distributedStorage.retrieveFile(fileId);
            if (result.isSuccess()) {
                System.out.println("✅ Archivo " + fileId + " verificado correctamente");
            } else {
                System.out.println("❌ Error verificando archivo " + fileId);
            }
        }
    }
}
```

### Caso de Uso 3: Monitoreo y Alertas

**Escenario**: Sistema de monitoreo que verifica continuamente la salud del sistema distribuido.

```java
@Component
public class DistributedStorageMonitor {
    
    private final DistributedStorageService storageService;
    private final AlertService alertService;
    private final ScheduledExecutorService scheduler;
    
    public DistributedStorageMonitor() {
        this.storageService = new DistributedStorageService();
        this.alertService = new AlertService();
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    @PostConstruct
    public void startMonitoring() {
        // Monitoreo de salud cada 5 minutos
        scheduler.scheduleAtFixedRate(this::checkSystemHealth, 0, 5, TimeUnit.MINUTES);
        
        // Verificación de integridad diaria
        scheduler.scheduleAtFixedRate(this::performIntegrityCheck, 1, 24, TimeUnit.HOURS);
    }
    
    private void checkSystemHealth() {
        try {
            SystemStatistics stats = storageService.getSystemStatistics();
            
            // Verificar nodos inactivos
            if (stats.getInactiveNodes() > 0) {
                alertService.sendAlert(AlertLevel.WARNING, 
                    "Nodos inactivos detectados: " + stats.getInactiveNodes() + 
                    " de " + stats.getTotalStorageNodes());
            }
            
            // Verificar tasa de éxito
            if (stats.getSuccessRate() < 95.0) {
                alertService.sendAlert(AlertLevel.CRITICAL,
                    "Tasa de éxito baja: " + String.format("%.2f%%", stats.getSuccessRate()));
            }
            
            // Verificar utilización de almacenamiento
            if (stats.getStorageUtilizationPercentage() > 90.0) {
                alertService.sendAlert(AlertLevel.WARNING,
                    "Almacenamiento casi lleno: " + 
                    String.format("%.2f%%", stats.getStorageUtilizationPercentage()));
            }
            
            // Log estadísticas
            logSystemStatistics(stats);
            
        } catch (Exception e) {
            alertService.sendAlert(AlertLevel.CRITICAL,
                "Error en monitoreo del sistema: " + e.getMessage());
        }
    }
    
    private void performIntegrityCheck() {
        try {
            SystemStatistics stats = storageService.getSystemStatistics();
            
            // Obtener lista de archivos para verificar (implementación específica)
            List<Long> fileIds = getFileIdsForIntegrityCheck();
            
            int corruptedFiles = 0;
            int totalChecked = 0;
            
            for (Long fileId : fileIds) {
                try {
                    FileIntegrityReport report = storageService.verifyFileIntegrity(fileId);
                    totalChecked++;
                    
                    if (!report.isIntegrityValid()) {
                        corruptedFiles++;
                        
                        alertService.sendAlert(AlertLevel.HIGH,
                            "Archivo corrupto detectado: ID " + fileId + 
                            " - Integridad: " + String.format("%.2f%%", report.getIntegrityPercentage()));
                    }
                    
                } catch (Exception e) {
                    alertService.sendAlert(AlertLevel.MEDIUM,
                        "Error verificando integridad del archivo " + fileId + ": " + e.getMessage());
                }
            }
            
            // Reporte de verificación de integridad
            double corruptionRate = totalChecked > 0 ? (double) corruptedFiles / totalChecked * 100.0 : 0.0;
            
            alertService.sendAlert(AlertLevel.INFO,
                "Verificación de integridad completada: " + 
                corruptedFiles + " archivos corruptos de " + totalChecked + 
                " verificados (" + String.format("%.2f%%", corruptionRate) + " corrupción)");
            
        } catch (Exception e) {
            alertService.sendAlert(AlertLevel.CRITICAL,
                "Error en verificación de integridad: " + e.getMessage());
        }
    }
    
    private void logSystemStatistics(SystemStatistics stats) {
        System.out.println("[MONITOR] " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println("  Nodos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes());
        System.out.println("  Archivos: " + stats.getTotalStoredFiles());
        System.out.println("  Éxito: " + String.format("%.2f%%", stats.getSuccessRate()));
        System.out.println("  Salud: " + stats.getSystemHealth());
        System.out.println("  Utilización: " + String.format("%.2f%%", stats.getStorageUtilizationPercentage()));
    }
}
```

---

## 🧪 Pruebas y Validación

### Suite de Testing JUnit

El sistema incluye una **suite completa de tests** en `DistributedStorageServiceTest.java` que cubre:

#### Categorías de Pruebas:

1. **Funcionalidad Básica** (Tests 1-5)
   - Inicialización del sistema
   - Almacenamiento de archivos
   - Recuperación de archivos
   - Verificación de integridad
   - Eliminación de archivos

2. **Validación y Casos Edge** (Tests 6-8)
   - Parámetros inválidos
   - Archivos inexistentes
   - Diferentes tamaños de archivo

3. **Estadísticas y Monitoreo** (Test 9)
   - Validación de métricas del sistema
   - Consistencia de datos

4. **Rendimiento y Concurrencia** (Tests 10-11)
   - Operaciones simultáneas
   - Métricas de rendimiento

5. **Estado del Sistema** (Test 12)
   - Verificación de consistencia final

#### Ejecutar Tests:

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests específicos
mvn test -Dtest=DistributedStorageServiceTest

# Ejecutar con reporte detallado
mvn test -Dtest=DistributedStorageServiceTest -Dmaven.surefire.debug=true
```

#### Interpretación de Resultados:

```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] --- TEST RESULTS ---
[INFO] ✅ Test 1: Verificar estado inicial del sistema - PASSED
[INFO] ✅ Test 2: Almacenar archivo básico - PASSED
[INFO] ✅ Test 3: Recuperar archivo almacenado - PASSED
[INFO] ✅ Test 4: Verificar integridad de archivo - PASSED
[INFO] ✅ Test 5: Eliminar archivo - PASSED
[INFO] ✅ Test 6: Validación de parámetros inválidos - PASSED
[INFO] ✅ Test 7: Archivo inexistente - PASSED
[INFO] ✅ Test 8: Archivos de diferentes tamaños - PASSED
[INFO] ✅ Test 9: Estadísticas del sistema - PASSED
[INFO] ✅ Test 10: Operaciones concurrentes - PASSED
[INFO] ✅ Test 11: Rendimiento básico - PASSED
[INFO] ✅ Test 12: Estado final del sistema - PASSED
```

### Testing Interactivo

#### Ejecutar Módulo de Testing:

```bash
# En modo desarrollo
java -cp target/classes com.distribuidos.clientbackend.testing.InteractiveTestingApplication
```

#### Escenarios de Testing Automático:

1. **Almacenamiento Masivo**: 3 archivos en paralelo
2. **Recuperación Masiva**: Todos los archivos almacenados
3. **Verificación de Integridad**: Validación de checksums
4. **Estadísticas del Sistema**: Métricas en tiempo real
5. **Eliminación Selectiva**: Limpieza controlada

#### Ejemplo de Reporte de Testing:

```
🤖 PRUEBAS AUTOMÁTICAS - Ejecutadas: 5, Exitosas: 5, Fallidas: 0, Éxito: 100.0%

🏁 RESUMEN DE PRUEBAS AUTOMÁTICAS
═══════════════════════════════════
📊 Total de pruebas: 5
✅ Pruebas exitosas: 5
❌ Pruebas fallidas: 0
📈 Tasa de éxito: 100.0%
🎉 ¡TODAS LAS PRUEBAS AUTOMÁTICAS EXITOSAS!
```

### Validación Manual

#### Checklist de Validación:

- [ ] **Conectividad**: Verificar conexión a todos los nodos
- [ ] **Almacenamiento**: Probar archivos de diferentes tamaños
- [ ] **Recuperación**: Validar integridad de datos recuperados
- [ ] **Replicación**: Confirmar factor de replicación
- [ ] **Tolerancia a Fallos**: Simular caída de nodos
- [ ] **Rendimiento**: Medir tiempos de respuesta
- [ ] **Integridad**: Verificar checksums
- [ ] **Limpieza**: Eliminar archivos de prueba

---

## 🚨 Troubleshooting

### Problemas Comunes

#### 1. "No se puede conectar a nodos de almacenamiento"

**Síntomas:**
```
❌ Error conectando a nodo StorageNode1: java.rmi.ConnectException
```

**Soluciones:**
1. Verificar que los nodos de almacenamiento estén ejecutándose:
   ```bash
   # Verificar procesos
   ps aux | grep StorageNode
   
   # Verificar puertos
   netstat -tlnp | grep :200[1-3]
   ```

2. Verificar conectividad de red:
   ```bash
   telnet localhost 2001
   telnet localhost 2002
   telnet localhost 2003
   ```

3. Revisar configuración de nodos:
   ```java
   // Verificar configuración en DistributedStorageService
   private static final NodeConfig[] DEFAULT_NODES = {
       new NodeConfig("StorageNode1", "localhost", 2001),
       new NodeConfig("StorageNode2", "localhost", 2002),
       new NodeConfig("StorageNode3", "localhost", 2003)
   };
   ```

#### 2. "Archivo no encontrado después de almacenamiento exitoso"

**Síntomas:**
```
✅ Archivo almacenado (ID: 12345)
❌ Error recuperando archivo: File not found
```

**Soluciones:**
1. Verificar estado de los nodos:
   ```java
   SystemStatistics stats = service.getSystemStatistics();
   System.out.println("Nodos activos: " + stats.getActiveNodes());
   ```

2. Verificar integridad:
   ```java
   FileIntegrityReport report = service.verifyFileIntegrity(fileId);
   System.out.println("Estado: " + report.getIntegrityStatus());
   ```

3. Revisar logs de nodos de almacenamiento para errores

#### 3. "Rendimiento degradado"

**Síntomas:**
- Operaciones lentas (>10 segundos)
- Timeouts frecuentes
- Tasa de éxito baja (<95%)

**Diagnóstico:**
```java
SystemStatistics stats = service.getSystemStatistics();

// Verificar nodos activos
if (stats.getActiveNodes() < stats.getTotalStorageNodes()) {
    System.out.println("⚠️ Algunos nodos están inactivos");
}

// Verificar utilización
if (stats.getStorageUtilizationPercentage() > 90.0) {
    System.out.println("⚠️ Almacenamiento casi lleno");
}

// Verificar tasa de éxito
if (stats.getSuccessRate() < 95.0) {
    System.out.println("⚠️ Tasa de éxito baja: " + stats.getSuccessRate() + "%");
}
```

**Soluciones:**
1. Reiniciar nodos problemáticos
2. Agregar más capacidad de almacenamiento
3. Verificar red entre nodos
4. Revisar logs para identificar cuellos de botella

#### 4. "Errores de integridad de datos"

**Síntomas:**
```
❌ Archivo corrupto detectado: ID 12345 - Integridad: 66.67%
```

**Diagnóstico:**
```java
FileIntegrityReport report = service.verifyFileIntegrity(fileId);

System.out.println("Nodos corruptos: " + report.getCorruptedNodes());
System.out.println("Nodos no disponibles: " + report.getUnavailableNodes());
System.out.println("Recomendación: " + report.getRecommendedAction());
```

**Soluciones:**
1. **Re-replicar archivo:**
   ```java
   // Eliminar archivo corrupto
   service.deleteFile(fileId);
   
   // Volver a almacenar desde backup
   DistributedFileResult result = service.storeFile(fileName, originalData);
   ```

2. **Verificar hardware de nodos afectados**
3. **Revisar logs de sistema en nodos corruptos**

### Logs y Diagnóstico

#### Habilitar Logging Detallado:

**logback.xml:**
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="com.distribuidos.clientbackend" level="DEBUG"/>
    <logger name="java.rmi" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

#### Ejecutar con Debug:

```bash
java -Djava.util.logging.config.file=logging.properties \
     -Djava.rmi.server.logCalls=true \
     -jar client-backend-1.5.0.jar
```

### Herramientas de Diagnóstico

#### Script de Verificación del Sistema:

```bash
#!/bin/bash
# system-check.sh

echo "=== DIAGNÓSTICO DEL SISTEMA DISTRIBUIDO ==="

# Verificar Java
echo "📋 Verificando Java..."
java -version

# Verificar nodos de almacenamiento
echo "🌐 Verificando nodos..."
for port in 2001 2002 2003; do
    if nc -z localhost $port; then
        echo "✅ Nodo en puerto $port: ACTIVO"
    else
        echo "❌ Nodo en puerto $port: INACTIVO"
    fi
done

# Verificar procesos
echo "⚡ Procesos activos..."
ps aux | grep -E "(StorageNode|ClientBackend)" | grep -v grep

# Verificar memoria
echo "💾 Uso de memoria..."
free -h

# Verificar red
echo "🌐 Conectividad..."
ping -c 1 localhost > /dev/null && echo "✅ Localhost OK" || echo "❌ Localhost ERROR"

echo "✅ Diagnóstico completado"
```

---

## 📖 Apéndices

### Apéndice A: Estructura Completa del Proyecto

```
client-backend/
├── pom.xml
├── README.md
├── TECHNICAL_DOCUMENTATION.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── distribuidos/
│   │   │           └── clientbackend/
│   │   │               ├── ClientBackendMain.java
│   │   │               ├── model/
│   │   │               │   ├── DistributedFileResult.java
│   │   │               │   ├── FileIntegrityReport.java
│   │   │               │   └── SystemStatistics.java
│   │   │               ├── service/
│   │   │               │   └── DistributedStorageService.java
│   │   │               └── testing/ (removible)
│   │   │                   └── InteractiveTestingApplication.java
│   │   └── resources/
│   │       ├── logback.xml
│   │       └── config/
│   │           └── nodes.properties
│   └── test/
│       └── java/
│           └── com/
│               └── distribuidos/
│                   └── clientbackend/
│                       └── service/
│                           └── DistributedStorageServiceTest.java
├── scripts/
│   ├── build-production.sh
│   ├── build-development.sh
│   └── system-check.sh
└── docker/
    ├── Dockerfile
    └── docker-compose.yml
```

### Apéndice B: Comandos Maven Útiles

```bash
# Compilación y empaquetado
mvn clean compile                    # Compilar código fuente
mvn clean package                    # Compilar y empaquetar
mvn clean package -Pproduction       # Empaquetar sin testing
mvn clean package -Pdevelopment      # Empaquetar con testing

# Testing
mvn test                            # Ejecutar tests unitarios
mvn test -Dtest=DistributedStorageServiceTest  # Test específico
mvn integration-test                # Tests de integración

# Análisis de código
mvn checkstyle:check                # Verificar estilo de código
mvn spotbugs:check                  # Detectar bugs potenciales
mvn dependency:analyze              # Analizar dependencias

# Distribución
mvn assembly:single                 # Crear JAR con dependencias
mvn site                           # Generar documentación
mvn deploy                         # Desplegar a repositorio
```

### Apéndice C: Variables de Entorno

```bash
# Configuración del sistema
export DISTRIBUTED_STORAGE_NODES="node1:2001,node2:2002,node3:2003"
export DISTRIBUTED_STORAGE_REPLICATION_FACTOR=2
export DISTRIBUTED_STORAGE_TIMEOUT=5000
export DISTRIBUTED_STORAGE_HEALTH_CHECK_INTERVAL=30

# Configuración de logging
export DISTRIBUTED_STORAGE_LOG_LEVEL=INFO
export DISTRIBUTED_STORAGE_LOG_FILE=/var/log/distributed-storage/client-backend.log

# Configuración de JVM
export JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"
export RMI_SERVER_HOSTNAME=localhost
```

### Apéndice D: Códigos de Error

| Código | Descripción | Acción Recomendada |
|--------|-------------|-------------------|
| `CONN_001` | No se puede conectar a nodos | Verificar nodos activos |
| `FILE_001` | Archivo no encontrado | Verificar ID del archivo |
| `FILE_002` | Archivo corrupto | Verificar integridad |
| `STOR_001` | Error de almacenamiento | Verificar espacio disponible |
| `REPL_001` | Error de replicación | Verificar nodos disponibles |
| `CHKS_001` | Checksum inválido | Verificar integridad de datos |
| `NETW_001` | Error de red | Verificar conectividad |
| `AUTH_001` | Error de autenticación | Verificar credenciales |
| `CONF_001` | Error de configuración | Revisar archivos de config |
| `SYST_001` | Error del sistema | Consultar logs detallados |

### Apéndice E: Métricas de Rendimiento

#### Benchmarks Típicos:

| Operación | Archivo Pequeño (<1KB) | Archivo Mediano (1-10KB) | Archivo Grande (>10KB) |
|-----------|------------------------|--------------------------|------------------------|
| **Almacenamiento** | <100ms | <500ms | <2000ms |
| **Recuperación** | <50ms | <200ms | <1000ms |
| **Verificación** | <200ms | <800ms | <3000ms |
| **Eliminación** | <100ms | <300ms | <1000ms |

#### Factores que Afectan el Rendimiento:

- **Número de nodos activos**: Más nodos = mayor redundancia pero posible latencia
- **Factor de replicación**: Mayor replicación = más tiempo de escritura
- **Latencia de red**: Conexiones lentas afectan todas las operaciones
- **Carga del sistema**: Operaciones concurrentes compiten por recursos
- **Tamaño del archivo**: Archivos grandes requieren más tiempo de transferencia

---

## 📞 Soporte y Contacto

### Información del Proyecto
- **Versión**: 1.5.0
- **Fecha de Documentación**: 2024
- **Autor**: Sistema Distribuidos - Proyecto Final
- **Licencia**: Academic Use

### Recursos Adicionales
- **Repositorio**: Cliente Backend del Sistema Distribuido
- **Documentación**: TECHNICAL_DOCUMENTATION.md
- **Tests**: Incluidos en el proyecto
- **Ejemplos**: Ver casos de uso en esta documentación

---

*Esta documentación está actualizada para Client Backend v1.5.0. Para versiones anteriores, consulte el historial de documentación.*