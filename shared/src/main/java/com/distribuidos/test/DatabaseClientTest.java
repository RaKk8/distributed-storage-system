package com.distribuidos.test;

import com.distribuidos.shared.tcp.DatabaseMessage;
import com.distribuidos.shared.tcp.DatabaseResponse;
import com.distribuidos.shared.model.*;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Cliente de prueba para validar la comunicación TCP con el servidor de base de datos
 */
public class DatabaseClientTest {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9001;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE COMUNICACIÓN TCP CON BASE DE DATOS ===\n");
        
        DatabaseClientTest test = new DatabaseClientTest();
        
        // Ejecutar todas las pruebas
        test.testConnection();
        test.testCreateUser();
        test.testFindUser();
        test.testCreateDirectory();
        test.testCreateFile();
        test.testFindFiles();
        test.testNodeOperations();
        
        System.out.println("\n=== PRUEBAS COMPLETADAS ===");
    }
    
    /**
     * Prueba de conexión básica
     */
    public void testConnection() {
        System.out.println("📡 Prueba de conexión al servidor...");
        
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("✅ Conexión establecida exitosamente");
            System.out.println("   - Host: " + SERVER_HOST + ":" + SERVER_PORT);
            System.out.println("   - Socket conectado: " + socket.isConnected());
            
        } catch (Exception e) {
            System.out.println("❌ Error en conexión: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba de creación de usuario
     */
    public void testCreateUser() {
        System.out.println("👤 Prueba de creación de usuario...");
        
        try {
            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setPassword("testpass123");
            newUser.setEmail("test@example.com");
            
            DatabaseMessage message = new DatabaseMessage(DatabaseMessage.SAVE, DatabaseMessage.USER, newUser);
            DatabaseResponse response = sendMessage(message);
            
            if (response.isSuccess()) {
                System.out.println("✅ Usuario creado exitosamente");
                System.out.println("   - ID generado: " + response.getData());
            } else {
                System.out.println("❌ Error creando usuario: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error en prueba de usuario: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba de búsqueda de usuario
     */
    public void testFindUser() {
        System.out.println("🔍 Prueba de búsqueda de usuario...");
        
        try {
            DatabaseMessage message = new DatabaseMessage(DatabaseMessage.FIND_BY_ID, DatabaseMessage.USER, 1L);
            DatabaseResponse response = sendMessage(message);
            
            if (response.isSuccess()) {
                System.out.println("✅ Usuario encontrado exitosamente");
                if (response.getData() instanceof User) {
                    User user = (User) response.getData();
                    System.out.println("   - Username: " + user.getUsername());
                    System.out.println("   - Email: " + user.getEmail());
                }
            } else {
                System.out.println("❌ Error buscando usuario: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error en búsqueda de usuario: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba de creación de directorio
     */
    public void testCreateDirectory() {
        System.out.println("📁 Prueba de creación de directorio...");
        
        try {
            Directory directory = new Directory();
            directory.setName("test-directory");
            directory.setFullPath("/test");
            // Se necesita asociar con un usuario existente
            User owner = new User();
            owner.setId(1L);
            directory.setOwner(owner);
            
            DatabaseMessage message = new DatabaseMessage(DatabaseMessage.SAVE, DatabaseMessage.DIRECTORY, directory);
            DatabaseResponse response = sendMessage(message);
            
            if (response.isSuccess()) {
                System.out.println("✅ Directorio creado exitosamente");
                System.out.println("   - ID generado: " + response.getData());
            } else {
                System.out.println("❌ Error creando directorio: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error en prueba de directorio: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba de creación de archivo
     */
    public void testCreateFile() {
        System.out.println("📄 Prueba de creación de archivo...");
        
        try {
            com.distribuidos.shared.model.File file = new com.distribuidos.shared.model.File();
            file.setName("test-file.txt");
            file.setFilePath("/test/test-file.txt");
            file.setFileSize(1024L);
            file.setChecksum("abc123def456");
            
            // Asociar con usuario y directorio
            User owner = new User();
            owner.setId(1L);
            file.setOwner(owner);
            
            Directory directory = new Directory();
            directory.setId(1L);
            file.setDirectory(directory);
            
            DatabaseMessage message = new DatabaseMessage(DatabaseMessage.SAVE, DatabaseMessage.FILE, file);
            DatabaseResponse response = sendMessage(message);
            
            if (response.isSuccess()) {
                System.out.println("✅ Archivo creado exitosamente");
                System.out.println("   - ID generado: " + response.getData());
            } else {
                System.out.println("❌ Error creando archivo: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error en prueba de archivo: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba de búsqueda de archivos
     */
    public void testFindFiles() {
        System.out.println("📋 Prueba de búsqueda de archivos...");
        
        try {
            DatabaseMessage message = new DatabaseMessage(DatabaseMessage.FIND_ALL, DatabaseMessage.FILE);
            DatabaseResponse response = sendMessage(message);
            
            if (response.isSuccess()) {
                System.out.println("✅ Archivos encontrados exitosamente");
                if (response.getData() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<com.distribuidos.shared.model.File> files = (List<com.distribuidos.shared.model.File>) response.getData();
                    System.out.println("   - Cantidad de archivos: " + files.size());
                    for (com.distribuidos.shared.model.File file : files) {
                        System.out.println("   - Archivo: " + file.getName() + " (" + file.getFileSize() + " bytes)");
                    }
                }
            } else {
                System.out.println("❌ Error buscando archivos: " + response.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error en búsqueda de archivos: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Prueba de operaciones con nodos
     */
    public void testNodeOperations() {
        System.out.println("🖥️ Prueba de operaciones con nodos...");
        
        try {
            // Crear nodo
            Node node = new Node();
            node.setNodeId("storage-node-1");
            node.setHostname("localhost");
            node.setPort(8001);
            node.setStoragePath("/storage/node1");
            node.setTotalCapacity(1000000000L); // 1GB
            node.setStatus(Node.NodeStatus.ONLINE);
            
            DatabaseMessage createMessage = new DatabaseMessage(DatabaseMessage.SAVE, DatabaseMessage.NODE, node);
            DatabaseResponse createResponse = sendMessage(createMessage);
            
            if (createResponse.isSuccess()) {
                System.out.println("✅ Nodo creado exitosamente");
                System.out.println("   - ID generado: " + createResponse.getData());
                
                // Buscar nodos activos
                DatabaseMessage findMessage = new DatabaseMessage(DatabaseMessage.FIND_ALL, DatabaseMessage.NODE);
                DatabaseResponse findResponse = sendMessage(findMessage);
                
                if (findResponse.isSuccess()) {
                    System.out.println("✅ Nodos encontrados");
                    if (findResponse.getData() instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Node> nodes = (List<Node>) findResponse.getData();
                        System.out.println("   - Cantidad de nodos: " + nodes.size());
                        for (Node n : nodes) {
                            System.out.println("   - Nodo: " + n.getNodeId() + " (" + n.getHostname() + ":" + n.getPort() + ")");
                        }
                    }
                }
            } else {
                System.out.println("❌ Error creando nodo: " + createResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error en prueba de nodos: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Envía un mensaje al servidor y recibe la respuesta
     */
    private DatabaseResponse sendMessage(DatabaseMessage message) throws Exception {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            
            // Enviar mensaje
            out.writeObject(message);
            out.flush();
            
            // Recibir respuesta
            return (DatabaseResponse) in.readObject();
        }
    }
}