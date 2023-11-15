package org.meveo.model.article;

import org.hibernate.annotations.GenericGenerator;
import org.meveo.model.AuditableEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.enums.RuleOperatorEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@CustomFieldEntity(cftCodePrefix = "Attribute_Mapping")
@Table(name = "billing_attribute_mapping")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = { @org.hibernate.annotations.Parameter(name = "sequence_name", value = "billing_attribute_mapping_seq"), })
@Cacheable
public class AttributeMapping extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_mapping_line_id")
    private ArticleMappingLine articleMappingLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @Column(name = "attribute_value")
    private String attributeValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "operator", nullable = false)
    private RuleOperatorEnum operator = RuleOperatorEnum.EQUAL;

    public AttributeMapping() {
    }

    public AttributeMapping(Attribute attribute, String attributeValue) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
    }

    public AttributeMapping(Attribute attribute, String attributeValue, RuleOperatorEnum operator) {
        this.attribute = attribute;
        this.attributeValue = attributeValue;
        this.operator = operator;
    }

    public ArticleMappingLine getArticleMappingLine() {
        return articleMappingLine;
    }

    public void setArticleMappingLine(ArticleMappingLine articleMappingLine) {
        this.articleMappingLine = articleMappingLine;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public RuleOperatorEnum getOperator() {
        return operator;
    }

    public void setOperator(RuleOperatorEnum operator) {
        this.operator = operator;
    }
}
