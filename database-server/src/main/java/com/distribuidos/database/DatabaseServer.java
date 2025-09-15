package com.distribuidos.database;

import com.distribuidos.database.config.DatabaseConfig;
import com.distribuidos.database.handler.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor de base de datos que maneja conexiones TCP
 */
public class DatabaseServer {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseServer.class);
    private static final int DEFAULT_PORT = 9001;
    private static final int THREAD_POOL_SIZE = 10;
    
    private final int port;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    
    public DatabaseServer() {
        this(DEFAULT_PORT);
    }
    
    public DatabaseServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }
    
    /**
     * Inicia el servidor de base de datos
     */
    public void start() throws IOException, SQLException {
        // Inicializar la base de datos
        DatabaseConfig.initialize();
        logger.info("Base de datos inicializada correctamente");
        
        // Crear el socket del servidor
        serverSocket = new ServerSocket(port);
        running = true;
        
        logger.info("Servidor de base de datos iniciado en puerto {}", port);
        logger.info("Esperando conexiones de clientes...");
        
        // Loop principal para aceptar conexiones
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nueva conexión desde: {}", clientSocket.getRemoteSocketAddress());
                
                // Manejar cada cliente en un hilo separado
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                threadPool.submit(clientHandler);
                
            } catch (IOException e) {
                if (running) {
                    logger.error("Error aceptando conexión del cliente", e);
                }
            }
        }
    }
    
    /**
     * Detiene el servidor de base de datos
     */
    public void stop() {
        running = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logger.error("Error cerrando el servidor socket", e);
        }
        
        threadPool.shutdown();
        
        try {
            DatabaseConfig.shutdown();
            logger.info("Base de datos cerrada correctamente");
        } catch (SQLException e) {
            logger.error("Error cerrando la base de datos", e);
        }
        
        logger.info("Servidor de base de datos detenido");
    }
    
    /**
     * Método principal
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        
        // Leer puerto desde argumentos si se proporciona
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.warn("Puerto inválido '{}', usando puerto por defecto {}", args[0], DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }
        
        DatabaseServer server = new DatabaseServer(port);
        
        // Manejar shutdown gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Recibida señal de shutdown, deteniendo servidor...");
            server.stop();
        }));
        
        try {
            server.start();
        } catch (IOException e) {
            logger.error("Error iniciando el servidor de base de datos", e);
            System.exit(1);
        } catch (SQLException e) {
            logger.error("Error inicializando la base de datos", e);
            System.exit(1);
        }
    }
}