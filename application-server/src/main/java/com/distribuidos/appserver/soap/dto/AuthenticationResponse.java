package com.distribuidos.appserver.soap.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * DTO para respuesta de autenticación en SOAP.
 */
@XmlRootElement(name = "AuthenticationResponse")
@XmlType(propOrder = {"success", "token", "userId", "message"})
public class AuthenticationResponse {
    
    private boolean success;
    private String token;
    private String userId;
    private String message;
    
    public AuthenticationResponse() {}
    
    public AuthenticationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static AuthenticationResponse success(String token, String userId) {
        AuthenticationResponse response = new AuthenticationResponse(true, "Authentication successful");
        response.setToken(token);
        response.setUserId(userId);
        return response;
    }
    
    public static AuthenticationResponse failure(String message) {
        return new AuthenticationResponse(false, message);
    }
    
    @XmlElement(required = true)
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @XmlElement
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    @XmlElement
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @XmlElement(required = true)
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "AuthenticationResponse{success=" + success + 
               ", userId='" + userId + "', message='" + message + "'}";
    }
}