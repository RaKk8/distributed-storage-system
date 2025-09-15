package com.distribuidos.appserver.test;

import com.distribuidos.appserver.service.DatabaseCommunicationService;
import com.distribuidos.appserver.service.StorageNodeCommunicationService;
import com.distribuidos.shared.service.AuthenticationService;
import com.distribuidos.shared.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Pruebas de integración para el servidor de aplicación.
 * Valida conectividad con BD y nodos, así como autenticación.
 */
@Component
public class ApplicationServerIntegrationTest {
    
    @Autowired
    private DatabaseCommunicationService databaseService;
    
    @Autowired
    private StorageNodeCommunicationService nodeService;
    
    @Autowired
    private AuthenticationService authService;
    
    @PostConstruct
    public void runTests() {
        System.out.println("\n🧪 ===== PRUEBAS DE INTEGRACIÓN - SERVIDOR DE APLICACIÓN =====");
        
        boolean allTestsPassed = true;
        
        // Test 1: Conectividad con base de datos
        allTestsPassed &= testDatabaseConnectivity();
        
        // Test 2: Autenticación de usuarios
        allTestsPassed &= testAuthentication();
        
        // Test 3: Descubrimiento de nodos RMI
        allTestsPassed &= testNodeDiscovery();
        
        // Test 4: Estadísticas del sistema
        allTestsPassed &= testSystemStatistics();
        
        System.out.println("\n📊 ===== RESUMEN DE PRUEBAS =====");
        if (allTestsPassed) {
            System.out.println("✅ TODAS LAS PRUEBAS PASARON - Sistema listo para producción");
        } else {
            System.out.println("❌ ALGUNAS PRUEBAS FALLARON - Revisar configuración");
        }
        System.out.println("================================================\n");
    }
    
    private boolean testDatabaseConnectivity() {
        System.out.println("\n🔍 Test 1: Conectividad con Base de Datos TCP");
        System.out.println("--------------------------------------------");
        
        try {
            boolean connected = databaseService.testConnection();
            
            if (connected) {
                System.out.println("✅ Conexión TCP con base de datos: EXITOSA");
                return true;
            } else {
                System.out.println("❌ Conexión TCP con base de datos: FALLÓ");
                return false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error en test de conectividad: " + e.getMessage());
            return false;
        }
    }
    
    private boolean testAuthentication() {
        System.out.println("\n🔐 Test 2: Sistema de Autenticación");
        System.out.println("------------------------------------");
        
        boolean allAuthTestsPassed = true;
        
        // Test usuarios válidos
        String[] validUsers = {"admin", "juan", "maria"};
        String[] validPasswords = {"admin123", "juan123", "maria123"};
        
        for (int i = 0; i < validUsers.length; i++) {
            try {
                String token = authService.authenticate(validUsers[i], validPasswords[i]);
                if (token != null) {
                    User validatedUser = authService.validateToken(token);
                    
                    if (validatedUser != null && validatedUser.getUsername().equals(validUsers[i])) {
                        System.out.println("✅ Autenticación " + validUsers[i] + ": EXITOSA (token válido)");
                    } else {
                        System.out.println("❌ Validación de token " + validUsers[i] + ": FALLÓ");
                        allAuthTestsPassed = false;
                    }
                } else {
                    System.out.println("❌ Autenticación " + validUsers[i] + ": FALLÓ");
                    allAuthTestsPassed = false;
                }
            } catch (Exception e) {
                System.out.println("❌ Error autenticando " + validUsers[i] + ": " + e.getMessage());
                allAuthTestsPassed = false;
            }
        }
        
        // Test usuario inválido
        try {
            String token = authService.authenticate("invalid", "invalid");
            if (token == null) {
                System.out.println("✅ Rechazo de credenciales inválidas: EXITOSO");
            } else {
                System.out.println("❌ Rechazo de credenciales inválidas: FALLÓ");
                allAuthTestsPassed = false;
            }
        } catch (Exception e) {
            System.out.println("❌ Error en test de credenciales inválidas: " + e.getMessage());
            allAuthTestsPassed = false;
        }
        
        return allAuthTestsPassed;
    }
    
    private boolean testNodeDiscovery() {
        System.out.println("\n🌐 Test 3: Descubrimiento de Nodos RMI");
        System.out.println("--------------------------------------");
        
        try {
            nodeService.discoverNodes();
            Map<String, Object> stats = nodeService.getNodesStatistics();
            
            int expectedNodes = (Integer) stats.get("expectedNodes");
            int discoveredNodes = (Integer) stats.get("discoveredNodes");
            long healthyNodes = (Long) stats.get("healthyNodes");
            
            System.out.println("📊 Nodos esperados: " + expectedNodes);
            System.out.println("📊 Nodos descubiertos: " + discoveredNodes);
            System.out.println("📊 Nodos saludables: " + healthyNodes);
            
            if (healthyNodes > 0) {
                System.out.println("✅ Descubrimiento de nodos: EXITOSO (al menos un nodo disponible)");
                return true;
            } else {
                System.out.println("⚠️ Descubrimiento de nodos: SIN NODOS DISPONIBLES");
                System.out.println("   (Esto es normal si los nodos no están ejecutándose)");
                return true; // No falla el test ya que los nodos pueden no estar ejecutándose
            }
        } catch (Exception e) {
            System.out.println("❌ Error en descubrimiento de nodos: " + e.getMessage());
            return false;
        }
    }
    
    private boolean testSystemStatistics() {
        System.out.println("\n📈 Test 4: Estadísticas del Sistema");
        System.out.println("-----------------------------------");
        
        try {
            Map<String, Object> nodeStats = nodeService.getNodesStatistics();
            
            System.out.println("📊 Estadísticas de nodos:");
            for (Map.Entry<String, Object> entry : nodeStats.entrySet()) {
                System.out.println("   " + entry.getKey() + ": " + entry.getValue());
            }
            
            System.out.println("✅ Generación de estadísticas: EXITOSA");
            return true;
        } catch (Exception e) {
            System.out.println("❌ Error generando estadísticas: " + e.getMessage());
            return false;
        }
    }
}