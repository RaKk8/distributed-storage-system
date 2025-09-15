package com.distribuidos.database.client;

import com.distribuidos.shared.tcp.DatabaseMessage;
import com.distribuidos.shared.tcp.DatabaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Cliente TCP para probar la conexión con el servidor de base de datos
 */
public class DatabaseClient {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseClient.class);
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9001;
    
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    
    /**
     * Conecta al servidor de base de datos
     */
    public boolean connect() {
        return connect(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /**
     * Conecta al servidor de base de datos
     */
    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            
            logger.info("Conectado al servidor de base de datos en {}:{}", host, port);
            return true;
            
        } catch (IOException e) {
            logger.error("Error conectando al servidor de base de datos", e);
            return false;
        }
    }
    
    /**
     * Envía un mensaje al servidor y recibe la respuesta
     */
    public DatabaseResponse sendMessage(DatabaseMessage message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            
            return (DatabaseResponse) inputStream.readObject();
            
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error enviando mensaje al servidor", e);
            return DatabaseResponse.error("Error de comunicación: " + e.getMessage());
        }
    }
    
    /**
     * Cierra la conexión
     */
    public void disconnect() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
            
            logger.info("Desconectado del servidor de base de datos");
            
        } catch (IOException e) {
            logger.error("Error cerrando conexión", e);
        }
    }
    
    /**
     * Método principal para pruebas
     */
    public static void main(String[] args) {
        DatabaseClient client = new DatabaseClient();
        
        System.out.println("=== Prueba del Cliente de Base de Datos ===");
        
        // Conectar al servidor
        if (!client.connect()) {
            System.out.println("❌ No se pudo conectar al servidor de base de datos");
            System.out.println("Asegúrate de que el servidor esté ejecutándose");
            return;
        }
        
        System.out.println("✅ Conectado exitosamente al servidor");
        
        // Prueba 1: Buscar todos los usuarios
        System.out.println("\n--- Prueba 1: Buscar todos los usuarios ---");
        DatabaseMessage message1 = new DatabaseMessage(DatabaseMessage.FIND_ALL, DatabaseMessage.USER);
        DatabaseResponse response1 = client.sendMessage(message1);
        
        if (response1.isSuccess()) {
            System.out.println("✅ Consulta exitosa:");
            System.out.println("   Datos: " + response1.getData());
        } else {
            System.out.println("❌ Error en consulta: " + response1.getErrorMessage());
        }
        
        // Prueba 2: Buscar usuario por ID
        System.out.println("\n--- Prueba 2: Buscar usuario con ID = 1 ---");
        DatabaseMessage message2 = new DatabaseMessage(DatabaseMessage.FIND_BY_ID, DatabaseMessage.USER, 1L);
        DatabaseResponse response2 = client.sendMessage(message2);
        
        if (response2.isSuccess()) {
            System.out.println("✅ Usuario encontrado:");
            System.out.println("   Datos: " + response2.getData());
        } else {
            System.out.println("❌ Error buscando usuario: " + response2.getErrorMessage());
        }
        
        // Prueba 3: Consulta personalizada
        System.out.println("\n--- Prueba 3: Consulta personalizada ---");
        DatabaseMessage message3 = new DatabaseMessage(DatabaseMessage.EXECUTE_QUERY, "");
        message3.setQuery("SELECT username, email FROM users WHERE id > ?");
        message3.setParameters(new Object[]{0});
        DatabaseResponse response3 = client.sendMessage(message3);
        
        if (response3.isSuccess()) {
            System.out.println("✅ Consulta personalizada exitosa:");
            System.out.println("   Resultados: " + response3.getData());
        } else {
            System.out.println("❌ Error en consulta personalizada: " + response3.getErrorMessage());
        }
        
        // Prueba 4: Buscar todas las tablas
        System.out.println("\n--- Prueba 4: Verificar estructura de la base de datos ---");
        DatabaseMessage message4 = new DatabaseMessage(DatabaseMessage.EXECUTE_QUERY, "");
        message4.setQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'PUBLIC'");
        DatabaseResponse response4 = client.sendMessage(message4);
        
        if (response4.isSuccess()) {
            System.out.println("✅ Tablas en la base de datos:");
            System.out.println("   Tablas: " + response4.getData());
        } else {
            System.out.println("❌ Error consultando tablas: " + response4.getErrorMessage());
        }
        
        // Desconectar
        client.disconnect();
        System.out.println("\n=== Pruebas completadas ===");
    }
}