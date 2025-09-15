package com.distribuidos.appserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Clase principal del servidor de aplicación.
 * Servidor Spring Boot que expone APIs SOAP/HTTPS y coordina
 * la comunicación con nodos RMI y base de datos TCP.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.distribuidos.appserver", "com.distribuidos.shared"})
public class ApplicationServerMain {
    
    public static void main(String[] args) {
        System.out.println("=== INICIANDO SERVIDOR DE APLICACIÓN ===");
        System.out.println("Puerto: 8080 (SOAP/HTTPS)");
        System.out.println("Protocolos: SOAP/HTTPS -> RMI -> TCP");
        System.out.println("=========================================");
        
        try {
            SpringApplication.run(ApplicationServerMain.class, args);
            System.out.println("✅ Servidor de aplicación iniciado exitosamente");
        } catch (Exception e) {
            System.err.println("❌ Error al iniciar el servidor de aplicación: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}