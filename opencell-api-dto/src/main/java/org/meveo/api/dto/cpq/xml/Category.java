package org.meveo.api.dto.cpq.xml;

import org.meveo.model.billing.InvoiceCategory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Category {
    @XmlAttribute
    private String code;
    @XmlAttribute
    private String label;
    @XmlAttribute
    private Integer sortIndex;
    @XmlElementWrapper(name = "subCategories")
    @XmlElement(name = "subCategory")
    private List<SubCategory> subCategories;

    public Category(InvoiceCategory category, List<SubCategory> subCategories, String tradingLanguage) {
        this.code = category.getCode();
        this.label = category.getDescriptionI18n().get(tradingLanguage) == null ? category.getDescription() : category.getDescriptionI18n().get(tradingLanguage);
        this.sortIndex = category.getSortIndex();
        this.subCategories = subCategories;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
