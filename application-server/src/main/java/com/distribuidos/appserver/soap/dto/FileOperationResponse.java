package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para respuesta de operaciones de archivo en SOAP.
 */
@XmlRootElement(name = "FileOperationResponse")
@XmlType(propOrder = {"success", "message", "fileId"})
public class FileOperationResponse {
    
    private boolean success;
    private String message;
    private String fileId;
    
    public FileOperationResponse() {}
    
    public FileOperationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static FileOperationResponse success(String message) {
        return new FileOperationResponse(true, message);
    }
    
    public static FileOperationResponse success(String message, String fileId) {
        FileOperationResponse response = new FileOperationResponse(true, message);
        response.setFileId(fileId);
        return response;
    }
    
    public static FileOperationResponse failure(String message) {
        return new FileOperationResponse(false, message);
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
    public String getFileId() {
        return fileId;
    }
    
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
    
    @Override
    public String toString() {
        return "FileOperationResponse{success=" + success + 
               ", message='" + message + "', fileId='" + fileId + "'}";
    }
}