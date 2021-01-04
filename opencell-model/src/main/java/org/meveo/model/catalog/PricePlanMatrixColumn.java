package org.meveo.model.catalog;


import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "cpq_price_plan_matrix_column")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_column_sq"), })
public class PricePlanMatrixColumn extends BusinessEntity {

    @OneToOne
    @JoinColumn(name = "ppm_id")
    @NotNull
    private PricePlanMatrix pricePlanMatrix;

    @Column(name = "position")
    @NotNull
    private Integer position;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @NotNull
    private ColumnTypeEnum type;

    @Column(name = "el_value")
    @NotNull
    private String elValue;

    @OneToOne
    @JoinColumn(name = "offer_id")
    @NotNull
    private OfferTemplate offerTemplate;

    @OneToOne
    @JoinColumn(name = "product_id")
    @NotNull
    private Product product;

    @OneToOne
    @JoinColumn(name = "attribute_id")
    @NotNull
    private Attribute attribute;

    public PricePlanMatrix getPricePlanMatrix() {
        return pricePlanMatrix;
    }

    public void setPricePlanMatrix(PricePlanMatrix pricePlanMatrix) {
        this.pricePlanMatrix = pricePlanMatrix;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public ColumnTypeEnum getType() {
        return type;
    }

    public void setType(ColumnTypeEnum type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public String getElValue() {
        return elValue;
    }

    public void setElValue(String elValue) {
        this.elValue = elValue;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }
}
