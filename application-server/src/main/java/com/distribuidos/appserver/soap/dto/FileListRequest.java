package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para solicitud de listado de archivos en SOAP.
 */
@XmlRootElement(name = "FileListRequest")
@XmlType(propOrder = {"token"})
public class FileListRequest {
    
    private String token;
    
    public FileListRequest() {}
    
    public FileListRequest(String token) {
        this.token = token;
    }
    
    @XmlElement(required = true)
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @Override
    public String toString() {
        return "FileListRequest{token='" + (token != null ? "***" : "null") + "'}";
    }
}