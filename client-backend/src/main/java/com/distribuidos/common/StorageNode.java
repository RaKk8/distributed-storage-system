package com.distribuidos.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota para nodos de almacenamiento distribuido.
 * Define las operaciones básicas que debe implementar cada nodo.
 */
public interface StorageNode extends Remote {
    
    /**
     * Almacena un archivo en el nodo
     */
    boolean storeFile(String fileName, byte[] content) throws RemoteException;
    
    /**
     * Recupera un archivo del nodo
     */
    byte[] retrieveFile(String fileName) throws RemoteException;
    
    /**
     * Elimina un archivo del nodo
     */
    boolean deleteFile(String fileName) throws RemoteException;
    
    /**
     * Verifica el estado de salud del nodo
     */
    boolean isHealthy() throws RemoteException;
    
    /**
     * Obtiene estadísticas del nodo
     */
    String getNodeStats() throws RemoteException;
    
    /**
     * Lista los archivos almacenados en el nodo
     */
    String[] listFiles() throws RemoteException;
    
    /**
     * Obtiene el espacio disponible en el nodo
     */
    long getAvailableSpace() throws RemoteException;
    
    /**
     * Obtiene el identificador único del nodo
     */
    String getNodeId() throws RemoteException;
}