package com.distribuidos.shared.soap;

import com.distribuidos.shared.dto.DirectoryDTO;
import com.distribuidos.shared.dto.FileDTO;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/**
 * Interfaz SOAP para operaciones de gestión de archivos
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface FileManagementService {
    
    /**
     * Autentica un usuario
     */
    @WebMethod
    String authenticateUser(@WebParam(name = "username") String username, 
                           @WebParam(name = "password") String password);
    
    /**
     * Crea un nuevo directorio
     */
    @WebMethod
    DirectoryDTO createDirectory(@WebParam(name = "token") String token,
                                @WebParam(name = "directoryName") String directoryName,
                                @WebParam(name = "parentDirectoryId") Long parentDirectoryId);
    
    /**
     * Lista directorios de un usuario
     */
    @WebMethod
    List<DirectoryDTO> listDirectories(@WebParam(name = "token") String token,
                                      @WebParam(name = "parentDirectoryId") Long parentDirectoryId);
    
    /**
     * Sube un archivo al sistema
     */
    @WebMethod
    FileDTO uploadFile(@WebParam(name = "token") String token,
                      @WebParam(name = "fileName") String fileName,
                      @WebParam(name = "fileData") byte[] fileData,
                      @WebParam(name = "directoryId") Long directoryId);
    
    /**
     * Descarga un archivo del sistema
     */
    @WebMethod
    byte[] downloadFile(@WebParam(name = "token") String token,
                       @WebParam(name = "fileId") Long fileId);
    
    /**
     * Lista archivos en un directorio
     */
    @WebMethod
    List<FileDTO> listFiles(@WebParam(name = "token") String token,
                           @WebParam(name = "directoryId") Long directoryId);
    
    /**
     * Elimina un archivo
     */
    @WebMethod
    boolean deleteFile(@WebParam(name = "token") String token,
                      @WebParam(name = "fileId") Long fileId);
    
    /**
     * Elimina un directorio
     */
    @WebMethod
    boolean deleteDirectory(@WebParam(name = "token") String token,
                           @WebParam(name = "directoryId") Long directoryId);
    
    /**
     * Renombra un archivo
     */
    @WebMethod
    FileDTO renameFile(@WebParam(name = "token") String token,
                      @WebParam(name = "fileId") Long fileId,
                      @WebParam(name = "newName") String newName);
    
    /**
     * Mueve un archivo a otro directorio
     */
    @WebMethod
    FileDTO moveFile(@WebParam(name = "token") String token,
                    @WebParam(name = "fileId") Long fileId,
                    @WebParam(name = "targetDirectoryId") Long targetDirectoryId);
    
    /**
     * Comparte un archivo con otro usuario
     */
    @WebMethod
    boolean shareFile(@WebParam(name = "token") String token,
                     @WebParam(name = "fileId") Long fileId,
                     @WebParam(name = "targetUsername") String targetUsername,
                     @WebParam(name = "permissionType") String permissionType);
    
    /**
     * Obtiene reporte de consumo de espacio
     */
    @WebMethod
    String getSpaceUsageReport(@WebParam(name = "token") String token);
    
    /**
     * Obtiene información del sistema
     */
    @WebMethod
    String getSystemInfo(@WebParam(name = "token") String token);
}