package com.sources.app.entities;

import java.io.Serializable;
import java.util.Objects;

public final class ServiceCategoryId implements Serializable {

    private final Long service;   // Corresponde a ID_SERVICE
    private final Long category;  // Corresponde a ID_CATEGORY

    public ServiceCategoryId() {
        this.service = null;
        this.category = null;
    }

    public ServiceCategoryId(Long service, Long category) {
        this.service = service;
        this.category = category;
    }

    // equals() y hashCode() son obligatorios para que funcione la clave compuesta
    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceCategoryId)) {
            return false;
        }
        ServiceCategoryId that = (ServiceCategoryId) o;
        return Objects.equals(service, that.service)
                && Objects.equals(category, that.category);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(service, category);
    }
}
