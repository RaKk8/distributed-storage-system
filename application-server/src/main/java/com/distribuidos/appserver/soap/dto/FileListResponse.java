package com.distribuidos.appserver.soap.dto;

import com.distribuidos.shared.model.File;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para respuesta de listado de archivos en SOAP.
 */
@XmlRootElement(name = "FileListResponse")
@XmlType(propOrder = {"success", "message", "files"})
public class FileListResponse {
    
    private boolean success;
    private String message;
    private List<FileInfo> files;
    
    public FileListResponse() {
        this.files = new ArrayList<>();
    }
    
    public FileListResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.files = new ArrayList<>();
    }
    
    public static FileListResponse success(String message, List<File> files) {
        FileListResponse response = new FileListResponse(true, message);
        
        // Convertir entities a DTOs
        for (File file : files) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setId(file.getId().toString());
            fileInfo.setName(file.getName());
            fileInfo.setSize(file.getFileSize() != null ? file.getFileSize() : 0L); // Usar getFileSize()
            fileInfo.setCreatedAt(file.getCreatedAt().toString());
            fileInfo.setUpdatedAt(file.getUpdatedAt().toString());
            response.files.add(fileInfo);
        }
        
        return response;
    }
    
    public static FileListResponse failure(String message) {
        return new FileListResponse(false, message);
    }
    
    @XmlElement(required = true)
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @XmlElement(required = true)
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @XmlElement
    public List<FileInfo> getFiles() {
        return files;
    }
    
    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }
    
    @Override
    public String toString() {
        return "FileListResponse{success=" + success + 
               ", message='" + message + "', filesCount=" + files.size() + "}";
    }
    
    /**
     * Clase interna para información de archivo en SOAP.
     */
    @XmlType(propOrder = {"id", "name", "size", "createdAt", "updatedAt"})
    public static class FileInfo {
        private String id;
        private String name;
        private long size;
        private String createdAt;
        private String updatedAt;
        
        public FileInfo() {}
        
        @XmlElement
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        @XmlElement
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        @XmlElement
        public long getSize() {
            return size;
        }
        
        public void setSize(long size) {
            this.size = size;
        }
        
        @XmlElement
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
        
        @XmlElement
        public String getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}