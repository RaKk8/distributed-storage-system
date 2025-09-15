package com.distribuidos.shared.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa un directorio en el sistema de archivos
 */
@Entity
@Table(name = "directories")
public class Directory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "full_path", nullable = false)
    private String fullPath;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Directory parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Directory> subdirectories;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL)
    private List<File> files;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Directory() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Directory(String name, String fullPath, User owner) {
        this();
        this.name = name;
        this.fullPath = fullPath;
        this.owner = owner;
    }
    
    public Directory(String name, Directory parent, User owner) {
        this();
        this.name = name;
        this.parent = parent;
        this.owner = owner;
        this.fullPath = parent != null ? parent.getFullPath() + "/" + name : "/" + name;
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
    
    public Directory getParent() {
        return parent;
    }
    
    public void setParent(Directory parent) {
        this.parent = parent;
        if (parent != null) {
            this.fullPath = parent.getFullPath() + "/" + this.name;
        } else {
            this.fullPath = "/" + this.name;
        }
    }
    
    public List<Directory> getSubdirectories() {
        return subdirectories;
    }
    
    public void setSubdirectories(List<Directory> subdirectories) {
        this.subdirectories = subdirectories;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<File> getFiles() {
        return files;
    }
    
    public void setFiles(List<File> files) {
        this.files = files;
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
    
    @Override
    public String toString() {
        return "Directory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fullPath='" + fullPath + '\'' +
                ", owner=" + (owner != null ? owner.getUsername() : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}