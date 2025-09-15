package com.distribuidos.storagenode2;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase principal para iniciar el Storage Node 2
 */
public class StorageNode2Main {
    
    private static final String SERVICE_NAME = "StorageNode2";
    private static final int RMI_PORT = 1100;
    private static final String NODE_ID = "storage-node-2";
    private static final String STORAGE_PATH = "./storage/node2";
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    
    public static void main(String[] args) {
        System.out.println();
        System.out.println("🚀 ================ INICIANDO STORAGE NODE 2 ================");
        System.out.println("⏰ Hora de inicio: " + getCurrentTimestamp());
        System.out.println("🌐 Puerto RMI: " + RMI_PORT);
        System.out.println("📂 Directorio de almacenamiento: " + STORAGE_PATH);
        System.out.println("🆔 Node ID: " + NODE_ID);
        System.out.println("=============================================================");
        System.out.println();
        
        try {
            // Crear el registry RMI
            System.out.println("🔧 " + getCurrentTimestamp() + " - Iniciando registry RMI en puerto " + RMI_PORT);
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            
            // Crear la implementación del nodo de almacenamiento
            System.out.println("📦 " + getCurrentTimestamp() + " - Creando instancia de StorageNode2");
            StorageNode2Impl storageNode = new StorageNode2Impl();
            
            // Registrar el servicio en el registry
            System.out.println("📝 " + getCurrentTimestamp() + " - Registrando servicio: " + SERVICE_NAME);
            registry.rebind(SERVICE_NAME, storageNode);
            
            System.out.println();
            System.out.println("✅ ======= STORAGE NODE 2 INICIADO EXITOSAMENTE =======");
            System.out.println("🌐 Servicio RMI disponible en: rmi://localhost:" + RMI_PORT + "/" + SERVICE_NAME);
            System.out.println("📂 Directorio de almacenamiento: " + STORAGE_PATH);
            System.out.println("🆔 Node ID: " + NODE_ID);
            System.out.println("⏰ Hora de inicio: " + getCurrentTimestamp());
            System.out.println("🔄 Estado: ACTIVO - Esperando conexiones...");
            System.out.println("=======================================================");
            System.out.println();
            
            // Configurar shutdown hook para limpieza
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println();
                System.out.println("🛑 " + getCurrentTimestamp() + " - Recibida señal de apagado");
                System.out.println("👋 " + getCurrentTimestamp() + " - Storage Node 2 terminado");
            }));
            
            // Mantener el programa ejecutándose
            System.out.println("💡 Presiona Ctrl+C para detener el nodo de almacenamiento");
            System.out.println();
            
            // Loop principal con heartbeat logging
            while (true) {
                Thread.sleep(30000); // 30 segundos
                Long[] storedFiles = storageNode.getStoredFiles();
                System.out.println("💓 " + getCurrentTimestamp() + " - Storage Node 2 activo - " +
                                 "Archivos: " + storedFiles.length);
            }
            
        } catch (Exception e) {
            System.err.println("❌ " + getCurrentTimestamp() + " - Error fatal en Storage Node 2: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static String getCurrentTimestamp() {
        return timeFormat.format(new Date());
    }
}