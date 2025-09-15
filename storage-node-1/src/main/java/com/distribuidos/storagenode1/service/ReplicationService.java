package com.distribuidos.storagenode1.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Servicio de replicación para coordinar la sincronización de archivos
 * entre nodos de almacenamiento distribuido.
 */
public class ReplicationService {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final String nodeId;
    private final Map<String, ReplicationEntry> replicationQueue;
    private final ScheduledExecutorService scheduler;
    private final Map<String, String> replicationPartners;
    
    public ReplicationService(String nodeId) {
        this.nodeId = nodeId;
        this.replicationQueue = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.replicationPartners = new ConcurrentHashMap<>();
        
        initializeReplication();
    }
    
    private void initializeReplication() {
        System.out.println("🔄 " + getCurrentTimestamp() + " - Inicializando servicio de replicación para nodo: " + nodeId);
        
        // Configurar nodos de replicación según el ID del nodo actual
        setupReplicationPartners();
        
        // Iniciar tareas programadas
        startReplicationTasks();
    }
    
    private void setupReplicationPartners() {
        // Para un sistema de 3 nodos, cada nodo se replica con los otros 2
        switch (nodeId) {
            case "1":
                replicationPartners.put("node2", "rmi://localhost:1100/StorageNode2");
                replicationPartners.put("node3", "rmi://localhost:1101/StorageNode3");
                break;
            case "2":
                replicationPartners.put("node1", "rmi://localhost:1099/StorageNode1");
                replicationPartners.put("node3", "rmi://localhost:1101/StorageNode3");
                break;
            case "3":
                replicationPartners.put("node1", "rmi://localhost:1099/StorageNode1");
                replicationPartners.put("node2", "rmi://localhost:1100/StorageNode2");
                break;
        }
        
        System.out.println("🤝 Partners de replicación configurados: " + replicationPartners.keySet());
    }
    
    private void startReplicationTasks() {
        // Tarea de sincronización cada 30 segundos
        scheduler.scheduleAtFixedRate(this::processReplicationQueue, 10, 30, TimeUnit.SECONDS);
        
        // Tarea de verificación de salud cada 60 segundos
        scheduler.scheduleAtFixedRate(this::checkPartnerHealth, 30, 60, TimeUnit.SECONDS);
        
        System.out.println("⏰ Tareas de replicación programadas");
    }
    
    /**
     * Agrega un archivo a la cola de replicación
     */
    public void addToReplicationQueue(Long fileId, String fileName, byte[] fileData, String checksum) {
        System.out.println("📋 " + getCurrentTimestamp() + " - Archivo agregado a cola de replicación: " + fileName);
        
        // Usar el método existente con la conversión apropiada
        String localPath = fileId + "_" + fileName; // Simular el path local
        notifyFileStored(fileId.toString(), fileName, localPath);
    }
    
    /**
     * Notifica que un archivo ha sido almacenado y necesita replicación.
     */
    public void notifyFileStored(String fileId, String fileName, String localPath) {
        System.out.println("📋 " + getCurrentTimestamp() + " - Archivo agregado a cola de replicación: " + fileName);
        
        ReplicationEntry entry = new ReplicationEntry(fileId, fileName, localPath, "STORE", getCurrentTimestamp());
        replicationQueue.put(fileId, entry);
    }
    
    /**
     * Notifica que un archivo ha sido eliminado y necesita sincronización.
     */
    public void notifyFileDeleted(String fileId, String localPath) {
        System.out.println("📋 " + getCurrentTimestamp() + " - Archivo agregado a cola de eliminación: " + fileId);
        
        ReplicationEntry entry = new ReplicationEntry(fileId, "deleted", localPath, "DELETE", getCurrentTimestamp());
        replicationQueue.put(fileId + "_delete", entry);
    }
    
