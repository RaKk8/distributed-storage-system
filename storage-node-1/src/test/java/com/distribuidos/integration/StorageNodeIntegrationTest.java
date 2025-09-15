package com.distribuidos.integration;

import com.distribuidos.shared.rmi.StorageNodeInterface;
import com.distribuidos.shared.rmi.NodeInfo;
import com.distribuidos.storagenode1.StorageNode1Impl;
import com.distribuidos.storagenode1.StorageNode1Main;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.io.TempDir;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de integración para comunicación RMI entre componentes
 */
class StorageNodeIntegrationTest {

    private static Registry registry;
    private static StorageNode1Impl storageNode1;
    private static final int RMI_PORT = 1098; // Puerto diferente para tests
    private static final String SERVICE_NAME = "StorageNode1Test";
    
    @TempDir
    static Path tempDir;

    @BeforeAll
    static void setUpAll() throws Exception {
        System.out.println("🔧 Configurando test de integración RMI...");
        
        // Configurar directorio temporal
        System.setProperty("storage.path", tempDir.toString() + "/integration-test");
        
        // Crear registry RMI
        registry = LocateRegistry.createRegistry(RMI_PORT);
        
        // Crear nodo de almacenamiento
        storageNode1 = new StorageNode1Impl();
        
        // Registrar en RMI
        registry.rebind(SERVICE_NAME, storageNode1);
        
        System.out.println("✅ Configuración de test completada - RMI registry en puerto " + RMI_PORT);
    }

    @AfterAll
    static void tearDownAll() throws Exception {
        if (registry != null) {
            try {
                registry.unbind(SERVICE_NAME);
            } catch (Exception e) {
                // Ignorar errores de cleanup
            }
        }
        System.out.println("🧹 Limpieza de test completada");
    }

    @Test
    void testRMIConnectionAndBasicOperations() throws Exception {
        System.out.println("🧪 Iniciando test de conexión RMI...");
        
        // Obtener referencia remota
        String rmiUrl = "rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME;
        StorageNodeInterface remoteNode = (StorageNodeInterface) Naming.lookup(rmiUrl);
        
        assertNotNull(remoteNode, "La referencia remota no debe ser null");
        
        // Test heartbeat remoto
        boolean isAlive = remoteNode.heartbeat();
        assertTrue(isAlive, "El heartbeat remoto debe funcionar");
        
        // Test obtener información del nodo
        NodeInfo nodeInfo = remoteNode.getNodeInfo();
        assertNotNull(nodeInfo, "NodeInfo remoto no debe ser null");
        assertEquals("storage-node-1", nodeInfo.getNodeId());
        
        System.out.println("✅ Test de conexión RMI completado exitosamente");
    }

    @Test
    void testRemoteFileOperations() throws Exception {
        System.out.println("🧪 Iniciando test de operaciones de archivo remotas...");
        
        // Obtener referencia remota
        String rmiUrl = "rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME;
        StorageNodeInterface remoteNode = (StorageNodeInterface) Naming.lookup(rmiUrl);
        
        // Datos de prueba
        Long fileId = 100L;
        String fileName = "remote-test-file.txt";
        String content = "Contenido de prueba para operaciones remotas";
        byte[] fileData = content.getBytes();
        String checksum = calculateChecksum(fileData);
        
        // Test almacenar archivo remoto
        String localPath = remoteNode.storeFile(fileId, fileName, fileData, checksum);
        assertNotNull(localPath, "El path local remoto no debe ser null");
        
        // Test recuperar archivo remoto
        byte[] retrievedData = remoteNode.retrieveFile(fileId, localPath);
        assertNotNull(retrievedData, "Los datos recuperados remotos no deben ser null");
        assertArrayEquals(fileData, retrievedData, "Los datos deben coincidir");
        
        // Test verificar archivo remoto
        boolean isValid = remoteNode.verifyFile(fileId, localPath, checksum);
        assertTrue(isValid, "La verificación remota debe ser exitosa");
        
        // Test eliminar archivo remoto
        boolean deleted = remoteNode.deleteFile(fileId, localPath);
        assertTrue(deleted, "La eliminación remota debe ser exitosa");
        
        System.out.println("✅ Test de operaciones remotas completado exitosamente");
    }

