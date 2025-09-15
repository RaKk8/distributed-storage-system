package com.distribuidos.storagenode1;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase principal para el nodo de almacenamiento distribuido #1.
 * Inicializa el servidor RMI y registra el servicio de almacenamiento.
 */
public class StorageNode1Main {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SERVICE_NAME = "StorageNode1";
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("🚀 INICIANDO STORAGE NODE 1");
        System.out.println("Timestamp: " + getCurrentTimestamp());
        System.out.println("===============================================");
        
        // Parsear argumentos
        int rmiPort = 1099;
        String nodeId = "1";
        String storagePath = "./storage/node1";
        
        if (args.length >= 1) {
            try {
                rmiPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ Puerto RMI inválido, usando default: " + rmiPort);
            }
        }
        
        if (args.length >= 2) {
            nodeId = args[1];
        }
        
        if (args.length >= 3) {
            storagePath = args[2];
        }
        
        System.out.println("📋 Configuración del nodo:");
        System.out.println("   🆔 Node ID: " + nodeId);
        System.out.println("   🌐 Puerto RMI: " + rmiPort);
        System.out.println("   📂 Directorio de almacenamiento: " + storagePath);
        System.out.println();
        
        try {
            // Crear el registry RMI
            System.out.println("🔧 " + getCurrentTimestamp() + " - Iniciando registry RMI en puerto " + rmiPort);
            Registry registry = LocateRegistry.createRegistry(rmiPort);
            
            // Crear la implementación del nodo de almacenamiento
            System.out.println("📦 " + getCurrentTimestamp() + " - Creando instancia de StorageNode1");
            StorageNode1Impl storageNode = new StorageNode1Impl();
            
            // Registrar el servicio en el registry
            System.out.println("📝 " + getCurrentTimestamp() + " - Registrando servicio: " + SERVICE_NAME);
            registry.rebind(SERVICE_NAME, storageNode);
            
            System.out.println();
            System.out.println("✅ ======= STORAGE NODE 1 INICIADO EXITOSAMENTE =======");
            System.out.println("🌐 Servicio RMI disponible en: rmi://localhost:" + rmiPort + "/" + SERVICE_NAME);
            System.out.println("📂 Directorio de almacenamiento: " + storagePath);
            System.out.println("🆔 Node ID: " + nodeId);
            System.out.println("⏰ Hora de inicio: " + getCurrentTimestamp());
            System.out.println("🔄 Estado: ACTIVO - Esperando conexiones...");
            System.out.println("=======================================================");
            System.out.println();
            
            // Configurar shutdown hook para limpieza
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("🛑 " + getCurrentTimestamp() + " - Recibida señal de apagado");
                System.out.println("👋 " + getCurrentTimestamp() + " - Storage Node 1 terminado");
            }));
            
            // Mantener el programa ejecutándose
            System.out.println("💡 Presiona Ctrl+C para detener el nodo de almacenamiento");
            System.out.println();
            
            // Loop principal con heartbeat logging
            while (true) {
                Thread.sleep(30000); // 30 segundos
                Long[] storedFiles = storageNode.getStoredFiles();
                System.out.println("💓 " + getCurrentTimestamp() + " - Storage Node 1 activo - " +
                                 "Archivos: " + storedFiles.length);
            }
            
        } catch (Exception e) {
            System.err.println("❌ " + getCurrentTimestamp() + " - Error fatal en Storage Node 1: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
}