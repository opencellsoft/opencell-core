package org.meveo.api.dto.cpq.xml;

import org.apache.commons.lang3.StringUtils;

public class Name {
    private String quality;
    private String name;
    private String firstName;
    private String lastName;

    public Name(org.meveo.model.shared.Name name) {
        if (name == null) {
            return;
        }
        this.name = getOrEmpty(name.getFirstName()) + getOrEmpty(name.getLastName());
        if (name.getTitle() != null) {
            this.quality = name.getTitle().getIsCompany() ? "Society" : name.getTitle().getDescription();
        }
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String getOrEmpty(String value) {
        return StringUtils.isNotBlank(value) ? value + " ": "";
    }
}
