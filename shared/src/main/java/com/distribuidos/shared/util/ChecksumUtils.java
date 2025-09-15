package com.distribuidos.shared.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Utilidades para cálculo de checksums y hashing
 */
public class ChecksumUtils {
    
    private static final String SHA256 = "SHA-256";
    private static final String MD5 = "MD5";
    
    /**
     * Calcula el checksum SHA-256 de un array de bytes
     * @param data Datos para calcular el checksum
     * @return Checksum en formato hexadecimal
     */
    public static String calculateSHA256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA256);
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Calcula el checksum MD5 de un array de bytes
     * @param data Datos para calcular el checksum
     * @return Checksum en formato hexadecimal
     */
    public static String calculateMD5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
    
    /**
     * Calcula el checksum SHA-256 de una cadena
     * @param text Texto para calcular el checksum
     * @return Checksum en formato hexadecimal
     */
    public static String calculateSHA256(String text) {
        return calculateSHA256(text.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Convierte un array de bytes a representación hexadecimal
     * @param bytes Array de bytes
     * @return Cadena hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Verifica si dos checksums son iguales
     * @param checksum1 Primer checksum
     * @param checksum2 Segundo checksum
     * @return true si son iguales
     */
    public static boolean verifyChecksum(String checksum1, String checksum2) {
        if (checksum1 == null || checksum2 == null) {
            return false;
        }
        return checksum1.equalsIgnoreCase(checksum2);
    }
    
    /**
     * Genera un ID único basado en timestamp y datos
     * @param data Datos adicionales para el ID
     * @return ID único
     */
    public static String generateUniqueId(String data) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String combined = timestamp + "_" + data;
        return calculateSHA256(combined).substring(0, 16);
    }
}