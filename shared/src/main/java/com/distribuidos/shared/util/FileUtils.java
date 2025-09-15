package com.distribuidos.shared.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utilidades para operaciones de archivos
 */
public class FileUtils {
    
    /**
     * Lee un archivo completo en un array de bytes
     * @param filePath Ruta del archivo
     * @return Contenido del archivo
     * @throws IOException Si hay error leyendo el archivo
     */
    public static byte[] readFileBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }
    
    /**
     * Escribe datos a un archivo
     * @param filePath Ruta del archivo
     * @param data Datos a escribir
     * @throws IOException Si hay error escribiendo el archivo
     */
    public static void writeFileBytes(String filePath, byte[] data) throws IOException {
        Path path = Paths.get(filePath);
        // Crear directorios padre si no existen
        Files.createDirectories(path.getParent());
        Files.write(path, data);
    }
    
    /**
     * Copia un archivo de origen a destino
     * @param sourcePath Ruta del archivo origen
     * @param targetPath Ruta del archivo destino
     * @throws IOException Si hay error copiando el archivo
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        Path source = Paths.get(sourcePath);
        Path target = Paths.get(targetPath);
        Files.createDirectories(target.getParent());
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
    
    /**
     * Elimina un archivo
     * @param filePath Ruta del archivo
     * @return true si se eliminó correctamente
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Verifica si un archivo existe
     * @param filePath Ruta del archivo
     * @return true si el archivo existe
     */
    public static boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }
    
    /**
     * Obtiene el tamaño de un archivo
     * @param filePath Ruta del archivo
     * @return Tamaño en bytes
     * @throws IOException Si hay error accediendo al archivo
     */
    public static long getFileSize(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.size(path);
    }
    
    /**
     * Crea un directorio si no existe
     * @param dirPath Ruta del directorio
     * @throws IOException Si hay error creando el directorio
     */
    public static void createDirectoryIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        Files.createDirectories(path);
    }
    
    /**
     * Obtiene la extensión de un archivo
     * @param fileName Nombre del archivo
     * @return Extensión del archivo (sin el punto)
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1);
    }
    
    /**
     * Genera un nombre de archivo único agregando timestamp
     * @param originalName Nombre original del archivo
     * @return Nombre único del archivo
     */
    public static String generateUniqueFileName(String originalName) {
        String extension = getFileExtension(originalName);
        String nameWithoutExtension = originalName;
        
        if (!extension.isEmpty()) {
            nameWithoutExtension = originalName.substring(0, originalName.lastIndexOf('.'));
        }
        
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uniqueName = nameWithoutExtension + "_" + timestamp;
        
        if (!extension.isEmpty()) {
            uniqueName += "." + extension;
        }
        
        return uniqueName;
    }
    
    /**
     * Sanitiza un nombre de archivo removiendo caracteres no válidos
     * @param fileName Nombre del archivo
     * @return Nombre sanitizado
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "untitled";
        }
        
        // Remover caracteres no válidos para nombres de archivo
        String sanitized = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        
        // Remover espacios al inicio y final
        sanitized = sanitized.trim();
        
        // Si queda vacío, usar nombre por defecto
        if (sanitized.isEmpty()) {
            sanitized = "untitled";
        }
        
        return sanitized;
    }
    
    /**
     * Calcula el espacio disponible en un directorio
     * @param dirPath Ruta del directorio
     * @return Espacio disponible en bytes
     */
    public static long getAvailableSpace(String dirPath) {
        try {
            Path path = Paths.get(dirPath);
            return Files.getFileStore(path).getUsableSpace();
        } catch (IOException e) {
            return 0L;
        }
    }
}