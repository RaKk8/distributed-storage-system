package com.distribuidos.shared.service;

import com.distribuidos.shared.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Servicio para gestión de usuarios con usuarios hardcodeados
 * Preparado para migrar a JWT cuando sea necesario
 */
public class AuthenticationService {
    
    // Usuarios hardcodeados
    private static final Map<String, User> HARDCODED_USERS = new HashMap<>();
    
    // Tokens activos (simula JWT, listo para migrar)
    private static final Map<String, String> ACTIVE_TOKENS = new HashMap<>();
    private static final Map<String, LocalDateTime> TOKEN_EXPIRY = new HashMap<>();
    
    // Duración del token en minutos
    private static final int TOKEN_DURATION_MINUTES = 60;
    
    static {
        // Inicializar usuarios hardcodeados
        User admin = new User("admin", "admin123", "admin@sistema.com");
        admin.setId(1L);
        
        User user1 = new User("juan", "juan123", "juan@usuario.com");
        user1.setId(2L);
        
        User user2 = new User("maria", "maria123", "maria@usuario.com");
        user2.setId(3L);
        
        HARDCODED_USERS.put("admin", admin);
        HARDCODED_USERS.put("juan", user1);
        HARDCODED_USERS.put("maria", user2);
    }
    
    /**
     * Autentica un usuario y genera un token
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Token de autenticación o null si falla
     */
    public static String authenticate(String username, String password) {
        User user = HARDCODED_USERS.get(username);
        
        if (user != null && user.getPassword().equals(password)) {
            // Generar token único
            String token = generateToken(username);
            
            // Almacenar token con expiración
            ACTIVE_TOKENS.put(token, username);
            TOKEN_EXPIRY.put(token, LocalDateTime.now().plusMinutes(TOKEN_DURATION_MINUTES));
            
            return token;
        }
        
        return null;
    }
    
    /**
     * Valida un token y retorna el usuario
     * @param token Token a validar
     * @return Usuario si el token es válido, null en caso contrario
     */
    public static User validateToken(String token) {
        if (token == null || !ACTIVE_TOKENS.containsKey(token)) {
            return null;
        }
        
        // Verificar expiración
        LocalDateTime expiry = TOKEN_EXPIRY.get(token);
        if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
            // Token expirado, eliminarlo
            ACTIVE_TOKENS.remove(token);
            TOKEN_EXPIRY.remove(token);
            return null;
        }
        
        // Token válido, obtener usuario
        String username = ACTIVE_TOKENS.get(token);
        return HARDCODED_USERS.get(username);
    }
    
    /**
     * Invalida un token (logout)
     * @param token Token a invalidar
     * @return true si se invalidó correctamente
     */
    public static boolean invalidateToken(String token) {
        boolean removed = ACTIVE_TOKENS.remove(token) != null;
        TOKEN_EXPIRY.remove(token);
        return removed;
    }
    
    /**
     * Obtiene un usuario por nombre de usuario
     * @param username Nombre de usuario
     * @return Usuario o null si no existe
     */
    public static User getUserByUsername(String username) {
        return HARDCODED_USERS.get(username);
    }
    
    /**
     * Obtiene un usuario por ID
     * @param userId ID del usuario
     * @return Usuario o null si no existe
     */
    public static User getUserById(Long userId) {
        return HARDCODED_USERS.values().stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Obtiene todos los usuarios disponibles
     * @return Map de usuarios
     */
    public static Map<String, User> getAllUsers() {
        return new HashMap<>(HARDCODED_USERS);
    }
    
    /**
     * Genera un token único para un usuario
     * Preparado para migrar a JWT
     */
    private static String generateToken(String username) {
        // Simple token generation - ready to migrate to JWT
        String uniqueId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        return username + "_" + timestamp + "_" + uniqueId.substring(0, 8);
    }
    
    /**
     * Renueva la expiración de un token válido
     * @param token Token a renovar
     * @return true si se renovó correctamente
     */
    public static boolean renewToken(String token) {
        if (ACTIVE_TOKENS.containsKey(token)) {
            TOKEN_EXPIRY.put(token, LocalDateTime.now().plusMinutes(TOKEN_DURATION_MINUTES));
            return true;
        }
        return false;
    }
    
    /**
     * Limpia tokens expirados
     */
    public static void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        TOKEN_EXPIRY.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
        ACTIVE_TOKENS.entrySet().removeIf(entry -> !TOKEN_EXPIRY.containsKey(entry.getKey()));
    }
    
    // TODO: Migrar a JWT cuando sea necesario
    // - Usar biblioteca como jjwt para generar/validar tokens JWT
    // - Implementar refresh tokens
    // - Agregar claims personalizados (roles, permisos)
    // - Configurar clave secreta desde configuración externa
}