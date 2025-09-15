package com.distribuidos.clientbackend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Resultado de una operación de archivo distribuido
 */
public class DistributedFileResult implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long fileId;
    private String fileName;
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private List<String> replicatedNodes;
    private Map<String, String> nodeLocations;
    private long fileSizeBytes;
    private String checksum;
    private int replicationFactor;
    private String primaryNode;
    
    // Constructores
    public DistributedFileResult() {
        this.timestamp = LocalDateTime.now();
    }
    
    public DistributedFileResult(Long fileId, String fileName, boolean success, String message) {
        this();
        this.fileId = fileId;
        this.fileName = fileName;
        this.success = success;
        this.message = message;
    }
    
    // Getters y Setters
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public List<String> getReplicatedNodes() { return replicatedNodes; }
    public void setReplicatedNodes(List<String> replicatedNodes) { this.replicatedNodes = replicatedNodes; }
    
    public Map<String, String> getNodeLocations() { return nodeLocations; }
    public void setNodeLocations(Map<String, String> nodeLocations) { this.nodeLocations = nodeLocations; }
    
    public long getFileSizeBytes() { return fileSizeBytes; }
    public void setFileSizeBytes(long fileSizeBytes) { this.fileSizeBytes = fileSizeBytes; }
    
    public String getChecksum() { return checksum; }
    public void setChecksum(String checksum) { this.checksum = checksum; }
    
    public int getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(int replicationFactor) { this.replicationFactor = replicationFactor; }
    
    public String getPrimaryNode() { return primaryNode; }
    public void setPrimaryNode(String primaryNode) { this.primaryNode = primaryNode; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DistributedFileResult{");
        sb.append("fileId=").append(fileId);
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", replicatedNodes=").append(replicatedNodes);
        sb.append(", fileSizeBytes=").append(fileSizeBytes);
        sb.append(", checksum='").append(checksum).append('\'');
        sb.append(", replicationFactor=").append(replicationFactor);
        sb.append(", primaryNode='").append(primaryNode).append('\'');
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Formatear el tamaño del archivo en bytes a formato legible
     */
    public String getFormattedFileSize() {
        return formatBytes(fileSizeBytes);
    }
    
    /**
     * Formatear bytes a formato legible (KB, MB, GB)
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * Crear resultado exitoso
     */
    public static DistributedFileResult success(Long fileId, String fileName, String message) {
        return new DistributedFileResult(fileId, fileName, true, message);
    }
    
    /**
     * Crear resultado fallido
     */
    public static DistributedFileResult failure(Long fileId, String fileName, String message) {
        return new DistributedFileResult(fileId, fileName, false, message);
    }
}
