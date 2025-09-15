package com.distribuidos.appserver.service;

import com.distribuidos.shared.rmi.StorageNodeInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para comunicación RMI con nodos de almacenamiento.
 * Maneja el registro, descobrimiento y comunicación con los nodos.
 */
@Service
public class StorageNodeCommunicationService {
    
    @Value("${storage.nodes.count}")
    private int expectedNodesCount;
    
    @Value("${storage.nodes.port.base}")
    private int basePort;
    
    @Value("${storage.nodes.host}")
    private String nodesHost;
    
    private final Map<String, StorageNodeInterface> availableNodes = new ConcurrentHashMap<>();
    private final Map<String, Boolean> nodeHealthStatus = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initialize() {
        System.out.println("🔧 Inicializando comunicación RMI con nodos...");
        System.out.println("📊 Nodos esperados: " + expectedNodesCount);
        System.out.println("🌐 Host: " + nodesHost + ", Puerto base: " + basePort);
    }
    
    /**
     * Descubre y registra nodos de almacenamiento disponibles.
     */
    public void discoverNodes() {
        System.out.println("🔍 Buscando nodos de almacenamiento disponibles...");
        
        availableNodes.clear();
        nodeHealthStatus.clear();
        
        for (int i = 1; i <= expectedNodesCount; i++) {
            String nodeName = "StorageNode" + i;
            int nodePort = basePort + i - 1;
            
            try {
                Registry registry = LocateRegistry.getRegistry(nodesHost, nodePort);
                StorageNodeInterface node = (StorageNodeInterface) registry.lookup(nodeName);
                
                // Probar conexión
                boolean isHealthy = node.heartbeat(); // Usar heartbeat en lugar de isHealthy
                String nodeId = "Node-" + i; // Generar ID del nodo localmente
                
                availableNodes.put(nodeName, node);
                nodeHealthStatus.put(nodeName, isHealthy);
                
                System.out.println("✅ Nodo encontrado: " + nodeName + " (" + nodeId + ") - " + 
                                 (isHealthy ? "HEALTHY" : "UNHEALTHY"));
                
            } catch (RemoteException | NotBoundException e) {
                System.out.println("❌ Nodo no disponible: " + nodeName + " (puerto " + nodePort + ")");
                nodeHealthStatus.put(nodeName, false);
            }
        }
        
        int healthyNodes = (int) nodeHealthStatus.values().stream().mapToLong(h -> h ? 1 : 0).sum();
        System.out.println("📈 Nodos saludables: " + healthyNodes + "/" + expectedNodesCount);
    }
    
    /**
     * Obtiene una lista de nodos saludables disponibles.
     */
    public List<StorageNodeInterface> getHealthyNodes() {
        List<StorageNodeInterface> healthyNodes = new ArrayList<>();
        
        for (Map.Entry<String, StorageNodeInterface> entry : availableNodes.entrySet()) {
            String nodeName = entry.getKey();
            StorageNodeInterface node = entry.getValue();
            
            if (nodeHealthStatus.getOrDefault(nodeName, false)) {
                try {
                    // Verificar que el nodo siga saludable
                    if (node.heartbeat()) { // Usar heartbeat en lugar de isHealthy
                        healthyNodes.add(node);
                    } else {
                        nodeHealthStatus.put(nodeName, false);
                        System.out.println("⚠️ Nodo se volvió no saludable: " + nodeName);
                    }
                } catch (RemoteException e) {
                    nodeHealthStatus.put(nodeName, false);
                    System.out.println("❌ Error de comunicación con nodo: " + nodeName);
                }
            }
        }
        
        return healthyNodes;
    }
    
    /**
     * Obtiene un nodo específico por nombre.
     */
    public Optional<StorageNodeInterface> getNode(String nodeName) {
        StorageNodeInterface node = availableNodes.get(nodeName);
        if (node != null && nodeHealthStatus.getOrDefault(nodeName, false)) {
            return Optional.of(node);
        }
        return Optional.empty();
    }
    
    /**
     * Obtiene estadísticas de los nodos.
     */
    public Map<String, Object> getNodesStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("expectedNodes", expectedNodesCount);
        stats.put("discoveredNodes", availableNodes.size());
        stats.put("healthyNodes", nodeHealthStatus.values().stream().mapToLong(h -> h ? 1 : 0).sum());
        stats.put("nodeStatus", new HashMap<>(nodeHealthStatus));
        
        return stats;
    }
    
    /**
     * Verifica la salud de todos los nodos registrados.
     */
    public void checkNodesHealth() {
        System.out.println("🏥 Verificando salud de nodos...");
        
        for (Map.Entry<String, StorageNodeInterface> entry : availableNodes.entrySet()) {
            String nodeName = entry.getKey();
            StorageNodeInterface node = entry.getValue();
            
            try {
                boolean isHealthy = node.heartbeat(); // Usar heartbeat en lugar de isHealthy
                nodeHealthStatus.put(nodeName, isHealthy);
                System.out.println("📊 " + nodeName + ": " + (isHealthy ? "HEALTHY" : "UNHEALTHY"));
            } catch (RemoteException e) {
                nodeHealthStatus.put(nodeName, false);
                System.out.println("❌ " + nodeName + ": UNREACHABLE");
            }
        }
    }
}