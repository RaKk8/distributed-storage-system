package com.distribuidos.appserver.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de los controladores REST del servidor de aplicación
 * Valida la funcionalidad de monitoreo y administración
 */
@SpringBootTest
@ActiveProfiles("test")
public class AdminControllerTest {
    
    private static final String SEPARATOR = "=" .repeat(60);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private MockMvc mockMvc;
    private AdminController adminController;
    
    @BeforeEach
    void setUp() {
        System.out.println(SEPARATOR);
        System.out.println("INICIANDO PRUEBAS DE CONTROLADORES ADMIN");
        System.out.println("Timestamp: " + LocalDateTime.now().format(TIMESTAMP_FORMAT));
        System.out.println(SEPARATOR);
        
        adminController = new AdminController();
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }
    
    @AfterEach
    void tearDown() {
        System.out.println(SEPARATOR);
        System.out.println("FINALIZANDO PRUEBAS DE CONTROLADORES ADMIN");
        System.out.println("Timestamp: " + LocalDateTime.now().format(TIMESTAMP_FORMAT));
        System.out.println(SEPARATOR);
    }
    
    @Test
    @DisplayName("Prueba de endpoint de estado del sistema")
    void testSystemStatus() throws Exception {
        System.out.println("\n📋 Ejecutando: Prueba de endpoint de estado del sistema");
        
        try {
            mockMvc.perform(get("/admin/status"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value("RUNNING"))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.uptime").exists());
            
            System.out.println("✅ Endpoint /admin/status respondiendo correctamente");
            System.out.println("✅ Formato JSON válido");
            System.out.println("✅ Campos requeridos presentes");
            
            System.out.println("\n🎯 RESULTADO: Endpoint de estado funcionando correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error en endpoint de estado: " + e.getMessage());
            throw e;
        }
    }
    
    @Test
    @DisplayName("Prueba de endpoint de métricas del sistema")
    void testSystemMetrics() throws Exception {
        System.out.println("\n📋 Ejecutando: Prueba de endpoint de métricas del sistema");
        
        try {
            mockMvc.perform(get("/admin/metrics"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.activeConnections").exists())
                    .andExpect(jsonPath("$.totalRequests").exists())
                    .andExpect(jsonPath("$.memoryUsage").exists());
            
            System.out.println("✅ Endpoint /admin/metrics respondiendo correctamente");
            System.out.println("✅ Métricas de sistema disponibles");
            System.out.println("✅ Información de monitoreo completa");
            
            System.out.println("\n🎯 RESULTADO: Endpoint de métricas funcionando correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error en endpoint de métricas: " + e.getMessage());
            throw e;
        }
    }
    
    @Test
    @DisplayName("Prueba de endpoint de nodos de almacenamiento")
    void testStorageNodes() throws Exception {
        System.out.println("\n📋 Ejecutando: Prueba de endpoint de nodos de almacenamiento");
        
        try {
            mockMvc.perform(get("/admin/storage-nodes"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.nodes").isArray());
            
            System.out.println("✅ Endpoint /admin/storage-nodes respondiendo correctamente");
            System.out.println("✅ Lista de nodos disponible");
            System.out.println("✅ Formato de respuesta válido");
            
            System.out.println("\n🎯 RESULTADO: Endpoint de nodos funcionando correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error en endpoint de nodos: " + e.getMessage());
            throw e;
        }
    }
    
    @Test
    @DisplayName("Prueba de endpoint de salud del sistema")
    void testHealthCheck() throws Exception {
        System.out.println("\n📋 Ejecutando: Prueba de endpoint de salud del sistema");
        
        try {
            mockMvc.perform(get("/admin/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.healthy").value(true))
                    .andExpect(jsonPath("$.components").exists());
            
            System.out.println("✅ Endpoint /admin/health respondiendo correctamente");
            System.out.println("✅ Estado de salud verificado");
            System.out.println("✅ Componentes del sistema monitoreados");
            
            System.out.println("\n🎯 RESULTADO: Endpoint de salud funcionando correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ Error en endpoint de salud: " + e.getMessage());
            throw e;
        }
    }
}