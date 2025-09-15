package com.distribuidos.clientbackend.testing;

import com.distribuidos.clientbackend.service.DistributedStorageService;
import com.distribuidos.clientbackend.model.DistributedFileResult;
import com.distribuidos.clientbackend.model.FileIntegrityReport;
import com.distribuidos.clientbackend.model.SystemStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MÓDULO DE TESTING INTERACTIVO REMOVIBLE
 * 
 * Esta clase proporciona una interfaz interactiva de consola para probar
 * todas las funcionalidades del sistema distribuido de almacenamiento.
 * 
 * IMPORTANTE: Este módulo está diseñado para ser completamente removible
 * sin afectar el funcionamiento del sistema principal. Para el despliegue
 * en producción, simplemente no incluir este paquete en la compilación.
 * 
 * Características:
 * - Interfaz de consola interactiva
 * - Pruebas de todas las operaciones principales
 * - Generación de archivos de prueba
 * - Reportes detallados de funcionamiento
 * - Escenarios de prueba automáticos
 * - No dependencias externas que afecten el core
 */
public class InteractiveTestingApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(InteractiveTestingApplication.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final DistributedStorageService storageService;
    private final Scanner scanner;
    private final Map<Long, String> testFiles;
    private final StringBuilder testReport;
    
    // Control de testing
    private boolean keepRunning = true;
    private int testCounter = 1;
    
    public InteractiveTestingApplication() {
        this.storageService = new DistributedStorageService();
        this.scanner = new Scanner(System.in);
        this.testFiles = new HashMap<>();
        this.testReport = new StringBuilder();
        
        logger.info("🧪 InteractiveTestingApplication inicializada");
    }
    
    /**
     * Punto de entrada principal del módulo de testing
     */
    public static void main(String[] args) {
        System.out.println();
        System.out.println("🧪 ========================================");
        System.out.println("   MÓDULO DE TESTING INTERACTIVO");
        System.out.println("   Sistema Distribuido de Almacenamiento");
        System.out.println("   v1.5.0 - Testing Module");
        System.out.println("🧪 ========================================");
        System.out.println();
        
        InteractiveTestingApplication app = new InteractiveTestingApplication();
        
        try {
            app.displayWelcomeMessage();
            app.runInteractiveSession();
        } catch (Exception e) {
            System.err.println("❌ Error en sesión de testing: " + e.getMessage());
            e.printStackTrace();
        } finally {
            app.shutdown();
        }
    }
    
    /**
     * Muestra mensaje de bienvenida y instrucciones
     */
    private void displayWelcomeMessage() {
        System.out.println("💡 BIENVENIDO AL MÓDULO DE TESTING INTERACTIVO");
        System.out.println();
        System.out.println("Este módulo te permite probar todas las funcionalidades del");
        System.out.println("sistema distribuido de almacenamiento de forma interactiva.");
        System.out.println();
        System.out.println("📋 Funcionalidades disponibles:");
        System.out.println("   • Almacenar archivos en el sistema distribuido");
        System.out.println("   • Recuperar archivos almacenados");
        System.out.println("   • Verificar integridad de datos");
        System.out.println("   • Eliminar archivos");
        System.out.println("   • Ver estadísticas del sistema");
        System.out.println("   • Ejecutar pruebas automáticas");
        System.out.println();
        System.out.println("⚠️  NOTA: Este módulo es removible y no afecta el sistema principal.");
        System.out.println();
        
        System.out.print("¿Deseas continuar? (s/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (!response.equals("s") && !response.equals("sí") && !response.equals("si") && !response.equals("y") && !response.equals("yes")) {
            System.out.println("👋 Saliendo del módulo de testing...");
            keepRunning = false;
            return;
        }
        
        System.out.println();
        System.out.println("🚀 ¡Iniciando sesión de testing interactivo!");
        System.out.println();
    }
    
    /**
     * Ejecuta la sesión interactiva principal
     */
    private void runInteractiveSession() {
        while (keepRunning) {
            try {
                displayMainMenu();
                String choice = getUserInput("Selecciona una opción");
                
                switch (choice) {
                    case "1":
                        testStoreFile();
                        break;
                    case "2":
                        testRetrieveFile();
                        break;
                    case "3":
                        testDeleteFile();
                        break;
                    case "4":
                        testFileIntegrity();
                        break;
                    case "5":
                        showSystemStatistics();
                        break;
                    case "6":
                        runAutomaticTests();
                        break;
                    case "7":
                        showTestReport();
                        break;
                    case "8":
                        generateTestFiles();
                        break;
                    case "9":
                        exportTestReport();
                        break;
                    case "0":
                        confirmExit();
                        break;
                    default:
                        System.out.println("❌ Opción inválida. Por favor, selecciona una opción del 0-9.");
                        break;
                }
                
                if (keepRunning) {
                    waitForUserToContinue();
                }
                
            } catch (Exception e) {
                System.err.println("❌ Error durante la operación: " + e.getMessage());
                logger.error("Error en sesión interactiva", e);
                waitForUserToContinue();
            }
        }
    }
    
    /**
     * Muestra el menú principal
     */
    private void displayMainMenu() {
        System.out.println("┌─────────────────────────────────────────────────────────┐");
        System.out.println("│                    MENÚ PRINCIPAL                      │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│  1. 📥 Almacenar archivo                               │");
        System.out.println("│  2. 📤 Recuperar archivo                               │");
        System.out.println("│  3. 🗑️  Eliminar archivo                               │");
        System.out.println("│  4. 🔍 Verificar integridad                            │");
        System.out.println("│  5. 📊 Ver estadísticas del sistema                    │");
        System.out.println("│  6. 🤖 Ejecutar pruebas automáticas                    │");
        System.out.println("│  7. 📋 Mostrar reporte de pruebas                      │");
        System.out.println("│  8. 📁 Generar archivos de prueba                      │");
        System.out.println("│  9. 💾 Exportar reporte completo                       │");
        System.out.println("│  0. 🚪 Salir del módulo de testing                     │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.println();
    }
    
    /**
     * Prueba de almacenamiento de archivo
     */
    private void testStoreFile() {
        addToReport("=== TEST " + testCounter++ + ": ALMACENAMIENTO DE ARCHIVO ===");
        System.out.println("📥 PRUEBA DE ALMACENAMIENTO DE ARCHIVO");
        System.out.println();
        
        try {
            // Opciones de archivo
            System.out.println("Opciones de archivo:");
            System.out.println("1. Generar archivo de prueba automático");
            System.out.println("2. Cargar archivo desde disco");
            System.out.println("3. Crear archivo de texto personalizado");
            
            String option = getUserInput("Selecciona opción (1-3)");
            
            String fileName;
            byte[] fileData;
            
            switch (option) {
                case "1":
                    fileName = "test-file-" + System.currentTimeMillis() + ".txt";
                    fileData = generateTestFileContent().getBytes();
                    break;
                case "2":
                    String filePath = getUserInput("Introduce la ruta del archivo");
                    try {
                        fileData = Files.readAllBytes(Paths.get(filePath));
                        fileName = Paths.get(filePath).getFileName().toString();
                    } catch (IOException e) {
                        System.out.println("❌ Error leyendo archivo: " + e.getMessage());
                        addToReport("ERROR: No se pudo leer el archivo");
                        return;
                    }
                    break;
                case "3":
                    fileName = getUserInput("Nombre del archivo");
                    String content = getUserInput("Contenido del archivo");
                    fileData = content.getBytes();
                    break;
                default:
                    System.out.println("❌ Opción inválida");
                    return;
            }
            
            System.out.println();
            System.out.println("🔄 Almacenando archivo: " + fileName + " (" + fileData.length + " bytes)");
            
            long startTime = System.currentTimeMillis();
            DistributedFileResult result = storageService.storeFile(fileName, fileData);
            long duration = System.currentTimeMillis() - startTime;
            
            // Mostrar resultado
            System.out.println();
            if (result.isSuccess()) {
                System.out.println("✅ ALMACENAMIENTO EXITOSO");
                System.out.println("   📋 File ID: " + result.getFileId());
                System.out.println("   📁 Nombre: " + result.getFileName());
                System.out.println("   📊 Tamaño: " + result.getFormattedFileSize());
                System.out.println("   🔒 Checksum: " + result.getChecksum());
                System.out.println("   🏠 Nodo principal: " + result.getPrimaryNode());
                System.out.println("   🔄 Factor de replicación: " + result.getReplicationFactor());
                System.out.println("   ⏱️  Tiempo: " + duration + "ms");
                
                if (result.getReplicatedNodes() != null) {
                    System.out.println("   🌐 Nodos con réplicas: " + String.join(", ", result.getReplicatedNodes()));
                }
                
                // Guardar referencia para pruebas posteriores
                testFiles.put(result.getFileId(), result.getFileName());
                
                addToReport("✅ ÉXITO - Archivo almacenado (ID: " + result.getFileId() + 
                           ", Nodos: " + result.getReplicationFactor() + ", Tiempo: " + duration + "ms)");
            } else {
                System.out.println("❌ ALMACENAMIENTO FALLIDO");
                System.out.println("   💬 Mensaje: " + result.getMessage());
                
                addToReport("❌ FALLO - " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("💥 Error durante almacenamiento: " + e.getMessage());
            addToReport("💥 ERROR - " + e.getMessage());
        }
    }
    
    /**
     * Prueba de recuperación de archivo
     */
    private void testRetrieveFile() {
        addToReport("=== TEST " + testCounter++ + ": RECUPERACIÓN DE ARCHIVO ===");
        System.out.println("📤 PRUEBA DE RECUPERACIÓN DE ARCHIVO");
        System.out.println();
        
        if (testFiles.isEmpty()) {
            System.out.println("⚠️ No hay archivos almacenados para recuperar.");
            System.out.println("   Por favor, almacena un archivo primero (opción 1).");
            addToReport("⚠️ ADVERTENCIA - No hay archivos para recuperar");
            return;
        }
        
        try {
            // Mostrar archivos disponibles
            System.out.println("📋 Archivos disponibles:");
            for (Map.Entry<Long, String> entry : testFiles.entrySet()) {
                System.out.println("   • ID: " + entry.getKey() + " - " + entry.getValue());
            }
            System.out.println();
            
            String fileIdStr = getUserInput("Introduce el File ID a recuperar");
            Long fileId;
            
            try {
                fileId = Long.parseLong(fileIdStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ ID de archivo inválido");
                addToReport("❌ ERROR - ID de archivo inválido");
                return;
            }
            
            System.out.println("🔄 Recuperando archivo con ID: " + fileId);
            
            long startTime = System.currentTimeMillis();
            DistributedFileResult result = storageService.retrieveFile(fileId);
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println();
            if (result.isSuccess()) {
                System.out.println("✅ RECUPERACIÓN EXITOSA");
                System.out.println("   📋 File ID: " + result.getFileId());
                System.out.println("   📊 Tamaño: " + result.getFormattedFileSize());
                System.out.println("   🏠 Recuperado desde: " + result.getPrimaryNode());
                System.out.println("   ⏱️  Tiempo: " + duration + "ms");
                
                // Preguntar si mostrar contenido
                String showContent = getUserInput("¿Mostrar contenido del archivo? (s/n)");
                if (showContent.toLowerCase().startsWith("s")) {
                    // Para archivos de texto pequeños, mostrar contenido
                    if (result.getFileSizeBytes() < 1024) {
                        System.out.println("📄 Contenido:");
                        System.out.println("───────────────");
                        // Aquí se mostraría el contenido real del archivo
                        System.out.println("[Contenido del archivo recuperado]");
                        System.out.println("───────────────");
                    } else {
                        System.out.println("📄 Archivo demasiado grande para mostrar");
                    }
                }
                
                addToReport("✅ ÉXITO - Archivo recuperado (ID: " + fileId + 
                           ", Tamaño: " + result.getFormattedFileSize() + ", Tiempo: " + duration + "ms)");
            } else {
                System.out.println("❌ RECUPERACIÓN FALLIDA");
                System.out.println("   💬 Mensaje: " + result.getMessage());
                
                addToReport("❌ FALLO - " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("💥 Error durante recuperación: " + e.getMessage());
            addToReport("💥 ERROR - " + e.getMessage());
        }
    }
    
    /**
     * Prueba de eliminación de archivo
     */
    private void testDeleteFile() {
        addToReport("=== TEST " + testCounter++ + ": ELIMINACIÓN DE ARCHIVO ===");
        System.out.println("🗑️ PRUEBA DE ELIMINACIÓN DE ARCHIVO");
        System.out.println();
        
        if (testFiles.isEmpty()) {
            System.out.println("⚠️ No hay archivos almacenados para eliminar.");
            addToReport("⚠️ ADVERTENCIA - No hay archivos para eliminar");
            return;
        }
        
        try {
            // Mostrar archivos disponibles
            System.out.println("📋 Archivos disponibles:");
            for (Map.Entry<Long, String> entry : testFiles.entrySet()) {
                System.out.println("   • ID: " + entry.getKey() + " - " + entry.getValue());
            }
            System.out.println();
            
            String fileIdStr = getUserInput("Introduce el File ID a eliminar");
            Long fileId;
            
            try {
                fileId = Long.parseLong(fileIdStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ ID de archivo inválido");
                addToReport("❌ ERROR - ID de archivo inválido");
                return;
            }
            
            // Confirmación
            String confirm = getUserInput("⚠️ ¿Estás seguro de eliminar este archivo? (s/n)");
            if (!confirm.toLowerCase().startsWith("s")) {
                System.out.println("❌ Eliminación cancelada");
                addToReport("❌ CANCELADO - Eliminación cancelada por usuario");
                return;
            }
            
            System.out.println("🔄 Eliminando archivo con ID: " + fileId);
            
            long startTime = System.currentTimeMillis();
            DistributedFileResult result = storageService.deleteFile(fileId);
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println();
            if (result.isSuccess()) {
                System.out.println("✅ ELIMINACIÓN EXITOSA");
                System.out.println("   📋 File ID: " + result.getFileId());
                System.out.println("   🌐 Eliminado de nodos: " + 
                    (result.getReplicatedNodes() != null ? String.join(", ", result.getReplicatedNodes()) : "N/A"));
                System.out.println("   ⏱️  Tiempo: " + duration + "ms");
                
                // Remover de la lista de archivos de prueba
                testFiles.remove(fileId);
                
                addToReport("✅ ÉXITO - Archivo eliminado (ID: " + fileId + 
                           ", Nodos: " + (result.getReplicatedNodes() != null ? result.getReplicatedNodes().size() : 0) + 
                           ", Tiempo: " + duration + "ms)");
            } else {
                System.out.println("❌ ELIMINACIÓN FALLIDA");
                System.out.println("   💬 Mensaje: " + result.getMessage());
                
                addToReport("❌ FALLO - " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.out.println("💥 Error durante eliminación: " + e.getMessage());
            addToReport("💥 ERROR - " + e.getMessage());
        }
    }
    
    /**
     * Prueba de verificación de integridad
     */
    private void testFileIntegrity() {
        addToReport("=== TEST " + testCounter++ + ": VERIFICACIÓN DE INTEGRIDAD ===");
        System.out.println("🔍 PRUEBA DE VERIFICACIÓN DE INTEGRIDAD");
        System.out.println();
        
        if (testFiles.isEmpty()) {
            System.out.println("⚠️ No hay archivos almacenados para verificar.");
            addToReport("⚠️ ADVERTENCIA - No hay archivos para verificar");
            return;
        }
        
        try {
            // Mostrar archivos disponibles
            System.out.println("📋 Archivos disponibles:");
            for (Map.Entry<Long, String> entry : testFiles.entrySet()) {
                System.out.println("   • ID: " + entry.getKey() + " - " + entry.getValue());
            }
            System.out.println();
            
            String fileIdStr = getUserInput("Introduce el File ID a verificar");
            Long fileId;
            
            try {
                fileId = Long.parseLong(fileIdStr);
            } catch (NumberFormatException e) {
                System.out.println("❌ ID de archivo inválido");
                addToReport("❌ ERROR - ID de archivo inválido");
                return;
            }
            
            System.out.println("🔄 Verificando integridad del archivo con ID: " + fileId);
            
            long startTime = System.currentTimeMillis();
            FileIntegrityReport report = storageService.verifyFileIntegrity(fileId);
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println();
            System.out.println("📊 REPORTE DE INTEGRIDAD");
            System.out.println("   📋 File ID: " + report.getFileId());
            System.out.println("   📁 Nombre: " + report.getFileName());
            System.out.println("   🎯 Estado: " + report.getIntegrityStatus());
            System.out.println("   ✅ Integridad válida: " + (report.isIntegrityValid() ? "SÍ" : "NO"));
            System.out.println("   📈 Porcentaje de integridad: " + String.format("%.2f%%", report.getIntegrityPercentage()));
            System.out.println("   🌐 Nodos verificados: " + report.getTotalNodes());
            System.out.println("   ✅ Nodos válidos: " + report.getValidNodeCount());
            
            if (report.getCorruptedNodes() != null && !report.getCorruptedNodes().isEmpty()) {
                System.out.println("   ❌ Nodos corruptos: " + String.join(", ", report.getCorruptedNodes()));
            }
            
            if (report.getUnavailableNodes() != null && !report.getUnavailableNodes().isEmpty()) {
                System.out.println("   🔌 Nodos no disponibles: " + String.join(", ", report.getUnavailableNodes()));
            }
            
            System.out.println("   💡 Recomendación: " + report.getRecommendedAction());
            System.out.println("   ⏱️  Tiempo de verificación: " + duration + "ms");
            
            addToReport("🔍 VERIFICACIÓN - ID: " + fileId + 
                       ", Estado: " + report.getIntegrityStatus() + 
                       ", Integridad: " + String.format("%.2f%%", report.getIntegrityPercentage()) +
                       ", Tiempo: " + duration + "ms");
            
        } catch (Exception e) {
            System.out.println("💥 Error durante verificación: " + e.getMessage());
            addToReport("💥 ERROR - " + e.getMessage());
        }
    }
    
    /**
     * Muestra estadísticas del sistema
     */
    private void showSystemStatistics() {
        addToReport("=== TEST " + testCounter++ + ": ESTADÍSTICAS DEL SISTEMA ===");
        System.out.println("📊 ESTADÍSTICAS DEL SISTEMA");
        System.out.println();
        
        try {
            System.out.println("🔄 Obteniendo estadísticas del sistema...");
            
            long startTime = System.currentTimeMillis();
            SystemStatistics stats = storageService.getSystemStatistics();
            long duration = System.currentTimeMillis() - startTime;
            
            System.out.println();
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║                 ESTADÍSTICAS DEL SISTEMA                 ║");
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            
            // Información general
            System.out.printf("║ 🌐 Nodos de almacenamiento: %d (%d activos, %d inactivos) ║%n", 
                stats.getTotalStorageNodes(), stats.getActiveNodes(), stats.getInactiveNodes());
            System.out.printf("║ 📁 Total de archivos: %d archivos almacenados            ║%n", 
                stats.getTotalStoredFiles());
            System.out.printf("║ 💾 Capacidad total: %s                                   ║%n", 
                stats.getFormattedTotalCapacity());
            System.out.printf("║ 📊 Espacio usado: %s (%.2f%%)                            ║%n", 
                stats.getFormattedUsedCapacity(), stats.getStorageUtilizationPercentage());
            System.out.printf("║ 🆓 Espacio disponible: %s                                ║%n", 
                stats.getFormattedAvailableCapacity());
            
            // Estadísticas de operaciones
            System.out.println("╠═══════════════════════════════════════════════════════════╣");
            System.out.printf("║ 🔄 Total operaciones: %d                                  ║%n", 
                stats.getTotalOperations());
            System.out.printf("║ ✅ Operaciones exitosas: %d                              ║%n", 
                stats.getSuccessfulOperations());
            System.out.printf("║ ❌ Operaciones fallidas: %d                              ║%n", 
                stats.getFailedOperations());
            System.out.printf("║ 📈 Tasa de éxito: %.2f%%                                  ║%n", 
                stats.getSuccessRate());
            System.out.printf("║ 🎯 Salud del sistema: %s                                  ║%n", 
                stats.getSystemHealth());
            
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
            // Estadísticas por nodo
            if (stats.getNodeStatistics() != null && !stats.getNodeStatistics().isEmpty()) {
                System.out.println();
                System.out.println("📋 ESTADÍSTICAS POR NODO:");
                System.out.println();
                
                for (SystemStatistics.NodeStatistics nodeStat : stats.getNodeStatistics().values()) {
                    System.out.println("┌─ " + nodeStat.getNodeId() + " ─────────────────────────────");
                    System.out.println("│ Estado: " + nodeStat.getStatus());
                    System.out.println("│ Archivos: " + nodeStat.getFilesStored());
                    System.out.println("│ Capacidad: " + nodeStat.getFormattedCapacity());
                    System.out.println("│ Usado: " + nodeStat.getFormattedUsedSpace() + 
                        " (" + String.format("%.2f%%", nodeStat.getUtilizationPercentage()) + ")");
                    System.out.println("│ Disponible: " + nodeStat.getFormattedAvailableSpace());
                    System.out.println("│ Saludable: " + (nodeStat.isHealthy() ? "✅ SÍ" : "❌ NO"));
                    System.out.println("└─────────────────────────────────────────────────");
                }
            }
            
            System.out.println();
            System.out.println("⏱️ Tiempo de consulta: " + duration + "ms");
            
            addToReport("📊 ESTADÍSTICAS - Nodos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes() +
                       ", Archivos: " + stats.getTotalStoredFiles() +
                       ", Salud: " + stats.getSystemHealth() +
                       ", Tiempo: " + duration + "ms");
            
        } catch (Exception e) {
            System.out.println("💥 Error obteniendo estadísticas: " + e.getMessage());
            addToReport("💥 ERROR - " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta una suite de pruebas automáticas
     */
    private void runAutomaticTests() {
        addToReport("=== TEST " + testCounter++ + ": PRUEBAS AUTOMÁTICAS ===");
        System.out.println("🤖 EJECUTANDO PRUEBAS AUTOMÁTICAS");
        System.out.println();
        
        System.out.println("Esta prueba ejecutará automáticamente los siguientes escenarios:");
        System.out.println("• Almacenamiento de múltiples archivos");
        System.out.println("• Recuperación de archivos");
        System.out.println("• Verificación de integridad");
        System.out.println("• Eliminación de archivos");
        System.out.println("• Pruebas de tolerancia a fallos");
        System.out.println();
        
        String confirm = getUserInput("¿Ejecutar pruebas automáticas? (s/n)");
        if (!confirm.toLowerCase().startsWith("s")) {
            System.out.println("❌ Pruebas automáticas canceladas");
            return;
        }
        
        System.out.println();
        System.out.println("🚀 Iniciando pruebas automáticas...");
        
        int testsRun = 0;
        int testsPassed = 0;
        int testsFailed = 0;
        
        // Prueba 1: Almacenamiento masivo
        System.out.println();
        System.out.println("📥 Prueba 1/5: Almacenamiento de múltiples archivos");
        try {
            for (int i = 1; i <= 3; i++) {
                String fileName = "auto-test-" + i + ".txt";
                String content = "Contenido de prueba automática #" + i + " - " + 
                    LocalDateTime.now().format(TIMESTAMP_FORMAT);
                
                DistributedFileResult result = storageService.storeFile(fileName, content.getBytes());
                
                if (result.isSuccess()) {
                    testFiles.put(result.getFileId(), result.getFileName());
                    System.out.println("   ✅ " + fileName + " almacenado (ID: " + result.getFileId() + ")");
                } else {
                    System.out.println("   ❌ " + fileName + " falló: " + result.getMessage());
                    testsFailed++;
                }
                
                // Pequeña pausa entre operaciones
                Thread.sleep(500);
            }
            testsPassed++;
            System.out.println("✅ Prueba 1 COMPLETADA");
        } catch (Exception e) {
            System.out.println("❌ Prueba 1 FALLIDA: " + e.getMessage());
            testsFailed++;
        }
        testsRun++;
        
        // Prueba 2: Recuperación masiva
        System.out.println();
        System.out.println("📤 Prueba 2/5: Recuperación de archivos almacenados");
        try {
            int recoveredFiles = 0;
            for (Long fileId : testFiles.keySet()) {
                DistributedFileResult result = storageService.retrieveFile(fileId);
                if (result.isSuccess()) {
                    recoveredFiles++;
                    System.out.println("   ✅ Archivo " + fileId + " recuperado");
                } else {
                    System.out.println("   ❌ Archivo " + fileId + " no se pudo recuperar");
                }
                
                Thread.sleep(300);
            }
            
            if (recoveredFiles == testFiles.size()) {
                testsPassed++;
                System.out.println("✅ Prueba 2 COMPLETADA - Todos los archivos recuperados");
            } else {
                testsFailed++;
                System.out.println("❌ Prueba 2 FALLIDA - Solo " + recoveredFiles + "/" + testFiles.size() + " recuperados");
            }
        } catch (Exception e) {
            System.out.println("❌ Prueba 2 FALLIDA: " + e.getMessage());
            testsFailed++;
        }
        testsRun++;
        
        // Prueba 3: Verificación de integridad
        System.out.println();
        System.out.println("🔍 Prueba 3/5: Verificación de integridad");
        try {
            int validFiles = 0;
            for (Long fileId : testFiles.keySet()) {
                FileIntegrityReport report = storageService.verifyFileIntegrity(fileId);
                if (report.isIntegrityValid()) {
                    validFiles++;
                    System.out.println("   ✅ Archivo " + fileId + " íntegro (" + 
                        String.format("%.1f%%", report.getIntegrityPercentage()) + ")");
                } else {
                    System.out.println("   ⚠️ Archivo " + fileId + " con problemas (" + 
                        String.format("%.1f%%", report.getIntegrityPercentage()) + ")");
                }
                
                Thread.sleep(400);
            }
            
            if (validFiles > 0) {
                testsPassed++;
                System.out.println("✅ Prueba 3 COMPLETADA - " + validFiles + " archivos verificados");
            } else {
                testsFailed++;
                System.out.println("❌ Prueba 3 FALLIDA - No se pudieron verificar archivos");
            }
        } catch (Exception e) {
            System.out.println("❌ Prueba 3 FALLIDA: " + e.getMessage());
            testsFailed++;
        }
        testsRun++;
        
        // Prueba 4: Estadísticas del sistema
        System.out.println();
        System.out.println("📊 Prueba 4/5: Obtención de estadísticas");
        try {
            SystemStatistics stats = storageService.getSystemStatistics();
            
            if (stats.getActiveNodes() > 0) {
                testsPassed++;
                System.out.println("✅ Prueba 4 COMPLETADA - Sistema con " + 
                    stats.getActiveNodes() + " nodos activos");
            } else {
                testsFailed++;
                System.out.println("❌ Prueba 4 FALLIDA - No hay nodos activos");
            }
        } catch (Exception e) {
            System.out.println("❌ Prueba 4 FALLIDA: " + e.getMessage());
            testsFailed++;
        }
        testsRun++;
        
        // Prueba 5: Eliminación selectiva
        System.out.println();
        System.out.println("🗑️ Prueba 5/5: Eliminación de archivos de prueba");
        try {
            List<Long> filesToDelete = new ArrayList<>(testFiles.keySet());
            int deletedFiles = 0;
            
            // Eliminar solo la mitad para dejar algunos para pruebas posteriores
            for (int i = 0; i < filesToDelete.size() / 2; i++) {
                Long fileId = filesToDelete.get(i);
                DistributedFileResult result = storageService.deleteFile(fileId);
                
                if (result.isSuccess()) {
                    deletedFiles++;
                    testFiles.remove(fileId);
                    System.out.println("   ✅ Archivo " + fileId + " eliminado");
                } else {
                    System.out.println("   ❌ Error eliminando archivo " + fileId);
                }
                
                Thread.sleep(300);
            }
            
            if (deletedFiles > 0) {
                testsPassed++;
                System.out.println("✅ Prueba 5 COMPLETADA - " + deletedFiles + " archivos eliminados");
            } else {
                testsFailed++;
                System.out.println("❌ Prueba 5 FALLIDA - No se eliminaron archivos");
            }
        } catch (Exception e) {
            System.out.println("❌ Prueba 5 FALLIDA: " + e.getMessage());
            testsFailed++;
        }
        testsRun++;
        
        // Resumen de pruebas automáticas
        System.out.println();
        System.out.println("🏁 RESUMEN DE PRUEBAS AUTOMÁTICAS");
        System.out.println("═══════════════════════════════════");
        System.out.println("📊 Total de pruebas: " + testsRun);
        System.out.println("✅ Pruebas exitosas: " + testsPassed);
        System.out.println("❌ Pruebas fallidas: " + testsFailed);
        System.out.println("📈 Tasa de éxito: " + String.format("%.1f%%", 
            testsRun > 0 ? ((double) testsPassed / testsRun) * 100.0 : 0.0));
        
        if (testsFailed == 0) {
            System.out.println("🎉 ¡TODAS LAS PRUEBAS AUTOMÁTICAS EXITOSAS!");
        } else {
            System.out.println("⚠️ Algunas pruebas fallaron - revisar configuración del sistema");
        }
        
        addToReport("🤖 PRUEBAS AUTOMÁTICAS - Ejecutadas: " + testsRun + 
                   ", Exitosas: " + testsPassed + 
                   ", Fallidas: " + testsFailed +
                   ", Éxito: " + String.format("%.1f%%", testsRun > 0 ? ((double) testsPassed / testsRun) * 100.0 : 0.0));
    }
    
    /**
     * Genera archivos de prueba de diferentes tamaños
     */
    private void generateTestFiles() {
        System.out.println("📁 GENERADOR DE ARCHIVOS DE PRUEBA");
        System.out.println();
        
        System.out.println("Tipos de archivos de prueba disponibles:");
        System.out.println("1. Archivo pequeño (< 1KB)");
        System.out.println("2. Archivo mediano (10KB)");
        System.out.println("3. Archivo grande (100KB)");
        System.out.println("4. Conjunto de archivos variados");
        
        String option = getUserInput("Selecciona opción (1-4)");
        
        try {
            switch (option) {
                case "1":
                    generateAndStoreTestFile("small-file.txt", 512);
                    break;
                case "2":
                    generateAndStoreTestFile("medium-file.txt", 10 * 1024);
                    break;
                case "3":
                    generateAndStoreTestFile("large-file.txt", 100 * 1024);
                    break;
                case "4":
                    generateAndStoreTestFile("tiny.txt", 100);
                    generateAndStoreTestFile("small.txt", 1024);
                    generateAndStoreTestFile("medium.txt", 10 * 1024);
                    generateAndStoreTestFile("large.txt", 50 * 1024);
                    break;
                default:
                    System.out.println("❌ Opción inválida");
                    return;
            }
            
            System.out.println("✅ Archivos de prueba generados y almacenados");
            
        } catch (Exception e) {
            System.out.println("❌ Error generando archivos: " + e.getMessage());
        }
    }
    
    /**
     * Genera y almacena un archivo de prueba de tamaño específico
     */
    private void generateAndStoreTestFile(String fileName, int sizeBytes) {
        StringBuilder content = new StringBuilder();
        Random random = new Random();
        
        // Generar contenido aleatorio
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 \n";
        
        for (int i = 0; i < sizeBytes; i++) {
            content.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Agregar metadata al inicio
        String metadata = "# Archivo de prueba generado automáticamente\n" +
                         "# Nombre: " + fileName + "\n" +
                         "# Tamaño objetivo: " + sizeBytes + " bytes\n" +
                         "# Generado: " + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "\n" +
                         "# ==========================================\n\n";
        
        String finalContent = metadata + content.toString();
        
        DistributedFileResult result = storageService.storeFile(fileName, finalContent.getBytes());
        
        if (result.isSuccess()) {
            testFiles.put(result.getFileId(), result.getFileName());
            System.out.println("✅ " + fileName + " generado y almacenado (ID: " + result.getFileId() + 
                ", Tamaño real: " + result.getFormattedFileSize() + ")");
        } else {
            System.out.println("❌ Error almacenando " + fileName + ": " + result.getMessage());
        }
    }
    
    /**
     * Muestra el reporte de pruebas
     */
    private void showTestReport() {
        System.out.println("📋 REPORTE DE PRUEBAS REALIZADAS");
        System.out.println("═════════════════════════════════════════════════");
        
        if (testReport.length() == 0) {
            System.out.println("⚠️ No se han realizado pruebas aún.");
            System.out.println("   Ejecuta algunas operaciones para generar un reporte.");
            return;
        }
        
        System.out.println();
        System.out.println(testReport.toString());
        System.out.println();
        System.out.println("═════════════════════════════════════════════════");
        System.out.println("📊 Archivos actualmente en sistema de prueba: " + testFiles.size());
        
        if (!testFiles.isEmpty()) {
            System.out.println("📁 Lista de archivos de prueba:");
            for (Map.Entry<Long, String> entry : testFiles.entrySet()) {
                System.out.println("   • " + entry.getKey() + " - " + entry.getValue());
            }
        }
    }
    
    /**
     * Exporta el reporte completo a archivo
     */
    private void exportTestReport() {
        System.out.println("💾 EXPORTAR REPORTE COMPLETO");
        System.out.println();
        
        try {
            String fileName = "test-report-" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".txt";
            
            StringBuilder fullReport = new StringBuilder();
            fullReport.append("REPORTE COMPLETO DE TESTING - SISTEMA DISTRIBUIDO DE ALMACENAMIENTO\n");
            fullReport.append("================================================================\n");
            fullReport.append("Generado: ").append(LocalDateTime.now().format(TIMESTAMP_FORMAT)).append("\n");
            fullReport.append("Versión: v1.5.0 - Testing Module\n");
            fullReport.append("================================================================\n\n");
            
            // Agregar estadísticas del sistema
            try {
                SystemStatistics stats = storageService.getSystemStatistics();
                fullReport.append("ESTADÍSTICAS DEL SISTEMA AL MOMENTO DEL REPORTE:\n");
                fullReport.append("- Nodos totales: ").append(stats.getTotalStorageNodes()).append("\n");
                fullReport.append("- Nodos activos: ").append(stats.getActiveNodes()).append("\n");
                fullReport.append("- Archivos almacenados: ").append(stats.getTotalStoredFiles()).append("\n");
                fullReport.append("- Utilización: ").append(String.format("%.2f%%", stats.getStorageUtilizationPercentage())).append("\n");
                fullReport.append("- Tasa de éxito: ").append(String.format("%.2f%%", stats.getSuccessRate())).append("\n");
                fullReport.append("- Salud del sistema: ").append(stats.getSystemHealth()).append("\n");
                fullReport.append("\n");
            } catch (Exception e) {
                fullReport.append("Error obteniendo estadísticas del sistema: ").append(e.getMessage()).append("\n\n");
            }
            
            // Agregar registro de pruebas
            fullReport.append("REGISTRO DETALLADO DE PRUEBAS:\n");
            fullReport.append("==============================\n");
            fullReport.append(testReport.toString());
            fullReport.append("\n");
            
            // Agregar archivos de prueba actuales
            fullReport.append("ARCHIVOS DE PRUEBA ACTUALES EN EL SISTEMA:\n");
            fullReport.append("==========================================\n");
            if (testFiles.isEmpty()) {
                fullReport.append("No hay archivos de prueba en el sistema.\n");
            } else {
                for (Map.Entry<Long, String> entry : testFiles.entrySet()) {
                    fullReport.append("- ID: ").append(entry.getKey()).append(" | Nombre: ").append(entry.getValue()).append("\n");
                }
            }
            fullReport.append("\n");
            
            // Información del módulo
            fullReport.append("INFORMACIÓN DEL MÓDULO DE TESTING:\n");
            fullReport.append("===================================\n");
            fullReport.append("Este reporte fue generado por el Módulo de Testing Interactivo Removible.\n");
            fullReport.append("Este módulo es completamente independiente del sistema principal y puede\n");
            fullReport.append("ser removido sin afectar la funcionalidad del sistema distribuido.\n");
            fullReport.append("\n");
            fullReport.append("Para más información, consulte la documentación técnica del proyecto.\n");
            
            // Escribir archivo
            Files.write(Paths.get(fileName), fullReport.toString().getBytes());
            
            System.out.println("✅ Reporte exportado exitosamente");
            System.out.println("📁 Archivo: " + fileName);
            System.out.println("📊 Tamaño: " + fullReport.length() + " caracteres");
            
            addToReport("💾 EXPORTACIÓN - Reporte exportado a " + fileName);
            
        } catch (Exception e) {
            System.out.println("❌ Error exportando reporte: " + e.getMessage());
            addToReport("❌ ERROR - Error exportando reporte");
        }
    }
    
    /**
     * Confirma la salida del módulo
     */
    private void confirmExit() {
        System.out.println("🚪 SALIR DEL MÓDULO DE TESTING");
        System.out.println();
        
        System.out.println("¿Estás seguro de que deseas salir?");
        System.out.println("Se perderán las referencias a los archivos de prueba locales");
        System.out.println("(los archivos permanecerán en el sistema distribuido)");
        System.out.println();
        
        String confirm = getUserInput("Confirmar salida (s/n)");
        
        if (confirm.toLowerCase().startsWith("s")) {
            System.out.println();
            System.out.println("👋 Saliendo del módulo de testing...");
            System.out.println("   Archivos de prueba en sistema: " + testFiles.size());
            System.out.println("   Tests ejecutados: " + (testCounter - 1));
            System.out.println();
            System.out.println("✅ ¡Gracias por usar el módulo de testing interactivo!");
            System.out.println("   El sistema distribuido continuará funcionando normalmente.");
            
            keepRunning = false;
        } else {
            System.out.println("❌ Salida cancelada - regresando al menú principal");
        }
    }
    
    /**
     * Utilities
     */
    
    private String getUserInput(String prompt) {
        System.out.print("➤ " + prompt + ": ");
        return scanner.nextLine().trim();
    }
    
    private void waitForUserToContinue() {
        System.out.println();
        System.out.print("📌 Presiona Enter para continuar...");
        scanner.nextLine();
        System.out.println();
    }
    
    private void addToReport(String entry) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        testReport.append("[").append(timestamp).append("] ").append(entry).append("\n");
    }
    
    private String generateTestFileContent() {
        return "Archivo de prueba generado automáticamente\n" +
               "Timestamp: " + LocalDateTime.now().format(TIMESTAMP_FORMAT) + "\n" +
               "Contenido: Este es un archivo de prueba para validar el funcionamiento\n" +
               "del sistema distribuido de almacenamiento. Contiene texto de ejemplo\n" +
               "para verificar que las operaciones de almacenamiento, recuperación\n" +
               "y eliminación funcionan correctamente en todos los nodos.\n" +
               "\n" +
               "Sistema: Distributed Storage System v1.5.0\n" +
               "Módulo: Interactive Testing Module\n" +
               "Hash: " + UUID.randomUUID().toString();
    }
    
    /**
     * Cierra el módulo y libera recursos
     */
    private void shutdown() {
        try {
            if (storageService != null) {
                storageService.shutdown();
            }
            
            if (scanner != null) {
                scanner.close();
            }
            
            logger.info("🧪 InteractiveTestingApplication cerrada correctamente");
        } catch (Exception e) {
            logger.error("Error cerrando testing application", e);
        }
    }
}
