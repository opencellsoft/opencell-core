package org.meveo.api.dto.cpq.xml;

public class Name {
    private String quality;
    private String name;

    public Name(org.meveo.model.shared.Name name) {
        if(name == null)
            return;
        this.name = name.getFirstName() + " " + name.getLastName();
        this.quality = name.getTitle().getIsCompany() ? "Society" :name.getTitle().getDescription();
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
}
