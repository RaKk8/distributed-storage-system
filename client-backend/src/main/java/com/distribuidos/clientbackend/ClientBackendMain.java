package com.distribuidos.clientbackend;

import com.distribuidos.clientbackend.service.DistributedStorageService;
import com.distribuidos.clientbackend.model.DistributedFileResult;
import com.distribuidos.clientbackend.model.FileIntegrityReport;
import com.distribuidos.clientbackend.model.SystemStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * CLIENTE BACKEND PRINCIPAL
 * 
 * Punto de entrada principal para el sistema de almacenamiento distribuido.
 * Esta clase proporciona una interfaz programática para todas las operaciones
 * del sistema distribuido sin dependencias del módulo de testing.
 * 
 * Funcionalidades principales:
 * - Almacenamiento distribuido de archivos
 * - Recuperación de archivos con tolerancia a fallos
 * - Verificación de integridad de datos
 * - Eliminación distribuida de archivos
 * - Monitoreo de estadísticas del sistema
 * 
 * Arquitectura:
 * - Conexión RMI a múltiples nodos de almacenamiento
 * - Balanceamiento de carga automático
 * - Replicación transparente de datos
 * - Monitoreo de salud de nodos
 * - Recuperación automática ante fallos
 * 
 * @version 1.5.0
 * @author Sistema Distribuidos - Proyecto Final
 */
public class ClientBackendMain {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientBackendMain.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Instancia única del servicio distribuido
    private static DistributedStorageService storageService;
    
    /**
     * Punto de entrada principal del Client Backend
     * 
     * @param args Argumentos de línea de comandos (opcional)
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("🌐 ========================================");
        System.out.println("   SISTEMA DISTRIBUIDO DE ALMACENAMIENTO");
        System.out.println("   Client Backend v1.5.0");
        System.out.println("   Modo: Producción");
        System.out.println("🌐 ========================================");
        System.out.println();
        
        try {
            // Inicializar el servicio distribuido
            initializeDistributedService();
            
            // Mostrar información del sistema
            displaySystemInfo();
            
            // Ejecutar operación de ejemplo (si se especifica)
            if (args.length > 0) {
                executeCommand(args);
            } else {
                // Modo standby - servicio listo para uso programático
                runInStandbyMode();
            }
            
        } catch (Exception e) {
            logger.error("Error en Client Backend Main", e);
            System.err.println("❌ Error fatal en Client Backend: " + e.getMessage());
            System.exit(1);
        } finally {
            // Limpieza al terminar
            shutdown();
        }
    }
    
    /**
     * Inicializa el servicio de almacenamiento distribuido
     */
    private static void initializeDistributedService() {
        logger.info("Inicializando DistributedStorageService...");
        System.out.println("🔄 Inicializando conexiones a nodos de almacenamiento...");
        
        try {
            storageService = new DistributedStorageService();
            
            // Verificar conectividad básica
            SystemStatistics initialStats = storageService.getSystemStatistics();
            
            System.out.println("✅ Servicio distribuido inicializado correctamente");
            System.out.println("   🌐 Nodos detectados: " + initialStats.getTotalStorageNodes());
            System.out.println("   ✅ Nodos activos: " + initialStats.getActiveNodes());
            System.out.println("   📊 Salud del sistema: " + initialStats.getSystemHealth());
            
            logger.info("DistributedStorageService inicializado - {} nodos activos de {}", 
                initialStats.getActiveNodes(), initialStats.getTotalStorageNodes());
            
        } catch (Exception e) {
            logger.error("Error inicializando DistributedStorageService", e);
            throw new RuntimeException("No se pudo inicializar el servicio distribuido: " + e.getMessage(), e);
        }
    }
    
