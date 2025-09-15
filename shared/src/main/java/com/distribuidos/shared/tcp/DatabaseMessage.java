package com.distribuidos.shared.tcp;

import java.io.Serializable;

/**
 * Mensaje para comunicación TCP entre servidor de aplicación y base de datos
 */
public class DatabaseMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String operation;
    private String entity;
    private Object data;
    private Long id;
    private String query;
    private Object[] parameters;
    private boolean success;
    private String errorMessage;
    
    // Operaciones disponibles
    public static final String SAVE = "SAVE";
    public static final String FIND_BY_ID = "FIND_BY_ID";
    public static final String FIND_ALL = "FIND_ALL";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String FIND_BY_CRITERIA = "FIND_BY_CRITERIA";
    public static final String EXECUTE_QUERY = "EXECUTE_QUERY";
    
    // Entidades
    public static final String USER = "USER";
    public static final String DIRECTORY = "DIRECTORY";
    public static final String FILE = "FILE";
    public static final String NODE = "NODE";
    public static final String FILE_REPLICA = "FILE_REPLICA";
    public static final String PERMISSION = "PERMISSION";
    
    // Constructors
    public DatabaseMessage() {}
    
    public DatabaseMessage(String operation, String entity) {
        this.operation = operation;
        this.entity = entity;
    }
    
    public DatabaseMessage(String operation, String entity, Object data) {
        this.operation = operation;
        this.entity = entity;
        this.data = data;
    }
    
    public DatabaseMessage(String operation, String entity, Long id) {
        this.operation = operation;
        this.entity = entity;
        this.id = id;
    }
    
    // Getters and Setters
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getEntity() {
        return entity;
    }
    
    public void setEntity(String entity) {
        this.entity = entity;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Object[] getParameters() {
        return parameters;
    }
    
    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    @Override
    public String toString() {
        return "DatabaseMessage{" +
                "operation='" + operation + '\'' +
                ", entity='" + entity + '\'' +
                ", id=" + id +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}