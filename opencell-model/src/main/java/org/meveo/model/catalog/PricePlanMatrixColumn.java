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
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "cpq_price_plan_matrix_column")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_column_sq"), })
@NamedQueries({
        @NamedQuery(name = "PricePlanMatrixColumn.findByAttributes", query = "select p from PricePlanMatrixColumn p where p.attribute in :attribute"),
        @NamedQuery(name = "PricePlanMatrixColumn.findByProduct", query = "select p from PricePlanMatrixColumn p where p.product in :product")
})
public class PricePlanMatrixColumn extends BusinessEntity {

    @OneToOne
    @JoinColumn(name = "ppm_version_id")
    @NotNull
    private PricePlanMatrixVersion pricePlanMatrixVersion;

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

    @OneToMany(mappedBy = "pricePlanMatrixColumn", fetch = FetchType.LAZY)
    List<PricePlanMatrixValue> pricePlanMatrixValues;

    public PricePlanMatrixVersion getPricePlanMatrixVersion() {
        return pricePlanMatrixVersion;
    }

    public void setPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        this.pricePlanMatrixVersion = pricePlanMatrixVersion;
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

    public List<PricePlanMatrixValue> getPricePlanMatrixValues() {
        return pricePlanMatrixValues;
    }

    public void setPricePlanMatrixValues(List<PricePlanMatrixValue> pricePlanMatrixValues) {
        this.pricePlanMatrixValues = pricePlanMatrixValues;
    }
}
