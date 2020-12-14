package org.meveo.model.article;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@CustomFieldEntity(cftCodePrefix = "Attribute_Mapping")
@Table(name = "billing_attribute_mapping")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_attribute_mapping_seq"), })
public class AttributeMapping extends BusinessEntity {

    @ManyToOne
    @JoinColumn(name = "article_mapping_line_id")
    private ArticleMappingLine articleMappingLine;

    @Column(name = "attribute")
    private String attribute;

    @Column(name = "attribute_value")
    private String attributeValue;

    public AttributeMapping() {
    }

    public AttributeMapping(String attribute, String attributeValue) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    public ArticleMappingLine getArticleMappingLine() {
        return articleMappingLine;
    }

    public void setArticleMappingLine(ArticleMappingLine articleMappingLine) {
        this.articleMappingLine = articleMappingLine;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