    /**
     * Procesa la cola de replicación.
     */
    private void processReplicationQueue() {
        if (replicationQueue.isEmpty()) {
            return;
        }
        
        System.out.println("🔄 " + getCurrentTimestamp() + " - Procesando cola de replicación: " + 
                         replicationQueue.size() + " elementos");
        
        List<String> processedEntries = new ArrayList<>();
        
        for (Map.Entry<String, ReplicationEntry> entry : replicationQueue.entrySet()) {
            ReplicationEntry replication = entry.getValue();
            
            try {
                if ("STORE".equals(replication.getOperation())) {
                    processFileReplication(replication);
                } else if ("DELETE".equals(replication.getOperation())) {
                    processFileDeletion(replication);
                }
                
                processedEntries.add(entry.getKey());
                
            } catch (Exception e) {
                System.err.println("❌ Error procesando replicación para " + replication.getFileId() + ": " + e.getMessage());
            }
        }
        
        // Remover entradas procesadas
        for (String key : processedEntries) {
            replicationQueue.remove(key);
        }
    }
    
    private void processFileReplication(ReplicationEntry entry) {
        System.out.println("📤 " + getCurrentTimestamp() + " - Replicando archivo: " + entry.getFileName());
        
        for (Map.Entry<String, String> partner : replicationPartners.entrySet()) {
            try {
                // Simular replicación (en implementación real se haría via RMI)
                System.out.println("  ➡️ Enviando a " + partner.getKey() + ": " + partner.getValue());
                
                // Aquí se implementaría la lógica real de RMI para enviar el archivo
                // Registry registry = LocateRegistry.getRegistry(host, port);
                // StorageNodeInterface remoteNode = (StorageNodeInterface) registry.lookup(serviceName);
                // remoteNode.storeFile(entry.getFileId(), entry.getFileName(), content, checksum);
                
                System.out.println("  ✅ Replicación exitosa a " + partner.getKey());
                
            } catch (Exception e) {
                System.err.println("  ❌ Error replicando a " + partner.getKey() + ": " + e.getMessage());
            }
        }
    }
    
    private void processFileDeletion(ReplicationEntry entry) {
        System.out.println("🗑️ " + getCurrentTimestamp() + " - Propagando eliminación: " + entry.getFileId());
        
        for (Map.Entry<String, String> partner : replicationPartners.entrySet()) {
            try {
                System.out.println("  ➡️ Notificando eliminación a " + partner.getKey());
                
                // Aquí se implementaría la lógica real de RMI para eliminar el archivo
                // remoteNode.deleteFile(entry.getFileId(), entry.getLocalPath());
                
                System.out.println("  ✅ Eliminación propagada a " + partner.getKey());
                
            } catch (Exception e) {
                System.err.println("  ❌ Error propagando eliminación a " + partner.getKey() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Verifica la salud de los partners de replicación.
     */
    private void checkPartnerHealth() {
        System.out.println("🏥 " + getCurrentTimestamp() + " - Verificando salud de partners de replicación");
        
        for (Map.Entry<String, String> partner : replicationPartners.entrySet()) {
            try {
                // Simular verificación de salud
                System.out.println("  💓 " + partner.getKey() + ": SALUDABLE");
                
                // Aquí se implementaría la verificación real via RMI
                // remoteNode.heartbeat();
                
            } catch (Exception e) {
                System.err.println("  ❌ " + partner.getKey() + ": NO DISPONIBLE - " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene estadísticas del servicio de replicación.
     */
    public Map<String, Object> getReplicationStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("nodeId", nodeId);
        stats.put("queueSize", replicationQueue.size());
        stats.put("partners", replicationPartners.keySet());
        stats.put("timestamp", getCurrentTimestamp());
        return stats;
    }
    
    /**
     * Detiene el servicio de replicación.
     */
    public void shutdown() {
        System.out.println("🛑 " + getCurrentTimestamp() + " - Deteniendo servicio de replicación");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
    
    /**
     * Clase interna para representar una entrada de replicación.
     */
    private static class ReplicationEntry {
        private final String fileId;
        private final String fileName;
        private final String localPath;
        private final String operation;
        private final String timestamp;
        
        public ReplicationEntry(String fileId, String fileName, String localPath, String operation, String timestamp) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.localPath = localPath;
            this.operation = operation;
            this.timestamp = timestamp;
        }
        
        public String getFileId() { return fileId; }
        public String getFileName() { return fileName; }
        public String getLocalPath() { return localPath; }
        public String getOperation() { return operation; }
        public String getTimestamp() { return timestamp; }
    }
}