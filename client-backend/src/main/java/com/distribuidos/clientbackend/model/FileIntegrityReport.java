package com.distribuidos.clientbackend.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Reporte de integridad de archivos en el sistema distribuido
 */
public class FileIntegrityReport implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long fileId;
    private String fileName;
    private LocalDateTime checkTimestamp;
    private boolean integrityValid;
    private String expectedChecksum;
    private Map<String, String> nodeChecksums;
    private List<String> validNodes;
    private List<String> corruptedNodes;
    private List<String> unavailableNodes;
    private int totalNodes;
    private int validNodeCount;
    private double integrityPercentage;
    private String recommendedAction;
    
    // Constructores
    public FileIntegrityReport() {
        this.checkTimestamp = LocalDateTime.now();
    }
    
    public FileIntegrityReport(Long fileId, String fileName) {
        this();
        this.fileId = fileId;
        this.fileName = fileName;
    }
    
    // Getters y Setters
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public LocalDateTime getCheckTimestamp() { return checkTimestamp; }
    public void setCheckTimestamp(LocalDateTime checkTimestamp) { this.checkTimestamp = checkTimestamp; }
    
    public boolean isIntegrityValid() { return integrityValid; }
    public void setIntegrityValid(boolean integrityValid) { this.integrityValid = integrityValid; }
    
    public String getExpectedChecksum() { return expectedChecksum; }
    public void setExpectedChecksum(String expectedChecksum) { this.expectedChecksum = expectedChecksum; }
    
    public Map<String, String> getNodeChecksums() { return nodeChecksums; }
    public void setNodeChecksums(Map<String, String> nodeChecksums) { this.nodeChecksums = nodeChecksums; }
    
    public List<String> getValidNodes() { return validNodes; }
    public void setValidNodes(List<String> validNodes) { this.validNodes = validNodes; }
    
    public List<String> getCorruptedNodes() { return corruptedNodes; }
    public void setCorruptedNodes(List<String> corruptedNodes) { this.corruptedNodes = corruptedNodes; }
    
    public List<String> getUnavailableNodes() { return unavailableNodes; }
    public void setUnavailableNodes(List<String> unavailableNodes) { this.unavailableNodes = unavailableNodes; }
    
    public int getTotalNodes() { return totalNodes; }
    public void setTotalNodes(int totalNodes) { this.totalNodes = totalNodes; }
    
    public int getValidNodeCount() { return validNodeCount; }
    public void setValidNodeCount(int validNodeCount) { this.validNodeCount = validNodeCount; }
    
    public double getIntegrityPercentage() { return integrityPercentage; }
    public void setIntegrityPercentage(double integrityPercentage) { this.integrityPercentage = integrityPercentage; }
    
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    
    /**
     * Calcula el porcentaje de integridad basado en nodos válidos
     */
    public void calculateIntegrityPercentage() {
        if (totalNodes > 0) {
            this.integrityPercentage = ((double) validNodeCount / totalNodes) * 100.0;
        } else {
            this.integrityPercentage = 0.0;
        }
    }
    
    /**
     * Genera recomendación de acción basada en el estado de integridad
     */
    public void generateRecommendedAction() {
        if (integrityPercentage >= 100.0) {
            this.recommendedAction = "Archivo íntegro en todos los nodos";
        } else if (integrityPercentage >= 66.0) {
            this.recommendedAction = "Reparar nodos corruptos desde nodos válidos";
        } else if (integrityPercentage >= 33.0) {
            this.recommendedAction = "CRÍTICO: Verificar y reparar urgentemente";
        } else {
            this.recommendedAction = "EMERGENCIA: Archivo posiblemente perdido";
        }
    }
    
    /**
     * Obtiene el estado de integridad como string
     */
    public String getIntegrityStatus() {
        if (integrityPercentage >= 100.0) {
            return "PERFECTO";
        } else if (integrityPercentage >= 80.0) {
            return "BUENO";
        } else if (integrityPercentage >= 60.0) {
            return "ACEPTABLE";
        } else if (integrityPercentage >= 40.0) {
            return "CRÍTICO";
        } else {
            return "EMERGENCIA";
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileIntegrityReport{");
        sb.append("fileId=").append(fileId);
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", integrityValid=").append(integrityValid);
        sb.append(", validNodeCount=").append(validNodeCount);
        sb.append(", totalNodes=").append(totalNodes);
        sb.append(", integrityPercentage=").append(String.format("%.2f", integrityPercentage)).append("%");
        sb.append(", status=").append(getIntegrityStatus());
        sb.append(", checkTimestamp=").append(checkTimestamp);
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Crear reporte de integridad válida
     */
    public static FileIntegrityReport valid(Long fileId, String fileName, int totalNodes) {
        FileIntegrityReport report = new FileIntegrityReport(fileId, fileName);
        report.setIntegrityValid(true);
        report.setTotalNodes(totalNodes);
        report.setValidNodeCount(totalNodes);
        report.calculateIntegrityPercentage();
        report.generateRecommendedAction();
        return report;
    }
    
    /**
     * Crear reporte de integridad comprometida
     */
    public static FileIntegrityReport compromised(Long fileId, String fileName, int validNodes, int totalNodes) {
        FileIntegrityReport report = new FileIntegrityReport(fileId, fileName);
        report.setIntegrityValid(false);
        report.setTotalNodes(totalNodes);
        report.setValidNodeCount(validNodes);
        report.calculateIntegrityPercentage();
        report.generateRecommendedAction();
        return report;
    }
}
