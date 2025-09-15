package com.distribuidos.shared.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaz RMI para el servidor de aplicación
 */
public interface ApplicationServerInterface extends Remote {
    
    /**
     * Registra un nodo de almacenamiento en el servidor
     * @param nodeId ID único del nodo
     * @param hostname Hostname del nodo
     * @param port Puerto del nodo
     * @param storagePath Ruta de almacenamiento del nodo
     * @param totalCapacity Capacidad total del nodo
     * @return true si se registró correctamente
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean registerNode(String nodeId, String hostname, Integer port, 
                        String storagePath, Long totalCapacity) throws RemoteException;
    
    /**
     * Desregistra un nodo del servidor
     * @param nodeId ID del nodo
     * @return true si se desregistró correctamente
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean unregisterNode(String nodeId) throws RemoteException;
    
    /**
     * Actualiza el heartbeat de un nodo
     * @param nodeId ID del nodo
     * @param nodeInfo Información actualizada del nodo
     * @return true si se actualizó correctamente
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean updateNodeHeartbeat(String nodeId, NodeInfo nodeInfo) throws RemoteException;
    
    /**
     * Obtiene la lista de nodos activos
     * @return Lista de información de nodos
     * @throws RemoteException Si hay error en la comunicación remota
     */
    List<NodeInfo> getActiveNodes() throws RemoteException;
    
    /**
     * Notifica sobre el estado de una operación de archivo
     * @param fileId ID del archivo
     * @param nodeId ID del nodo
     * @param operation Tipo de operación (STORE, DELETE, VERIFY)
     * @param success Si la operación fue exitosa
     * @param message Mensaje adicional
     * @return true si se procesó la notificación
     * @throws RemoteException Si hay error en la comunicación remota
     */
    boolean notifyFileOperation(Long fileId, String nodeId, String operation, 
                               boolean success, String message) throws RemoteException;
}