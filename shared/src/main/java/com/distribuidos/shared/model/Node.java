package com.distribuidos.shared.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un nodo de almacenamiento en el sistema distribuido
 */
@Entity
@Table(name = "nodes")
public class Node {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "node_id", unique = true, nullable = false)
    private String nodeId;
    
    @Column(nullable = false)
    private String hostname;
    
    @Column(nullable = false)
    private Integer port;
    
    @Column(name = "storage_path", nullable = false)
    private String storagePath;
    
    @Column(name = "total_capacity")
    private Long totalCapacity;
    
    @Column(name = "used_capacity")
    private Long usedCapacity;
    
    @Enumerated(EnumType.STRING)
    private NodeStatus status;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @OneToMany(mappedBy = "node", cascade = CascadeType.ALL)
    private List<FileReplica> replicas;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum NodeStatus {
        ONLINE, OFFLINE, MAINTENANCE
    }
    
    // Constructors
    public Node() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = NodeStatus.OFFLINE;
        this.usedCapacity = 0L;
    }
    
    public Node(String nodeId, String hostname, Integer port, String storagePath, Long totalCapacity) {
        this();
        this.nodeId = nodeId;
        this.hostname = hostname;
        this.port = port;
        this.storagePath = storagePath;
        this.totalCapacity = totalCapacity;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getStoragePath() {
        return storagePath;
    }
    
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
    
    public Long getTotalCapacity() {
        return totalCapacity;
    }
    
    public void setTotalCapacity(Long totalCapacity) {
        this.totalCapacity = totalCapacity;
    }
    
    public Long getUsedCapacity() {
        return usedCapacity;
    }
    
    public void setUsedCapacity(Long usedCapacity) {
        this.usedCapacity = usedCapacity;
    }
    
    public NodeStatus getStatus() {
        return status;
    }
    
    public void setStatus(NodeStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getLastHeartbeat() {
        return lastHeartbeat;
    }
    
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }
    
    public List<FileReplica> getReplicas() {
        return replicas;
    }
    
    public void setReplicas(List<FileReplica> replicas) {
        this.replicas = replicas;
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
    
    /**
     * Calcula el espacio disponible en el nodo
     */
    public Long getAvailableCapacity() {
        return totalCapacity - usedCapacity;
    }
    
    /**
     * Calcula el porcentaje de uso del nodo
     */
    public double getUsagePercentage() {
        if (totalCapacity == 0) return 0.0;
        return (double) usedCapacity / totalCapacity * 100.0;
    }
    
    /**
     * Verifica si el nodo está en línea
     */
    public boolean isOnline() {
        return status == NodeStatus.ONLINE;
    }
    
    /**
     * Obtiene la dirección completa del nodo (hostname:port)
     */
    public String getAddress() {
        return hostname + ":" + port;
    }
    
    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", nodeId='" + nodeId + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", status=" + status +
                ", usedCapacity=" + usedCapacity +
                ", totalCapacity=" + totalCapacity +
                ", lastHeartbeat=" + lastHeartbeat +
                '}';
    }
}