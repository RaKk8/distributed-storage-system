# Sistema de Almacenamiento Distribuido - v1.5.0

## 📋 Resumen de Cambios

**Fecha:** 15 de Septiembre, 2025  
**Versión:** 1.5.0  
**Estado:** ESTABLE - Todas las pruebas pasando (12/12)  

## 🚀 Nuevas Características y Mejoras

### 1. Sistema de Registro de Nombres de Archivos
- **Implementación:** Nuevo `fileNamesRegistry` (ConcurrentHashMap) para preservar nombres originales
- **Método:** `extractFileNameFromNode()` para recuperación precisa de nombres
- **Beneficio:** Recuperación correcta de archivos con sus nombres originales (ej: "retrieve-test.txt")

### 2. Sistema de Reset de Estadísticas para Testing
- **Implementación:** Método `resetStatistics()` para aislamiento de pruebas
- **Funcionalidad:** Reinicio de contadores operacionales y estadísticas
- **Beneficio:** Pruebas determinísticas sin interferencia entre tests

### 3. Mejoras en Thread-Safety
- **Implementación:** Uso de `ConcurrentHashMap` para operaciones concurrentes
- **Validación:** Pruebas concurrentes exitosas (5/5)
- **Beneficio:** Sistema robusto para operaciones paralelas

## 🔧 Correcciones de Errores

### Error 1: Recuperación de Nombres de Archivos
**Problema:** `testRetrieveFile` fallaba esperando "retrieve-test.txt" pero recibía "recovered-file"
```
Expected: retrieve-test.txt
Actual: recovered-file
```

**Solución Implementada:**
```java
// Agregado al DistributedStorageService.java
private final Map<String, String> fileNamesRegistry = new ConcurrentHashMap<>();

// En storeFile()
fileNamesRegistry.put(fileId, fileName);

// Nuevo método
private String extractFileNameFromNode(String nodeName) {
    return fileNamesRegistry.get(nodeName);
}
```

### Error 2: Acumulación de Contadores de Operaciones
**Problema:** `testSystemStatistics` esperaba 12 operaciones pero obtenía 18 (acumulación entre pruebas)
```
Expected: 12
Actual: 18
```

**Solución Implementada:**
```java
// Agregado resetStatistics() method
public void resetStatistics() {
    this.operationCount.set(0);
    this.successfulOperations.set(0);
    // Reset de otros contadores
}

// En testSystemStatistics()
storageService.resetStatistics();
```

## 📊 Resultados de Pruebas

### Resumen Ejecutivo
- ✅ **Pruebas Totales:** 12/12 exitosas
- ✅ **Fallos:** 0
- ✅ **Errores:** 0
- ✅ **Tiempo Total:** 2.802s

### Métricas de Rendimiento
- **Almacenamiento Promedio:** 8ms
- **Recuperación Promedio:** 4ms  
- **Verificación Promedio:** 51ms
- **Operaciones Concurrentes:** 5/5 exitosas

### Estado del Sistema
- **Nodos Configurados:** 3
- **Nodos Activos:** 2 (storage-node-1, storage-node-2)
- **Comunicación RMI:** Activa en puertos 1099-1101
- **Factor de Replicación:** 2x
- **Integridad de Datos:** 100%

## 🏗️ Arquitectura del Sistema

```
Client Backend v1.5.0 (Coordinador Central)
├── RMI Registry Manager
├── DistributedStorageService
├── SystemStatistics
└── FileNamesRegistry (NUEVO)

Storage Nodes:
├── storage-node-1 (Puerto: 1099) ✅ ACTIVO
├── storage-node-2 (Puerto: 1100) ✅ ACTIVO  
└── storage-node-3 (Puerto: 1101) ❌ INACTIVO
```

## 🧪 Casos de Prueba Validados

### Funcionalidades Core
1. **testInitialSystemState** - Estado inicial del sistema
2. **testStoreFile** - Almacenamiento básico con replicación
3. **testRetrieveFile** - Recuperación con nombres correctos ✅ CORREGIDO
4. **testFileIntegrity** - Verificación de integridad SHA-256
5. **testDeleteFile** - Eliminación distribuida

### Manejo de Errores
6. **testInvalidParameters** - Validación de parámetros
7. **testNonExistentFile** - Manejo de archivos inexistentes

### Rendimiento y Escalabilidad
8. **testDifferentFileSizes** - Archivos de 1B, 1KB, 10KB
9. **testSystemStatistics** - Estadísticas con reset ✅ CORREGIDO
10. **testConcurrentOperations** - 5 operaciones paralelas
11. **testBasicPerformance** - Métricas de latencia
12. **testFinalSystemState** - Estado final consistente

## 📁 Estructura de Archivos Modificados

```
client-backend/
├── src/main/java/com/distribuidos/clientbackend/
│   ├── service/
│   │   └── DistributedStorageService.java ✅ MODIFICADO
│   └── test/java/com/distribuidos/clientbackend/
│       └── service/
│           └── DistributedStorageServiceTest.java ✅ MODIFICADO
docs/
└── test-report.html ✅ NUEVO
```

## 🔍 Detalles Técnicos de Implementación

### Cambios en DistributedStorageService.java
```java
// Líneas añadidas: ~15
// Nuevos imports: java.util.concurrent.ConcurrentHashMap
// Nuevos métodos: resetStatistics(), extractFileNameFromNode()
// Nuevos atributos: fileNamesRegistry
```

### Cambios en DistributedStorageServiceTest.java  
```java
// Líneas añadidas: 1
// Modificación en testSystemStatistics: Agregada llamada a resetStatistics()
```

## 🎯 Evidencias de Calidad

### Log de Compilación Exitosa
```
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time: 2.802 s
```

### Comunicación RMI Validada
```
✅ Conexión establecida con nodo: storage-node-1
✅ Conexión establecida con nodo: storage-node-2  
✅ Almacenamiento exitoso en 2 nodos
✅ Recuperación exitosa desde storage-node-1
```

## 🔮 Próximos Pasos Recomendados

1. **Tolerancia a Fallos:** Implementar reconexión automática para storage-node-3
2. **Monitoreo:** Agregar métricas de latencia y throughput en tiempo real
3. **Seguridad:** Implementar autenticación y encriptación
4. **Optimización:** Cache para operaciones frecuentes y compresión de archivos

## 📈 Impacto de las Mejoras

- **Confiabilidad:** 100% de pruebas pasando (anteriormente 83.33%)
- **Precisión:** Recuperación correcta de nombres de archivos
- **Aislamiento:** Pruebas determinísticas sin interferencia
- **Robustez:** Thread-safety mejorado para operaciones concurrentes
- **Mantenibilidad:** Logging detallado y documentación completa

---

**Desarrollado por:** GitHub Copilot  
**Cliente:** Oztal  
**Curso:** Sistemas Distribuidos - 9no Semestre  
**Institución:** Universidad