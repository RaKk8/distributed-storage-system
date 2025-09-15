package com.distribuidos.clientbackend.service;

import com.distribuidos.shared.rmi.StorageNodeInterface;
import com.distribuidos.shared.rmi.NodeInfo;
import com.distribuidos.clientbackend.model.DistributedFileResult;
import com.distribuidos.clientbackend.model.FileIntegrityReport;
import com.distribuidos.clientbackend.model.SystemStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.*;

/**
 * Servicio principal para gestión de almacenamiento distribuido
 * Implementa lógica de distribución, balanceamento de carga y tolerancia a fallos
 */
public class DistributedStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(DistributedStorageService.class);
    
    // Configuración de nodos
    private final Map<String, NodeConfig> storageNodes;
    private final Map<String, StorageNodeInterface> activeConnections;
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;
    
    // Estadísticas
    private long totalOperations = 0;
    private long successfulOperations = 0;
    private long failedOperations = 0;
    
    // Registro de nombres de archivos
    private final Map<Long, String> fileNamesRegistry = new ConcurrentHashMap<>();
    
    // Configuración
    private static final int DEFAULT_REPLICATION_FACTOR = 2;
    private static final int HEALTH_CHECK_INTERVAL_SECONDS = 30;
    
    /**
     * Configuración de nodo de almacenamiento
     */
    public static class NodeConfig {
        public final String nodeId;
        public final String hostname;
        public final int port;
        public final String serviceName;
        public final String rmiUrl;
        
        public NodeConfig(String nodeId, String hostname, int port, String serviceName) {
            this.nodeId = nodeId;
            this.hostname = hostname;
            this.port = port;
            this.serviceName = serviceName;
            this.rmiUrl = "rmi://" + hostname + ":" + port + "/" + serviceName;
        }
    }
    
    /**
     * Constructor
     */
    public DistributedStorageService() {
        this.storageNodes = new ConcurrentHashMap<>();
        this.activeConnections = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(10);
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
        
        initializeStorageNodes();
        startHealthCheckMonitoring();
        
        logger.info("🚀 DistributedStorageService inicializado con {} nodos configurados", 
                   storageNodes.size());
    }
    
    /**
     * Inicializa la configuración de nodos de almacenamiento
     */
    private void initializeStorageNodes() {
        // Configurar los 3 nodos de almacenamiento
        storageNodes.put("storage-node-1", 
            new NodeConfig("storage-node-1", "localhost", 1099, "StorageNode1"));
        storageNodes.put("storage-node-2", 
            new NodeConfig("storage-node-2", "localhost", 1100, "StorageNode2"));
        storageNodes.put("storage-node-3", 
            new NodeConfig("storage-node-3", "localhost", 1101, "StorageNode3"));
        
        logger.info("📋 Configurados {} nodos de almacenamiento", storageNodes.size());
    }
    
    /**
     * Inicia el monitoreo de salud de nodos
     */
    private void startHealthCheckMonitoring() {
        scheduledExecutor.scheduleAtFixedRate(this::performHealthCheck, 
            10, HEALTH_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
        
        logger.info("💓 Monitoreo de salud iniciado (intervalo: {}s)", HEALTH_CHECK_INTERVAL_SECONDS);
    }
    
    /**
     * Realiza verificación de salud de todos los nodos
     */
    private void performHealthCheck() {
        logger.debug("🏥 Iniciando verificación de salud de nodos...");
        
        for (NodeConfig nodeConfig : storageNodes.values()) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    StorageNodeInterface node = getNodeConnection(nodeConfig.nodeId);
                    boolean isHealthy = node.heartbeat();
                    
                    if (isHealthy) {
                        logger.debug("💚 Nodo {} saludable", nodeConfig.nodeId);
                    } else {
                        logger.warn("💛 Nodo {} respondió heartbeat pero con problemas", nodeConfig.nodeId);
                    }
                    
                    return isHealthy;
                    
                } catch (Exception e) {
                    logger.warn("❤️‍🩹 Nodo {} no disponible: {}", nodeConfig.nodeId, e.getMessage());
                    activeConnections.remove(nodeConfig.nodeId);
                    return false;
                }
            }, executorService);
        }
    }
    
    /**
     * Obtiene o crea una conexión a un nodo específico
     */
    private StorageNodeInterface getNodeConnection(String nodeId) throws Exception {
        StorageNodeInterface connection = activeConnections.get(nodeId);
        
        if (connection == null) {
            NodeConfig config = storageNodes.get(nodeId);
            if (config == null) {
                throw new IllegalArgumentException("Nodo no configurado: " + nodeId);
            }
            
            logger.debug("🔌 Conectando a nodo: {}", config.rmiUrl);
            connection = (StorageNodeInterface) Naming.lookup(config.rmiUrl);
            
            // Verificar conexión con heartbeat
            if (connection.heartbeat()) {
                activeConnections.put(nodeId, connection);
                logger.info("✅ Conexión establecida con nodo: {}", nodeId);
            } else {
                throw new Exception("Nodo no responde a heartbeat: " + nodeId);
            }
        }
        
        return connection;
    }
    
    /**
     * Almacena un archivo en el sistema distribuido
     */
    public DistributedFileResult storeFile(String fileName, byte[] fileData) {
        return storeFile(generateFileId(), fileName, fileData);
    }
    
    /**
     * Almacena un archivo con ID específico
     */
    public DistributedFileResult storeFile(Long fileId, String fileName, byte[] fileData) {
        // Validaciones de entrada
        if (fileName == null || fileName.trim().isEmpty()) {
            failedOperations++;
            return new DistributedFileResult(null, fileName, false, "Nombre de archivo inválido");
        }
        
        if (fileData == null || fileData.length == 0) {
            failedOperations++;
            return new DistributedFileResult(null, fileName, false, "Datos de archivo inválidos");
        }
        
        logger.info("📥 Iniciando almacenamiento de archivo: {} (ID: {}, Size: {} bytes)", 
                   fileName, fileId, fileData.length);
        
        // Registrar el nombre del archivo para futuras recuperaciones
        fileNamesRegistry.put(fileId, fileName);
        
        totalOperations++;
        
        try {
            // Calcular checksum
            String checksum = calculateChecksum(fileData);
            
            // Seleccionar nodos para replicación
            List<String> selectedNodes = selectNodesForReplication(DEFAULT_REPLICATION_FACTOR);
            
            if (selectedNodes.isEmpty()) {
                String error = "No hay nodos disponibles para almacenamiento";
                logger.error("❌ {}", error);
                failedOperations++;
                return DistributedFileResult.failure(fileId, fileName, error);
            }
            
            // Almacenar en paralelo en nodos seleccionados
            List<CompletableFuture<String>> storageFutures = new ArrayList<>();
            Map<String, String> nodeLocations = new ConcurrentHashMap<>();
            
            for (String nodeId : selectedNodes) {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        StorageNodeInterface node = getNodeConnection(nodeId);
                        String localPath = node.storeFile(fileId, fileName, fileData, checksum);
                        nodeLocations.put(nodeId, localPath);
                        logger.info("✅ Archivo almacenado en {}: {}", nodeId, localPath);
                        return localPath;
                    } catch (Exception e) {
                        logger.error("❌ Error almacenando en {}: {}", nodeId, e.getMessage());
                        throw new RuntimeException("Error en nodo " + nodeId, e);
                    }
                }, executorService);
                
                storageFutures.add(future);
            }
            
            // Esperar resultados con timeout
            try {
                CompletableFuture.allOf(storageFutures.toArray(new CompletableFuture[0]))
                    .get(30, TimeUnit.SECONDS);
                
                // Crear resultado exitoso
                DistributedFileResult result = DistributedFileResult.success(fileId, fileName, 
                    "Archivo almacenado exitosamente en " + selectedNodes.size() + " nodos");
                result.setReplicatedNodes(selectedNodes);
                result.setNodeLocations(nodeLocations);
                result.setFileSizeBytes(fileData.length);
                result.setChecksum(checksum);
                result.setReplicationFactor(selectedNodes.size());
                result.setPrimaryNode(selectedNodes.get(0));
                
                successfulOperations++;
                logger.info("🎉 Almacenamiento exitoso de archivo: {} en {} nodos", fileName, selectedNodes.size());
                
                return result;
                
            } catch (TimeoutException e) {
                String error = "Timeout en almacenamiento después de 30 segundos";
                logger.error("⏰ {}", error);
                failedOperations++;
                return DistributedFileResult.failure(fileId, fileName, error);
            }
            
        } catch (Exception e) {
            String error = "Error general en almacenamiento: " + e.getMessage();
            logger.error("💥 {}", error, e);
            failedOperations++;
            return DistributedFileResult.failure(fileId, fileName, error);
        }
    }
    
    /**
     * Recupera un archivo del sistema distribuido
     */
    public DistributedFileResult retrieveFile(Long fileId) {
        // Validación de entrada
        if (fileId == null || fileId <= 0) {
            failedOperations++;
            return new DistributedFileResult(fileId, "unknown", false, "ID de archivo inválido");
        }
        
        logger.info("📤 Iniciando recuperación de archivo con ID: {}", fileId);
        
        totalOperations++;
        
        try {
            // Intentar recuperar desde cualquier nodo disponible
            List<String> availableNodes = getAvailableNodes();
            
            if (availableNodes.isEmpty()) {
                String error = "No hay nodos disponibles para recuperación";
                logger.error("❌ {}", error);
                failedOperations++;
                return DistributedFileResult.failure(fileId, "unknown", error);
            }
            
            // Intentar recuperación desde cada nodo hasta encontrar el archivo
            for (String nodeId : availableNodes) {
                try {
                    StorageNodeInterface node = getNodeConnection(nodeId);
                    byte[] fileData = node.retrieveFile(fileId, null);
                    
                    if (fileData != null && fileData.length > 0) {
                        // Obtener el nombre del archivo desde el nodo remoto
                        String fileName = extractFileNameFromNode(node, fileId);
                        if (fileName == null || fileName.isEmpty()) {
                            fileName = "recovered-file-" + fileId;
                        }
                        
                        DistributedFileResult result = DistributedFileResult.success(fileId, fileName, 
                            "Archivo recuperado exitosamente desde " + nodeId);
                        result.setFileSizeBytes(fileData.length);
                        result.setPrimaryNode(nodeId);
                        
                        successfulOperations++;
                        logger.info("🎉 Archivo recuperado exitosamente desde nodo: {}", nodeId);
                        
                        return result;
                    }
                    
                } catch (Exception e) {
                    logger.warn("⚠️ Error recuperando desde {}: {}", nodeId, e.getMessage());
                    // Continuar con el siguiente nodo
                }
            }
            
            String error = "Archivo no encontrado en ningún nodo disponible";
            logger.error("🔍 {}", error);
            failedOperations++;
            return DistributedFileResult.failure(fileId, "unknown", error);
            
        } catch (Exception e) {
            String error = "Error general en recuperación: " + e.getMessage();
            logger.error("💥 {}", error, e);
            failedOperations++;
            return DistributedFileResult.failure(fileId, "unknown", error);
        }
    }
    
    /**
     * Elimina un archivo del sistema distribuido
     */
    public DistributedFileResult deleteFile(Long fileId) {
        // Validación de entrada
        if (fileId == null || fileId <= 0) {
            failedOperations++;
            return new DistributedFileResult(fileId, "unknown", false, "ID de archivo inválido");
        }
        
        logger.info("🗑️ Iniciando eliminación de archivo con ID: {}", fileId);
        
        totalOperations++;
        
        try {
            List<String> availableNodes = getAvailableNodes();
            
            if (availableNodes.isEmpty()) {
                String error = "No hay nodos disponibles para eliminación";
                logger.error("❌ {}", error);
                failedOperations++;
                return DistributedFileResult.failure(fileId, "unknown", error);
            }
            
            // Eliminar desde todos los nodos disponibles
            List<CompletableFuture<Boolean>> deletionFutures = new ArrayList<>();
            List<String> successfulDeletions = new ArrayList<>();
            
            for (String nodeId : availableNodes) {
                CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        StorageNodeInterface node = getNodeConnection(nodeId);
                        boolean deleted = node.deleteFile(fileId, null);
                        
                        if (deleted) {
                            synchronized (successfulDeletions) {
                                successfulDeletions.add(nodeId);
                            }
                            logger.info("✅ Archivo eliminado de nodo: {}", nodeId);
                        }
                        
                        return deleted;
                        
                    } catch (Exception e) {
                        logger.warn("⚠️ Error eliminando desde {}: {}", nodeId, e.getMessage());
                        return false;
                    }
                }, executorService);
                
                deletionFutures.add(future);
            }
            
            // Esperar todas las eliminaciones
            try {
                CompletableFuture.allOf(deletionFutures.toArray(new CompletableFuture[0]))
                    .get(15, TimeUnit.SECONDS);
                
                if (!successfulDeletions.isEmpty()) {
                    DistributedFileResult result = DistributedFileResult.success(fileId, "deleted-file", 
                        "Archivo eliminado de " + successfulDeletions.size() + " nodos");
                    result.setReplicatedNodes(successfulDeletions);
                    
                    successfulOperations++;
                    logger.info("🎉 Eliminación exitosa de archivo de {} nodos", successfulDeletions.size());
                    
                    return result;
                } else {
                    String error = "No se pudo eliminar el archivo de ningún nodo";
                    logger.error("❌ {}", error);
                    failedOperations++;
                    return DistributedFileResult.failure(fileId, "unknown", error);
                }
                
            } catch (TimeoutException e) {
                String error = "Timeout en eliminación después de 15 segundos";
                logger.error("⏰ {}", error);
                failedOperations++;
                return DistributedFileResult.failure(fileId, "unknown", error);
            }
            
        } catch (Exception e) {
            String error = "Error general en eliminación: " + e.getMessage();
            logger.error("💥 {}", error, e);
            failedOperations++;
            return DistributedFileResult.failure(fileId, "unknown", error);
        }
    }
    
    /**
     * Verifica la integridad de un archivo
     */
    public FileIntegrityReport verifyFileIntegrity(Long fileId) {
        // Validación de entrada
        if (fileId == null || fileId <= 0) {
            FileIntegrityReport report = new FileIntegrityReport(fileId, "unknown");
            report.setIntegrityValid(false);
            report.setRecommendedAction("ID de archivo inválido");
            return report;
        }
        
        logger.info("🔍 Verificando integridad de archivo con ID: {}", fileId);
        
        FileIntegrityReport report = new FileIntegrityReport(fileId, "file-" + fileId);
        Map<String, String> nodeChecksums = new ConcurrentHashMap<>();
        List<String> validNodes = new ArrayList<>();
        List<String> corruptedNodes = new ArrayList<>();
        List<String> unavailableNodes = new ArrayList<>();
        
        List<String> availableNodes = getAvailableNodes();
        
        // Verificar en paralelo en todos los nodos
        List<CompletableFuture<Void>> verificationFutures = new ArrayList<>();
        
        for (String nodeId : availableNodes) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    StorageNodeInterface node = getNodeConnection(nodeId);
                    
                    // Obtener archivo y calcular checksum
                    byte[] fileData = node.retrieveFile(fileId, null);
                    if (fileData != null) {
                        String checksum = calculateChecksum(fileData);
                        nodeChecksums.put(nodeId, checksum);
                        
                        synchronized (validNodes) {
                            validNodes.add(nodeId);
                        }
                    } else {
                        synchronized (unavailableNodes) {
                            unavailableNodes.add(nodeId);
                        }
                    }
                    
                } catch (Exception e) {
                    logger.warn("⚠️ Error verificando en {}: {}", nodeId, e.getMessage());
                    synchronized (unavailableNodes) {
                        unavailableNodes.add(nodeId);
                    }
                }
            }, executorService);
            
            verificationFutures.add(future);
        }
        
        // Esperar todas las verificaciones
        try {
            CompletableFuture.allOf(verificationFutures.toArray(new CompletableFuture[0]))
                .get(20, TimeUnit.SECONDS);
                
        } catch (Exception e) {
            logger.warn("⚠️ Timeout o error en verificación de integridad: {}", e.getMessage());
        }
        
        // Analizar resultados
        report.setNodeChecksums(nodeChecksums);
        report.setValidNodes(validNodes);
        report.setCorruptedNodes(corruptedNodes);
        report.setUnavailableNodes(unavailableNodes);
        report.setTotalNodes(availableNodes.size());
        report.setValidNodeCount(validNodes.size());
        
        // Determinar checksum esperado (el más común)
        if (!nodeChecksums.isEmpty()) {
            Map<String, Integer> checksumCount = new HashMap<>();
            for (String checksum : nodeChecksums.values()) {
                checksumCount.put(checksum, checksumCount.getOrDefault(checksum, 0) + 1);
            }
            
            String expectedChecksum = checksumCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
            
            report.setExpectedChecksum(expectedChecksum);
            
            // Identificar nodos corruptos
            for (Map.Entry<String, String> entry : nodeChecksums.entrySet()) {
                if (!expectedChecksum.equals(entry.getValue())) {
                    corruptedNodes.add(entry.getKey());
                    validNodes.remove(entry.getKey());
                }
            }
        }
        
        report.setValidNodeCount(validNodes.size());
        report.calculateIntegrityPercentage();
        report.generateRecommendedAction();
        
        boolean isValid = corruptedNodes.isEmpty() && !validNodes.isEmpty();
        report.setIntegrityValid(isValid);
        
        logger.info("🔍 Verificación completada: {} válidos, {} corruptos, {} no disponibles", 
                   validNodes.size(), corruptedNodes.size(), unavailableNodes.size());
        
        return report;
    }
    
    /**
     * Obtiene estadísticas del sistema
     */
    public SystemStatistics getSystemStatistics() {
        logger.info("📊 Generando estadísticas del sistema...");
        
        SystemStatistics stats = new SystemStatistics();
        stats.setTotalStorageNodes(storageNodes.size());
        
        List<String> availableNodes = getAvailableNodes();
        stats.setActiveNodes(availableNodes.size());
        stats.setInactiveNodes(storageNodes.size() - availableNodes.size());
        
        // Estadísticas de operaciones
        stats.setTotalOperations(totalOperations);
        stats.setSuccessfulOperations(successfulOperations);
        stats.setFailedOperations(failedOperations);
        
        // Obtener estadísticas de nodos en paralelo
        Map<String, SystemStatistics.NodeStatistics> nodeStats = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> statsFutures = new ArrayList<>();
        
        for (String nodeId : availableNodes) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    StorageNodeInterface node = getNodeConnection(nodeId);
                    NodeInfo nodeInfo = node.getNodeInfo();
                    
                    SystemStatistics.NodeStatistics nodeStat = new SystemStatistics.NodeStatistics();
                    nodeStat.setNodeId(nodeId);
                    nodeStat.setStatus("ACTIVE");
                    nodeStat.setCapacity(nodeInfo.getTotalCapacity());
                    nodeStat.setUsedSpace(nodeInfo.getUsedCapacity());
                    nodeStat.setAvailableSpace(nodeInfo.getAvailableCapacity());
                    nodeStat.setLastHeartbeat(LocalDateTime.now());
                    nodeStat.setHealthy(true);
                    nodeStat.calculateUtilization();
                    
                    Long[] storedFiles = node.getStoredFiles();
                    nodeStat.setFilesStored(storedFiles != null ? storedFiles.length : 0);
                    
                    nodeStats.put(nodeId, nodeStat);
                    
                } catch (Exception e) {
                    logger.warn("⚠️ Error obteniendo estadísticas de {}: {}", nodeId, e.getMessage());
                    
                    SystemStatistics.NodeStatistics nodeStat = new SystemStatistics.NodeStatistics();
                    nodeStat.setNodeId(nodeId);
                    nodeStat.setStatus("ERROR");
                    nodeStat.setHealthy(false);
                    nodeStats.put(nodeId, nodeStat);
                }
            }, executorService);
            
            statsFutures.add(future);
        }
        
        // Esperar estadísticas
        try {
            CompletableFuture.allOf(statsFutures.toArray(new CompletableFuture[0]))
                .get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("⚠️ Timeout obteniendo estadísticas de nodos: {}", e.getMessage());
        }
        
        stats.setNodeStatistics(nodeStats);
        
        // Calcular estadísticas agregadas
        long totalFiles = 0;
        long totalCapacity = 0;
        long totalUsed = 0;
        
        for (SystemStatistics.NodeStatistics nodeStat : nodeStats.values()) {
            totalFiles += nodeStat.getFilesStored();
            totalCapacity += nodeStat.getCapacity();
            totalUsed += nodeStat.getUsedSpace();
        }
        
        stats.setTotalStoredFiles(totalFiles);
        stats.setTotalStorageCapacity(totalCapacity);
        stats.setUsedStorageCapacity(totalUsed);
        stats.setAvailableStorageCapacity(totalCapacity - totalUsed);
        
        stats.calculateDerivedStatistics();
        
        logger.info("📊 Estadísticas generadas: {} nodos activos, {} archivos, {:.2f}% utilización", 
                   availableNodes.size(), totalFiles, stats.getStorageUtilizationPercentage());
        
        return stats;
    }
    
    /**
     * Selecciona nodos para replicación basado en disponibilidad y carga
     */
    private List<String> selectNodesForReplication(int replicationFactor) {
        List<String> availableNodes = getAvailableNodes();
        
        if (availableNodes.size() <= replicationFactor) {
            return availableNodes;
        }
        
        // Seleccionar nodos con menor carga (implementación simple)
        Collections.shuffle(availableNodes);
        return availableNodes.subList(0, Math.min(replicationFactor, availableNodes.size()));
    }
    
    /**
     * Obtiene lista de nodos disponibles
     */
    private List<String> getAvailableNodes() {
        List<String> available = new ArrayList<>();
        
        for (String nodeId : storageNodes.keySet()) {
            try {
                StorageNodeInterface node = getNodeConnection(nodeId);
                if (node.heartbeat()) {
                    available.add(nodeId);
                }
            } catch (Exception e) {
                // Nodo no disponible
                activeConnections.remove(nodeId);
            }
        }
        
        return available;
    }
    
    /**
     * Genera un ID único para archivo
     */
    private Long generateFileId() {
        return System.currentTimeMillis() + (long) (Math.random() * 1000);
    }
    
    /**
     * Calcula checksum SHA-256
     */
    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
    
    /**
     * Cierra el servicio y libera recursos
     */
    public void shutdown() {
        logger.info("🛑 Cerrando DistributedStorageService...");
        
        executorService.shutdown();
        scheduledExecutor.shutdown();
        
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        activeConnections.clear();
        logger.info("👋 DistributedStorageService cerrado correctamente");
    }
    
    /**
     * Extrae el nombre del archivo original desde el registro local
     */
    private String extractFileNameFromNode(StorageNodeInterface node, Long fileId) {
        // Primero, intentar obtener desde el registro local
        String fileName = fileNamesRegistry.get(fileId);
        if (fileName != null && !fileName.isEmpty()) {
            return fileName;
        }
        
        // Si no está en el registro, usar un nombre por defecto
        logger.debug("Nombre de archivo no encontrado en registro para ID: {}, usando nombre por defecto", fileId);
        return "recovered-file-" + fileId + ".dat";
    }
    
    /**
     * Resetea los contadores de estadísticas para testing
     */
    public void resetStatistics() {
        totalOperations = 0;
        successfulOperations = 0;
        failedOperations = 0;
        logger.debug("🔄 Estadísticas reseteadas para testing");
    }
}
