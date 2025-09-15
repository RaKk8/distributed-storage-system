package com.distribuidos.appserver.test;

import com.distribuidos.appserver.ApplicationServerMain;
import com.distribuidos.appserver.soap.dto.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Ejecutor principal de pruebas para el servidor de aplicación.
 * Ejecuta pruebas de integración y SOAP completas.
 */
public class ApplicationServerTestRunner {
    
    private static final String SOAP_URL = "http://localhost:8080/ws";
    private static final String TEST_FILE_CONTENT = "Este es un archivo de prueba para el sistema distribuido.";
    
    public static void main(String[] args) {
        System.out.println("🚀 ===== INICIANDO PRUEBAS DEL SERVIDOR DE APLICACIÓN =====");
        
        ConfigurableApplicationContext context = null;
        
        try {
            // 1. Iniciar servidor de aplicación
            System.out.println("\n📡 Iniciando servidor de aplicación...");
            context = SpringApplication.run(ApplicationServerMain.class, args);
            
            // Esperar a que el servidor se inicie completamente
            System.out.println("⏳ Esperando inicialización completa...");
            TimeUnit.SECONDS.sleep(5);
            
            // 2. Ejecutar pruebas SOAP
            runSOAPTests();
            
            // 3. Mostrar estadísticas finales
            showSystemStatistics();
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (context != null) {
                System.out.println("\n🛑 Deteniendo servidor de aplicación...");
                context.close();
            }
        }
        
        System.out.println("\n✅ ===== PRUEBAS COMPLETADAS =====");
    }
    
    private static void runSOAPTests() {
        System.out.println("\n🧪 ===== PRUEBAS SOAP/HTTPS =====");
        
        try {
            SOAPTestClient soapClient = new SOAPTestClient(SOAP_URL);
            
            // Test 1: Autenticación exitosa
            System.out.println("\n🔐 Test 1: Autenticación Exitosa");
            AuthenticationResponse authResponse = soapClient.authenticate("admin", "admin123");
            
            if (!authResponse.isSuccess()) {
                System.out.println("❌ Autenticación falló: " + authResponse.getMessage());
                return;
            }
            
            String token = authResponse.getToken();
            System.out.println("✅ Token obtenido: " + (token != null ? "***" : "null"));
            
            // Test 2: Autenticación fallida
            System.out.println("\n❌ Test 2: Autenticación Fallida (credenciales inválidas)");
            AuthenticationResponse badAuthResponse = soapClient.authenticate("invalid", "invalid");
            if (!badAuthResponse.isSuccess()) {
                System.out.println("✅ Rechazo correcto de credenciales inválidas");
            } else {
                System.out.println("❌ Se aceptaron credenciales inválidas (ERROR)");
            }
            
            // Test 3: Subida de archivo
            System.out.println("\n📤 Test 3: Subida de Archivo");
            byte[] fileContent = TEST_FILE_CONTENT.getBytes(StandardCharsets.UTF_8);
            FileOperationResponse uploadResponse = soapClient.uploadFile(token, "test-file.txt", fileContent);
            
            if (uploadResponse.isSuccess()) {
                System.out.println("✅ Archivo subido exitosamente");
            } else {
                System.out.println("❌ Error subiendo archivo: " + uploadResponse.getMessage());
            }
            
            // Test 4: Listado de archivos
            System.out.println("\n📋 Test 4: Listado de Archivos");
            FileListResponse listResponse = soapClient.listFiles(token);
            
            if (listResponse.isSuccess()) {
                System.out.println("✅ Listado obtenido: " + listResponse.getFiles().size() + " archivos");
                for (FileListResponse.FileInfo file : listResponse.getFiles()) {
                    System.out.println("   📄 " + file.getName() + " (" + file.getSize() + " bytes)");
                }
            } else {
                System.out.println("❌ Error listando archivos: " + listResponse.getMessage());
            }
            
            // Test 5: Descarga de archivo
            System.out.println("\n📥 Test 5: Descarga de Archivo");
            FileDownloadResponse downloadResponse = soapClient.downloadFile(token, "test-file.txt");
            
            if (downloadResponse.isSuccess()) {
                String downloadedContent = new String(downloadResponse.getContent(), StandardCharsets.UTF_8);
                if (TEST_FILE_CONTENT.equals(downloadedContent)) {
                    System.out.println("✅ Archivo descargado correctamente (contenido verificado)");
                } else {
                    System.out.println("⚠️ Archivo descargado pero contenido no coincide");
                }
            } else {
                System.out.println("❌ Error descargando archivo: " + downloadResponse.getMessage());
            }
            
            // Test 6: Eliminación de archivo
            System.out.println("\n🗑️ Test 6: Eliminación de Archivo");
            FileOperationResponse deleteResponse = soapClient.deleteFile(token, "test-file.txt");
            
            if (deleteResponse.isSuccess()) {
                System.out.println("✅ Archivo eliminado exitosamente");
            } else {
                System.out.println("❌ Error eliminando archivo: " + deleteResponse.getMessage());
            }
            
            // Test 7: Token inválido
            System.out.println("\n🚫 Test 7: Token Inválido");
            FileListResponse invalidTokenResponse = soapClient.listFiles("invalid-token");
            if (!invalidTokenResponse.isSuccess()) {
                System.out.println("✅ Rechazo correcto de token inválido");
            } else {
                System.out.println("❌ Se aceptó token inválido (ERROR)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en pruebas SOAP: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void showSystemStatistics() {
        System.out.println("\n📊 ===== ESTADÍSTICAS DEL SISTEMA =====");
        System.out.println("🌐 Servidor SOAP: http://localhost:8080/ws");
        System.out.println("📄 WSDL: http://localhost:8080/ws/storage.wsdl");
        System.out.println("🔗 Protocolos: SOAP/HTTPS -> RMI -> TCP");
        System.out.println("💾 Base de datos: TCP puerto 9001");
        System.out.println("🔧 Nodos RMI: puertos 1099-1101");
        System.out.println("======================================");
    }
}