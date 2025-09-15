package com.distribuidos.storagenode2.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Servicio para gestión de almacenamiento de archivos en el sistema local.
 * Maneja la escritura, lectura y organización de archivos.
 */
public class FileStorageService {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final String basePath;
    private final String nodeId;
    private final Map<String, String> fileIndex;
    
    public FileStorageService(String basePath, String nodeId) {
        this.basePath = basePath;
        this.nodeId = nodeId;
        this.fileIndex = new ConcurrentHashMap<>();
        initializeStorage();
    }
    
    private void initializeStorage() {
        try {
            Path baseDir = Paths.get(basePath);
            if (!Files.exists(baseDir)) {
                Files.createDirectories(baseDir);
                System.out.println("📁 " + getCurrentTimestamp() + " - Directorio de almacenamiento creado: " + basePath);
            }
            
            // Crear subdirectorios para organización
            createSubdirectories();
            
        } catch (IOException e) {
            System.err.println("❌ Error inicializando almacenamiento: " + e.getMessage());
        }
    }
    
    private void createSubdirectories() throws IOException {
        String[] subdirs = {"data", "temp", "backup", "metadata"};
        
        for (String subdir : subdirs) {
            Path subdirPath = Paths.get(basePath, subdir);
            if (!Files.exists(subdirPath)) {
                Files.createDirectories(subdirPath);
                System.out.println("📂 Subdirectorio creado: " + subdir);
            }
        }
    }
    
