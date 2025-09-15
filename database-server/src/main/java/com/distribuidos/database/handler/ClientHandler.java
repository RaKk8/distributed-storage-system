package com.distribuidos.database.handler;

import com.distribuidos.database.service.DatabaseService;
import com.distribuidos.shared.tcp.DatabaseMessage;
import com.distribuidos.shared.tcp.DatabaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Manejador de clientes TCP para el servidor de base de datos
 */
public class ClientHandler implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    
    private final Socket clientSocket;
    private final DatabaseService databaseService;
    
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.databaseService = new DatabaseService();
    }
    
    @Override
    public void run() {
        try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream())) {
            
            logger.info("Cliente conectado desde: {}", clientSocket.getRemoteSocketAddress());
            
            // Procesar mensajes del cliente
            while (!clientSocket.isClosed()) {
                try {
                    // Leer mensaje del cliente
                    DatabaseMessage message = (DatabaseMessage) inputStream.readObject();
                    logger.debug("Mensaje recibido: {}", message);
                    
                    // Procesar el mensaje
                    DatabaseResponse response = processMessage(message);
                    
                    // Enviar respuesta
                    outputStream.writeObject(response);
                    outputStream.flush();
                    
                    logger.debug("Respuesta enviada: {}", response);
                    
                } catch (EOFException e) {
                    // Cliente cerró la conexión
                    logger.info("Cliente desconectado: {}", clientSocket.getRemoteSocketAddress());
                    break;
                } catch (ClassNotFoundException e) {
                    logger.error("Error deserializando mensaje del cliente", e);
                    // Enviar respuesta de error
                    DatabaseResponse errorResponse = DatabaseResponse.error("Error deserializando mensaje");
                    outputStream.writeObject(errorResponse);
                    outputStream.flush();
                }
            }
            
        } catch (IOException e) {
            logger.error("Error en comunicación con cliente: {}", clientSocket.getRemoteSocketAddress(), e);
        } finally {
            closeConnection();
        }
    }
    
    /**
     * Procesa un mensaje de base de datos
     */
    private DatabaseResponse processMessage(DatabaseMessage message) {
        try {
            String operation = message.getOperation();
            String entity = message.getEntity();
            
            logger.debug("Procesando operación: {} en entidad: {}", operation, entity);
            
            switch (operation) {
                case DatabaseMessage.SAVE:
                    return databaseService.save(entity, message.getData());
                    
                case DatabaseMessage.FIND_BY_ID:
                    return databaseService.findById(entity, message.getId());
                    
                case DatabaseMessage.FIND_ALL:
                    return databaseService.findAll(entity);
                    
                case DatabaseMessage.UPDATE:
                    return databaseService.update(entity, message.getData());
                    
                case DatabaseMessage.DELETE:
                    return databaseService.delete(entity, message.getId());
                    
                case DatabaseMessage.FIND_BY_CRITERIA:
                    return databaseService.findByCriteria(entity, message.getQuery(), message.getParameters());
                    
                case DatabaseMessage.EXECUTE_QUERY:
                    return databaseService.executeQuery(message.getQuery(), message.getParameters());
                    
                default:
                    return DatabaseResponse.error("Operación no soportada: " + operation);
            }
            
        } catch (Exception e) {
            logger.error("Error procesando mensaje", e);
            return DatabaseResponse.error("Error interno del servidor: " + e.getMessage());
        }
    }
    
    /**
     * Cierra la conexión con el cliente
     */
    private void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                logger.info("Conexión cerrada con cliente: {}", clientSocket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            logger.error("Error cerrando conexión con cliente", e);
        }
    }
}