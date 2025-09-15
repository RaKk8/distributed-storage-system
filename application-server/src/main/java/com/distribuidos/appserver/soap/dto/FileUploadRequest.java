package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para solicitud de subida de archivos en SOAP.
 */
@XmlRootElement(name = "FileUploadRequest")
@XmlType(propOrder = {"token", "fileName", "content"})
public class FileUploadRequest {
    
    private String token;
    private String fileName;
    private byte[] content;
    
    public FileUploadRequest() {}
    
    public FileUploadRequest(String token, String fileName, byte[] content) {
        this.token = token;
        this.fileName = fileName;
        this.content = content;
    }
    
    @XmlElement(required = true)
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @XmlElement(required = true)
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    @XmlElement(required = true)
    public byte[] getContent() {
        return content;
    }
    
    public void setContent(byte[] content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "FileUploadRequest{fileName='" + fileName + "', size=" + 
               (content != null ? content.length : 0) + " bytes}";
    }
}