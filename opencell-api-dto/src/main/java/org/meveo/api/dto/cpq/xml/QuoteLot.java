package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class QuoteLot {
    @XmlAttribute
    private String code;
    @XmlAttribute
    private Integer duration;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private Date executionDate;
    @XmlElementWrapper(name = "categories")
    @XmlElement(name = "category")
    private List<Category> categories;

    public QuoteLot(org.meveo.model.quote.QuoteLot lot, List<Category> categories) {
        code = lot.getCode();
        name = lot.getName();
        duration = lot.getDuration();
        executionDate = lot.getExecutionDate();
        this.categories = categories;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
