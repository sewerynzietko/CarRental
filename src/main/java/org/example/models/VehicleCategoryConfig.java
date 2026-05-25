package org.example.models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "vehicle_category_config")
@Getter @Setter @ToString
public class VehicleCategoryConfig {
    @Id
    private String category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, String> attributes = new HashMap<>();

    public VehicleCategoryConfig() {}

    @Builder
    public VehicleCategoryConfig(String category, Map<String, String> attributes) {
        this.category = category;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void addAttribute(String name, String type) {
        attributes.put(name, type);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public VehicleCategoryConfig copy() {
        return VehicleCategoryConfig.builder()
                .category(category)
                .attributes(new HashMap<>(attributes))
                .build();
    }
}