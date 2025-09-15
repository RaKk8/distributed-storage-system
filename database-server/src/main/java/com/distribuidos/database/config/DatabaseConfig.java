package com.distribuidos.database.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuración de la base de datos
 * Preparado para migrar fácilmente de H2 a MySQL
 */
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    
    // Configuración para H2 (en memoria)
    private static final String H2_URL = "jdbc:h2:mem:distributedfs;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String H2_DRIVER = "org.h2.Driver";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";
    
    // TODO: Configuración para MySQL (descomentar cuando se migre)
    /*
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/distributedfs";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MYSQL_USERNAME = "root";
    private static final String MYSQL_PASSWORD = "password";
    */
    
    private static HikariDataSource dataSource;
    
    /**
     * Inicializa la conexión a la base de datos
     */
    public static void initialize() throws SQLException {
        HikariConfig config = new HikariConfig();
        
        // Configuración H2 (cambiar a MySQL cuando sea necesario)
        config.setJdbcUrl(H2_URL);
        config.setDriverClassName(H2_DRIVER);
        config.setUsername(H2_USERNAME);
        config.setPassword(H2_PASSWORD);
        
        // Configuración del pool de conexiones
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        dataSource = new HikariDataSource(config);
        
        // Crear las tablas
        createTables();
        
        logger.info("Base de datos H2 inicializada correctamente");
    }
    
    /**
     * Obtiene una conexión a la base de datos
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource no está inicializado. Llame a initialize() primero.");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Cierra el pool de conexiones
     */
    public static void shutdown() throws SQLException {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Pool de conexiones cerrado");
        }
    }
    
    /**
     * Crea las tablas necesarias en la base de datos
     */
    private static void createTables() throws SQLException {
        try (Connection connection = getConnection();
             Statement stmt = connection.createStatement()) {
            
            // Tabla de usuarios
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(255) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) UNIQUE NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Tabla de directorios
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS directories (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "full_path VARCHAR(1000) NOT NULL, " +
                "parent_id BIGINT, " +
                "owner_id BIGINT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (parent_id) REFERENCES directories(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Tabla de archivos
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS files (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "original_name VARCHAR(255), " +
                "file_path VARCHAR(1000) NOT NULL, " +
                "file_size BIGINT, " +
                "mime_type VARCHAR(255), " +
                "checksum VARCHAR(255), " +
                "directory_id BIGINT, " +
                "owner_id BIGINT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (directory_id) REFERENCES directories(id) ON DELETE SET NULL, " +
                "FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Tabla de nodos
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS nodes (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "node_id VARCHAR(255) UNIQUE NOT NULL, " +
                "hostname VARCHAR(255) NOT NULL, " +
                "port INTEGER NOT NULL, " +
                "storage_path VARCHAR(1000) NOT NULL, " +
                "total_capacity BIGINT, " +
                "used_capacity BIGINT DEFAULT 0, " +
                "status VARCHAR(50) DEFAULT 'OFFLINE', " +
                "last_heartbeat TIMESTAMP, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Tabla de réplicas de archivos
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS file_replicas (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "file_id BIGINT NOT NULL, " +
                "node_id BIGINT NOT NULL, " +
                "local_path VARCHAR(1000) NOT NULL, " +
                "replica_checksum VARCHAR(255), " +
                "status VARCHAR(50) DEFAULT 'PENDING', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_verified TIMESTAMP, " +
                "FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (node_id) REFERENCES nodes(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Tabla de permisos
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS permissions (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT NOT NULL, " +
                "file_id BIGINT, " +
                "directory_id BIGINT, " +
                "permission_type VARCHAR(50) NOT NULL, " +
                "granted_by BIGINT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "expires_at TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (file_id) REFERENCES files(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (directory_id) REFERENCES directories(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (granted_by) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
            
            // Insertar usuarios iniciales
            insertInitialData(stmt);
            
            logger.info("Tablas creadas exitosamente");
        }
    }
    
    /**
     * Inserta datos iniciales en la base de datos
     */
    private static void insertInitialData(Statement stmt) throws SQLException {
        // Verificar si ya existen usuarios
        try {
            stmt.executeQuery("SELECT COUNT(*) FROM users").next();
            // Si llegamos aquí, la tabla ya tiene datos
            return;
        } catch (SQLException e) {
            // La tabla está vacía, insertar datos iniciales
        }
        
        // Insertar usuarios hardcodeados
        stmt.execute(
            "INSERT INTO users (id, username, password, email) VALUES " +
            "(1, 'admin', 'admin123', 'admin@sistema.com'), " +
            "(2, 'juan', 'juan123', 'juan@usuario.com'), " +
            "(3, 'maria', 'maria123', 'maria@usuario.com')"
        );
        
        logger.info("Datos iniciales insertados");
    }
}