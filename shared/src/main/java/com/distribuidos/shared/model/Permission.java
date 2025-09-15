package com.distribuidos.shared.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa los permisos de acceso a archivos y directorios
 */
@Entity
@Table(name = "permissions")
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private File file;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directory_id")
    private Directory directory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_type", nullable = false)
    private PermissionType permissionType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by", nullable = false)
    private User grantedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    public enum PermissionType {
        READ, WRITE, DELETE, SHARE
    }
    
    // Constructors
    public Permission() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Permission(User user, File file, PermissionType permissionType, User grantedBy) {
        this();
        this.user = user;
        this.file = file;
        this.permissionType = permissionType;
        this.grantedBy = grantedBy;
    }
    
    public Permission(User user, Directory directory, PermissionType permissionType, User grantedBy) {
        this();
        this.user = user;
        this.directory = directory;
        this.permissionType = permissionType;
        this.grantedBy = grantedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public Directory getDirectory() {
        return directory;
    }
    
    public void setDirectory(Directory directory) {
        this.directory = directory;
    }
    
    public PermissionType getPermissionType() {
        return permissionType;
    }
    
    public void setPermissionType(PermissionType permissionType) {
        this.permissionType = permissionType;
    }
    
    public User getGrantedBy() {
        return grantedBy;
    }
    
    public void setGrantedBy(User grantedBy) {
        this.grantedBy = grantedBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    /**
     * Verifica si el permiso está vigente (no ha expirado)
     */
    public boolean isActive() {
        return expiresAt == null || LocalDateTime.now().isBefore(expiresAt);
    }
    
    /**
     * Verifica si es un permiso para archivo
     */
    public boolean isFilePermission() {
        return file != null;
    }
    
    /**
     * Verifica si es un permiso para directorio
     */
    public boolean isDirectoryPermission() {
        return directory != null;
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", file=" + (file != null ? file.getName() : "null") +
                ", directory=" + (directory != null ? directory.getName() : "null") +
                ", permissionType=" + permissionType +
                ", grantedBy=" + (grantedBy != null ? grantedBy.getUsername() : "null") +
                ", createdAt=" + createdAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}