package com.distribuidos.appserver.test;

import com.distribuidos.appserver.soap.dto.*;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * Cliente de prueba SOAP para validar endpoints.
 * Simula las operaciones de un cliente real.
 */
public class SOAPTestClient extends WebServiceGatewaySupport {
    
    private final String serviceUrl;
    
    public SOAPTestClient(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        
        // Configurar marshaller
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.distribuidos.appserver.soap.dto");
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
        
        // Configurar template
        WebServiceTemplate template = getWebServiceTemplate();
        template.setDefaultUri(serviceUrl);
    }
    
    /**
     * Prueba de autenticación.
     */
    public AuthenticationResponse authenticate(String username, String password) {
        System.out.println("🔐 SOAP Client: Autenticando usuario " + username);
        
        AuthenticationRequest request = new AuthenticationRequest(username, password);
        
        try {
            AuthenticationResponse response = (AuthenticationResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, request);
            
            System.out.println("📨 SOAP Response: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error en autenticación SOAP: " + e.getMessage());
            return AuthenticationResponse.failure("Client error: " + e.getMessage());
        }
    }
    
    /**
     * Prueba de subida de archivo.
     */
    public FileOperationResponse uploadFile(String token, String fileName, byte[] content) {
        System.out.println("📤 SOAP Client: Subiendo archivo " + fileName);
        
        FileUploadRequest request = new FileUploadRequest(token, fileName, content);
        
        try {
            FileOperationResponse response = (FileOperationResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, request);
            
            System.out.println("📨 SOAP Response: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error en subida SOAP: " + e.getMessage());
            return FileOperationResponse.failure("Client error: " + e.getMessage());
        }
    }
    
    /**
     * Prueba de listado de archivos.
     */
    public FileListResponse listFiles(String token) {
        System.out.println("📋 SOAP Client: Listando archivos");
        
        FileListRequest request = new FileListRequest(token);
        
        try {
            FileListResponse response = (FileListResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, request);
            
            System.out.println("📨 SOAP Response: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error en listado SOAP: " + e.getMessage());
            return FileListResponse.failure("Client error: " + e.getMessage());
        }
    }
    
    /**
     * Prueba de descarga de archivo.
     */
    public FileDownloadResponse downloadFile(String token, String fileName) {
        System.out.println("📥 SOAP Client: Descargando archivo " + fileName);
        
        FileDownloadRequest request = new FileDownloadRequest(token, fileName);
        
        try {
            FileDownloadResponse response = (FileDownloadResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, request);
            
            System.out.println("📨 SOAP Response: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error en descarga SOAP: " + e.getMessage());
            return FileDownloadResponse.failure("Client error: " + e.getMessage());
        }
    }
    
    /**
     * Prueba de eliminación de archivo.
     */
    public FileOperationResponse deleteFile(String token, String fileName) {
        System.out.println("🗑️ SOAP Client: Eliminando archivo " + fileName);
        
        FileDeleteRequest request = new FileDeleteRequest(token, fileName);
        
        try {
            FileOperationResponse response = (FileOperationResponse) getWebServiceTemplate()
                .marshalSendAndReceive(serviceUrl, request);
            
            System.out.println("📨 SOAP Response: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error en eliminación SOAP: " + e.getMessage());
            return FileOperationResponse.failure("Client error: " + e.getMessage());
        }
    }
}