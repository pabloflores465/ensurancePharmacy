package com.sources.app.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Utility class to manage port allocation and .env file tracking for backend instances.
 */
public class PortManager {
    // Usar una ruta absoluta al archivo .env en el directorio raíz del proyecto
    private static final String ENV_FILE_PATH = System.getProperty("user.dir") + "/../../.env";
    private static final String INSURANCE_PREFIX = "VITE_SEGURO_";
    private static final String PHARMACY_PREFIX = "VITE_FARMACIA_";
    private static final int DEFAULT_INSURANCE_PORT = 8080;
    private static final int DEFAULT_PHARMACY_PORT = 8081;
    
    private String instanceType;
    private int assignedPort;
    private int instanceNumber;
    
    /**
     * Creates a port manager for the specified backend type
     *
     * @param isInsurance true if this is an insurance backend, false if pharmacy
     * @param defaultPort the default port defined in App.java
     */
    public PortManager(boolean isInsurance, int defaultPort) {
        this.instanceType = isInsurance ? INSURANCE_PREFIX : PHARMACY_PREFIX;
        System.out.println("PortManager: Usando archivo .env en: " + ENV_FILE_PATH);
        System.out.println("PortManager: Directorio actual: " + System.getProperty("user.dir"));
        
        this.assignedPort = allocatePort(isInsurance, defaultPort);
        registerInstance();
        
        System.out.println("PortManager: Registrado como " + instanceType + instanceNumber + " en puerto " + assignedPort);
        
        // Add shutdown hook to remove entry when JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("PortManager: Ejecutando shutdown hook para eliminar " + instanceType + instanceNumber);
            removeInstance();
        }));
    }
    
    /**
     * Gets the allocated port for this instance
     * 
     * @return the port number
     */
    public int getPort() {
        return assignedPort;
    }
    
    /**
     * Allocates the next available port for this backend type
     */
    private int allocatePort(boolean isInsurance, int defaultPort) {
        Map<String, String> envEntries = readEnvFile();
        
        // Find existing entries of this type
        String prefix = isInsurance ? INSURANCE_PREFIX : PHARMACY_PREFIX;
        Map<Integer, Integer> instances = new HashMap<>();
        
        int maxInstanceNum = 0;
        for (Map.Entry<String, String> entry : envEntries.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                try {
                    int instanceNum = Integer.parseInt(entry.getKey().substring(prefix.length()));
                    int port = Integer.parseInt(entry.getValue());
                    instances.put(instanceNum, port);
                    maxInstanceNum = Math.max(maxInstanceNum, instanceNum);
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }
        }
        
        // Allocate the next available instance number
        instanceNumber = maxInstanceNum + 1;
        
        // If this is the first instance, use the default port
        if (instanceNumber == 1) {
            return defaultPort;
        }
        
        // Otherwise, use the default port + offset
        int basePort = isInsurance ? DEFAULT_INSURANCE_PORT : DEFAULT_PHARMACY_PORT;
        return basePort + (instanceNumber - 1);
    }
    
    /**
     * Registers this instance in the .env file
     */
    private void registerInstance() {
        Map<String, String> envEntries = readEnvFile();
        envEntries.put(instanceType + instanceNumber, String.valueOf(assignedPort));
        writeEnvFile(envEntries);
    }
    
    /**
     * Removes this instance from the .env file
     */
    private void removeInstance() {
        Map<String, String> envEntries = readEnvFile();
        envEntries.remove(instanceType + instanceNumber);
        writeEnvFile(envEntries);
    }
    
    /**
     * Reads the current .env file contents
     * 
     * @return a map of environment variable names to values
     */
    private Map<String, String> readEnvFile() {
        Map<String, String> result = new HashMap<>();
        File envFile = new File(ENV_FILE_PATH);
        
        // Create parent directories and the file if needed
        if (!envFile.exists()) {
            try {
                envFile.getParentFile().mkdirs();
                envFile.createNewFile();
                System.out.println("PortManager: Creado nuevo archivo .env en " + envFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("PortManager: Error al crear archivo .env: " + e.getMessage());
            }
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorar comentarios y líneas vacías
                if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                    continue;
                }
                
                int equalPos = line.indexOf('=');
                if (equalPos > 0) {
                    String key = line.substring(0, equalPos).trim();
                    String value = line.substring(equalPos + 1).trim();
                    result.put(key, value);
                }
            }
            System.out.println("PortManager: Leídas " + result.size() + " entradas del archivo .env");
        } catch (IOException e) {
            System.err.println("PortManager: Error al leer archivo .env: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Writes the updated .env file
     * 
     * @param entries the environment variables to write
     */
    private void writeEnvFile(Map<String, String> entries) {
        File envFile = new File(ENV_FILE_PATH);
        
        try {
            // Asegurar que el archivo existe
            if (!envFile.exists()) {
                envFile.getParentFile().mkdirs();
                envFile.createNewFile();
            }
            
            // Leer las líneas existentes para preservar comentarios
            List<String> existingLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                        existingLines.add(line);
                    }
                }
            } catch (IOException e) {
                // Si no se puede leer, solo continuar con la escritura
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(envFile))) {
                // Primero escribir los comentarios
                for (String line : existingLines) {
                    writer.write(line);
                    writer.newLine();
                }
                
                // Luego escribir las entradas
                for (Map.Entry<String, String> entry : entries.entrySet()) {
                    writer.write(entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                }
                
                System.out.println("PortManager: Escritas " + entries.size() + " entradas al archivo .env");
            }
        } catch (IOException e) {
            System.err.println("PortManager: Error al escribir archivo .env: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 