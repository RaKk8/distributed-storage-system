package com.distribuidos.appserver.service;

import com.distribuidos.shared.tcp.DatabaseMessage;
import com.distribuidos.shared.tcp.DatabaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Servicio para comunicación TCP con el servidor de base de datos.
 * Maneja todas las operaciones de persistencia del sistema.
 */
@Service
public class DatabaseCommunicationService {
    
    @Value("${database.tcp.host}")
    private String databaseHost;
    
    @Value("${database.tcp.port}")
    private int databasePort;
    
    @Value("${database.tcp.timeout}")
    private int timeout;
    
    /**
     * Envía un mensaje al servidor de base de datos y obtiene la respuesta.
     */
    public DatabaseResponse sendMessage(DatabaseMessage message) {
        System.out.println("📡 Enviando mensaje TCP a BD: " + message.getOperation());
        
        try (Socket socket = new Socket(databaseHost, databasePort)) {
            socket.setSoTimeout(timeout);
            
            // Enviar mensaje
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(message);
            out.flush();
            
            // Recibir respuesta
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            DatabaseResponse response = (DatabaseResponse) in.readObject();
            
            System.out.println("✅ Respuesta TCP recibida: " + 
                             (response.isSuccess() ? "SUCCESS" : "ERROR"));
            
            if (!response.isSuccess()) {
                System.err.println("❌ Error en BD: " + response.getErrorMessage());
            }
            
            return response;
            
        } catch (SocketTimeoutException e) {
            System.err.println("⏰ Timeout en comunicación TCP con BD");
            return DatabaseResponse.error("Timeout en comunicación con base de datos");
        } catch (Exception e) {
            System.err.println("❌ Error en comunicación TCP: " + e.getMessage());
            return DatabaseResponse.error("Error de comunicación: " + e.getMessage());
        }
    }
    
    /**
     * Verifica la conectividad con el servidor de base de datos.
     */
    public boolean testConnection() {
        System.out.println("🔍 Probando conexión TCP con BD...");
        
        DatabaseMessage pingMessage = new DatabaseMessage();
        pingMessage.setOperation("PING");
        
        DatabaseResponse response = sendMessage(pingMessage);
        boolean connected = response.isSuccess();
        
        System.out.println(connected ? 
            "✅ Conexión TCP con BD establecida" : 
            "❌ No se pudo conectar con BD");
            
        return connected;
    }
}