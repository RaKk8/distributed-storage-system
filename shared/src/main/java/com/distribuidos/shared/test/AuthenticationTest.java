package com.distribuidos.shared.test;

import com.distribuidos.shared.model.User;
import com.distribuidos.shared.service.AuthenticationService;

/**
 * Clase de prueba para el servicio de autenticación
 */
public class AuthenticationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Pruebas del Servicio de Autenticación ===\n");
        
        // Prueba 1: Autenticación exitosa
        System.out.println("--- Prueba 1: Autenticación exitosa ---");
        String token1 = AuthenticationService.authenticate("admin", "admin123");
        if (token1 != null) {
            System.out.println("✅ Autenticación exitosa para admin");
            System.out.println("   Token generado: " + token1);
        } else {
            System.out.println("❌ Fallo en autenticación para admin");
        }
        
        // Prueba 2: Validación de token
        System.out.println("\n--- Prueba 2: Validación de token ---");
        if (token1 != null) {
            User user = AuthenticationService.validateToken(token1);
            if (user != null) {
                System.out.println("✅ Token válido para usuario: " + user.getUsername());
                System.out.println("   Email: " + user.getEmail());
                System.out.println("   ID: " + user.getId());
            } else {
                System.out.println("❌ Token inválido");
            }
        }
        
        // Prueba 3: Autenticación fallida
        System.out.println("\n--- Prueba 3: Autenticación fallida ---");
        String token2 = AuthenticationService.authenticate("admin", "password_incorrecto");
        if (token2 == null) {
            System.out.println("✅ Autenticación fallida correctamente para credenciales incorrectas");
        } else {
            System.out.println("❌ Se autenticó con credenciales incorrectas");
        }
        
        // Prueba 4: Múltiples usuarios
        System.out.println("\n--- Prueba 4: Autenticación de múltiples usuarios ---");
        String tokenJuan = AuthenticationService.authenticate("juan", "juan123");
        String tokenMaria = AuthenticationService.authenticate("maria", "maria123");
        
        if (tokenJuan != null && tokenMaria != null) {
            System.out.println("✅ Autenticación exitosa para múltiples usuarios");
            System.out.println("   Token Juan: " + tokenJuan.substring(0, 20) + "...");
            System.out.println("   Token María: " + tokenMaria.substring(0, 20) + "...");
        } else {
            System.out.println("❌ Fallo en autenticación de múltiples usuarios");
        }
        
        // Prueba 5: Buscar usuarios
        System.out.println("\n--- Prueba 5: Búsqueda de usuarios ---");
        User adminUser = AuthenticationService.getUserByUsername("admin");
        User juanUser = AuthenticationService.getUserById(2L);
        
        if (adminUser != null && juanUser != null) {
            System.out.println("✅ Búsqueda de usuarios exitosa");
            System.out.println("   Admin: " + adminUser);
            System.out.println("   Juan: " + juanUser);
        } else {
            System.out.println("❌ Fallo en búsqueda de usuarios");
        }
        
        // Prueba 6: Invalidar token
        System.out.println("\n--- Prueba 6: Invalidar token ---");
        if (token1 != null) {
            boolean invalidated = AuthenticationService.invalidateToken(token1);
            if (invalidated) {
                System.out.println("✅ Token invalidado correctamente");
                
                // Verificar que el token ya no es válido
                User userAfterInvalidation = AuthenticationService.validateToken(token1);
                if (userAfterInvalidation == null) {
                    System.out.println("✅ Token ya no es válido después de invalidación");
                } else {
                    System.out.println("❌ Token sigue siendo válido después de invalidación");
                }
            } else {
                System.out.println("❌ Fallo al invalidar token");
            }
        }
        
        // Prueba 7: Listar todos los usuarios
        System.out.println("\n--- Prueba 7: Listar todos los usuarios ---");
        var allUsers = AuthenticationService.getAllUsers();
        System.out.println("✅ Usuarios disponibles:");
        allUsers.forEach((username, user) -> {
            System.out.println("   - " + username + ": " + user.getEmail());
        });
        
        System.out.println("\n=== Pruebas de autenticación completadas ===");
    }
}