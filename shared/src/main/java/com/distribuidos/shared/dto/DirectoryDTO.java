package com.distribuidos.shared.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para representar información de directorios
 */
public class DirectoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String fullPath;
    private Long parentId;
    private String parentPath;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DirectoryDTO> subdirectories;
    private List<FileDTO> files;
    private int totalFiles;
    private int totalSubdirectories;
    private Long totalSize;
    
    // Constructors
    public DirectoryDTO() {}
    
    public DirectoryDTO(Long id, String name, String fullPath, String ownerUsername) {
        this.id = id;
        this.name = name;
        this.fullPath = fullPath;
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
    
    public String getFullPath() {
        return fullPath;
    }
    
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }
    
    public Long getParentId() {
        return parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    
    public String getParentPath() {
        return parentPath;
    }
    
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
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
    
    public List<DirectoryDTO> getSubdirectories() {
        return subdirectories;
    }
    
    public void setSubdirectories(List<DirectoryDTO> subdirectories) {
        this.subdirectories = subdirectories;
        this.totalSubdirectories = subdirectories != null ? subdirectories.size() : 0;
    }
    
    public List<FileDTO> getFiles() {
        return files;
    }
    
    public void setFiles(List<FileDTO> files) {
        this.files = files;
        this.totalFiles = files != null ? files.size() : 0;
        
        // Calcular tamaño total
        if (files != null) {
            this.totalSize = files.stream()
                    .mapToLong(f -> f.getFileSize() != null ? f.getFileSize() : 0L)
                    .sum();
        } else {
            this.totalSize = 0L;
        }
    }
    
    public int getTotalFiles() {
        return totalFiles;
    }
    
    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }
    
    public int getTotalSubdirectories() {
        return totalSubdirectories;
    }
    
    public void setTotalSubdirectories(int totalSubdirectories) {
        this.totalSubdirectories = totalSubdirectories;
    }
    
    public Long getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }
    
    /**
     * Formatea el tamaño total en una representación legible
     */
    public String getFormattedSize() {
        if (totalSize == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = totalSize.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    /**
     * Verifica si es el directorio raíz
     */
    public boolean isRoot() {
        return parentId == null;
    }
    
    @Override
    public String toString() {
        return "DirectoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", ownerUsername='" + ownerUsername + '\'' +
                ", totalFiles=" + totalFiles +
                ", totalSubdirectories=" + totalSubdirectories +
                ", totalSize=" + getFormattedSize() +
                ", createdAt=" + createdAt +
                '}';
    }
}