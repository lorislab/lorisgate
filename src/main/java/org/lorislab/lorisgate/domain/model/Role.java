package org.lorislab.lorisgate.domain.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Role {
    private String name;
    private String description;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
