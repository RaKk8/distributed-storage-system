package com.distribuidos.shared.tcp;

import java.io.Serializable;

/**
 * Respuesta de la base de datos para comunicación TCP
 */
public class DatabaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private Object data;
    private String errorMessage;
    private int affectedRows;
    private String operationType;
    
    // Constructors
    public DatabaseResponse() {}
    
    public DatabaseResponse(boolean success) {
        this.success = success;
    }
    
    public DatabaseResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }
    
    public DatabaseResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
    
    // Static factory methods
    public static DatabaseResponse success(Object data) {
        return new DatabaseResponse(true, data);
    }
    
    public static DatabaseResponse success() {
        return new DatabaseResponse(true);
    }
    
    public static DatabaseResponse error(String errorMessage) {
        return new DatabaseResponse(false, errorMessage);
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public int getAffectedRows() {
        return affectedRows;
    }
    
    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    @Override
    public String toString() {
        return "DatabaseResponse{" +
                "success=" + success +
                ", data=" + data +
                ", errorMessage='" + errorMessage + '\'' +
                ", affectedRows=" + affectedRows +
                ", operationType='" + operationType + '\'' +
                '}';
    }
}