package com.distribuidos.shared.rmi;

import java.io.Serializable;

/**
 * Clase que contiene información del estado de un nodo de almacenamiento
 */
public class NodeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nodeId;
    private String hostname;
    private Integer port;
    private Long totalCapacity;
    private Long usedCapacity;
    private Long availableCapacity;
    private double usagePercentage;
    private String status;
    private int activeConnections;
    private long uptime;
    
    // Constructors
    public NodeInfo() {}
    
    public NodeInfo(String nodeId, String hostname, Integer port, Long totalCapacity, 
                   Long usedCapacity, String status) {
        this.nodeId = nodeId;
        this.hostname = hostname;
        this.port = port;
        this.totalCapacity = totalCapacity;
        this.usedCapacity = usedCapacity;
        this.availableCapacity = totalCapacity - usedCapacity;
        this.usagePercentage = totalCapacity > 0 ? (double) usedCapacity / totalCapacity * 100.0 : 0.0;
        this.status = status;
    }
    
    // Getters and Setters
    public String getNodeId() {
        return nodeId;
    }
    
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public Integer getPort() {
        return port;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    public Long getTotalCapacity() {
        return totalCapacity;
    }
    
    public void setTotalCapacity(Long totalCapacity) {
        this.totalCapacity = totalCapacity;
        updateCalculatedFields();
    }
    
    public Long getUsedCapacity() {
        return usedCapacity;
    }
    
    public void setUsedCapacity(Long usedCapacity) {
        this.usedCapacity = usedCapacity;
        updateCalculatedFields();
    }
    
    public Long getAvailableCapacity() {
        return availableCapacity;
    }
    
    public void setAvailableCapacity(Long availableCapacity) {
        this.availableCapacity = availableCapacity;
    }
    
    public double getUsagePercentage() {
        return usagePercentage;
    }
    
    public void setUsagePercentage(double usagePercentage) {
        this.usagePercentage = usagePercentage;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getActiveConnections() {
        return activeConnections;
    }
    
    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }
    
    public long getUptime() {
        return uptime;
    }
    
    public void setUptime(long uptime) {
        this.uptime = uptime;
    }
    
    /**
     * Actualiza los campos calculados cuando cambian capacity values
     */
    private void updateCalculatedFields() {
        if (totalCapacity != null && usedCapacity != null) {
            this.availableCapacity = totalCapacity - usedCapacity;
            this.usagePercentage = totalCapacity > 0 ? (double) usedCapacity / totalCapacity * 100.0 : 0.0;
        }
    }
    
    /**
     * Obtiene la dirección completa del nodo
     */
    public String getAddress() {
        return hostname + ":" + port;
    }
    
    /**
     * Formatea el tamaño en bytes a una representación legible
     */
    public static String formatBytes(Long bytes) {
        if (bytes == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    @Override
    public String toString() {
        return "NodeInfo{" +
                "nodeId='" + nodeId + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", totalCapacity=" + formatBytes(totalCapacity) +
                ", usedCapacity=" + formatBytes(usedCapacity) +
                ", availableCapacity=" + formatBytes(availableCapacity) +
                ", usagePercentage=" + String.format("%.2f%%", usagePercentage) +
                ", status='" + status + '\'' +
                ", activeConnections=" + activeConnections +
                ", uptime=" + uptime +
                '}';
    }
}