    /**
     * Almacena un archivo en el sistema de archivos local.
     */
    public String storeFile(Long fileId, String fileName, byte[] content, String checksum) throws IOException {
        System.out.println("💾 " + getCurrentTimestamp() + " - Almacenando archivo: " + fileName);
        
        // Crear nombre único
        String uniqueFileName = fileId + "_" + fileName;
        Path filePath = Paths.get(basePath, "data", uniqueFileName);
        
        // Escribir archivo
        Files.write(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        // Guardar metadatos
        String metadata = String.format("id:%s,name:%s,size:%d,checksum:%s,stored:%s", 
                                       fileId, fileName, content.length, checksum, getCurrentTimestamp());
        Path metadataPath = Paths.get(basePath, "metadata", uniqueFileName + ".meta");
        Files.write(metadataPath, metadata.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        
        // Actualizar índice
        fileIndex.put(fileId.toString(), filePath.toString());
        
        System.out.println("✅ Archivo almacenado en: " + filePath);
        return filePath.toString();
    }
    
    /**
     * Recupera un archivo del sistema de archivos local.
     */
    public byte[] retrieveFile(Long fileId, String localPath) throws IOException {
        Path path;
        
        if (localPath != null && !localPath.isEmpty()) {
            path = Paths.get(localPath);
        } else {
            String filePath = fileIndex.get(fileId.toString());
            if (filePath == null) {
                throw new IOException("Archivo no encontrado: " + fileId);
            }
            path = Paths.get(filePath);
        }
        
        if (!Files.exists(path)) {
            throw new IOException("Archivo físico no encontrado: " + path);
        }
        
        System.out.println("📤 " + getCurrentTimestamp() + " - Recuperando archivo: " + fileId);
        return Files.readAllBytes(path);
    }
    
    /**
     * Elimina un archivo del sistema de archivos local.
     */
    public boolean deleteFile(Long fileId, String localPath) throws IOException {
        Path path;
        
        if (localPath != null && !localPath.isEmpty()) {
            path = Paths.get(localPath);
        } else {
            String filePath = fileIndex.get(fileId.toString());
            if (filePath == null) {
                return false;
            }
            path = Paths.get(filePath);
        }
        
        boolean deleted = Files.deleteIfExists(path);
        
        if (deleted) {
            // Eliminar metadatos
            String fileName = path.getFileName().toString();
            Path metadataPath = Paths.get(basePath, "metadata", fileName + ".meta");
            Files.deleteIfExists(metadataPath);
            
            // Remover del índice
            fileIndex.remove(fileId.toString());
            
            System.out.println("🗑️ " + getCurrentTimestamp() + " - Archivo eliminado: " + fileId);
        }
        
        return deleted;
    }
    
    /**
     * Verifica la integridad de un archivo usando checksum
     */
    public boolean verifyFile(Long fileId, String localPath, String expectedChecksum) throws IOException {
        Path path;
        
        if (localPath != null && !localPath.isEmpty()) {
            path = Paths.get(localPath);
        } else {
            String filePath = fileIndex.get(fileId.toString());
            if (filePath == null) {
                return false;
            }
            path = Paths.get(filePath);
        }
        
        if (!Files.exists(path)) {
            return false;
        }
        
        // Calcular checksum del archivo actual
        byte[] fileContent = Files.readAllBytes(path);
        String actualChecksum = calculateChecksum(fileContent);
        
        return actualChecksum.equals(expectedChecksum);
    }
    
    /**
     * Calcula el checksum SHA-256 de un array de bytes
     */
    private String calculateChecksum(byte[] data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculando checksum", e);
        }
    }
    
    /**
     * Crea una copia de seguridad de un archivo.
     */
    public String createBackup(Long fileId) throws IOException {
        String originalPath = fileIndex.get(fileId.toString());
        if (originalPath == null) {
            throw new IOException("Archivo original no encontrado: " + fileId);
        }
        
        Path original = Paths.get(originalPath);
        if (!Files.exists(original)) {
            throw new IOException("Archivo físico no encontrado: " + originalPath);
        }
        
        String backupFileName = fileId + "_backup_" + System.currentTimeMillis();
        Path backupPath = Paths.get(basePath, "backup", backupFileName);
        
        Files.copy(original, backupPath);
        
        System.out.println("💾 " + getCurrentTimestamp() + " - Backup creado: " + backupPath);
        return backupPath.toString();
    }
    
    /**
     * Obtiene estadísticas del almacenamiento.
     */
    public Map<String, Object> getStorageStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        
        try {
            Path baseDir = Paths.get(basePath);
            
            // Contar archivos y calcular tamaño
            long totalFiles = 0;
            long totalSize = 0;
            
            if (Files.exists(baseDir)) {
                totalFiles = Files.walk(baseDir)
                        .filter(Files::isRegularFile)
                        .count();
                
                totalSize = Files.walk(baseDir)
                        .filter(Files::isRegularFile)
                        .mapToLong(path -> {
                            try {
                                return Files.size(path);
                            } catch (IOException e) {
                                return 0L;
                            }
                        })
                        .sum();
            }
            
            stats.put("totalFiles", totalFiles);
            stats.put("totalSize", totalSize);
            stats.put("indexedFiles", fileIndex.size());
            stats.put("basePath", basePath);
            stats.put("timestamp", getCurrentTimestamp());
            
        } catch (IOException e) {
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Obtiene la lista de IDs de archivos almacenados
     * @return Array de IDs de archivos
     */
    public Long[] getStoredFiles() {
        try {
            Path dataDir = Paths.get(basePath, "data");
            if (!Files.exists(dataDir)) {
                return new Long[0];
            }
            
            return Files.list(dataDir)
                    .filter(Files::isRegularFile)
                    .map(path -> {
                        String fileName = path.getFileName().toString();
                        // Extraer el ID del archivo (formato: fileId_originalName)
                        if (fileName.contains("_")) {
                            try {
                                return Long.parseLong(fileName.substring(0, fileName.indexOf("_")));
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        }
                        return null;
                    })
                    .filter(id -> id != null)
                    .toArray(Long[]::new);
                    
        } catch (IOException e) {
            System.err.println("❌ Error obteniendo lista de archivos: " + e.getMessage());
            return new Long[0];
        }
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }
}