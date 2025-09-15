package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para solicitud de descarga de archivos en SOAP.
 */
@XmlRootElement(name = "FileDownloadRequest")
@XmlType(propOrder = {"token", "fileName"})
public class FileDownloadRequest {
    
    private String token;
    private String fileName;
    
    public FileDownloadRequest() {}
    
    public FileDownloadRequest(String token, String fileName) {
        this.token = token;
        this.fileName = fileName;
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
    
    @Override
    public String toString() {
        return "FileDownloadRequest{fileName='" + fileName + "'}";
    }
}