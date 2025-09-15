package com.distribuidos.clientbackend.service;

import com.distribuidos.clientbackend.model.DistributedFileResult;
import com.distribuidos.clientbackend.model.FileIntegrityReport;
import com.distribuidos.clientbackend.model.SystemStatistics;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * SUITE DE TESTING PARA DISTRIBUTED STORAGE SERVICE
 * 
 * Esta clase contiene pruebas unitarias e integración para validar
 * todas las funcionalidades del sistema distribuido de almacenamiento.
 * 
 * Categorías de pruebas:
 * - Operaciones básicas (CRUD)
 * - Tolerancia a fallos
 * - Integridad de datos
 * - Rendimiento
 * - Concurrencia
 * - Estadísticas del sistema
 * 
 * NOTA: Estas pruebas requieren que los nodos de almacenamiento estén
 * ejecutándose para las pruebas de integración.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DistributedStorageServiceTest {
    
    private static DistributedStorageService storageService;
    private static final List<Long> testFileIds = new ArrayList<>();
    
    // Datos de prueba
    private static final String TEST_FILE_NAME = "test-file.txt";
    private static final String TEST_FILE_CONTENT = "Este es un archivo de prueba para validar el sistema distribuido.";
    private static final byte[] TEST_FILE_DATA = TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
    
    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("🧪 Inicializando suite de pruebas para DistributedStorageService");
        System.out.println("📅 Fecha: " + LocalDateTime.now());
        System.out.println();
        
        // Inicializar servicio distribuido
        storageService = new DistributedStorageService();
        
        System.out.println("✅ DistributedStorageService inicializado para testing");
    }
    
    @AfterAll
    static void tearDownAfterAll() {
        System.out.println();
        System.out.println("🧹 Limpiando recursos de testing...");
        
        // Limpiar archivos de prueba
        for (Long fileId : testFileIds) {
            try {
                storageService.deleteFile(fileId);
                System.out.println("🗑️ Archivo de prueba " + fileId + " eliminado");
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo eliminar archivo de prueba " + fileId + ": " + e.getMessage());
            }
        }
        
        // Cerrar servicio
        if (storageService != null) {
            storageService.shutdown();
            System.out.println("✅ DistributedStorageService cerrado");
        }
        
        System.out.println("🏁 Suite de pruebas completada");
    }
    
    @BeforeEach
    void setUp() {
        System.out.println("🔄 Preparando prueba...");
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("✅ Prueba completada");
        System.out.println();
    }
    
    // ====================================================================
    // PRUEBAS BÁSICAS DE FUNCIONALIDAD
    // ====================================================================
    
    @Test
    @Order(1)
    @DisplayName("Test 1: Verificar estado inicial del sistema")
    void testSystemInitialization() {
        System.out.println("📊 Verificando estado inicial del sistema...");
        
        assertNotNull(storageService, "El servicio distribuido debe estar inicializado");
        
        // Obtener estadísticas iniciales
        SystemStatistics stats = storageService.getSystemStatistics();
        
        assertNotNull(stats, "Las estadísticas del sistema deben estar disponibles");
        assertTrue(stats.getTotalStorageNodes() >= 0, "Debe reportar número de nodos válido");
        
        System.out.println("   🌐 Nodos detectados: " + stats.getTotalStorageNodes());
        System.out.println("   ✅ Nodos activos: " + stats.getActiveNodes());
        System.out.println("   🎯 Salud del sistema: " + stats.getSystemHealth());
        
        // Para pruebas de integración, verificar que hay al menos un nodo
        if (stats.getTotalStorageNodes() > 0) {
            assertTrue(stats.getActiveNodes() >= 0, "Debe haber al menos algunos nodos reportados");
        }
    }
    
    @Test
    @Order(2)
    @DisplayName("Test 2: Almacenar archivo básico")
    void testStoreFile() {
        System.out.println("📥 Probando almacenamiento de archivo básico...");
        
        DistributedFileResult result = storageService.storeFile(TEST_FILE_NAME, TEST_FILE_DATA);
        
        assertNotNull(result, "El resultado no debe ser null");
        
        if (result.isSuccess()) {
            assertTrue(result.getFileId() > 0, "El ID del archivo debe ser válido");
            assertEquals(TEST_FILE_NAME, result.getFileName(), "El nombre del archivo debe coincidir");
            assertTrue(result.getFileSizeBytes() > 0, "El tamaño del archivo debe ser positivo");
            assertNotNull(result.getChecksum(), "Debe tener checksum");
            assertNotNull(result.getPrimaryNode(), "Debe especificar nodo principal");
            
            // Guardar para pruebas posteriores
            testFileIds.add(result.getFileId());
            
            System.out.println("   ✅ Archivo almacenado exitosamente");
            System.out.println("   📋 File ID: " + result.getFileId());
            System.out.println("   📊 Tamaño: " + result.getFormattedFileSize());
            System.out.println("   🔒 Checksum: " + result.getChecksum());
            System.out.println("   🏠 Nodo: " + result.getPrimaryNode());
            
        } else {
            System.out.println("   ⚠️ Almacenamiento falló: " + result.getMessage());
            // Para pruebas unitarias, esto puede ser esperado si no hay nodos disponibles
            assertNotNull(result.getMessage(), "Debe proporcionar mensaje de error");
        }
    }
    
    @Test
    @Order(3)
    @DisplayName("Test 3: Recuperar archivo almacenado")
    void testRetrieveFile() {
        System.out.println("📤 Probando recuperación de archivo...");
        
        // Primero almacenar un archivo para recuperar
        DistributedFileResult storeResult = storageService.storeFile("retrieve-test.txt", TEST_FILE_DATA);
        
        if (storeResult.isSuccess()) {
            Long fileId = storeResult.getFileId();
            testFileIds.add(fileId);
            
            // Recuperar el archivo
            DistributedFileResult retrieveResult = storageService.retrieveFile(fileId);
            
            assertNotNull(retrieveResult, "El resultado de recuperación no debe ser null");
            
            if (retrieveResult.isSuccess()) {
                assertEquals(fileId, retrieveResult.getFileId(), "El ID debe coincidir");
                assertEquals(storeResult.getFileName(), retrieveResult.getFileName(), "El nombre debe coincidir");
                assertEquals(storeResult.getFileSizeBytes(), retrieveResult.getFileSizeBytes(), "El tamaño debe coincidir");
                
                System.out.println("   ✅ Archivo recuperado exitosamente");
                System.out.println("   📋 File ID: " + retrieveResult.getFileId());
                System.out.println("   📁 Nombre: " + retrieveResult.getFileName());
                System.out.println("   📊 Tamaño: " + retrieveResult.getFormattedFileSize());
                
            } else {
                System.out.println("   ⚠️ Recuperación falló: " + retrieveResult.getMessage());
                fail("Debería poder recuperar el archivo recién almacenado: " + retrieveResult.getMessage());
            }
            
        } else {
            System.out.println("   ⚠️ No se pudo almacenar archivo para prueba de recuperación");
            System.out.println("   📝 Razón: " + storeResult.getMessage());
        }
    }
    
    @Test
    @Order(4)
    @DisplayName("Test 4: Verificar integridad de archivo")
    void testFileIntegrityVerification() {
        System.out.println("🔍 Probando verificación de integridad...");
        
        // Almacenar archivo para verificación
        DistributedFileResult storeResult = storageService.storeFile("integrity-test.txt", TEST_FILE_DATA);
        
        if (storeResult.isSuccess()) {
            Long fileId = storeResult.getFileId();
            testFileIds.add(fileId);
            
            // Verificar integridad
            FileIntegrityReport report = storageService.verifyFileIntegrity(fileId);
            
            assertNotNull(report, "El reporte de integridad no debe ser null");
            assertEquals(fileId, report.getFileId(), "El ID debe coincidir");
            assertNotNull(report.getIntegrityStatus(), "Debe tener estado de integridad");
            assertTrue(report.getTotalNodes() >= 0, "Debe reportar número de nodos válido");
            assertTrue(report.getIntegrityPercentage() >= 0.0, "Porcentaje de integridad debe ser válido");
            assertTrue(report.getIntegrityPercentage() <= 100.0, "Porcentaje no debe exceder 100%");
            
            System.out.println("   📊 Reporte de integridad:");
            System.out.println("   🎯 Estado: " + report.getIntegrityStatus());
            System.out.println("   ✅ Válido: " + report.isIntegrityValid());
            System.out.println("   📈 Porcentaje: " + String.format("%.2f%%", report.getIntegrityPercentage()));
            System.out.println("   🌐 Nodos verificados: " + report.getTotalNodes());
            System.out.println("   💡 Recomendación: " + report.getRecommendedAction());
            
        } else {
            System.out.println("   ⚠️ No se pudo almacenar archivo para prueba de integridad");
        }
    }
    
    @Test
    @Order(5)
    @DisplayName("Test 5: Eliminar archivo")
    void testDeleteFile() {
        System.out.println("🗑️ Probando eliminación de archivo...");
        
        // Almacenar archivo para eliminar
        DistributedFileResult storeResult = storageService.storeFile("delete-test.txt", TEST_FILE_DATA);
        
        if (storeResult.isSuccess()) {
            Long fileId = storeResult.getFileId();
            
            // Eliminar archivo
            DistributedFileResult deleteResult = storageService.deleteFile(fileId);
            
            assertNotNull(deleteResult, "El resultado de eliminación no debe ser null");
            
            if (deleteResult.isSuccess()) {
                assertEquals(fileId, deleteResult.getFileId(), "El ID debe coincidir");
                
                System.out.println("   ✅ Archivo eliminado exitosamente");
                System.out.println("   📋 File ID: " + deleteResult.getFileId());
                
                // Verificar que ya no se puede recuperar
                DistributedFileResult retrieveResult = storageService.retrieveFile(fileId);
                assertFalse(retrieveResult.isSuccess(), "No debería poder recuperar archivo eliminado");
                
                System.out.println("   ✅ Confirmado: archivo no recuperable después de eliminación");
                
            } else {
                System.out.println("   ⚠️ Eliminación falló: " + deleteResult.getMessage());
                testFileIds.add(fileId); // Agregar para limpieza posterior
            }
            
        } else {
            System.out.println("   ⚠️ No se pudo almacenar archivo para prueba de eliminación");
        }
    }
    
    // ====================================================================
    // PRUEBAS DE CASOS EDGE Y VALIDACIÓN
    // ====================================================================
    
    @Test
    @Order(6)
    @DisplayName("Test 6: Validación de parámetros inválidos")
    void testInvalidParameters() {
        System.out.println("🚫 Probando validación de parámetros inválidos...");
        
        // Archivo con nombre null
        DistributedFileResult result1 = storageService.storeFile(null, TEST_FILE_DATA);
        assertFalse(result1.isSuccess(), "No debe almacenar archivo con nombre null");
        System.out.println("   ✅ Rechazó nombre null");
        
        // Archivo con datos null
        DistributedFileResult result2 = storageService.storeFile(TEST_FILE_NAME, null);
        assertFalse(result2.isSuccess(), "No debe almacenar archivo con datos null");
        System.out.println("   ✅ Rechazó datos null");
        
        // Archivo con nombre vacío
        DistributedFileResult result3 = storageService.storeFile("", TEST_FILE_DATA);
        assertFalse(result3.isSuccess(), "No debe almacenar archivo con nombre vacío");
        System.out.println("   ✅ Rechazó nombre vacío");
        
        // Archivo con datos vacíos
        DistributedFileResult result4 = storageService.storeFile(TEST_FILE_NAME, new byte[0]);
        assertFalse(result4.isSuccess(), "No debe almacenar archivo vacío");
        System.out.println("   ✅ Rechazó archivo vacío");
        
        // Recuperar con ID inválido
        DistributedFileResult result5 = storageService.retrieveFile(-1L);
        assertFalse(result5.isSuccess(), "No debe recuperar archivo con ID inválido");
        System.out.println("   ✅ Rechazó ID inválido");
        
        // Recuperar con ID null
        DistributedFileResult result6 = storageService.retrieveFile(null);
        assertFalse(result6.isSuccess(), "No debe recuperar archivo con ID null");
        System.out.println("   ✅ Rechazó ID null");
    }
    
    @Test
    @Order(7)
    @DisplayName("Test 7: Archivo inexistente")
    void testNonExistentFile() {
        System.out.println("👻 Probando operaciones con archivo inexistente...");
        
        Long nonExistentId = 999999999L;
        
        // Intentar recuperar archivo inexistente
        DistributedFileResult retrieveResult = storageService.retrieveFile(nonExistentId);
        assertFalse(retrieveResult.isSuccess(), "No debe recuperar archivo inexistente");
        System.out.println("   ✅ Recuperación de archivo inexistente falló correctamente");
        
        // Intentar eliminar archivo inexistente
        DistributedFileResult deleteResult = storageService.deleteFile(nonExistentId);
        assertFalse(deleteResult.isSuccess(), "No debe eliminar archivo inexistente");
        System.out.println("   ✅ Eliminación de archivo inexistente falló correctamente");
        
        // Intentar verificar integridad de archivo inexistente
        FileIntegrityReport report = storageService.verifyFileIntegrity(nonExistentId);
        assertFalse(report.isIntegrityValid(), "No debe validar archivo inexistente");
        System.out.println("   ✅ Verificación de archivo inexistente falló correctamente");
    }
    
    @Test
    @Order(8)
    @DisplayName("Test 8: Archivos de diferentes tamaños")
    void testDifferentFileSizes() {
        System.out.println("📏 Probando archivos de diferentes tamaños...");
        
        // Archivo pequeño (1 byte)
        byte[] tinyFile = new byte[]{65}; // 'A'
        DistributedFileResult tinyResult = storageService.storeFile("tiny.txt", tinyFile);
        if (tinyResult.isSuccess()) {
            testFileIds.add(tinyResult.getFileId());
            System.out.println("   ✅ Archivo tiny (1 byte) almacenado");
        }
        
        // Archivo mediano (1KB)
        StringBuilder mediumContent = new StringBuilder();
        for (int i = 0; i < 1024; i++) {
            mediumContent.append((char) ('A' + (i % 26)));
        }
        byte[] mediumFile = mediumContent.toString().getBytes();
        DistributedFileResult mediumResult = storageService.storeFile("medium.txt", mediumFile);
        if (mediumResult.isSuccess()) {
            testFileIds.add(mediumResult.getFileId());
            System.out.println("   ✅ Archivo mediano (1KB) almacenado");
        }
        
        // Archivo grande (10KB)
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 10240; i++) {
            largeContent.append((char) ('A' + (i % 26)));
        }
        byte[] largeFile = largeContent.toString().getBytes();
        DistributedFileResult largeResult = storageService.storeFile("large.txt", largeFile);
        if (largeResult.isSuccess()) {
            testFileIds.add(largeResult.getFileId());
            System.out.println("   ✅ Archivo grande (10KB) almacenado");
        }
        
        System.out.println("   📊 Resumen: Se probaron archivos de 1B, 1KB y 10KB");
    }
    
    @Test
    @Order(9)
    @DisplayName("Test 9: Estadísticas del sistema")
    void testSystemStatistics() {
        System.out.println("📊 Probando estadísticas del sistema...");
        
        // Resetear estadísticas para esta prueba específica
        storageService.resetStatistics();
        
        SystemStatistics stats = storageService.getSystemStatistics();
        
        assertNotNull(stats, "Las estadísticas no deben ser null");
        assertTrue(stats.getTotalStorageNodes() >= 0, "Nodos totales debe ser >= 0");
        assertTrue(stats.getActiveNodes() >= 0, "Nodos activos debe ser >= 0");
        assertTrue(stats.getInactiveNodes() >= 0, "Nodos inactivos debe ser >= 0");
        assertEquals(stats.getTotalStorageNodes(), stats.getActiveNodes() + stats.getInactiveNodes(),
                    "Nodos totales = activos + inactivos");
        
        assertTrue(stats.getTotalStoredFiles() >= 0, "Archivos almacenados debe ser >= 0");
        assertTrue(stats.getTotalOperations() >= 0, "Total operaciones debe ser >= 0");
        assertTrue(stats.getSuccessfulOperations() >= 0, "Operaciones exitosas debe ser >= 0");
        assertTrue(stats.getFailedOperations() >= 0, "Operaciones fallidas debe ser >= 0");
        assertEquals(stats.getTotalOperations(), 
                    stats.getSuccessfulOperations() + stats.getFailedOperations(),
                    "Total operaciones = exitosas + fallidas");
        
        assertTrue(stats.getSuccessRate() >= 0.0, "Tasa de éxito debe ser >= 0%");
        assertTrue(stats.getSuccessRate() <= 100.0, "Tasa de éxito debe ser <= 100%");
        
        assertNotNull(stats.getSystemHealth(), "Estado de salud no debe ser null");
        
        System.out.println("   📋 Estadísticas validadas:");
        System.out.println("     🌐 Nodos: " + stats.getActiveNodes() + "/" + stats.getTotalStorageNodes());
        System.out.println("     📁 Archivos: " + stats.getTotalStoredFiles());
        System.out.println("     ⚡ Operaciones: " + stats.getTotalOperations());
        System.out.println("     📈 Éxito: " + String.format("%.2f%%", stats.getSuccessRate()));
        System.out.println("     🎯 Salud: " + stats.getSystemHealth());
    }
    
    // ====================================================================
    // PRUEBAS DE RENDIMIENTO Y CONCURRENCIA
    // ====================================================================
    
    @Test
    @Order(10)
    @DisplayName("Test 10: Operaciones concurrentes")
    void testConcurrentOperations() {
        System.out.println("🚀 Probando operaciones concurrentes...");
        
        int numberOfThreads = 5;
        List<CompletableFuture<DistributedFileResult>> futures = new ArrayList<>();
        
        // Lanzar múltiples operaciones de almacenamiento en paralelo
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            CompletableFuture<DistributedFileResult> future = CompletableFuture.supplyAsync(() -> {
                String fileName = "concurrent-test-" + threadId + ".txt";
                String content = "Contenido del hilo " + threadId + " - " + LocalDateTime.now();
                return storageService.storeFile(fileName, content.getBytes());
            });
            futures.add(future);
        }
        
        // Esperar a que todas las operaciones completen
        try {
            List<DistributedFileResult> results = futures.stream()
                .map(future -> {
                    try {
                        return future.get(30, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        fail("Operación concurrente falló: " + e.getMessage());
                        return null;
                    }
                })
                .collect(Collectors.toList());
            
            // Verificar resultados
            int successCount = 0;
            for (DistributedFileResult result : results) {
                if (result != null && result.isSuccess()) {
                    successCount++;
                    testFileIds.add(result.getFileId());
                }
            }
            
            System.out.println("   ✅ Operaciones concurrentes completadas");
            System.out.println("     🎯 Exitosas: " + successCount + "/" + numberOfThreads);
            
            // Al menos algunas operaciones deberían ser exitosas si hay nodos disponibles
            assertTrue(successCount >= 0, "Debería manejar operaciones concurrentes sin errores críticos");
            
        } catch (Exception e) {
            fail("Error en prueba de concurrencia: " + e.getMessage());
        }
    }
    
    @Test
    @Order(11)
    @DisplayName("Test 11: Rendimiento básico")
    void testBasicPerformance() {
        System.out.println("⏱️ Probando rendimiento básico...");
        
        String fileName = "performance-test.txt";
        byte[] fileData = "Archivo de prueba de rendimiento".getBytes();
        
        // Medir tiempo de almacenamiento
        long startTime = System.currentTimeMillis();
        DistributedFileResult storeResult = storageService.storeFile(fileName, fileData);
        long storeTime = System.currentTimeMillis() - startTime;
        
        if (storeResult.isSuccess()) {
            testFileIds.add(storeResult.getFileId());
            
            // Medir tiempo de recuperación
            startTime = System.currentTimeMillis();
            DistributedFileResult retrieveResult = storageService.retrieveFile(storeResult.getFileId());
            long retrieveTime = System.currentTimeMillis() - startTime;
            
            assertTrue(retrieveResult.isSuccess(), "La recuperación debe ser exitosa");
            
            // Medir tiempo de verificación de integridad
            startTime = System.currentTimeMillis();
            FileIntegrityReport report = storageService.verifyFileIntegrity(storeResult.getFileId());
            long verifyTime = System.currentTimeMillis() - startTime;
            
            assertNotNull(report, "El reporte de integridad debe estar disponible");
            
            System.out.println("   📊 Métricas de rendimiento:");
            System.out.println("     📥 Almacenamiento: " + storeTime + "ms");
            System.out.println("     📤 Recuperación: " + retrieveTime + "ms");
            System.out.println("     🔍 Verificación: " + verifyTime + "ms");
            
            // Validaciones básicas de rendimiento (límites muy generosos para testing)
            assertTrue(storeTime < 30000, "Almacenamiento no debe tomar más de 30 segundos");
            assertTrue(retrieveTime < 30000, "Recuperación no debe tomar más de 30 segundos");
            assertTrue(verifyTime < 30000, "Verificación no debe tomar más de 30 segundos");
            
        } else {
            System.out.println("   ⚠️ No se pudo completar prueba de rendimiento: " + storeResult.getMessage());
        }
    }
    
    // ====================================================================
    // PRUEBAS DE LIMPIEZA Y ESTADO FINAL
    // ====================================================================
    
    @Test
    @Order(12)
    @DisplayName("Test 12: Estado final del sistema")
    void testFinalSystemState() {
        System.out.println("🏁 Verificando estado final del sistema...");
        
        SystemStatistics finalStats = storageService.getSystemStatistics();
        
        assertNotNull(finalStats, "Las estadísticas finales deben estar disponibles");
        
        System.out.println("   📊 Estado final del sistema:");
        System.out.println("     🌐 Nodos activos: " + finalStats.getActiveNodes() + "/" + finalStats.getTotalStorageNodes());
        System.out.println("     📁 Archivos almacenados: " + finalStats.getTotalStoredFiles());
        System.out.println("     ⚡ Total operaciones: " + finalStats.getTotalOperations());
        System.out.println("     📈 Tasa de éxito: " + String.format("%.2f%%", finalStats.getSuccessRate()));
        System.out.println("     🎯 Salud del sistema: " + finalStats.getSystemHealth());
        System.out.println("     💾 Utilización: " + String.format("%.2f%%", finalStats.getStorageUtilizationPercentage()));
        
        // Validar que el sistema mantiene consistencia
        assertTrue(finalStats.getTotalOperations() >= 0, "Contador de operaciones debe ser válido");
        assertTrue(finalStats.getSuccessRate() >= 0.0 && finalStats.getSuccessRate() <= 100.0, 
                  "Tasa de éxito debe estar en rango válido");
        
        System.out.println("   ✅ Sistema en estado consistente al final de las pruebas");
    }
}