    @Test
    void testMultipleStorageOperations() throws Exception {
        System.out.println("🧪 Iniciando test de múltiples operaciones...");
        
        // Obtener referencia remota
        String rmiUrl = "rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME;
        StorageNodeInterface remoteNode = (StorageNodeInterface) Naming.lookup(rmiUrl);
        
        // Almacenar múltiples archivos
        int numFiles = 5;
        Long[] fileIds = new Long[numFiles];
        String[] localPaths = new String[numFiles];
        
        for (int i = 0; i < numFiles; i++) {
            fileIds[i] = (long) (200 + i);
            String fileName = "multi-test-file-" + i + ".txt";
            String content = "Contenido del archivo " + i + " para test múltiple";
            byte[] fileData = content.getBytes();
            String checksum = calculateChecksum(fileData);
            
            localPaths[i] = remoteNode.storeFile(fileIds[i], fileName, fileData, checksum);
            assertNotNull(localPaths[i], "Path " + i + " no debe ser null");
        }
        
        // Verificar que todos los archivos están almacenados
        Long[] storedFiles = remoteNode.getStoredFiles();
        assertTrue(storedFiles.length >= numFiles, "Debe haber al menos " + numFiles + " archivos");
        
        // Limpiar archivos de prueba
        for (int i = 0; i < numFiles; i++) {
            boolean deleted = remoteNode.deleteFile(fileIds[i], localPaths[i]);
            assertTrue(deleted, "Archivo " + i + " debe eliminarse correctamente");
        }
        
        System.out.println("✅ Test de múltiples operaciones completado exitosamente");
    }

    @Test
    void testNodeInfoAndMetrics() throws Exception {
        System.out.println("🧪 Iniciando test de información y métricas del nodo...");
        
        // Obtener referencia remota
        String rmiUrl = "rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME;
        StorageNodeInterface remoteNode = (StorageNodeInterface) Naming.lookup(rmiUrl);
        
        // Obtener información del nodo
        NodeInfo nodeInfo = remoteNode.getNodeInfo();
        
        // Verificar campos obligatorios
        assertNotNull(nodeInfo.getNodeId(), "NodeId no debe ser null");
        assertNotNull(nodeInfo.getHostname(), "Hostname no debe ser null");
        assertNotNull(nodeInfo.getPort(), "Port no debe ser null");
        assertNotNull(nodeInfo.getStatus(), "Status no debe ser null");
        
        // Verificar valores específicos
        assertEquals("storage-node-1", nodeInfo.getNodeId());
        assertEquals("localhost", nodeInfo.getHostname());
        assertEquals(1099, nodeInfo.getPort()); // Puerto del nodo real, no del test
        assertEquals("ACTIVE", nodeInfo.getStatus());
        
        // Verificar métricas de capacidad
        assertNotNull(nodeInfo.getTotalCapacity(), "TotalCapacity no debe ser null");
        assertNotNull(nodeInfo.getUsedCapacity(), "UsedCapacity no debe ser null");
        assertNotNull(nodeInfo.getAvailableCapacity(), "AvailableCapacity no debe ser null");
        
        assertTrue(nodeInfo.getTotalCapacity() > 0, "TotalCapacity debe ser mayor a 0");
        assertTrue(nodeInfo.getAvailableCapacity() >= 0, "AvailableCapacity debe ser mayor o igual a 0");
        
        System.out.println("✅ Test de información del nodo completado:");
        System.out.println("   - Node ID: " + nodeInfo.getNodeId());
        System.out.println("   - Hostname: " + nodeInfo.getHostname());
        System.out.println("   - Port: " + nodeInfo.getPort());
        System.out.println("   - Status: " + nodeInfo.getStatus());
        System.out.println("   - Total Capacity: " + NodeInfo.formatBytes(nodeInfo.getTotalCapacity()));
        System.out.println("   - Available Capacity: " + NodeInfo.formatBytes(nodeInfo.getAvailableCapacity()));
    }

    @Test
    void testConcurrentOperations() throws Exception {
        System.out.println("🧪 Iniciando test de operaciones concurrentes...");
        
        // Obtener referencia remota
        String rmiUrl = "rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME;
        StorageNodeInterface remoteNode = (StorageNodeInterface) Naming.lookup(rmiUrl);
        
        // Crear múltiples threads para operaciones concurrentes
        int numThreads = 3;
        Thread[] threads = new Thread[numThreads];
        boolean[] results = new boolean[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    Long fileId = (long) (300 + threadId);
                    String fileName = "concurrent-test-" + threadId + ".txt";
                    String content = "Contenido concurrente del thread " + threadId;
                    byte[] fileData = content.getBytes();
                    String checksum = calculateChecksum(fileData);
                    
                    // Operaciones concurrentes
                    String localPath = remoteNode.storeFile(fileId, fileName, fileData, checksum);
                    byte[] retrieved = remoteNode.retrieveFile(fileId, localPath);
                    boolean verified = remoteNode.verifyFile(fileId, localPath, checksum);
                    boolean deleted = remoteNode.deleteFile(fileId, localPath);
                    
                    results[threadId] = (localPath != null && retrieved != null && verified && deleted);
                    
                } catch (Exception e) {
                    System.err.println("Error en thread " + threadId + ": " + e.getMessage());
                    results[threadId] = false;
                }
            });
        }
        
        // Ejecutar threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Esperar a que terminen
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verificar resultados
        for (int i = 0; i < numThreads; i++) {
            assertTrue(results[i], "Thread " + i + " debe completarse exitosamente");
        }
        
        System.out.println("✅ Test de operaciones concurrentes completado exitosamente");
    }

    /**
     * Calcula checksum SHA-256 para las pruebas
     */
    private String calculateChecksum(byte[] data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculando checksum", e);
        }
    }
}