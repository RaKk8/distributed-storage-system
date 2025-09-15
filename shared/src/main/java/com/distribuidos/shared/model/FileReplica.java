package com.distribuidos.shared.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una réplica de archivo en un nodo específico
 */
@Entity
@Table(name = "file_replicas")
public class FileReplica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;
    
    @Column(name = "local_path", nullable = false)
    private String localPath;
    
    @Column(name = "replica_checksum")
    private String replicaChecksum;
    
    @Enumerated(EnumType.STRING)
    private ReplicaStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_verified")
    private LocalDateTime lastVerified;
    
    public enum ReplicaStatus {
        ACTIVE, CORRUPTED, PENDING, DELETED
    }
    
    // Constructors
    public FileReplica() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ReplicaStatus.PENDING;
    }
    
    public FileReplica(File file, Node node, String localPath) {
        this();
        this.file = file;
        this.node = node;
        this.localPath = localPath;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public File getFile() {
        return file;
    }
    
    public void setFile(File file) {
        this.file = file;
    }
    
    public Node getNode() {
        return node;
    }
    
    public void setNode(Node node) {
        this.node = node;
    }
    
    public String getLocalPath() {
        return localPath;
    }
    
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
    
    public String getReplicaChecksum() {
        return replicaChecksum;
    }
    
    public void setReplicaChecksum(String replicaChecksum) {
        this.replicaChecksum = replicaChecksum;
    }
    
    public ReplicaStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReplicaStatus status) {
        this.status = status;
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
    
    public LocalDateTime getLastVerified() {
        return lastVerified;
    }
    
    public void setLastVerified(LocalDateTime lastVerified) {
        this.lastVerified = lastVerified;
    }
    
    /**
     * Verifica si la réplica está activa y disponible
     */
    public boolean isActive() {
        return status == ReplicaStatus.ACTIVE;
    }
    
    /**
     * Verifica si el checksum de la réplica coincide con el archivo original
     */
    public boolean isChecksumValid() {
        return file != null && 
               file.getChecksum() != null && 
               file.getChecksum().equals(replicaChecksum);
    }
    
    @Override
    public String toString() {
        return "FileReplica{" +
                "id=" + id +
                ", file=" + (file != null ? file.getName() : "null") +
                ", node=" + (node != null ? node.getNodeId() : "null") +
                ", localPath='" + localPath + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}