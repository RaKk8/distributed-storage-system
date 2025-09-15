package com.distribuidos.appserver.soap;

import com.distribuidos.appserver.service.DistributedFileService;
import com.distribuidos.appserver.soap.dto.*;
import com.distribuidos.shared.model.File;
import com.distribuidos.shared.model.User;
import com.distribuidos.shared.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;

/**
 * Endpoint SOAP para el sistema de almacenamiento distribuido.
 * Expone las funcionalidades principales via SOAP/HTTPS.
 */
@Endpoint
public class StorageServiceEndpoint {
    
    private static final String NAMESPACE_URI = "http://soap.appserver.distribuidos.com/";
    
    @Autowired
    private AuthenticationService authService;
    
    @Autowired
    private DistributedFileService fileService;
    
    /**
     * Autentica un usuario en el sistema.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AuthenticationRequest")
    @ResponsePayload
    public AuthenticationResponse authenticate(@RequestPayload AuthenticationRequest request) {
        System.out.println("🔐 SOAP: Solicitud de autenticación para usuario: " + request.getUsername());
        
        try {
            String token = authService.authenticate(request.getUsername(), request.getPassword());
            
            if (token != null) {
                System.out.println("✅ SOAP: Autenticación exitosa para: " + request.getUsername());
                return AuthenticationResponse.success(token, request.getUsername());
            } else {
                System.out.println("❌ SOAP: Credenciales inválidas para: " + request.getUsername());
                return AuthenticationResponse.failure("Invalid credentials");
            }
            
        } catch (Exception e) {
            System.err.println("❌ SOAP: Error en autenticación: " + e.getMessage());
            return AuthenticationResponse.failure("Authentication error: " + e.getMessage());
        }
    }
    
    /**
     * Sube un archivo al sistema distribuido.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FileUploadRequest")
    @ResponsePayload
    public FileOperationResponse uploadFile(@RequestPayload FileUploadRequest request) {
        System.out.println("📤 SOAP: Solicitud de subida de archivo: " + request.getFileName());
        
        try {
            // Validar token
            User user = authService.validateToken(request.getToken());
            if (user == null) {
                System.out.println("❌ SOAP: Token inválido para subida de archivo");
                return FileOperationResponse.failure("Invalid or expired token");
            }
            
            System.out.println("✅ SOAP: Token válido para usuario: " + user.getUsername());
            
            // Almacenar archivo
            boolean success = fileService.storeFile(request.getFileName(), request.getContent(), user.getUsername());
            
            if (success) {
                System.out.println("✅ SOAP: Archivo subido exitosamente: " + request.getFileName());
                return FileOperationResponse.success("File uploaded successfully", request.getFileName());
            } else {
                System.err.println("❌ SOAP: Error almacenando archivo: " + request.getFileName());
                return FileOperationResponse.failure("Failed to store file");
            }
            
        } catch (Exception e) {
            System.err.println("❌ SOAP: Error en subida de archivo: " + e.getMessage());
            return FileOperationResponse.failure("Upload error: " + e.getMessage());
        }
    }
    
    /**
     * Descarga un archivo del sistema distribuido.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FileDownloadRequest")
    @ResponsePayload
    public FileDownloadResponse downloadFile(@RequestPayload FileDownloadRequest request) {
        System.out.println("📥 SOAP: Solicitud de descarga de archivo: " + request.getFileName());
        
        try {
            // Validar token
            User user = authService.validateToken(request.getToken());
            if (user == null) {
                System.out.println("❌ SOAP: Token inválido para descarga de archivo");
                return FileDownloadResponse.failure("Invalid or expired token");
            }
            
            System.out.println("✅ SOAP: Token válido para usuario: " + user.getUsername());
            
            // Recuperar archivo
            byte[] content = fileService.retrieveFile(request.getFileName(), user.getUsername());
            
            if (content != null) {
                System.out.println("✅ SOAP: Archivo descargado exitosamente: " + request.getFileName());
                return FileDownloadResponse.success("File downloaded successfully", content);
            } else {
                System.err.println("❌ SOAP: Archivo no encontrado: " + request.getFileName());
                return FileDownloadResponse.failure("File not found");
            }
            
        } catch (Exception e) {
            System.err.println("❌ SOAP: Error en descarga de archivo: " + e.getMessage());
            return FileDownloadResponse.failure("Download error: " + e.getMessage());
        }
    }
    
    /**
     * Lista los archivos del usuario.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FileListRequest")
    @ResponsePayload
    public FileListResponse listFiles(@RequestPayload FileListRequest request) {
        System.out.println("📋 SOAP: Solicitud de listado de archivos");
        
        try {
            // Validar token
            User user = authService.validateToken(request.getToken());
            if (user == null) {
                System.out.println("❌ SOAP: Token inválido para listado de archivos");
                return FileListResponse.failure("Invalid or expired token");
            }
            
            System.out.println("✅ SOAP: Token válido para usuario: " + user.getUsername());
            
            // Listar archivos
            List<File> files = fileService.listUserFiles(user.getUsername());
            
            System.out.println("✅ SOAP: " + files.size() + " archivos listados para usuario: " + user.getUsername());
            return FileListResponse.success("Files retrieved successfully", files);
            
        } catch (Exception e) {
            System.err.println("❌ SOAP: Error listando archivos: " + e.getMessage());
            return FileListResponse.failure("List error: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un archivo del sistema distribuido.
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FileDeleteRequest")
    @ResponsePayload
    public FileOperationResponse deleteFile(@RequestPayload FileDeleteRequest request) {
        System.out.println("🗑️ SOAP: Solicitud de eliminación de archivo: " + request.getFileName());
        
        try {
            // Validar token
            User user = authService.validateToken(request.getToken());
            if (user == null) {
                System.out.println("❌ SOAP: Token inválido para eliminación de archivo");
                return FileOperationResponse.failure("Invalid or expired token");
            }
            
            System.out.println("✅ SOAP: Token válido para usuario: " + user.getUsername());
            
            // Eliminar archivo
            boolean success = fileService.deleteFile(request.getFileName(), user.getUsername());
            
            if (success) {
                System.out.println("✅ SOAP: Archivo eliminado exitosamente: " + request.getFileName());
                return FileOperationResponse.success("File deleted successfully");
            } else {
                System.err.println("❌ SOAP: Error eliminando archivo: " + request.getFileName());
                return FileOperationResponse.failure("Failed to delete file");
            }
            
        } catch (Exception e) {
            System.err.println("❌ SOAP: Error en eliminación de archivo: " + e.getMessage());
            return FileOperationResponse.failure("Delete error: " + e.getMessage());
        }
    }
}