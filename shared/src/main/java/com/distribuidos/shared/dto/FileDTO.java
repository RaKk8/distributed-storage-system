package com.distribuidos.shared.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO para representar información de archivos en operaciones
 */
public class FileDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String checksum;
    private Long directoryId;
    private String directoryPath;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int replicaCount;
    
    // Constructors
    public FileDTO() {}
    
    public FileDTO(Long id, String name, String originalName, Long fileSize, 
                   String mimeType, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.originalName = originalName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.ownerUsername = ownerUsername;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public String getChecksum() {
        return checksum;
    }
    
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    public Long getDirectoryId() {
        return directoryId;
    }
    
    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }
    
    public String getDirectoryPath() {
        return directoryPath;
    }
    
    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerUsername() {
        return ownerUsername;
    }
    
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getReplicaCount() {
        return replicaCount;
    }
    
    public void setReplicaCount(int replicaCount) {
        this.replicaCount = replicaCount;
    }
    
    /**
     * Formatea el tamaño del archivo en una representación legible
     */
    public String getFormattedSize() {
        if (fileSize == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    @Override
    public String toString() {
        return "FileDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", fileSize=" + getFormattedSize() +
                ", mimeType='" + mimeType + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", replicaCount=" + replicaCount +
                ", createdAt=" + createdAt +
                '}';
    }
}