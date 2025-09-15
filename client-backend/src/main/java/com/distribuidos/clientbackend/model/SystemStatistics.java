package com.distribuidos.clientbackend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Estadísticas del sistema distribuido de almacenamiento
 */
public class SystemStatistics implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private LocalDateTime generationTimestamp;
    private int totalStorageNodes;
    private int activeNodes;
    private int inactiveNodes;
    private long totalStoredFiles;
    private long totalStorageCapacity;
    private long usedStorageCapacity;
    private long availableStorageCapacity;
    private double storageUtilizationPercentage;
    private Map<String, NodeStatistics> nodeStatistics;
    private List<String> recentOperations;
    private long totalOperations;
    private long successfulOperations;
    private long failedOperations;
    private double successRate;
    private String systemHealth;
    
    // Clase interna para estadísticas de nodo
    public static class NodeStatistics implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String nodeId;
        private String status;
        private long filesStored;
        private long capacity;
        private long usedSpace;
        private long availableSpace;
        private double utilizationPercentage;
        private LocalDateTime lastHeartbeat;
        private boolean isHealthy;
        
        // Constructores, getters y setters
        public NodeStatistics() {}
        
        public NodeStatistics(String nodeId, String status) {
            this.nodeId = nodeId;
            this.status = status;
        }
        
        public String getNodeId() { return nodeId; }
        public void setNodeId(String nodeId) { this.nodeId = nodeId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public long getFilesStored() { return filesStored; }
        public void setFilesStored(long filesStored) { this.filesStored = filesStored; }
        
        public long getCapacity() { return capacity; }
        public void setCapacity(long capacity) { this.capacity = capacity; }
        
        public long getUsedSpace() { return usedSpace; }
        public void setUsedSpace(long usedSpace) { this.usedSpace = usedSpace; }
        
        public long getAvailableSpace() { return availableSpace; }
        public void setAvailableSpace(long availableSpace) { this.availableSpace = availableSpace; }
        
        public double getUtilizationPercentage() { return utilizationPercentage; }
        public void setUtilizationPercentage(double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }
        
        public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
        public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
        
        public boolean isHealthy() { return isHealthy; }
        public void setHealthy(boolean healthy) { isHealthy = healthy; }
        
        public void calculateUtilization() {
            if (capacity > 0) {
                this.utilizationPercentage = ((double) usedSpace / capacity) * 100.0;
            }
        }
        
        public String getFormattedCapacity() {
            return formatBytes(capacity);
        }
        
        public String getFormattedUsedSpace() {
            return formatBytes(usedSpace);
        }
        
        public String getFormattedAvailableSpace() {
            return formatBytes(availableSpace);
        }
    }
    
    // Constructores
    public SystemStatistics() {
        this.generationTimestamp = LocalDateTime.now();
    }
    
    // Getters y Setters
    public LocalDateTime getGenerationTimestamp() { return generationTimestamp; }
    public void setGenerationTimestamp(LocalDateTime generationTimestamp) { this.generationTimestamp = generationTimestamp; }
    
    public int getTotalStorageNodes() { return totalStorageNodes; }
    public void setTotalStorageNodes(int totalStorageNodes) { this.totalStorageNodes = totalStorageNodes; }
    
    public int getActiveNodes() { return activeNodes; }
    public void setActiveNodes(int activeNodes) { this.activeNodes = activeNodes; }
    
    public int getInactiveNodes() { return inactiveNodes; }
    public void setInactiveNodes(int inactiveNodes) { this.inactiveNodes = inactiveNodes; }
    
    public long getTotalStoredFiles() { return totalStoredFiles; }
    public void setTotalStoredFiles(long totalStoredFiles) { this.totalStoredFiles = totalStoredFiles; }
    
    public long getTotalStorageCapacity() { return totalStorageCapacity; }
    public void setTotalStorageCapacity(long totalStorageCapacity) { this.totalStorageCapacity = totalStorageCapacity; }
    
    public long getUsedStorageCapacity() { return usedStorageCapacity; }
    public void setUsedStorageCapacity(long usedStorageCapacity) { this.usedStorageCapacity = usedStorageCapacity; }
    
    public long getAvailableStorageCapacity() { return availableStorageCapacity; }
    public void setAvailableStorageCapacity(long availableStorageCapacity) { this.availableStorageCapacity = availableStorageCapacity; }
    
    public double getStorageUtilizationPercentage() { return storageUtilizationPercentage; }
    public void setStorageUtilizationPercentage(double storageUtilizationPercentage) { this.storageUtilizationPercentage = storageUtilizationPercentage; }
    
    public Map<String, NodeStatistics> getNodeStatistics() { return nodeStatistics; }
    public void setNodeStatistics(Map<String, NodeStatistics> nodeStatistics) { this.nodeStatistics = nodeStatistics; }
    
    public List<String> getRecentOperations() { return recentOperations; }
    public void setRecentOperations(List<String> recentOperations) { this.recentOperations = recentOperations; }
    
    public long getTotalOperations() { return totalOperations; }
    public void setTotalOperations(long totalOperations) { this.totalOperations = totalOperations; }
    
    public long getSuccessfulOperations() { return successfulOperations; }
    public void setSuccessfulOperations(long successfulOperations) { this.successfulOperations = successfulOperations; }
    
    public long getFailedOperations() { return failedOperations; }
    public void setFailedOperations(long failedOperations) { this.failedOperations = failedOperations; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public String getSystemHealth() { return systemHealth; }
    public void setSystemHealth(String systemHealth) { this.systemHealth = systemHealth; }
    
    /**
     * Calcula estadísticas derivadas
     */
    public void calculateDerivedStatistics() {
        // Calcular porcentaje de utilización
        if (totalStorageCapacity > 0) {
            this.storageUtilizationPercentage = ((double) usedStorageCapacity / totalStorageCapacity) * 100.0;
        }
        
        // Calcular tasa de éxito
        if (totalOperations > 0) {
            this.successRate = ((double) successfulOperations / totalOperations) * 100.0;
        }
        
        // Determinar salud del sistema
        determineSystemHealth();
    }
    
    /**
     * Determina la salud general del sistema
     */
    private void determineSystemHealth() {
        double activeNodePercentage = totalStorageNodes > 0 ? 
            ((double) activeNodes / totalStorageNodes) * 100.0 : 0.0;
        
        if (activeNodePercentage >= 100.0 && successRate >= 95.0) {
            this.systemHealth = "EXCELENTE";
        } else if (activeNodePercentage >= 80.0 && successRate >= 90.0) {
            this.systemHealth = "BUENO";
        } else if (activeNodePercentage >= 60.0 && successRate >= 80.0) {
            this.systemHealth = "ACEPTABLE";
        } else if (activeNodePercentage >= 40.0 && successRate >= 60.0) {
            this.systemHealth = "CRÍTICO";
        } else {
            this.systemHealth = "EMERGENCIA";
        }
    }
    
    /**
     * Formatear bytes a formato legible
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    public String getFormattedTotalCapacity() {
        return formatBytes(totalStorageCapacity);
    }
    
    public String getFormattedUsedCapacity() {
        return formatBytes(usedStorageCapacity);
    }
    
    public String getFormattedAvailableCapacity() {
        return formatBytes(availableStorageCapacity);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SystemStatistics{");
        sb.append("activeNodes=").append(activeNodes).append("/").append(totalStorageNodes);
        sb.append(", totalFiles=").append(totalStoredFiles);
        sb.append(", utilization=").append(String.format("%.2f", storageUtilizationPercentage)).append("%");
        sb.append(", successRate=").append(String.format("%.2f", successRate)).append("%");
        sb.append(", health=").append(systemHealth);
        sb.append(", timestamp=").append(generationTimestamp);
        sb.append('}');
        return sb.toString();
    }
}
