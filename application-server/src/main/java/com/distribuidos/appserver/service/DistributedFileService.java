package com.distribuidos.appserver.service;

import com.distribuidos.shared.model.File;
import com.distribuidos.shared.model.User;
import com.distribuidos.shared.rmi.StorageNodeInterface;
import com.distribuidos.shared.tcp.DatabaseMessage;
import com.distribuidos.shared.tcp.DatabaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio principal para gestión de archivos distribuidos.
 * Coordina operaciones entre nodos RMI y base de datos TCP.
 */
@Service
public class DistributedFileService {
    
    @Autowired
    private DatabaseCommunicationService databaseService;
    
    @Autowired
    private StorageNodeCommunicationService nodeService;
    
    @Value("${replication.factor}")
    private int replicationFactor;
    
    /**
     * Almacena un archivo en el sistema distribuido.
     */
    public boolean storeFile(String fileName, byte[] content, String ownerId) {
        System.out.println("📁 Almacenando archivo: " + fileName + " (propietario: " + ownerId + ")");
        
        try {
            // 1. Crear entrada en base de datos
            File file = new File();
            file.setName(fileName);
            file.setFileSize((long) content.length); // Usar setFileSize en lugar de setSize
            // file.setOwner() -- Para simplificar, usaremos solo el ownerId en el query
            file.setCreatedAt(LocalDateTime.now()); // Usar LocalDateTime.now()
            file.setUpdatedAt(LocalDateTime.now());
            
            DatabaseMessage dbMessage = new DatabaseMessage();
            dbMessage.setOperation("CREATE_FILE");
            dbMessage.setData(file);
            
            DatabaseResponse dbResponse = databaseService.sendMessage(dbMessage);
            if (!dbResponse.isSuccess()) {
                System.err.println("❌ Error creando entrada en BD: " + dbResponse.getErrorMessage());
                return false;
            }
            
            file = (File) dbResponse.getData();
            System.out.println("✅ Entrada en BD creada con ID: " + file.getId());
            
            // 2. Seleccionar nodos para replicación
            List<StorageNodeInterface> healthyNodes = nodeService.getHealthyNodes();
            if (healthyNodes.size() < replicationFactor) {
                System.err.println("❌ Nodos insuficientes para replicación. Disponibles: " + 
                                 healthyNodes.size() + ", Requeridos: " + replicationFactor);
                return false;
            }
            
            // Seleccionar nodos aleatoriamente
            Collections.shuffle(healthyNodes);
            List<StorageNodeInterface> selectedNodes = healthyNodes.subList(0, replicationFactor);
            
            // 3. Almacenar en nodos seleccionados
            List<String> successfulNodes = new ArrayList<>();
            for (StorageNodeInterface node : selectedNodes) {
                try {
                    String nodeId = "Node-" + successfulNodes.size(); // Generar ID temporalmente
                    System.out.println("📤 Enviando archivo a nodo: " + nodeId);
                    
                    // Usar la firma correcta del método storeFile
                    String localPath = node.storeFile(file.getId(), file.getName(), content, "checksum-placeholder");
                    if (localPath != null && !localPath.isEmpty()) {
                        successfulNodes.add(nodeId);
                        System.out.println("✅ Archivo almacenado en nodo: " + nodeId);
                    } else {
                        System.err.println("❌ Error almacenando en nodo: " + nodeId);
                    }
                } catch (RemoteException e) {
                    System.err.println("❌ Error de comunicación con nodo: " + e.getMessage());
                }
            }
            
            // 4. Actualizar réplicas en base de datos
            if (successfulNodes.size() > 0) {
                DatabaseMessage replicaMessage = new DatabaseMessage();
                replicaMessage.setOperation("UPDATE_FILE_REPLICAS");
                Map<String, Object> replicaData = new HashMap<>();
                replicaData.put("fileId", file.getId());
                replicaData.put("nodeIds", successfulNodes);
                replicaMessage.setData(replicaData);
                
                DatabaseResponse replicaResponse = databaseService.sendMessage(replicaMessage);
                if (replicaResponse.isSuccess()) {
                    System.out.println("✅ Información de réplicas actualizada. Nodos: " + successfulNodes);
                    return true;
                } else {
                    System.err.println("⚠️ Archivo almacenado pero error actualizando réplicas");
                    return true; // El archivo se almacenó, aunque falle la actualización de réplicas
                }
            } else {
                System.err.println("❌ No se pudo almacenar en ningún nodo");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error general almacenando archivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Recupera un archivo del sistema distribuido.
     */
    public byte[] retrieveFile(String fileName, String userId) {
        System.out.println("📥 Recuperando archivo: " + fileName + " (usuario: " + userId + ")");
        
        try {
            // 1. Buscar archivo en base de datos
            DatabaseMessage dbMessage = new DatabaseMessage();
            dbMessage.setOperation("GET_FILE_BY_NAME");
            Map<String, Object> fileQuery = new HashMap<>();
            fileQuery.put("fileName", fileName);
            fileQuery.put("userId", userId);
            dbMessage.setData(fileQuery);
            
            DatabaseResponse dbResponse = databaseService.sendMessage(dbMessage);
            if (!dbResponse.isSuccess()) {
                System.err.println("❌ Archivo no encontrado en BD: " + fileName);
                return null;
            }
            
            File file = (File) dbResponse.getData();
            System.out.println("✅ Archivo encontrado en BD con ID: " + file.getId());
            
            // 2. Obtener nodos con réplicas
            DatabaseMessage replicaMessage = new DatabaseMessage();
            replicaMessage.setOperation("GET_FILE_REPLICAS");
            replicaMessage.setData(file.getId());
            
            DatabaseResponse replicaResponse = databaseService.sendMessage(replicaMessage);
            if (!replicaResponse.isSuccess()) {
                System.err.println("❌ No se encontraron réplicas para archivo: " + fileName);
                return null;
            }
            
            @SuppressWarnings("unchecked")
            List<String> replicaNodes = (List<String>) replicaResponse.getData();
            System.out.println("📍 Réplicas encontradas en nodos: " + replicaNodes);
            
            // 3. Intentar recuperar de nodos disponibles
            for (String nodeId : replicaNodes) {
                try {
                    Optional<StorageNodeInterface> nodeOpt = nodeService.getNode("StorageNode" + nodeId);
                    if (nodeOpt.isPresent()) {
                        StorageNodeInterface node = nodeOpt.get();
                        System.out.println("📤 Solicitando archivo a nodo: " + nodeId);
                        
                        // Usar la firma correcta del método retrieveFile (necesitamos la ruta local)
                        // Por ahora usar una ruta placeholder - esto debería obtenerse de la BD
                        String localPath = "/storage/" + file.getId() + "_" + file.getName();
                        byte[] content = node.retrieveFile(file.getId(), localPath);
                        if (content != null) {
                            System.out.println("✅ Archivo recuperado exitosamente desde nodo: " + nodeId);
                            return content;
                        } else {
                            System.out.println("⚠️ Archivo no encontrado en nodo: " + nodeId);
                        }
                    } else {
                        System.out.println("❌ Nodo no disponible: " + nodeId);
                    }
                } catch (RemoteException e) {
                    System.err.println("❌ Error comunicándose con nodo " + nodeId + ": " + e.getMessage());
                }
            }
            
            System.err.println("❌ No se pudo recuperar el archivo de ningún nodo");
            return null;
            
        } catch (Exception e) {
            System.err.println("❌ Error general recuperando archivo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lista archivos del usuario.
     */
    public List<File> listUserFiles(String userId) {
        System.out.println("📋 Listando archivos del usuario: " + userId);
        
        DatabaseMessage dbMessage = new DatabaseMessage();
        dbMessage.setOperation("LIST_USER_FILES");
        dbMessage.setData(userId);
        
        DatabaseResponse dbResponse = databaseService.sendMessage(dbMessage);
        if (dbResponse.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<File> files = (List<File>) dbResponse.getData();
            System.out.println("✅ " + files.size() + " archivos encontrados para usuario: " + userId);
            return files;
        } else {
            System.err.println("❌ Error listando archivos: " + dbResponse.getErrorMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Elimina un archivo del sistema distribuido.
     */
    public boolean deleteFile(String fileName, String userId) {
        System.out.println("🗑️ Eliminando archivo: " + fileName + " (usuario: " + userId + ")");
        
        try {
            // 1. Buscar archivo en base de datos
            DatabaseMessage dbMessage = new DatabaseMessage();
            dbMessage.setOperation("GET_FILE_BY_NAME");
            Map<String, Object> fileQuery = new HashMap<>();
            fileQuery.put("fileName", fileName);
            fileQuery.put("userId", userId);
            dbMessage.setData(fileQuery);
            
            DatabaseResponse dbResponse = databaseService.sendMessage(dbMessage);
            if (!dbResponse.isSuccess()) {
                System.err.println("❌ Archivo no encontrado: " + fileName);
                return false;
            }
            
            File file = (File) dbResponse.getData();
            
            // 2. Obtener nodos con réplicas
            DatabaseMessage replicaMessage = new DatabaseMessage();
            replicaMessage.setOperation("GET_FILE_REPLICAS");
            replicaMessage.setData(file.getId());
            
            DatabaseResponse replicaResponse = databaseService.sendMessage(replicaMessage);
            if (replicaResponse.isSuccess()) {
                @SuppressWarnings("unchecked")
                List<String> replicaNodes = (List<String>) replicaResponse.getData();
                
                // 3. Eliminar de nodos
                for (String nodeId : replicaNodes) {
                    try {
                        Optional<StorageNodeInterface> nodeOpt = nodeService.getNode("StorageNode" + nodeId);
                        if (nodeOpt.isPresent()) {
                            StorageNodeInterface node = nodeOpt.get();
                            // Usar la firma correcta del método deleteFile
                            String localPath = "/storage/" + file.getId() + "_" + file.getName();
                            boolean deleted = node.deleteFile(file.getId(), localPath);
                            System.out.println((deleted ? "✅" : "❌") + 
                                             " Eliminación en nodo " + nodeId + ": " + 
                                             (deleted ? "SUCCESS" : "FAILED"));
                        }
                    } catch (RemoteException e) {
                        System.err.println("❌ Error eliminando de nodo " + nodeId + ": " + e.getMessage());
                    }
                }
            }
            
            // 4. Eliminar de base de datos
            DatabaseMessage deleteMessage = new DatabaseMessage();
            deleteMessage.setOperation("DELETE_FILE");
            deleteMessage.setData(file.getId());
            
            DatabaseResponse deleteResponse = databaseService.sendMessage(deleteMessage);
            if (deleteResponse.isSuccess()) {
                System.out.println("✅ Archivo eliminado completamente: " + fileName);
                return true;
            } else {
                System.err.println("❌ Error eliminando de BD: " + deleteResponse.getErrorMessage());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error general eliminando archivo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}