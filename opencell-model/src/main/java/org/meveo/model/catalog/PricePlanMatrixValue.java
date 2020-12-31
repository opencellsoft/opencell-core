package org.meveo.model.catalog;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "cpq_price_plan_matrix_value")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_value_sq"), })
public class PricePlanMatrixValue extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "ppm_column_id")
    private PricePlanMatrixColumn pricePlanMatrixColumn;

    @OneToOne
    @JoinColumn(name= "ppml_id")
    private PricePlanMatrixLine pricePlanMatrixLine;

    @Column(name = "long_value")
    private Long longValue;

    @Column (name = "double_value")
    private Double doubleValue;

    @Column (name = "string_value")
    private String stringValue;

    @Column (name = "date_value")
    private Date dateValue;

    @Column (name = "from_date_value")
    private Date fromDateValue;

    @Column (name = "to_date_value")
    private Date toDateValue;

    @Column (name = "from_double_value")
    private Double fromDoubleValue;

    @Column (name = "to_double_value")
    private Double toDoubleValue;

    public PricePlanMatrixColumn getPricePlanMatrixColumn() {
        return pricePlanMatrixColumn;
    }

    public void setPricePlanMatrixColumn(PricePlanMatrixColumn pricePlanMatrixColumn) {
        this.pricePlanMatrixColumn = pricePlanMatrixColumn;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
