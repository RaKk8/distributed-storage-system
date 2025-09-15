package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para respuesta de descarga de archivos en SOAP.
 */
@XmlRootElement(name = "FileDownloadResponse")
@XmlType(propOrder = {"success", "message", "content"})
public class FileDownloadResponse {
    
    private boolean success;
    private String message;
    private byte[] content;
    
    public FileDownloadResponse() {}
    
    public FileDownloadResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static FileDownloadResponse success(String message, byte[] content) {
        FileDownloadResponse response = new FileDownloadResponse(true, message);
        response.setContent(content);
        return response;
    }
    
    public static FileDownloadResponse failure(String message) {
        return new FileDownloadResponse(false, message);
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
    public byte[] getContent() {
        return content;
    }
    
    public void setContent(byte[] content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "FileDownloadResponse{success=" + success + 
               ", message='" + message + "', size=" + 
               (content != null ? content.length : 0) + " bytes}";
    }
}