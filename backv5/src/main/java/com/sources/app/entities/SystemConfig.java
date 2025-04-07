package com.sources.app.entities;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SYSTEM_CONFIG")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONFIG")
    private Long idConfig;

    @Column(name = "CONFIG_KEY", nullable = false, unique = true)
    private String configKey;

    @Column(name = "CONFIG_VALUE", nullable = false)
    private String configValue;

    @Column(name = "CONFIG_DESCRIPTION")
    private String description;

    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    // Esta anotación asegura que lastUpdated se actualice automáticamente
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = new Date();
    }

    // Constructores
    public SystemConfig() {
    }

    public SystemConfig(String configKey, String configValue, String description) {
        this.configKey = configKey;
        this.configValue = configValue;
        this.description = description;
        this.lastUpdated = new Date();
    }

    // Getters y Setters
    public Long getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Long idConfig) {
        this.idConfig = idConfig;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
} 