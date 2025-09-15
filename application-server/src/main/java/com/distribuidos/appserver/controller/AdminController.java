package com.distribuidos.appserver.controller;

import com.distribuidos.appserver.service.DatabaseCommunicationService;
import com.distribuidos.appserver.service.StorageNodeCommunicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para monitoreo y debug del servidor de aplicación.
 * Proporciona endpoints para verificar el estado del sistema.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private DatabaseCommunicationService databaseService;
    
    @Autowired
    private StorageNodeCommunicationService nodeService;
    
    /**
     * Verifica el estado general del sistema.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        System.out.println("🏥 Admin: Verificación de salud del sistema");
        
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verificar BD
            boolean dbConnected = databaseService.testConnection();
            health.put("database", dbConnected ? "CONNECTED" : "DISCONNECTED");
            
            // Verificar nodos
            nodeService.checkNodesHealth();
            Map<String, Object> nodeStats = nodeService.getNodesStatistics();
            health.put("nodes", nodeStats);
            
            // Estado general
            boolean systemHealthy = dbConnected && ((Long) nodeStats.get("healthyNodes")) >= 0;
            health.put("systemStatus", systemHealthy ? "HEALTHY" : "DEGRADED");
            health.put("timestamp", System.currentTimeMillis());
            
            System.out.println("✅ Admin: Estado del sistema generado");
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            System.err.println("❌ Admin: Error verificando salud: " + e.getMessage());
            health.put("error", e.getMessage());
            health.put("systemStatus", "ERROR");
            return ResponseEntity.status(500).body(health);
        }
    }
    
    /**
     * Fuerza el redescubrimiento de nodos.
     */
    @GetMapping("/nodes/discover")
    public ResponseEntity<Map<String, Object>> discoverNodes() {
        System.out.println("🔍 Admin: Forzando redescubrimiento de nodos");
        
        try {
            nodeService.discoverNodes();
            Map<String, Object> result = nodeService.getNodesStatistics();
            result.put("action", "NODE_DISCOVERY_COMPLETED");
            
            System.out.println("✅ Admin: Redescubrimiento completado");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("❌ Admin: Error en redescubrimiento: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("action", "NODE_DISCOVERY_FAILED");
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Obtiene información detallada del sistema.
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        System.out.println("ℹ️ Admin: Obteniendo información del sistema");
        
        Map<String, Object> info = new HashMap<>();
        
        try {
            // Información general
            info.put("application", "Distributed Storage System - Application Server");
            info.put("version", "1.0.0");
            info.put("protocols", new String[]{"SOAP/HTTPS", "RMI", "TCP"});
            
            // Configuración
            Map<String, Object> config = new HashMap<>();
            config.put("soapPort", 8080);
            config.put("databasePort", 9001);
            config.put("rmiPortBase", 1099);
            config.put("replicationFactor", 2);
            info.put("configuration", config);
            
            // Runtime
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> runtimeInfo = new HashMap<>();
            runtimeInfo.put("maxMemory", runtime.maxMemory() / (1024 * 1024) + " MB");
            runtimeInfo.put("totalMemory", runtime.totalMemory() / (1024 * 1024) + " MB");
            runtimeInfo.put("freeMemory", runtime.freeMemory() / (1024 * 1024) + " MB");
            runtimeInfo.put("processors", runtime.availableProcessors());
            info.put("runtime", runtimeInfo);
            
            System.out.println("✅ Admin: Información del sistema generada");
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            System.err.println("❌ Admin: Error obteniendo información: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}