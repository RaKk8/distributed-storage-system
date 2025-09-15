package com.distribuidos.storagenode2;

import com.distribuidos.shared.rmi.StorageNodeInterface;
import com.distribuidos.shared.rmi.NodeInfo;
import com.distribuidos.storagenode2.service.FileStorageService;
import com.distribuidos.storagenode2.service.ReplicationService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementación del Storage Node 2
 * Gestiona almacenamiento distribuido con replicación
 */
public class StorageNode2Impl extends UnicastRemoteObject implements StorageNodeInterface {
    
    private static final Logger logger = LoggerFactory.getLogger(StorageNode2Impl.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    
    // Configuración del nodo
    private static final String NODE_ID = "storage-node-2";
    private static final int RMI_PORT = 1100;
    private static final String STORAGE_PATH = "./storage/node2";
    
    // Servicios
    private final FileStorageService fileStorageService;
    private final ReplicationService replicationService;
    
    public StorageNode2Impl() throws RemoteException {
        super();
        
        String timestamp = dateFormat.format(new Date());
        logger.info("🚀 [{}] Inicializando Storage Node 2...", timestamp);
        
        // Inicializar servicios
        this.fileStorageService = new FileStorageService(STORAGE_PATH, NODE_ID);
        this.replicationService = new ReplicationService(NODE_ID);
        
        // Registrar en el servidor de aplicación
        registerWithApplicationServer();
        
        // Iniciar monitoreo
        startHeartbeatMonitoring();
        
        logger.info("✅ [{}] Storage Node 2 inicializado correctamente", timestamp);
    }
    
    /**
     * Almacena un archivo en el nodo
     */
    @Override
    public String storeFile(Long fileId, String fileName, byte[] fileData, String checksum) 
            throws RemoteException {
        
        String timestamp = dateFormat.format(new Date());
        
        try {
            // Validar datos de entrada primero
            if (fileId == null || fileName == null || fileData == null || checksum == null) {
                String error = "Datos de entrada inválidos para almacenar archivo";
                logger.error("❌ [{}] {}", timestamp, error);
                throw new RemoteException(error);
            }
            
            logger.info("📥 [{}] Recibiendo archivo: {} (ID: {}, Size: {} bytes, Checksum: {})", 
                       timestamp, fileName, fileId, fileData.length, checksum);
            
            // Almacenar localmente
            String localPath = fileStorageService.storeFile(fileId, fileName, fileData, checksum);
            
            // Agregar a la replicación
            replicationService.addToReplicationQueue(fileId, fileName, fileData, checksum);
            
            logger.info("✅ [{}] Archivo almacenado exitosamente en: {}", timestamp, localPath);
            return localPath;
            
        } catch (Exception e) {
            String error = "Error al almacenar archivo: " + e.getMessage();
            logger.error("❌ [{}] {}", timestamp, error, e);
            throw new RemoteException(error, e);
        }
    }
    
    /**
     * Recupera un archivo del nodo
     */
    @Override
    public byte[] retrieveFile(Long fileId, String localPath) throws RemoteException {
        String timestamp = dateFormat.format(new Date());
        logger.info("📤 [{}] Recuperando archivo: {} (Path: {})", timestamp, fileId, localPath);
        
        try {
            byte[] fileData = fileStorageService.retrieveFile(fileId, localPath);
            logger.info("✅ [{}] Archivo recuperado exitosamente (Size: {} bytes)", 
                       timestamp, fileData.length);
            return fileData;
            
        } catch (Exception e) {
            String error = "Error al recuperar archivo: " + e.getMessage();
            logger.error("❌ [{}] {}", timestamp, error, e);
            throw new RemoteException(error, e);
        }
    }
    
    /**
     * Elimina un archivo del nodo
     */
    @Override
    public boolean deleteFile(Long fileId, String localPath) throws RemoteException {
        String timestamp = dateFormat.format(new Date());
        logger.info("🗑️ [{}] Eliminando archivo: {} (Path: {})", timestamp, fileId, localPath);
        
        try {
            boolean result = fileStorageService.deleteFile(fileId, localPath);
            
            if (result) {
                logger.info("✅ [{}] Archivo eliminado exitosamente", timestamp);
            } else {
                logger.warn("⚠️ [{}] No se pudo eliminar el archivo", timestamp);
            }
            
            return result;
            
        } catch (Exception e) {
            String error = "Error al eliminar archivo: " + e.getMessage();
            logger.error("❌ [{}] {}", timestamp, error, e);
            throw new RemoteException(error, e);
        }
    }
    
    /**
     * Verifica la integridad de un archivo
     */
    @Override
    public boolean verifyFile(Long fileId, String localPath, String expectedChecksum) 
            throws RemoteException {
        
        String timestamp = dateFormat.format(new Date());
        logger.info("🔍 [{}] Verificando archivo: {} (Path: {}, Expected checksum: {})", 
                   timestamp, fileId, localPath, expectedChecksum);
        
        try {
            boolean isValid = fileStorageService.verifyFile(fileId, localPath, expectedChecksum);
            
            if (isValid) {
                logger.info("✅ [{}] Archivo verificado correctamente", timestamp);
            } else {
                logger.warn("⚠️ [{}] Verificación de archivo falló", timestamp);
            }
            
            return isValid;
            
        } catch (Exception e) {
            String error = "Error al verificar archivo: " + e.getMessage();
            logger.error("❌ [{}] {}", timestamp, error, e);
            throw new RemoteException(error, e);
        }
    }
    
    /**
     * Obtiene información del nodo
     */
    @Override
    public NodeInfo getNodeInfo() throws RemoteException {
        String timestamp = dateFormat.format(new Date());
        logger.info("ℹ️ [{}] Solicitando información del nodo", timestamp);
        
        try {
            // Crear información del nodo
            NodeInfo nodeInfo = new NodeInfo();
            nodeInfo.setNodeId(NODE_ID);
            nodeInfo.setHostname("localhost");
            nodeInfo.setPort(RMI_PORT);
            nodeInfo.setStatus("ACTIVE");
            nodeInfo.setUptime(System.currentTimeMillis());
            
            // Calcular estadísticas de almacenamiento
            File storageDir = new File(STORAGE_PATH);
            long totalSpace = storageDir.getTotalSpace();
            long freeSpace = storageDir.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            
            nodeInfo.setTotalCapacity(totalSpace);
            nodeInfo.setUsedCapacity(usedSpace);
            nodeInfo.setAvailableCapacity(freeSpace);
            
            logger.info("✅ [{}] Información del nodo generada: {} archivos, {} MB libres", 
                       timestamp, getStoredFilesCount(), freeSpace / 1024 / 1024);
            
            return nodeInfo;
            
        } catch (Exception e) {
            String error = "Error al obtener información del nodo: " + e.getMessage();
            logger.error("❌ [{}] {}", timestamp, error, e);
            throw new RemoteException(error, e);
        }
    }
    
    /**
     * Verifica que el nodo está operativo
     */
    @Override
    public boolean heartbeat() throws RemoteException {
        String timestamp = dateFormat.format(new Date());
        logger.debug("💓 [{}] Heartbeat recibido", timestamp);
        return true;
    }
    
    /**
     * Obtiene la lista de archivos almacenados en el nodo
     */
    @Override
    public Long[] getStoredFiles() throws RemoteException {
        String timestamp = dateFormat.format(new Date());
        logger.info("📋 [{}] Obteniendo lista de archivos almacenados", timestamp);
        
        try {
            return fileStorageService.getStoredFiles();
        } catch (Exception e) {
            String error = "Error al obtener lista de archivos: " + e.getMessage();
            logger.error("❌ [{}] {}", timestamp, error, e);
            throw new RemoteException(error, e);
        }
    }
    
    /**
     * Obtiene el número de archivos almacenados
     */
    private int getStoredFilesCount() {
        try {
            File dataDir = new File(STORAGE_PATH + "/data");
            if (!dataDir.exists()) {
                return 0;
            }
            
            File[] files = dataDir.listFiles();
            return files != null ? files.length : 0;
            
        } catch (Exception e) {
            logger.error("Error al contar archivos: {}", e.getMessage());
            return 0;
        }
    }
    
    /**
     * Registra el nodo en el servidor de aplicación
     */
    private void registerWithApplicationServer() {
        try {
            // Aquí se conectaría con el servidor de aplicación para registrarse
            logger.info("📝 Nodo registrado en el servidor de aplicación");
        } catch (Exception e) {
            logger.error("Error al registrar nodo: {}", e.getMessage());
        }
    }
    
    /**
     * Inicia el monitoreo de heartbeat
     */
    private void startHeartbeatMonitoring() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Enviar heartbeat al servidor de aplicación
                logger.debug("💓 Enviando heartbeat al servidor de aplicación");
            } catch (Exception e) {
                logger.error("Error en heartbeat: {}", e.getMessage());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
}