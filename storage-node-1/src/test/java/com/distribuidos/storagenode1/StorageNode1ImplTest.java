package com.distribuidos.storagenode1;

import com.distribuidos.shared.rmi.NodeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para StorageNode1Impl
 */
class StorageNode1ImplTest {

    private StorageNode1Impl storageNode;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws RemoteException {
        // Se usa el directorio temporal para las pruebas
        System.setProperty("storage.path", tempDir.toString() + "/node1");
        storageNode = new StorageNode1Impl();
    }

    @AfterEach
    void tearDown() {
        storageNode = null;
    }

    @Test
    void testStoreFile() throws RemoteException {
        // Datos de prueba
        Long fileId = 1L;
        String fileName = "test-file.txt";
        String content = "Contenido de prueba para Storage Node 1";
        byte[] fileData = content.getBytes();
        String checksum = "test-checksum-123";

        // Ejecutar
        String localPath = storageNode.storeFile(fileId, fileName, fileData, checksum);

        // Verificar
        assertNotNull(localPath, "El path local no debe ser null");
        assertTrue(localPath.contains(fileName), "El path debe contener el nombre del archivo");
        
        System.out.println("✅ Test storeFile completado - Archivo almacenado en: " + localPath);
    }

    @Test
    void testStoreAndRetrieveFile() throws RemoteException {
        // Datos de prueba
        Long fileId = 2L;
        String fileName = "test-retrieve.txt";
        String content = "Contenido para test de recuperación";
        byte[] originalData = content.getBytes();
        String checksum = "retrieve-checksum-456";

        // Almacenar archivo
        String localPath = storageNode.storeFile(fileId, fileName, originalData, checksum);
        assertNotNull(localPath);

        // Recuperar archivo
        byte[] retrievedData = storageNode.retrieveFile(fileId, localPath);

        // Verificar
        assertNotNull(retrievedData, "Los datos recuperados no deben ser null");
        assertArrayEquals(originalData, retrievedData, "Los datos recuperados deben coincidir con los originales");
        
        System.out.println("✅ Test storeAndRetrieveFile completado - " + retrievedData.length + " bytes recuperados");
    }

    @Test
    void testDeleteFile() throws RemoteException {
        // Datos de prueba
        Long fileId = 3L;
        String fileName = "test-delete.txt";
        byte[] fileData = "Archivo para eliminar".getBytes();
        String checksum = "delete-checksum-789";

        // Almacenar archivo
        String localPath = storageNode.storeFile(fileId, fileName, fileData, checksum);
        assertNotNull(localPath);

        // Eliminar archivo
        boolean deleted = storageNode.deleteFile(fileId, localPath);

        // Verificar
        assertTrue(deleted, "El archivo debe haberse eliminado exitosamente");
        
        System.out.println("✅ Test deleteFile completado - Archivo eliminado correctamente");
    }

    @Test
    void testVerifyFile() throws RemoteException {
        // Datos de prueba
        Long fileId = 4L;
        String fileName = "test-verify.txt";
        String content = "Contenido para verificación";
        byte[] fileData = content.getBytes();
        
        // Calcular checksum real
        String expectedChecksum = calculateChecksum(fileData);

        // Almacenar archivo
        String localPath = storageNode.storeFile(fileId, fileName, fileData, expectedChecksum);
        assertNotNull(localPath);

        // Verificar archivo con checksum correcto
        boolean isValid = storageNode.verifyFile(fileId, localPath, expectedChecksum);
        assertTrue(isValid, "La verificación debe ser exitosa con checksum correcto");

        // Verificar archivo con checksum incorrecto
        boolean isInvalid = storageNode.verifyFile(fileId, localPath, "checksum-incorrecto");
        assertFalse(isInvalid, "La verificación debe fallar con checksum incorrecto");
        
        System.out.println("✅ Test verifyFile completado - Verificación funcionando correctamente");
    }

    @Test
    void testGetNodeInfo() throws RemoteException {
        // Ejecutar
        NodeInfo nodeInfo = storageNode.getNodeInfo();

        // Verificar
        assertNotNull(nodeInfo, "NodeInfo no debe ser null");
        assertEquals("storage-node-1", nodeInfo.getNodeId(), "El nodeId debe ser correcto");
        assertEquals("localhost", nodeInfo.getHostname(), "El hostname debe ser localhost");
        assertEquals(1099, nodeInfo.getPort(), "El puerto debe ser 1099");
        assertEquals("ACTIVE", nodeInfo.getStatus(), "El estado debe ser ACTIVE");
        
        System.out.println("✅ Test getNodeInfo completado - Información del nodo: " + nodeInfo.getNodeId());
    }

    @Test
    void testHeartbeat() throws RemoteException {
        // Ejecutar
        boolean isAlive = storageNode.heartbeat();

        // Verificar
        assertTrue(isAlive, "El heartbeat debe retornar true");
        
        System.out.println("✅ Test heartbeat completado - Nodo respondiendo correctamente");
    }

    @Test
    void testGetStoredFiles() throws RemoteException {
        // Almacenar algunos archivos
        storageNode.storeFile(10L, "file1.txt", "contenido1".getBytes(), "checksum1");
        storageNode.storeFile(11L, "file2.txt", "contenido2".getBytes(), "checksum2");

        // Ejecutar
        Long[] storedFiles = storageNode.getStoredFiles();

        // Verificar
        assertNotNull(storedFiles, "La lista de archivos no debe ser null");
        assertTrue(storedFiles.length >= 2, "Debe haber al menos 2 archivos almacenados");
        
        System.out.println("✅ Test getStoredFiles completado - " + storedFiles.length + " archivos encontrados");
    }

    @Test
    void testStoreFileWithInvalidData() {
        // Verificar que se lance excepción con datos inválidos
        assertThrows(RemoteException.class, () -> {
            storageNode.storeFile(null, "test.txt", "data".getBytes(), "checksum");
        }, "Debe lanzar RemoteException con fileId null");

        assertThrows(RemoteException.class, () -> {
            storageNode.storeFile(1L, null, "data".getBytes(), "checksum");
        }, "Debe lanzar RemoteException con fileName null");

        assertThrows(RemoteException.class, () -> {
            storageNode.storeFile(1L, "test.txt", null, "checksum");
        }, "Debe lanzar RemoteException con fileData null");

        assertThrows(RemoteException.class, () -> {
            storageNode.storeFile(1L, "test.txt", "data".getBytes(), null);
        }, "Debe lanzar RemoteException con checksum null");
        
        System.out.println("✅ Test storeFileWithInvalidData completado - Validaciones funcionando");
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