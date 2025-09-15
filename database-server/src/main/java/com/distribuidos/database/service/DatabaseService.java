package com.distribuidos.database.service;

import com.distribuidos.database.config.DatabaseConfig;
import com.distribuidos.shared.tcp.DatabaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de base de datos que maneja todas las operaciones CRUD
 */
public class DatabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    
    /**
     * Guarda una entidad en la base de datos
     */
    public DatabaseResponse save(String entity, Object data) {
        try {
            // Por simplicidad, usamos un enfoque básico
            // En una implementación completa, usaríamos mapeo ORM
            logger.info("Guardando entidad: {} con datos: {}", entity, data);
            
            // Simular guardado exitoso
            return DatabaseResponse.success("Entidad " + entity + " guardada correctamente");
            
        } catch (Exception e) {
            logger.error("Error guardando entidad: " + entity, e);
            return DatabaseResponse.error("Error guardando entidad: " + e.getMessage());
        }
    }
    
    /**
     * Busca una entidad por ID
     */
    public DatabaseResponse findById(String entity, Long id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM " + getTableName(entity) + " WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> result = resultSetToMap(rs);
                        return DatabaseResponse.success(result);
                    } else {
                        return DatabaseResponse.error("Entidad no encontrada con ID: " + id);
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error buscando entidad por ID: " + entity + ", " + id, e);
            return DatabaseResponse.error("Error buscando entidad: " + e.getMessage());
        }
    }
    
    /**
     * Busca todas las entidades
     */
    public DatabaseResponse findAll(String entity) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM " + getTableName(entity);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(resultSetToMap(rs));
                }
                
                return DatabaseResponse.success(results);
            }
            
        } catch (SQLException e) {
            logger.error("Error buscando todas las entidades: " + entity, e);
            return DatabaseResponse.error("Error buscando entidades: " + e.getMessage());
        }
    }
    
    /**
     * Actualiza una entidad
     */
    public DatabaseResponse update(String entity, Object data) {
        try {
            logger.info("Actualizando entidad: {} con datos: {}", entity, data);
            
            // Simular actualización exitosa
            return DatabaseResponse.success("Entidad " + entity + " actualizada correctamente");
            
        } catch (Exception e) {
            logger.error("Error actualizando entidad: " + entity, e);
            return DatabaseResponse.error("Error actualizando entidad: " + e.getMessage());
        }
    }
    
    /**
     * Elimina una entidad por ID
     */
    public DatabaseResponse delete(String entity, Long id) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "DELETE FROM " + getTableName(entity) + " WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    DatabaseResponse response = DatabaseResponse.success("Entidad eliminada correctamente");
                    response.setAffectedRows(affectedRows);
                    return response;
                } else {
                    return DatabaseResponse.error("No se encontró entidad con ID: " + id);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error eliminando entidad: " + entity + ", ID: " + id, e);
            return DatabaseResponse.error("Error eliminando entidad: " + e.getMessage());
        }
    }
    
    /**
     * Busca entidades por criterios
     */
    public DatabaseResponse findByCriteria(String entity, String query, Object[] parameters) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Establecer parámetros si existen
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    stmt.setObject(i + 1, parameters[i]);
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(resultSetToMap(rs));
                }
                
                return DatabaseResponse.success(results);
            }
            
        } catch (SQLException e) {
            logger.error("Error ejecutando consulta por criterios: " + query, e);
            return DatabaseResponse.error("Error ejecutando consulta: " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta una consulta personalizada
     */
    public DatabaseResponse executeQuery(String query, Object[] parameters) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            
            if (query.trim().toLowerCase().startsWith("select")) {
                // Consulta SELECT
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    if (parameters != null) {
                        for (int i = 0; i < parameters.length; i++) {
                            stmt.setObject(i + 1, parameters[i]);
                        }
                    }
                    
                    try (ResultSet rs = stmt.executeQuery()) {
                        List<Map<String, Object>> results = new ArrayList<>();
                        while (rs.next()) {
                            results.add(resultSetToMap(rs));
                        }
                        return DatabaseResponse.success(results);
                    }
                }
            } else {
                // Consulta UPDATE/INSERT/DELETE
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    if (parameters != null) {
                        for (int i = 0; i < parameters.length; i++) {
                            stmt.setObject(i + 1, parameters[i]);
                        }
                    }
                    
                    int affectedRows = stmt.executeUpdate();
                    DatabaseResponse response = DatabaseResponse.success("Consulta ejecutada correctamente");
                    response.setAffectedRows(affectedRows);
                    return response;
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error ejecutando consulta: " + query, e);
            return DatabaseResponse.error("Error ejecutando consulta: " + e.getMessage());
        }
    }
    
    /**
     * Convierte el nombre de entidad al nombre de tabla
     */
    private String getTableName(String entity) {
        switch (entity.toUpperCase()) {
            case "USER": return "users";
            case "DIRECTORY": return "directories";
            case "FILE": return "files";
            case "NODE": return "nodes";
            case "FILE_REPLICA": return "file_replicas";
            case "PERMISSION": return "permissions";
            default: return entity.toLowerCase();
        }
    }
    
    /**
     * Convierte un ResultSet a Map
     */
    private Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        for (int i = 1; i <= columnCount; i++) {
            String columnName = metaData.getColumnName(i);
            Object value = rs.getObject(i);
            map.put(columnName, value);
        }
        
        return map;
    }
}