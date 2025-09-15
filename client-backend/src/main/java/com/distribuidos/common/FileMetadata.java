package com.distribuidos.common;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Metadatos de archivo distribuido que incluye información
 * sobre ubicaciones, integridad y replicación.
 */
public class FileMetadata implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String fileName;
    private final long size;
    private final String checksum;
    private final List<String> nodeLocations;
    private final long timestamp;
    
    public FileMetadata(String fileName, long size, String checksum, List<String> nodeLocations) {
        this.fileName = fileName;
        this.size = size;
        this.checksum = checksum;
        this.nodeLocations = new ArrayList<>(nodeLocations);
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public long getSize() {
        return size;
    }
    
    public String getChecksum() {
        return checksum;
    }
    
    public List<String> getNodeLocations() {
        return new ArrayList<>(nodeLocations);
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void addNodeLocation(String nodeId) {
        if (!nodeLocations.contains(nodeId)) {
            nodeLocations.add(nodeId);
        }
    }
    
    public void removeNodeLocation(String nodeId) {
        nodeLocations.remove(nodeId);
    }
    
    public int getReplicationCount() {
        return nodeLocations.size();
    }
    
    @Override
    public String toString() {
        return String.format("FileMetadata{file='%s', size=%d, replicas=%d, checksum='%s'}", 
                           fileName, size, nodeLocations.size(), checksum.substring(0, 8) + "...");
    }
}