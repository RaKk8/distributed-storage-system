package com.distribuidos.shared.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz RMI para operaciones de archivos en nodos de almacenamiento
 */
public interface StorageNodeInterface extends Remote {
    
    /**
     * Almacena un archivo en el nodo
     * @param fileId ID del archivo
     * @param fileName Nombre del archivo
     * @param fileData Datos del archivo
     * @param checksum Checksum del archivo
     * @return Ruta local donde se almacenó el archivo
     * @throws RemoteException Si hay error en la comunicación remota
     */
    String storeFile(Long fileId, String fileName, byte[] fileData, String checksum) throws RemoteException;
    
    /**
     * Recupera un archivo del nodo
     * @param fileId ID del archivo
     * @param localPath Ruta local del archivo en el nodo
     * @return Datos del archivo
     * @throws RemoteException Si hay error en la comunicación remota
     */
    byte[] retrieveFile(Long fileId, String localPath) throws RemoteException;
    
    /**
     * Elimina un archivo del nodo
     * @param fileId ID del archivo
     * @param localPath Ruta local del archivo en el nodo
     * @return true si se eliminó correctamente
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean deleteFile(Long fileId, String localPath) throws RemoteException;
    
    /**
     * Verifica la integridad de un archivo
     * @param fileId ID del archivo
     * @param localPath Ruta local del archivo
     * @param expectedChecksum Checksum esperado
     * @return true si el archivo es íntegro
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean verifyFile(Long fileId, String localPath, String expectedChecksum) throws RemoteException;
    
    /**
     * Obtiene información del estado del nodo
     * @return Información del nodo (capacidad, uso, etc.)
     * @throws RemoteException Si hay error en la comunicación remota
     */
    NodeInfo getNodeInfo() throws RemoteException;
    
    /**
     * Envía heartbeat al servidor de aplicación
     * @return true si el nodo está operativo
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean heartbeat() throws RemoteException;
    
    /**
     * Obtiene la lista de archivos almacenados en el nodo
     * @return Array de IDs de archivos
     * @throws RemoteException Si hay error en la comunicación remota
     */
    Long[] getStoredFiles() throws RemoteException;
}