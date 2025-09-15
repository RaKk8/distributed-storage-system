package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para autenticación de usuarios en SOAP.
 */
@XmlRootElement(name = "AuthenticationRequest")
@XmlType(propOrder = {"username", "password"})
public class AuthenticationRequest {
    
    private String username;
    private String password;
    
    public AuthenticationRequest() {}
    
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    @XmlElement(required = true)
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    @XmlElement(required = true)
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "AuthenticationRequest{username='" + username + "'}";
    }
}