    /**
     * Muestra información del sistema al inicio
     */
    private static void displaySystemInfo() {
        try {
            System.out.println();
            System.out.println("📋 INFORMACIÓN DEL SISTEMA");
            System.out.println("──────────────────────────────────");
            
            SystemStatistics stats = storageService.getSystemStatistics();
            
            System.out.println("🌐 Arquitectura: Distribuida con " + stats.getTotalStorageNodes() + " nodos");
            System.out.println("⚡ Nodos activos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes());
            System.out.println("💾 Capacidad total: " + stats.getFormattedTotalCapacity());
            System.out.println("📊 Utilización: " + String.format("%.2f%%", stats.getStorageUtilizationPercentage()));
            System.out.println("📁 Archivos almacenados: " + stats.getTotalStoredFiles());
            System.out.println("🎯 Estado: " + stats.getSystemHealth());
            System.out.println("🕐 Tiempo de inicio: " + LocalDateTime.now().format(TIMESTAMP_FORMAT));
            
            logger.info("Información del sistema mostrada - {} archivos en {} nodos", 
                stats.getTotalStoredFiles(), stats.getActiveNodes());
            
        } catch (Exception e) {
            logger.warn("Error obteniendo información del sistema", e);
            System.out.println("⚠️ No se pudo obtener información completa del sistema");
        }
    }
    
    /**
     * Ejecuta comandos desde línea de comandos
     */
    private static void executeCommand(String[] args) {
        String command = args[0].toLowerCase();
        
        logger.info("Ejecutando comando: {}", command);
        System.out.println();
        System.out.println("🚀 Ejecutando comando: " + command);
        System.out.println();
        
        try {
            switch (command) {
                case "store":
                    if (args.length >= 2) {
                        storeFileFromPath(args[1]);
                    } else {
                        System.out.println("❌ Uso: java ClientBackendMain store <ruta_archivo>");
                    }
                    break;
                    
                case "retrieve":
                    if (args.length >= 2) {
                        retrieveFileById(args[1]);
                    } else {
                        System.out.println("❌ Uso: java ClientBackendMain retrieve <file_id>");
                    }
                    break;
                    
                case "delete":
                    if (args.length >= 2) {
                        deleteFileById(args[1]);
                    } else {
                        System.out.println("❌ Uso: java ClientBackendMain delete <file_id>");
                    }
                    break;
                    
                case "verify":
                    if (args.length >= 2) {
                        verifyFileIntegrity(args[1]);
                    } else {
                        System.out.println("❌ Uso: java ClientBackendMain verify <file_id>");
                    }
                    break;
                    
                case "stats":
                    showDetailedStatistics();
                    break;
                    
                case "health":
                    checkSystemHealth();
                    break;
                    
                default:
                    showCommandHelp();
                    break;
            }
        } catch (Exception e) {
            logger.error("Error ejecutando comando: {}", command, e);
            System.out.println("❌ Error ejecutando comando: " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta en modo standby para uso programático
     */
    private static void runInStandbyMode() {
        System.out.println();
        System.out.println("🟢 SISTEMA EN MODO STANDBY");
        System.out.println("──────────────────────────────────");
        System.out.println("El Client Backend está listo para recibir operaciones.");
        System.out.println("Puede ser utilizado programáticamente por otras aplicaciones.");
        System.out.println();
        System.out.println("💡 Para operaciones de línea de comandos, use:");
        System.out.println("   java ClientBackendMain <comando> [argumentos]");
        System.out.println();
        System.out.println("📚 Comandos disponibles: store, retrieve, delete, verify, stats, health");
        System.out.println();
        System.out.println("🔗 Para testing interactivo, ejecute el InteractiveTestingApplication");
        System.out.println("   (solo disponible en modo desarrollo)");
        System.out.println();
        
        logger.info("Client Backend en modo standby - listo para operaciones programáticas");
        
        // Mantener servicio activo para uso programático
        System.out.println("✅ Servicio distribuido activo y disponible");
        System.out.println("   Presione Ctrl+C para terminar");
        
        // Hook para limpieza al terminar
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println();
            System.out.println("🔄 Cerrando Client Backend...");
            shutdown();
            System.out.println("👋 Client Backend terminado");
        }));
        
        // Mantener el proceso activo
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            logger.info("Client Backend interrumpido");
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Almacena un archivo desde una ruta del sistema de archivos
     */
    private static void storeFileFromPath(String filePath) {
        try {
            System.out.println("📥 Almacenando archivo: " + filePath);
            
            // Leer archivo
            byte[] fileData = Files.readAllBytes(Paths.get(filePath));
            String fileName = Paths.get(filePath).getFileName().toString();
            
            System.out.println("   📁 Nombre: " + fileName);
            System.out.println("   📊 Tamaño: " + formatBytes(fileData.length));
            
            // Almacenar
            long startTime = System.currentTimeMillis();
            DistributedFileResult result = storageService.storeFile(fileName, fileData);
            long duration = System.currentTimeMillis() - startTime;
            
            // Mostrar resultado
            if (result.isSuccess()) {
                System.out.println("✅ ALMACENAMIENTO EXITOSO");
                System.out.println("   🆔 File ID: " + result.getFileId());
                System.out.println("   🔒 Checksum: " + result.getChecksum());
                System.out.println("   🏠 Nodo principal: " + result.getPrimaryNode());
                System.out.println("   🔄 Replicación: " + result.getReplicationFactor() + " nodos");
                System.out.println("   ⏱️  Tiempo: " + duration + "ms");
            } else {
                System.out.println("❌ ALMACENAMIENTO FALLIDO");
                System.out.println("   💬 Error: " + result.getMessage());
            }
            
        } catch (IOException e) {
            System.out.println("❌ Error leyendo archivo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error durante almacenamiento: " + e.getMessage());
        }
    }
    
    /**
     * Recupera un archivo por ID
     */
    private static void retrieveFileById(String fileIdStr) {
        try {
            Long fileId = Long.parseLong(fileIdStr);
            
            System.out.println("📤 Recuperando archivo ID: " + fileId);
            
            long startTime = System.currentTimeMillis();
            DistributedFileResult result = storageService.retrieveFile(fileId);
            long duration = System.currentTimeMillis() - startTime;
            
            if (result.isSuccess()) {
                System.out.println("✅ RECUPERACIÓN EXITOSA");
                System.out.println("   📁 Nombre: " + result.getFileName());
                System.out.println("   📊 Tamaño: " + result.getFormattedFileSize());
                System.out.println("   🏠 Recuperado desde: " + result.getPrimaryNode());
                System.out.println("   ⏱️  Tiempo: " + duration + "ms");
                
                // Guardar archivo localmente
                String outputPath = "retrieved_" + result.getFileName();
                // Files.write(Paths.get(outputPath), result.getFileData());
                System.out.println("   💾 Archivo disponible para guardado en: " + outputPath);
            } else {
                System.out.println("❌ RECUPERACIÓN FALLIDA");
                System.out.println("   💬 Error: " + result.getMessage());
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ ID de archivo inválido: " + fileIdStr);
        } catch (Exception e) {
            System.out.println("❌ Error durante recuperación: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un archivo por ID
     */
    private static void deleteFileById(String fileIdStr) {
        try {
            Long fileId = Long.parseLong(fileIdStr);
            
            System.out.println("🗑️ Eliminando archivo ID: " + fileId);
            
            long startTime = System.currentTimeMillis();
            DistributedFileResult result = storageService.deleteFile(fileId);
            long duration = System.currentTimeMillis() - startTime;
            
            if (result.isSuccess()) {
                System.out.println("✅ ELIMINACIÓN EXITOSA");
                System.out.println("   🆔 File ID: " + result.getFileId());
                System.out.println("   🌐 Eliminado de nodos: " + 
                    (result.getReplicatedNodes() != null ? result.getReplicatedNodes().size() : 1));
                System.out.println("   ⏱️  Tiempo: " + duration + "ms");
            } else {
                System.out.println("❌ ELIMINACIÓN FALLIDA");
                System.out.println("   💬 Error: " + result.getMessage());
            }
            
        } catch (NumberFormatException e) {
            System.out.println("❌ ID de archivo inválido: " + fileIdStr);
        } catch (Exception e) {
            System.out.println("❌ Error durante eliminación: " + e.getMessage());
        }
    }
    
    /**
     * Verifica la integridad de un archivo
     */
    private static void verifyFileIntegrity(String fileIdStr) {
        try {
            Long fileId = Long.parseLong(fileIdStr);
            
            System.out.println("🔍 Verificando integridad del archivo ID: " + fileId);
            
            long startTime = System.currentTimeMillis();
            FileIntegrityReport report = storageService.verifyFileIntegrity(fileId);
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println("📊 REPORTE DE INTEGRIDAD");
            System.out.println("   🆔 File ID: " + report.getFileId());
            System.out.println("   📁 Nombre: " + report.getFileName());
            System.out.println("   🎯 Estado: " + report.getIntegrityStatus());
            System.out.println("   ✅ Válido: " + (report.isIntegrityValid() ? "SÍ" : "NO"));
            System.out.println("   📈 Integridad: " + String.format("%.2f%%", report.getIntegrityPercentage()));
            System.out.println("   🌐 Nodos verificados: " + report.getTotalNodes());
            System.out.println("   ✅ Nodos válidos: " + report.getValidNodeCount());
            
            if (report.getCorruptedNodes() != null && !report.getCorruptedNodes().isEmpty()) {
                System.out.println("   ❌ Nodos corruptos: " + String.join(", ", report.getCorruptedNodes()));
            }
            
            System.out.println("   💡 Recomendación: " + report.getRecommendedAction());
            System.out.println("   ⏱️  Tiempo: " + duration + "ms");
            
        } catch (NumberFormatException e) {
            System.out.println("❌ ID de archivo inválido: " + fileIdStr);
        } catch (Exception e) {
            System.out.println("❌ Error durante verificación: " + e.getMessage());
        }
    }
    
    /**
     * Muestra estadísticas detalladas del sistema
     */
    private static void showDetailedStatistics() {
        try {
            System.out.println("📊 ESTADÍSTICAS DETALLADAS DEL SISTEMA");
            System.out.println("═════════════════════════════════════════");
            
            SystemStatistics stats = storageService.getSystemStatistics();
            
            // Información general
            System.out.println("🌐 INFORMACIÓN GENERAL:");
            System.out.println("   • Nodos totales: " + stats.getTotalStorageNodes());
            System.out.println("   • Nodos activos: " + stats.getActiveNodes());
            System.out.println("   • Nodos inactivos: " + stats.getInactiveNodes());
            System.out.println("   • Archivos almacenados: " + stats.getTotalStoredFiles());
            System.out.println("   • Salud del sistema: " + stats.getSystemHealth());
            
            // Capacidad
            System.out.println();
            System.out.println("💾 CAPACIDAD DE ALMACENAMIENTO:");
            System.out.println("   • Capacidad total: " + stats.getFormattedTotalCapacity());
            System.out.println("   • Espacio usado: " + stats.getFormattedUsedCapacity());
            System.out.println("   • Espacio disponible: " + stats.getFormattedAvailableCapacity());
            System.out.println("   • Utilización: " + String.format("%.2f%%", stats.getStorageUtilizationPercentage()));
            
            // Operaciones
            System.out.println();
            System.out.println("⚡ ESTADÍSTICAS DE OPERACIONES:");
            System.out.println("   • Total operaciones: " + stats.getTotalOperations());
            System.out.println("   • Operaciones exitosas: " + stats.getSuccessfulOperations());
            System.out.println("   • Operaciones fallidas: " + stats.getFailedOperations());
            System.out.println("   • Tasa de éxito: " + String.format("%.2f%%", stats.getSuccessRate()));
            
            // Estadísticas por nodo
            if (stats.getNodeStatistics() != null && !stats.getNodeStatistics().isEmpty()) {
                System.out.println();
                System.out.println("🏠 ESTADÍSTICAS POR NODO:");
                
                for (SystemStatistics.NodeStatistics nodeStat : stats.getNodeStatistics().values()) {
                    System.out.println("   ┌─ " + nodeStat.getNodeId() + " ──────────────");
                    System.out.println("   │ Estado: " + nodeStat.getStatus());
                    System.out.println("   │ Archivos: " + nodeStat.getFilesStored());
                    System.out.println("   │ Capacidad: " + nodeStat.getFormattedCapacity());
                    System.out.println("   │ Usado: " + nodeStat.getFormattedUsedSpace() + 
                        " (" + String.format("%.1f%%", nodeStat.getUtilizationPercentage()) + ")");
                    System.out.println("   │ Disponible: " + nodeStat.getFormattedAvailableSpace());
                    System.out.println("   │ Saludable: " + (nodeStat.isHealthy() ? "✅ SÍ" : "❌ NO"));
                    System.out.println("   └──────────────────────────────");
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error obteniendo estadísticas: " + e.getMessage());
        }
    }
    
    /**
     * Verifica la salud del sistema
     */
    private static void checkSystemHealth() {
        try {
            System.out.println("🏥 VERIFICACIÓN DE SALUD DEL SISTEMA");
            System.out.println("────────────────────────────────────────");
            
            SystemStatistics stats = storageService.getSystemStatistics();
            
            // Análisis de salud
            boolean systemHealthy = stats.getActiveNodes() > 0;
            double healthPercentage = stats.getTotalStorageNodes() > 0 ? 
                ((double) stats.getActiveNodes() / stats.getTotalStorageNodes()) * 100.0 : 0.0;
            
            System.out.println("🎯 Estado general: " + stats.getSystemHealth());
            System.out.println("📊 Nivel de salud: " + String.format("%.1f%%", healthPercentage));
            System.out.println("⚡ Nodos operativos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes());
            
            if (systemHealthy) {
                System.out.println("✅ SISTEMA OPERATIVO");
                if (healthPercentage >= 100.0) {
                    System.out.println("   🌟 Todos los nodos funcionando perfectamente");
                } else if (healthPercentage >= 75.0) {
                    System.out.println("   🟢 Sistema funcionando con capacidad reducida");
                } else if (healthPercentage >= 50.0) {
                    System.out.println("   🟡 Sistema funcionando con limitaciones");
                } else {
                    System.out.println("   🟠 Sistema funcionando en modo degradado");
                }
            } else {
                System.out.println("❌ SISTEMA NO OPERATIVO");
                System.out.println("   🚨 Todos los nodos están inaccesibles");
            }
            
            // Recomendaciones
            System.out.println();
            System.out.println("💡 RECOMENDACIONES:");
            if (stats.getInactiveNodes() > 0) {
                System.out.println("   • Verificar conectividad de " + stats.getInactiveNodes() + " nodo(s) inactivo(s)");
            }
            if (stats.getStorageUtilizationPercentage() > 80.0) {
                System.out.println("   • Considerar agregar más capacidad de almacenamiento");
            }
            if (stats.getSuccessRate() < 95.0) {
                System.out.println("   • Revisar logs de errores para identificar problemas");
            }
            if (systemHealthy && healthPercentage >= 95.0) {
                System.out.println("   • ✅ Sistema funcionando óptimamente");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error verificando salud del sistema: " + e.getMessage());
        }
    }
    
    /**
     * Muestra ayuda de comandos
     */
    private static void showCommandHelp() {
        System.out.println("📚 COMANDOS DISPONIBLES");
        System.out.println("═══════════════════════");
        System.out.println();
        System.out.println("📥 store <ruta_archivo>");
        System.out.println("   Almacena un archivo en el sistema distribuido");
        System.out.println("   Ejemplo: java ClientBackendMain store /path/to/file.txt");
        System.out.println();
        System.out.println("📤 retrieve <file_id>");
        System.out.println("   Recupera un archivo por su ID");
        System.out.println("   Ejemplo: java ClientBackendMain retrieve 12345");
        System.out.println();
        System.out.println("🗑️ delete <file_id>");
        System.out.println("   Elimina un archivo del sistema distribuido");
        System.out.println("   Ejemplo: java ClientBackendMain delete 12345");
        System.out.println();
        System.out.println("🔍 verify <file_id>");
        System.out.println("   Verifica la integridad de un archivo");
        System.out.println("   Ejemplo: java ClientBackendMain verify 12345");
        System.out.println();
        System.out.println("📊 stats");
        System.out.println("   Muestra estadísticas detalladas del sistema");
        System.out.println("   Ejemplo: java ClientBackendMain stats");
        System.out.println();
        System.out.println("🏥 health");
        System.out.println("   Verifica la salud del sistema");
        System.out.println("   Ejemplo: java ClientBackendMain health");
        System.out.println();
        System.out.println("💡 Sin argumentos: Ejecuta en modo standby para uso programático");
    }
    
    /**
     * Obtiene la instancia del servicio distribuido para uso programático
     */
    public static DistributedStorageService getStorageService() {
        if (storageService == null) {
            throw new IllegalStateException("El servicio distribuido no ha sido inicializado. " +
                "Ejecute ClientBackendMain.main() primero.");
        }
        return storageService;
    }
    
    /**
     * API programática para almacenar archivos
     */
    public static DistributedFileResult storeFile(String fileName, byte[] fileData) {
        if (storageService == null) {
            throw new IllegalStateException("Servicio no inicializado");
        }
        return storageService.storeFile(fileName, fileData);
    }
    
    /**
     * API programática para recuperar archivos
     */
    public static DistributedFileResult retrieveFile(Long fileId) {
        if (storageService == null) {
            throw new IllegalStateException("Servicio no inicializado");
        }
        return storageService.retrieveFile(fileId);
    }
    
    /**
     * API programática para eliminar archivos
     */
    public static DistributedFileResult deleteFile(Long fileId) {
        if (storageService == null) {
            throw new IllegalStateException("Servicio no inicializado");
        }
        return storageService.deleteFile(fileId);
    }
    
    /**
     * API programática para verificar integridad
     */
    public static FileIntegrityReport verifyFileIntegrity(Long fileId) {
        if (storageService == null) {
            throw new IllegalStateException("Servicio no inicializado");
        }
        return storageService.verifyFileIntegrity(fileId);
    }
    
    /**
     * API programática para obtener estadísticas
     */
    public static SystemStatistics getSystemStatistics() {
        if (storageService == null) {
            throw new IllegalStateException("Servicio no inicializado");
        }
        return storageService.getSystemStatistics();
    }
    
    /**
     * Utility para formatear bytes
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Cierre limpio del servicio
     */
    private static void shutdown() {
        try {
            if (storageService != null) {
                logger.info("Cerrando DistributedStorageService...");
                storageService.shutdown();
                storageService = null;
            }
        } catch (Exception e) {
            logger.error("Error durante shutdown", e);
        }
    }
}
